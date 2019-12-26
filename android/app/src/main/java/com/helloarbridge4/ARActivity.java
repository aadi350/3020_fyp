package com.helloarbridge4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.SelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;

import java.nio.FloatBuffer;


public class ARActivity extends AppCompatActivity {
    private static final String TAG = ARActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final int FRAME_COUNT_THRESH = 60;
    private static final int PERSONAL_ID = 2131230837;
    private static final int CARRYON_ID = 2131230835;
    private static final int DUFFEL_ID = 2131230836;

    private ArFragment arFragment;

    //Main suitcase
    private TransformableNode andy;
    private ModelRenderable andyRenderable;
    private ModelRenderable greenRenderable;
    private ModelRenderable redRenderable;
    private TransformableNode green;
    private TransformableNode red;


    //Personal Item
    private ModelRenderable personalItemRenderable;
    private TransformableNode personalItem;
    private ModelRenderable personalItemGreenRenderable;
    private TransformableNode personalItemGreen;
    private ModelRenderable personalItemRedRenderable;
    private TransformableNode personalItemRed;

    //duffel
    private TransformableNode duffel;
    private ModelRenderable duffelRenderable;
    private TransformableNode duffelGreen;
    private ModelRenderable duffelGreenRenderable;
    private TransformableNode duffelRed;
    private ModelRenderable duffelRedRenderable;

    private ArSceneView arSceneView;
    private TextView textView;

    private int frameCount = 0;

    //local coordinates of placed object anchor
    private Vector3 anchorPosition;
    private boolean placed = false;
    private Switch toggle;
    private RadioGroup radioGroup;
    private RadioButton radioPersonal;
    private RadioButton radioDuffel;
    private RadioButton radioCarryon;
    private AnchorNode anchorNode;
    private int changeVar = 0;

    private sizeCheck sizeCheckObj;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ARActivity","onCreate");
        super.onCreate(savedInstanceState);

        try{
            session = new Session(this);
        } catch (Exception e) {
            Log.e("session", e.getMessage());
        }

        sizeCheckObj = new sizeCheck();
        session.getConfig().setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL);

        //connect views
        setContentView(R.layout.ar_layout);
        toggle = findViewById(R.id.change_duffel);
        textView = findViewById(R.id.ux_indicatorText);
        radioGroup = findViewById(R.id.change_type);
        //connect switch
        toggle = findViewById(R.id.change_duffel);
        Log.i("ARACT", "OnCreate init");

        try {
            arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        } catch (NullPointerException n){
            Log.wtf("arFragment", n.getMessage());
        }

        loadRenderables();

        radioPersonal = findViewById(R.id.radio_personal);
        radioDuffel = findViewById(R.id.radio_duffel);
        radioCarryon = findViewById(R.id.radio_carryon);


        //for debugging
        textView = findViewById(R.id.ux_indicatorText);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arFragment.getTransformationSystem().setSelectionVisualizer(new CustomVisualizer());


        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.REQ_MSG);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }


        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (isRenderableNull() || placed) {
                        Log.d("renderableNull: ", String.valueOf(isRenderableNull()));
                        return;
                    }
                    Log.d("renderableNull: ", String.valueOf(isRenderableNull()));

                    //horizontal plane detection
                    session.getConfig().setPlaneFindingMode(Config.PlaneFindingMode.DISABLED);

                    // Create the Anchor at hit result
                    Anchor anchor = hitResult.createAnchor();
                    placed = true;

                    anchorNode = new AnchorNode(anchor);
                    anchorPosition = anchorNode.getLocalPosition();

                    //attach arFragment to hitResult via anchorNode
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    try {
                        Config config = new Config(arSceneView.getSession());
                        config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL);
                    } catch (Exception e){
                        Log.e("getSession",e.getMessage());
                    }

                    // Create the transformable andy and add it to the anchor.
                    andy = new TransformableNode(arFragment.getTransformationSystem());
                    red = new TransformableNode(arFragment.getTransformationSystem());
                    green = new TransformableNode(arFragment.getTransformationSystem());
                    personalItem = new TransformableNode(arFragment.getTransformationSystem());
                    personalItemGreen = new TransformableNode(arFragment.getTransformationSystem());
                    personalItemRed = new TransformableNode(arFragment.getTransformationSystem());
                    duffel= new TransformableNode(arFragment.getTransformationSystem());
                    duffelRed = new TransformableNode(arFragment.getTransformationSystem());
                    duffelGreen = new TransformableNode(arFragment.getTransformationSystem());

                    //choose model orientation based on switch
                    setModel();
                    arFragment.getArSceneView().getScene().addOnUpdateListener(this::onSceneUpdate);
                });

        radioGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    Log.d("onCheckedChangeListener", "radioGroup entered");
                    removeAllModels();
                    Log.d("onCheckedChangeListener", "models removed");

                    switch (checkedId){
                        case CARRYON_ID:
                            //carryon
                            Log.i("onCheckedChangeListener", "Main Attached");
                            attachMain();
                            break;
                        case DUFFEL_ID:
                            //duffel
                            Log.i("onCheckedChangeListener", "Duffel Attached");
                            attachduffel();
                            break;
                        case PERSONAL_ID:
                            //personal item
                            Log.i("onCheckedChangeListener", "Personal Attached");
                            attachPersonalMain();
                            break;
                        default:
                            //carryon
                            Log.i("onCheckedChangeListener", "Default");
                            break;
                    }
                }

        );
    }

    private void onSceneUpdate(FrameTime frameTime) {
        frameCount++;
        session.getConfig().setPlaneFindingMode(Config.PlaneFindingMode.DISABLED);

        // Let the fragment update its state first.
        Log.i("radioGroup",String.valueOf(radioGroup.getCheckedRadioButtonId()));

        arFragment.onUpdate(frameTime);
        anchorPosition = anchorNode.getLocalPosition();
        Log.i("anchorPosition", String.valueOf(anchorPosition));

        // If there is no frame then don't process anything.
        if (arFragment.getArSceneView().getArFrame() == null) {
            return;
        }
        // If ARCore is not tracking yet, then don't process anything.
        if (arFragment.getArSceneView().getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
            return;
        }

        //acquire feature points
        Frame frame = arFragment.getArSceneView().getArFrame();
        PointCloud pointCloud = frame.acquirePointCloud();
        FloatBuffer points = pointCloud.getPoints();
        Log.i("pointCloud",String.valueOf(points));
        Log.i("onSceneUpdate", "prior to sizeCheckObject");

        //set as carryon for testing
        sizeCheckObj.setObjectType(
                radioPersonal.isChecked(),
                radioDuffel.isChecked(),
                radioCarryon.isChecked()
        );
        Log.i("radioPersonal", Boolean.toString(radioPersonal.isChecked()));
        Log.i("radioDuffel", Boolean.toString(radioDuffel.isChecked()));
        Log.i("radioCarryon", Boolean.toString(radioCarryon.isChecked()));

        sizeCheckObj.setObjectSizeLimits();
        sizeCheckObj.setObjectAnchor(anchorPosition);

        Log.d("setObjectAnchor", String.valueOf(anchorPosition));


        sizeCheckObj.loadPointsFromFloatBuffer(points);
        Log.i("onSceneUpdate", "loadPointsFromFloatBuffer");

        sizeCheckObj.comparePointsToLimits();
        int fits = sizeCheckObj.ifObjectFits();

        pointCloud.release();

        if (frameCount == FRAME_COUNT_THRESH) {
            frameCount = 0;

            //set appropriate colour renderable
            if (returnTrueIfChanged(fits))
            {
                switch (fits) {
                    case 0:
                        //No Object Detected
                        setModel();
                        Log.d("OBJ_DETECT","Object Not Detected, Fits: " + fits);
                        break;
                    case 1:
                        //Oversized object detected
                        setRedModel();
                        Log.d("OBJ_DETECT","Large Object Detected, Fits: " + fits);
                        break;
                    case 2:
                        //Object detected within bounds
                        setGreenModel();
                        Log.d("OBJ_DETECT","Object Detected, Fits: " + fits);
                        break;
                    default:
                        removeAllModels();
                        break;
                }
            }
            textView.setText(fits);
        }
    }

    private boolean isRenderableNull(){
        return (
                andyRenderable == null ||
                        duffelRenderable == null ||
                        greenRenderable == null ||
                        redRenderable == null ||
                        personalItemRenderable == null ||
                        personalItemGreenRenderable == null ||
                        personalItemRedRenderable == null
        );
    }

    private void loadRenderables(){
        //Main suitcase object
        ModelRenderable.builder()
                .setSource(this, Uri.parse("suitcase.sfb"))
                .build()
                .thenAccept(renderable -> andyRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });


        //main Duffel Object
        ModelRenderable.builder()
                .setSource(this, Uri.parse("duffel.sfb"))
                .build()
                .thenAccept(renderable -> duffelRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
        //Green Duffel Object
        ModelRenderable.builder()
                .setSource(this, Uri.parse("duffel_green.sfb"))
                .build()
                .thenAccept(renderable -> duffelGreenRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
        //Red Duffel Object
        ModelRenderable.builder()
                .setSource(this, Uri.parse("duffel_red.sfb"))
                .build()
                .thenAccept(renderable -> duffelRedRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        //green suitcase object
        ModelRenderable.builder()
                .setSource(this, Uri.parse("suitcase_green.sfb"))
                .build()
                .thenAccept(renderable -> greenRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        //red suitcase object
        ModelRenderable.builder()
                .setSource(this, Uri.parse("suitcase_red.sfb"))
                .build()
                .thenAccept(renderable -> redRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        //personal item object
        ModelRenderable.builder()
                .setSource(this, Uri.parse("personalItem.sfb"))
                .build()
                .thenAccept(renderable -> personalItemRenderable= renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        //personal item red object
        ModelRenderable.builder()
                .setSource(this, Uri.parse("personalItem_red.sfb"))
                .build()
                .thenAccept(renderable -> personalItemRedRenderable= renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        //personal item green object
        ModelRenderable.builder()
                .setSource(this, Uri.parse("personalItem_green.sfb"))
                .build()
                .thenAccept(renderable -> personalItemGreenRenderable= renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
    }

    private void attachMain() {
        //attach main object
        if (anchorNode != null){
            andyRenderable.setShadowCaster(false);
            andy.getScaleController().setEnabled(false);
            andy.setParent(this.anchorNode);
            andy.setRenderable(andyRenderable);
            andy.select();
        }
    }

    private void attachduffel() {
        if (anchorNode != null) {
            //attach duffel
            duffelRenderable.setShadowCaster(false);
            duffel.getScaleController().setEnabled(false);
            duffel.setParent(this.anchorNode);
            duffel.setRenderable(duffelRenderable);
            duffel.select();
        }
    }

    private void attachPersonalMain() {
        if (anchorNode != null) {
            personalItemRenderable.setShadowCaster(false);
            personalItem.getScaleController().setEnabled(false);
            personalItem.setParent(this.anchorNode);
            personalItem.setRenderable(personalItemRenderable);
            personalItem.select();
        }
    }

    private void attachPersonalRed() {
        personalItemRedRenderable.setShadowCaster(false);
        personalItemRed.getScaleController().setEnabled(false);
        personalItemRed.setParent(this.anchorNode);
        personalItemRed.setRenderable(personalItemRedRenderable);
        personalItemRed.select();
    }

    private void attachPersonalGreen() {
        personalItemGreenRenderable.setShadowCaster(false);
        personalItemGreen.getScaleController().setEnabled(false);
        personalItemGreen.setParent(this.anchorNode);
        personalItemGreen.setRenderable(personalItemGreenRenderable);
        personalItemGreen.select();
    }

    private void attachDuffelGreen() {
        duffelGreenRenderable.setShadowCaster(false);
        duffelGreen.getScaleController().setEnabled(false);
        duffelGreen.setParent(this.anchorNode);
        duffelGreen.setRenderable(personalItemGreenRenderable);
        duffelGreen.select();
    }

    private void attachDuffelRed() {
        duffelRedRenderable.setShadowCaster(false);
        duffelRed.getScaleController().setEnabled(false);
        duffelRed.setParent(this.anchorNode);
        duffelRed.setRenderable(personalItemGreenRenderable);
        duffelRed.select();
    }

    private void attachGreenMain(){
        //attach green suitcase
        greenRenderable.setShadowCaster(false);
        green.getScaleController().setEnabled(false);
        green.setParent(this.anchorNode);
        green.setRenderable(greenRenderable);
        green.select();
    }

    private void attachRedMain(){
        //attach red suitcase
        redRenderable.setShadowCaster(false);
        red.getScaleController().setEnabled(false);
        red.setParent(this.anchorNode);
        red.setRenderable(redRenderable);
        red.select();
    }

    private void checkModel() {
        if (toggle.isChecked())
        //attach duffel
        {
            attachduffel();
        }
        else
        {
            attachMain();
        }
    }

    private void setModel() {
        Log.d("setModel", "entered");
        removeAllModels();
        int toggleId = radioGroup.getCheckedRadioButtonId();
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

    private void setRedModel() {
        // do something, the isChecked will be
        // true if the switch is in the On position
        int toggleId = radioGroup.getCheckedRadioButtonId();
        removeAllModels();
        switch (toggleId){
            case PERSONAL_ID:
                attachPersonalRed();
                break;
            case DUFFEL_ID:
                attachDuffelRed();
                break;
            case CARRYON_ID:
                attachRedMain();
                break;
            default:
                attachRedMain();
                break;
        }
        Log.i("setRedModel", String.valueOf(toggleId));
    }

    private void setGreenModel() {

        // do something, the isChecked will be
        // true if the switch is in the On position
        int toggleId = radioGroup.getCheckedRadioButtonId();
        removeAllModels();
        switch (toggleId){
            case PERSONAL_ID:
                attachPersonalGreen();
                break;
            case DUFFEL_ID:
                attachDuffelGreen();
                break;
            case CARRYON_ID:
                attachRedMain();
                break;
            default:
                attachGreenMain();
                break;
        }
        Log.i("setGreenModel", String.valueOf(toggleId));
    }

    private void removeAllModels(){
        try{
            andy.setRenderable(null);
            red.setRenderable(null);
            green.setRenderable(null);
            personalItem.setRenderable(null);
            personalItemGreen.setRenderable(null);
            personalItemRed.setRenderable(null);
            duffel.setRenderable(null);
            duffelGreen.setRenderable(null);
            duffelRed.setRenderable(null);
        } catch (Exception e){
            Log.e("removeAllModels", e.getMessage());
        }
    }

    private boolean returnTrueIfChanged(int i) {

        if (this.changeVar == i) {
            return false;
        }
        this.changeVar = i;
        return true;
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
