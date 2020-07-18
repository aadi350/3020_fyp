package com.reactlibrary;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.SelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;

import com.reactlibrary.Object.ObjectCodes;
import com.reactlibrary.ColourChange.ColourChangeHandler;
import com.reactlibrary.SizeCheck.SizeCheckHandler;
import java.util.Objects;


public class ARActivity extends AppCompatActivity {
    private static final String TAG = "ARActivity";
    private static final double MIN_OPENGL_VERSION = 3.0;

    private static final int PERSONAL_ID = R.id.radio_personal;
    private static final int CARRYON_ID = R.id.radio_carryon;
    private static final int DUFFEL_ID = R.id.radio_duffel;

    private ArFragment arFragment;
    private ImageButton removeObjects;
    private ArSceneView arSceneView;
    private boolean cameraPermissionRequested;
    boolean objectPlaced = false;

    private ColourChangeHandler colourChangeHandler;
    private TransformableNode node;
    private Pose planePose;
    private Pose androidSensorPose;
    private long cTime = 0, pTime = 0;

    private SizeCheckHandler sizeHandler;
    private ObjectCodes currentModel;

    //local coordinates of placed object anchor
    private RadioGroup radioGroup;
    private TextView debugTextView;
    private ViewFlipper viewFlipper;
    private AnchorNode anchorNode;
    PointCloudVisualiser pcVis;

    private View tapToPlace;
    private View scanObject;

    public final int AR_LAYOUT = R.layout.ar_layout;
    private Session session;
    private Config config;

    private Animation in, out;

    //ViewFlipper IDs
    final int PROMPT_TEXT = R.id.prompt_text;
    final int OBJECT_PLACED = R.id.object_placed;
    final int FITS_TEXT = R.id.fits_text;
    final int LARGE_TEXT = R.id.large_text;

    int PROMPT_ID, OBJECT_PLACED_ID, FITS_ID, LARGE_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //receives intent to launch from RN Bridge
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(AR_LAYOUT);

        sizeHandler = new SizeCheckHandler();
        sizeHandler.setObject(ObjectCodes.CARRYON);

        //connecting views
        viewFlipper = findViewById(R.id.viewFlipper);
        radioGroup = findViewById(R.id.change_type);
        removeObjects = findViewById(R.id.removeObjects);
        debugTextView = findViewById(R.id.debugTextView);

        tapToPlace = findViewById(R.id.tapToPlace);
        scanObject = findViewById(R.id.scanObject);

        //On-screen text prompts
        tapToPlace.setVisibility(View.INVISIBLE);
        scanObject.setVisibility(View.INVISIBLE);

        //ViewFlipper animation
        in = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        out = AnimationUtils.loadAnimation(this, R.anim.slide_out_up);
        viewFlipper.setInAnimation(in);
        viewFlipper.setOutAnimation(out);

        PROMPT_ID = viewFlipper.indexOfChild(findViewById(PROMPT_TEXT));
        OBJECT_PLACED_ID = viewFlipper.indexOfChild(findViewById(OBJECT_PLACED));
        FITS_ID = viewFlipper.indexOfChild(findViewById(FITS_TEXT));
        LARGE_ID = viewFlipper.indexOfChild(findViewById(LARGE_TEXT));


        viewFlipper.setDisplayedChild(PROMPT_ID);

        //Initialise Fragment and handler
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        colourChangeHandler = new ColourChangeHandler(this.getApplicationContext());


        Config.LightEstimationMode lightEstimationMode =
                Config.LightEstimationMode.AMBIENT_INTENSITY;

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                        return;
                    }

                    viewFlipper.setDisplayedChild(OBJECT_PLACED_ID);

                    planePose = plane.getCenterPose();

                    measure();
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

        //Buttons for choosing type of object placed
        radioGroup.setOnCheckedChangeListener(
                (group, checkedId) -> setModel(checkedId)
        );


        //Button for removing object placed
        removeObjects.setOnClickListener(
                w -> {
                    if (objectPlaced) {
                        objectPlaced = false;
                        try {
                            removeAnchorNode(anchorNode);
                        } catch (NullPointerException e) {
                            Log.w(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                        }
                        radioGroup.clearCheck();
                    }
                }
        );
    }


    private boolean scan = false;

    public void measure() {
        if (scan) {
            scan = false;
            return;
        }

        if (arFragment.getArSceneView().getArFrame() == null) {
            return;
        }
        // If ARCore is not tracking yet, then don't process anything.
        if (arFragment.getArSceneView().getSession() == null) {
            return;
        }

        scan = true;

        pcVis = new PointCloudVisualiser(getApplicationContext());
        arFragment.getArSceneView().getScene().addChild(pcVis);
        // If there is no frame then don't process anything.
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onSceneUpdate);
    }

    private void removeAnchorNode(AnchorNode nodeToremove) throws NullPointerException {
        //Remove an anchor node
        if (nodeToremove != null) {
            arFragment.getArSceneView().getScene().removeChild(nodeToremove);
            Objects.requireNonNull(nodeToremove.getAnchor()).detach();
            nodeToremove.setParent(null);
        }
    }

    private void createNode(ArFragment arFragment) throws NullPointerException {
        try {
            node = new TransformableNode(arFragment.getTransformationSystem());
        } catch (Exception e) {
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
        } catch (NullPointerException e) {
            Log.e("getSession", e.getMessage());
        }
    }

    private boolean hasTrackingPlane() {
        try {
            session.getAllTrackables(Plane.class);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void initAnchor(HitResult hitResult) {
        // Create the Anchor at hit result
        Anchor anchor = hitResult.createAnchor();
        anchorNode = new AnchorNode(anchor);
        //attach arFragment to hitResult via anchorNode
        anchorNode.setParent(arFragment.getArSceneView().getScene());
    }

    private void initSession() {
        try {
            session = new Session(this);
            config = new Config(session);
            //config.setLightEstimationMode(Config.LightEstimationMode.AMBIENT_INTENSITY);
            config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL);
        } catch (Exception e) {
            Log.e(TAG, "session: " + e.getMessage());
        }
    }

    private void onSceneUpdate(FrameTime frameTime) {
        Log.d(TAG, "onSceneUpdate");

        if (!scan) return;

        Frame frame = arFragment.getArSceneView().getArFrame();

        if (!objectPlaced) {
            try {
                frame.getUpdatedTrackables(Plane.class);
                Log.d("LIGHT", frame.getLightEstimate().getEnvironmentalHdrMainLightIntensity().toString());
            } catch (NullPointerException e) {
                viewFlipper.setDisplayedChild(PROMPT_ID);
                Log.e(TAG, e.getLocalizedMessage());
            }
        }

        PointCloud pointCloud = frame.acquirePointCloud();

        androidSensorPose = frame.getAndroidSensorPose();
        sizeHandler.updateAnchor(node.getWorldPosition());

        pcVis.update(pointCloud);
        sizeHandler.loadPointCloud(pointCloud);

        if (!readyToMeasure()) return;

        Log.d("SizeCheckHandler", "readyToMeasure");

        FitCodes fitCode = sizeHandler.checkIfFits();
        debugTextView.setText(sizeHandler.getBoxDim().toString());


        if (fitCode != null && fitCode != FitCodes.NONE) {
            colourChangeHandler.setObject(fitCode);

            if (fitCode == FitCodes.FIT) {
                viewFlipper.setDisplayedChild(FITS_ID);
            } else if (fitCode == FitCodes.LARGE) {
                viewFlipper.setDisplayedChild(LARGE_ID);
            }

        }

        pointCloud.release();
    }

    private boolean readyToMeasure() {
        final long TIME_3_SECONDS = 3000;

        cTime = System.currentTimeMillis();
        if (cTime - pTime > TIME_3_SECONDS) {
            pTime = cTime;
            Log.d(TAG, "(true)");
            return true;
        }
        return false;
    }

    private void setModel(int toggleId) {
        removeAllModels();
        if (toggleId == PERSONAL_ID) {
            currentModel = ObjectCodes.PERSONAL;
            colourChangeHandler.updateObject(currentModel);
            colourChangeHandler.setObject(FitCodes.NONE);
            sizeHandler.setObject(ObjectCodes.PERSONAL);
        } else if (toggleId == DUFFEL_ID) {
            currentModel = ObjectCodes.DUFFEL;
            colourChangeHandler.updateObject(currentModel);
            colourChangeHandler.setObject(FitCodes.NONE);
            sizeHandler.setObject(ObjectCodes.DUFFEL);
        } else {
            currentModel = ObjectCodes.CARRYON;
            colourChangeHandler.updateObject(currentModel);
            colourChangeHandler.setObject(FitCodes.NONE);
            sizeHandler.setObject(ObjectCodes.CARRYON);
        }
        Log.d("setModel", "exit");
    }

    private void removeAllModels() {
        try {
            node.setRenderable(null);
        } catch (Exception e) {
            Log.e("removeAllModels", e.getMessage());
        }
    }

    /*------------------------------------API Required Calls--------------------------------------*/
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

    @Override
    protected void onResume() {
        super.onResume();
        if (arSceneView == null) {
            return;
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Config.LightEstimationMode lightEstimationMode =
                        Config.LightEstimationMode.AMBIENT_INTENSITY;
                Session session = new Session(this);

                Config config = new Config(session);
                config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
                config.setLightEstimationMode(lightEstimationMode);
                session.configure(config);
                if (session == null) {
                    cameraPermissionRequested = true;
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            Log.e(TAG, "No Camera");
            finish();
        }

    }

    /*------------------------------------LifeCycle Methods--------------------------------------*/
    @Override
    public void onPause() {
        super.onPause();
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (arSceneView != null) {
            arSceneView.destroy();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            finish();
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            finish();
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class CustomVisualizer implements SelectionVisualizer {
        @Override
        public void applySelectionVisual(BaseTransformableNode node) {
        }

        @Override
        public void removeSelectionVisual(BaseTransformableNode node) {
        }
    }
}