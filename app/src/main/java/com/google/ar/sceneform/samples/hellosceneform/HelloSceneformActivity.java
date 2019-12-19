/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.samples.hellosceneform;

import android.support.v7.widget.SwitchCompat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.samples.renderers.PointCloudRenderer;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.Console;
import java.nio.FloatBuffer;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class HelloSceneformActivity extends AppCompatActivity {
  private static final String TAG = HelloSceneformActivity.class.getSimpleName();
  private static final double MIN_OPENGL_VERSION = 3.0;
  private ArFragment arFragment;
  private ArSceneView arSceneView;
  private TextView textView;

  //Sceneform Models
  private ModelRenderable andyRenderable;
  private ModelRenderable duffelRenderable;
  private TransformableNode andy;
  private TransformableNode duffel;
  private ModelRenderable greenRenderable;
  private ModelRenderable redRenderable;
  private TransformableNode green;
  private TransformableNode red;



  //local coordinates of placed object anchor
  private Vector3 anchorPosition;
  private boolean placed = false;
  private sizeCheck SizeCheck;
  private SwitchCompat toggle;
  private AnchorNode anchorNode;

  private sizeCheck sizeCheckObj = new sizeCheck();


    @Override
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      //instantiate sizeCheck object





      //connect views
      setContentView(R.layout.activity_ux);
      //connect switch
      toggle = findViewById(R.id.change_duffel);

      //for debugging
      textView = findViewById(R.id.ux_indicatorText);

      arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

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

      arFragment.setOnTapArPlaneListener(
              (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                  if (andyRenderable == null || placed) {
                      return;
                  }

                  // Create the Anchor at hit result
                  Anchor anchor = hitResult.createAnchor();
                  placed = true;
                  Log.i("TAP","Tap registered");
                  anchorNode = new AnchorNode(anchor);
                  anchorPosition = anchorNode.getLocalPosition();

                  /*-------------------------------------------------------------------------------*/
                  Log.d("OBJLocationDebug",String.valueOf(anchorPosition.x) +
                                                    String.valueOf(anchorPosition.y) +
                                                    String.valueOf(anchorPosition.z));
                  /*-------------------------------------------------------------------------------*/

                  //attach arFragment to hitResult via anchorNode
                  anchorNode.setParent(arFragment.getArSceneView().getScene());

                  // Create the transformable andy and add it to the anchor.
                  andy = new TransformableNode(arFragment.getTransformationSystem());
                  duffel= new TransformableNode(arFragment.getTransformationSystem());
                  red = new TransformableNode(arFragment.getTransformationSystem());
                  green = new TransformableNode(arFragment.getTransformationSystem());

                  //choose model orientation based on switch
                  checkModel();
                  setModel();

                  arFragment.getArSceneView().getScene().addOnUpdateListener(this::onSceneUpdate);
              });

        
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

    private void attachGreeen(){
        //attach green suitcase
        greenRenderable.setShadowCaster(false);
        green.getScaleController().setEnabled(false);
        green.setParent(this.anchorNode);
        green.setRenderable(greenRenderable);
        green.select();
    }

    private void attachRed(){
        //attach red suitcase
        redRenderable.setShadowCaster(false);
        red.getScaleController().setEnabled(false);
        red.setParent(this.anchorNode);
        red.setRenderable(greenRenderable);
        red.select();
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

    private void onSceneUpdate(FrameTime frameTime) {
        // Let the fragment update its state first.
        Log.i("onSceneUpdate","onSceneUpdate");
        arFragment.onUpdate(frameTime);


        anchorPosition = anchorNode.getLocalPosition();
        /*-------------------------------------------------------------------------------*/
        //Debugging position of placed object
        String[] pos = new String[3];
        pos[0] = String.valueOf(anchorPosition.x);
        pos[1] = String.valueOf(anchorPosition.y);
        pos[2] = String.valueOf(anchorPosition.z);
        Log.d("OBJLocationDebug",pos[0] + pos[1] + pos[2]);
        /*-------------------------------------------------------------------------------*/
        // If there is no frame then don't process anything.
        if (arFragment.getArSceneView().getArFrame() == null) {
            return;
        }
        // If ARCore is not tracking yet, then don't process anything.
        if (arFragment.getArSceneView().getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
            return;
        }

        Frame frame = arFragment.getArSceneView().getArFrame();
        PointCloud pointCloud=frame.acquirePointCloud();
        FloatBuffer points = pointCloud.getPoints();


        //set as carryon for testing
        Log.i("onSceneUpdate","prior to sizeCheckObject");
        sizeCheckObj.setObjectType(true);
        sizeCheckObj.setObjectAnchor(anchorPosition);
        Log.i("onSceneUpdate", "loadPointsFromFloatBuffer");
        sizeCheckObj.loadPointsFromFloatBuffer(points);
        sizeCheckObj.comparePointsToLimits();
        boolean fits = sizeCheckObj.ifObjectFits();

        /*----------------------------------------------------------------------------------------*/
        //SizeCheck finds whether object is inside box
        //Debugging output PointCloud
//        String x = String.valueOf(points.get());
//        String y = String.valueOf(points.get());
//        String z = String.valueOf(points.get());
        String debug_text = (fits) ? "" : "No Object Detected";
        //System.out.println(x + y + z);
        textView.setText(String.valueOf(debug_text));
        Log.d("OBJ_DETECT",debug_text);
        /*----------------------------------------------------------------------------------------*/
    }

}
