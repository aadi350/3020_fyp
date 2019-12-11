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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Session;
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
    private Session session;

    private ArFragment arFragment;
    private ModelRenderable andyRenderable;
    private ModelRenderable duffelRenderable;
    private TransformableNode andy;
    private TransformableNode duffel;

    private ArSceneView arSceneView;
    private TextView textView;
    //local coordinates of placed object anchor
    private Vector3 anchorPosition;
    private boolean placed = false;
    private Switch toggle;
    private AnchorNode anchorNode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ARActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_layout);
        //connect switch
        toggle = findViewById(R.id.change_duffel);

        Log.i("ARACT", "OnCreate init");

        //for debugging
        textView = findViewById(R.id.ux_indicatorText);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arFragment.getTransformationSystem().setSelectionVisualizer(new CustomVisualizer());


        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.REQ_MSG);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

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

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (andyRenderable == null) {
                        return;
                    }
                    andyRenderable.setShadowCaster(false);
                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    placed = true;
                    Log.i("TAP","Tap registered");
                    anchorNode = new AnchorNode(anchor);
                    anchorPosition = anchorNode.getLocalPosition();

                    //attach arFragment to hitResult via anchorNode
                    anchorNode.setParent(arFragment.getArSceneView().getScene());
                    // Create the transformable andy and add it to the anchor.
                    andy = new TransformableNode(arFragment.getTransformationSystem());
                    duffel= new TransformableNode(arFragment.getTransformationSystem());

                    //choose model orientation based on switch
                    checkModel();
                    setModel();

                    arFragment.getArSceneView().getScene().addOnUpdateListener(this::onSceneUpdate);
                });
    }

    private void onSceneUpdate(FrameTime frameTime) {
        // Let the fragment update its state first.
//        Log.i("onSceneUpdate","onSceneUpdate");
//        arFragment.onUpdate(frameTime);
//
//        // If there is no frame then don't process anything.
//        if (arFragment.getArSceneView().getArFrame() == null) {
//            return;
//        }
//        // If ARCore is not tracking yet, then don't process anything.
//        if (arFragment.getArSceneView().getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
//            return;
//        }
//
//        Frame frame = arFragment.getArSceneView().getArFrame();
//        PointCloud pointCloud=frame.acquirePointCloud();
//        FloatBuffer points = pointCloud.getPoints();


        /*----------------------------------------------------------------------------------------*/
        //SizeCheck finds whether object is inside box
        //boolean fits = SizeCheck.objectFits(points, anchorPosition);
        //Debugging output PointCloud
//        String x = String.valueOf(points.get());
//        String y = String.valueOf(points.get());
//        String z = String.valueOf(points.get());

        //System.out.println(x + y + z);
//        textView.setText(String.valueOf(fits));
        /*----------------------------------------------------------------------------------------*/
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

    private void attachMain() {
        //attach main object
        andyRenderable.setShadowCaster(false);
        andy.getScaleController().setEnabled(false);
        andy.setParent(this.anchorNode);
        andy.setRenderable(andyRenderable);
        andy.select();
    }

    private void attachduffel() {
        //attach duffel
        duffelRenderable.setShadowCaster(false);
        duffel.getScaleController().setEnabled(false);
        duffel.setParent(this.anchorNode);
        duffel.setRenderable(duffelRenderable);
        duffel.select();
    }

    private void setModel() {
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // do something, the isChecked will be
            // true if the switch is in the On position

            andy.setParent(null);
            andy.setRenderable(null);
            duffel.setParent(null);
            duffel.setRenderable(null);

            if (isChecked)
            {
                //attach duffel
                duffel.getScaleController().setEnabled(false);
                duffel.setParent(anchorNode);
                duffel.setRenderable(duffelRenderable);
                duffel.select();
            }
            else
            {
                //attach main object
                andy.getScaleController().setEnabled(false);
                andy.setParent(anchorNode);
                andy.setRenderable(andyRenderable);
                andy.select();
            }
        });
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
