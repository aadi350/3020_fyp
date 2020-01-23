package com.helloarbridge4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher;
import com.google.ar.core.Anchor;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.SelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.helloarbridge4.Helper.DisplayRotationHelper;
import com.helloarbridge4.Object.ObjectCodes;
import com.helloarbridge4.Object.ObjectHandler;
import com.helloarbridge4.Object.SceneFormObject;
import com.helloarbridge4.Render.BackgroundRenderer;
import com.helloarbridge4.Render.PlaneRenderer;
import com.helloarbridge4.Render.PointCloudRenderer;
import com.helloarbridge4.SizeCheck.FitCodes;
import com.helloarbridge4.SizeCheck.ObjectDetection;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class ARActivity extends AppCompatActivity  {
    private static final String TAG = ARActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final int FRAME_COUNT_THRESH = 15;

    private static final int PERSONAL_ID = R.id.radio_personal;
    private static final int CARRYON_ID = R.id.radio_carryon;
    private static final int DUFFEL_ID = R.id.radio_duffel;

    private ArFragment arFragment;
    private ImageButton removeObjects;
    private TextView onScreenText;
    boolean objectPlaced = false;

    private ObjectHandler sceneFormObjectHandler;
    private TransformableNode node;

    private int frameCount = 0;

    //local coordinates of placed object anchor
    private Vector3 anchorPosition;
    private RadioGroup radioGroup;
    private AnchorNode anchorNode;

    private Session session;
    private Config config;
    private Scene scene;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            //RN Bridge
            Intent intent = getIntent();
            String message = intent.getStringExtra(MainActivity.REQ_MSG);
            super.onCreate(savedInstanceState);

            if (!checkIsSupportedDeviceOrFinish(this)) {
                return;
            }

            setContentView(R.layout.ar_layout);

            radioGroup = findViewById(R.id.change_type);
            onScreenText = findViewById(R.id.onScreenText);
            removeObjects = findViewById(R.id.removeObjects);

            initFragment();
            initSession();

            sceneFormObjectHandler = new ObjectHandler(this.getApplicationContext());


            arFragment.setOnTapArPlaneListener(
                    (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                        disablePlaneDetection();
                        if(plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING)
                            return;


                        if (!objectPlaced) {
                            initAnchor(hitResult);
                            createNode(arFragment);
                            sceneFormObjectHandler.setAnchorNode(anchorNode);
                            sceneFormObjectHandler.setTransformableNode(node);
                            setModel(radioGroup.getCheckedRadioButtonId());
                            objectPlaced = true;
                        }
                    });

            arFragment.getArSceneView().getScene().addOnUpdateListener(this::onSceneUpdate);

            radioGroup.setOnCheckedChangeListener(
                    (group, checkedId) -> {
                        setModel(checkedId);
                    }

            );

            removeObjects.setOnClickListener(
                    w -> {
                        //TODO abstract into function
                        if (objectPlaced) {
                            objectPlaced = false;
                            removeAnchorNode(anchorNode);
                            radioGroup.clearCheck();

                        }
                    }
            );
    }



  private void removeAnchorNode(AnchorNode nodeToremove) {
        //Remove an anchor node
        if (nodeToremove != null) {
            arFragment.getArSceneView().getScene().removeChild(nodeToremove);
            nodeToremove.getAnchor().detach();
            nodeToremove.setParent(null);
            nodeToremove = null;
        }
    }


    private void changeObjectColour(FitCodes fit_code) {
        //TODO abstract into  class
        int objectId = radioGroup.getCheckedRadioButtonId();
        switch (objectId) {
            case CARRYON_ID:
                switch (fit_code) {
                    case NONE:
                        sceneFormObjectHandler.setCarryOnNeutral();
                        break;
                    case FIT:
                        sceneFormObjectHandler.setCarryOnFits();
                        break;
                    case LARGE:
                        sceneFormObjectHandler.setCarryOnLarge();
                        break;
                }
                break;
            case DUFFEL_ID:
                switch (fit_code) {
                    case NONE:
                        sceneFormObjectHandler.setDuffelNeutral();
                        break;
                    case FIT:
                        sceneFormObjectHandler.setDuffelFits();
                        break;
                    case LARGE:
                        sceneFormObjectHandler.setCarryOnFits();
                        break;
                }
                break;
            case PERSONAL_ID:
                switch (fit_code) {
                    case NONE:
                        sceneFormObjectHandler.setPersonalItemNeutral();
                        break;
                    case FIT:
                        sceneFormObjectHandler.setPersonalItemFits();
                        break;
                    case LARGE:
                        sceneFormObjectHandler.setPersonalItemLarge();
                        break;
                }
                break;
        }
    }

    private void createNode(ArFragment arFragment) {
        try {
            node = new TransformableNode(arFragment.getTransformationSystem());
        } catch (Exception e){
            Log.d(TAG, "objectHandler: " + e.getMessage());
        }
    }

    private void disablePlaneDetection() {
        try {
            //disable plane detection
            arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
            arFragment.getPlaneDiscoveryController().hide();
            arFragment.getPlaneDiscoveryController().setInstructionView(null);
            arFragment.getArSceneView().getPlaneRenderer().setEnabled(false);
        } catch (Exception e){
            Log.e("getSession",e.getMessage());
        }
    }

    private void initAnchor(HitResult hitResult) {
        // Create the Anchor at hit result
        Anchor anchor = hitResult.createAnchor();
        anchorNode = new AnchorNode(anchor);
        anchorPosition = anchorNode.getLocalPosition();
        //attach arFragment to hitResult via anchorNode
        anchorNode.setParent(arFragment.getArSceneView().getScene());
    }

    private void initFragment() {
        try {
            arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
            scene = arFragment.getArSceneView().getScene();
            //arFragment.getTransformationSystem().setSelectionVisualizer(new CustomVisualizer());
        } catch (NullPointerException n){
            Log.wtf("arFragment", n.getMessage());
        }
    }

    private void initSession() {
        try{
            session = new Session(this);
            config = new Config(session);
            horizontalPlaneDetection();
        } catch (Exception e) {
            Log.e(TAG, "session: " + e.getMessage());
        }
    }

    private void horizontalPlaneDetection() {
        config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL);
    }

    private void onSceneUpdate(FrameTime frameTime) {
        arFragment.onUpdate(frameTime);
        disablePlaneDetection();


        // If there is no frame then don't process anything.
        if (arFragment.getArSceneView().getArFrame() == null) {
            return;
        }
        // If ARCore is not tracking yet, then don't process anything.
        if (arFragment.getArSceneView().getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
            return;
        }

        Boolean objectDetected = isObjectDetected();
        if (objectDetected) {
            changeObjectColour(FitCodes.FIT);
        } else {
            changeObjectColour(FitCodes.NONE);
        }

        System.out.println("Object Detected: " + objectDetected);


        try {
            //onScreenText.setText(objectDetected.toString());
            setOnScreenText(arFragment);
        } catch (NullPointerException e) {
            Log.e(TAG, "planeDetection: " + e.getMessage());
        }
    }

    private Boolean isObjectDetected() {
        try{
            Frame frame = arFragment.getArSceneView().getArFrame();
            PointCloud pointCloud = frame.acquirePointCloud();
            if (pointCloud == null) return false;
            ObjectDetection objectDetection = ObjectDetection.getObjectDetector();
            objectDetection.loadPointCloud(pointCloud);
            return objectDetection.objectWithinRegion(node);
        } catch(NullPointerException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    private void setOnScreenText(ArFragment arFragment) throws NullPointerException {
        Frame frame = arFragment.getArSceneView().getArFrame();
        for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
            if (plane != null || node.getRenderable() == null) {
                onScreenText.setText(R.string.planeDetected);
            } if(node.getRenderable() != null) {
                onScreenText.setText(R.string.objectPlaced);
            }else {
                onScreenText.setText(R.string.planeNotDetected);
            }
        }
    }



    private void setModel(int toggleId) {
        removeAllModels();
        switch (toggleId){
            case PERSONAL_ID:
                sceneFormObjectHandler.setPersonalItemNeutral();
                break;
            case DUFFEL_ID:
                sceneFormObjectHandler.setDuffelNeutral();
                break;
            case CARRYON_ID:
                sceneFormObjectHandler.setCarryOnNeutral();
                break;
            default:
                break;
        }
        Log.d("setModel", "exxit");
    }

    private void removeAllModels(){
        try{
            node.setRenderable(null);
        } catch (Exception e){
            Log.e("removeAllModels", e.getMessage());
        }
    }

    //API Required Calls
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }
    public class CustomVisualizer implements SelectionVisualizer {
        @Override
        public void applySelectionVisual(BaseTransformableNode node) {}
        @Override
        public void removeSelectionVisual(BaseTransformableNode node) {}
    }



}
