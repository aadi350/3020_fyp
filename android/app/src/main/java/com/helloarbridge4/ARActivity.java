package com.helloarbridge4;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.LightEstimate;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Light;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.SelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;

import com.helloarbridge4.Object.ObjectCodes;
import com.helloarbridge4.SizeCheck.ColourChangeHandler;
import com.helloarbridge4.SizeCheck.FitCodes;
import com.helloarbridge4.SizeCheck.SizeCheckHandler;

import java.nio.FloatBuffer;


public class ARActivity extends AppCompatActivity{
    private static final String SCN_TAG = "OnSceneUpdate";
    private static final String TAG = ARActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final int FRAME_COUNT_THRESH = 60;
    private static final float LIGHT_THRESH = 0.3f;

    private static final int PERSONAL_ID = R.id.radio_personal;
    private static final int CARRYON_ID = R.id.radio_carryon;
    private static final int DUFFEL_ID = R.id.radio_duffel;

    private ArFragment arFragment;
    private ImageButton removeObjects;
    private TextView onScreenText;
    boolean objectPlaced = false;

    private ColourChangeHandler colourChangeHandler;
    private TransformableNode node;

    private int frames = 0;
    private int framesStart = 0;


    private SizeCheckHandler sizeHandler;
    private ObjectCodes currentModel;

    //local coordinates of placed object anchor
    private Vector3 anchorPosition;
    private RadioGroup radioGroup;
    private AnchorNode anchorNode;

    static TextView debugText_height, debugText_width, debugText_length;

    private Session session;
    private Config config;
    private Scene scene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            //RN Bridge
            Intent intent = getIntent();
            sizeHandler = new SizeCheckHandler();
            String message = intent.getStringExtra(MainActivity.REQ_MSG);
            super.onCreate(savedInstanceState);

            if (!checkIsSupportedDeviceOrFinish(this)) {
                return;
            }
            //Perimssions handling
        try {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.CAMERA},
                    0
            );

            ArCoreApk.Availability ArCoreSupported = ArCoreApk.getInstance().checkAvailability(this);
            ArCoreApk.getInstance().requestInstall(this, ArCoreSupported.isSupported());

            } catch (UnavailableUserDeclinedInstallationException e) {
                Log.e(TAG, e.getLocalizedMessage());
            } catch (UnavailableDeviceNotCompatibleException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }


            //connecting views
            setContentView(R.layout.ar_layout);
            radioGroup = findViewById(R.id.change_type);
            onScreenText = findViewById(R.id.onScreenText);
            removeObjects = findViewById(R.id.removeObjects);

            //TODO remove
            debugText_height = findViewById(R.id.debugText_height);
            debugText_width = findViewById(R.id.debugText_width);
            debugText_length = findViewById(R.id.debugText_length);
            //end remove

            colourChangeHandler = new ColourChangeHandler(this.getApplicationContext());

            initFragment();
            initSession();
//
//                Frame frame = arFragment.getArSceneView().getArFrame();
//                LightEstimate lightEstimate = frame.getLightEstimate();
//                Float pixelIntensity = lightEstimate.getPixelIntensity();


                    arFragment.setOnTapArPlaneListener(
                            (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                                if(plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                                    return;
                                }

                                disablePlaneDetection();

                                if (!objectPlaced) {
                                    initAnchor(hitResult);
                                    createNode(arFragment);

                                    colourChangeHandler.setAnchorNode(anchorNode);
                                    colourChangeHandler.setTransformableNode(node);

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
                                if (objectPlaced) {
                                    objectPlaced = false;
                                    framesStart = 0;
                                    try {
                                        removeAnchorNode(anchorNode);
                                    } catch (NullPointerException e) {
                                        Log.w(TAG, e.getLocalizedMessage());
                                    }
                                    radioGroup.clearCheck();
                                }
                            }
                    );

        }



  private void removeAnchorNode(AnchorNode nodeToremove) throws NullPointerException {
        //Remove an anchor node
        if (nodeToremove != null) {
            arFragment.getArSceneView().getScene().removeChild(nodeToremove);
            nodeToremove.getAnchor().detach();
            nodeToremove.setParent(null);
            nodeToremove = null;
        }
    }

    private void createNode(ArFragment arFragment) throws NullPointerException {
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
        } catch (NullPointerException e){
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
            config.setLightEstimationMode(Config.LightEstimationMode.AMBIENT_INTENSITY);
            config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL);
        } catch (Exception e) {
            Log.e(TAG, "session: " + e.getMessage());
        }
    }

    private void onSceneUpdate(FrameTime frameTime) {

        frames++;
        Log.d(SCN_TAG, String.valueOf(frames));
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

        try {
            setOnScreenText(arFragment);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }


        Frame frame = arFragment.getArSceneView().getArFrame();


                if (nodeNotNull() && objectPlaced) {
                    FitCodes fits = FitCodes.NONE;
                    try {

                        PointCloud pointCloud = frame.acquirePointCloud();
                        FloatBuffer pointBuffer = pointCloud.getPoints();
                        Vector3 anchorNodePosition = anchorNode.getWorldPosition();
                        fits = sizeHandler.checkIfFits(currentModel, anchorNodePosition, pointBuffer);
                    } catch (Exception e) {
                        Log.e(TAG,e.getLocalizedMessage());
                    }

                    colourChangeHandler.setObject(fits);
                    //TODO remove
                    updateDebugText(
                                String.valueOf(sizeHandler.getBoxLength()),
                                String.valueOf(sizeHandler.getBoxWidth()),
                                String.valueOf(sizeHandler.getHighZ())
                    );
                }


    }

    private boolean nodeNotNull() {
        return (node != null);
    }



    private void setModel(int toggleId) {
        removeAllModels();
        switch (toggleId){
            case PERSONAL_ID:
                currentModel = ObjectCodes.PERSONAL;
                colourChangeHandler.updateObject(currentModel);
                colourChangeHandler.setObject(FitCodes.NONE);
                break;
            case DUFFEL_ID:
                currentModel = ObjectCodes.DUFFEL;
                colourChangeHandler.updateObject(currentModel);
                colourChangeHandler.setObject(FitCodes.NONE);
                break;
            case CARRYON_ID:
                currentModel = ObjectCodes.CARRYON;
                colourChangeHandler.updateObject(currentModel);
                colourChangeHandler.setObject(FitCodes.NONE);
                break;
            default:
                break;
        }
        Log.d("setModel", "exit");
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


    //TODO remove
    private void updateDebugText(String textOne, String textTwo, String textThree) {
        String lengthText = "Length: " + textOne;
        String widthText = "Width: " + textTwo;
        String heightText = "HighZ: " + textThree;
        debugText_length.setText(lengthText);
        debugText_width.setText(widthText);
        debugText_height.setText(heightText);

    }

    //TODO remove
    private void setOnScreenText(ArFragment arFragment) throws NullPointerException {
        Frame frame = arFragment.getArSceneView().getArFrame();

        if (frame != null) {
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

    }




}
