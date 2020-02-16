import android.graphics.PointF;
import android.util.Log;

import com.google.ar.sceneform.math.Vector3;
import com.helloarbridge4.Point3F.Point3F;
import com.helloarbridge4.Point3F.PointFilter;

import org.junit.Assert;
import org.junit.Test;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PointFilterTest {

    final int LOOP_COUNT = 1000000;
    final float Z_THRESH = 0.1f;
    final Vector3 REGION_LIMITS = new Vector3(0.8f,0.8f,0.8f);
    final float POINT_CONF_LIM = 0.7f;

    @Test
    public void filterByConfidence() {
        FloatBuffer floatBuffer = FloatBuffer.allocate(LOOP_COUNT);

        for (int i = 0; i < LOOP_COUNT; i++) {
            floatBuffer.put((float) Math.random());
        }

        List<Point3F> pointList = PointFilter.filterByConfidence(floatBuffer);

        for (Point3F point : pointList) {
            Assert.assertTrue(point.c >= POINT_CONF_LIM);
        }

        floatBuffer.clear();
        Assert.assertNotNull(PointFilter.filterByConfidence(floatBuffer));
    }

    @Test
    public void filterByRegion() {

        FloatBuffer floatBuffer = FloatBuffer.allocate(LOOP_COUNT);
        ArrayList<Point3F> pointList = new ArrayList<>();
        Vector3 refPoint = new Vector3(0f,0f,0f);
        Float MAX = 15f, MIN = -15f;

        for (int i = 0; i < LOOP_COUNT; i++) {
            pointList.add(new Point3F(
                    generateRandomInRange(MAX,MIN),
                    generateRandomInRange(MAX,MIN),
                    generateRandomInRange(MAX,MIN)
                    ));
        }


        System.out.println(pointList.size());
        ArrayList<Point3F> closeList = PointFilter.filterByRegion(pointList, refPoint);
        System.out.println(closeList.size());
        for (Point3F point : closeList) {
            System.out.println(point.toString());
            Assert.assertTrue(
                    Math.abs(point.x - refPoint.x) <= REGION_LIMITS.x &&
                            Math.abs(point.y - refPoint.y) <= REGION_LIMITS.y &&
                            Math.abs(point.z - refPoint.z) <= REGION_LIMITS.z
            );

        }

        floatBuffer.clear();
        Assert.assertNotNull(PointFilter.filterByConfidence(floatBuffer));

    }

    @Test
    public void filterByGround() {
        Vector3 ground = new Vector3(Vector3.zero());
        ArrayList<Point3F> pointList = new ArrayList<>();
        Random rd = new Random();
        for (int i = 0; i < LOOP_COUNT; i++) {
            pointList.add(
                    new Point3F(
                            rd.nextFloat(),
                            rd.nextFloat(),
                            rd.nextFloat()
                    )
            );
        }

        ArrayList<Point3F> filteredList = PointFilter.filterGround(pointList, ground);

        for (Point3F point : filteredList) {
            Assert.assertTrue(
                    point.z > Z_THRESH
            );
        }

        filteredList.clear();
        Assert.assertNotNull(PointFilter.filterGround(filteredList, ground));

        filteredList = null;
        Assert.assertNotNull(PointFilter.filterGround(filteredList, ground));
    }

    float generateRandomInRange(Float max, Float min) {
        Random rd = new Random();

        return (min + rd.nextFloat()*(max - min));
    }


}
