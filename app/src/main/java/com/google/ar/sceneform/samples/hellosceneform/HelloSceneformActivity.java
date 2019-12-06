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

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.view.KeyEventDispatcher;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
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
  private ModelRenderable andyRenderable;

    @Override
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_ux);
      arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
//        arSceneView = (ArSceneView) findViewById(R.id.ux_scnview);
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


      arFragment.setOnTapArPlaneListener(
              (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                  if (andyRenderable == null) {
                      return;
                  }
                  andyRenderable.setShadowCaster(false);
                  // Create the Anchor at hit result
                  Anchor anchor = hitResult.createAnchor();
                  Log.i("TAP","Tap registered");
                  AnchorNode anchorNode = new AnchorNode(anchor);
                  anchorNode.setParent(arFragment.getArSceneView().getScene());

                  // Create the transformable andy and add it to the anchor.
                  TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
                  andy.getScaleController().setEnabled(false);
                  andy.setParent(anchorNode);
                  andy.setRenderable(andyRenderable);
                  andy.select();
                  arFragment.getArSceneView().getScene().addOnUpdateListener(this::onSceneUpdate);
              });

        
  }

    private void onSceneUpdate(FrameTime frameTime) {
        // Let the fragment update its state first.
        Log.i("onSceneUpdate","onSceneUpdate");
        arFragment.onUpdate(frameTime);

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

        //Debugging output PointCloud
        String x = String.valueOf(points.get());
        String y = String.valueOf(points.get());
        String z = String.valueOf(points.get());
        System.out.println(x + y + z);


    }

}
