package com.helloarbridge4;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.SelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.helloarbridge4.Object.ObjectHandler;


public class ARActivity extends AppCompatActivity {
    private static final String TAG = ARActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final int FRAME_COUNT_THRESH = 15;

    private static final int PERSONAL_ID = R.id.radio_personal;
    private static final int CARRYON_ID = R.id.radio_carryon;
    private static final int DUFFEL_ID = R.id.radio_duffel;

    private ArFragment arFragment;
    private ImageButton removeObjects;
    private TextView onScreenText;

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

            setContentView(R.layout.ar_layout);



            radioGroup = findViewById(R.id.change_type);
            onScreenText = findViewById(R.id.onScreenText);
            removeObjects = findViewById(R.id.removeObjects);
            initFragment();

            if (!checkIsSupportedDeviceOrFinish(this)) {
                return;
            }

            initSession();

            sceneFormObjectHandler = new ObjectHandler(this.getApplicationContext());

            arFragment.setOnTapArPlaneListener(
                    (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                        initAnchor(hitResult);
                        disablePlaneDetection(config);
                        createNode(arFragment);
                        setModel(radioGroup.getCheckedRadioButtonId());
                    });

            arFragment.getArSceneView().getScene().addOnUpdateListener(this::onSceneUpdate);

            radioGroup.setOnCheckedChangeListener(
                    (group, checkedId) -> {
                        setModel(checkedId);
                    }

            );

            removeObjects.setOnClickListener(
                    w -> {
                        node.setRenderable(null);
                        radioGroup.check(R.id.radio_carryon);
                    }
            );
    }

    private void createNode(ArFragment arFragment) {
        try {
            node = new TransformableNode(arFragment.getTransformationSystem());
        } catch (Exception e){
            Log.d(TAG, "objectHandler: " + e.getMessage());
        }
    }

    private void disablePlaneDetection(Config config) {
        try {
            //disable plane detection
            config.setPlaneFindingMode(Config.PlaneFindingMode.DISABLED);
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
            horizontalPlaneDetection(config);
        } catch (Exception e) {
            Log.e(TAG, "session: " + e.getMessage());
        }
    }

    private void horizontalPlaneDetection(Config config) {
        config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL);
    }

    private void onSceneUpdate(FrameTime frameTime) {
        arFragment.onUpdate(frameTime);
        disablePlaneDetection(config);

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
            Log.e(TAG, "planeDetection: " + e.getMessage());
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

    private void attachMain() {
        if (anchorNode == null) return;
        sceneFormObjectHandler.setCarryOnNeutral(anchorNode, node);
    }

    private void attachduffel() {
        if (anchorNode == null) return;
        sceneFormObjectHandler.setDuffelNeutral(anchorNode, node);
    }

    private void attachPersonalMain() {
        if (anchorNode == null) return;
        sceneFormObjectHandler.setPersonalItemNeutral(anchorNode,node);
    }

    private void attachPersonalRed() {
       //TO-DO
    }

    private void attachPersonalGreen() {
        //TO-DO
    }

    private void attachDuffelGreen() {
        //TO-DO
    }

    private void attachDuffelRed() {
        //TO-DO
    }

    private void attachGreenMain(){
        //TO-DO
    }

    private void attachRedMain(){
        //TO-DO
    }

    private void setModel(int toggleId) {
        removeAllModels();
        switch (toggleId){
            case PERSONAL_ID:
                attachPersonalMain();
                break;
            case DUFFEL_ID:
                attachduffel();
                break;
            case CARRYON_ID:
                attachMain();
                break;
            default:
                attachMain();
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
