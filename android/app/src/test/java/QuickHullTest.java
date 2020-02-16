import com.helloarbridge4.Point3F.Point3F;
import com.helloarbridge4.SizeCheck.QuickHull;

import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Random;

public class QuickHullTest {
    float MAX = 10f;
    float MIN = -10f;
    final int LOOP_COUNT = 10000;

    @Test
    public void repeatedQuickHull() {
        int RECUR_COUNT = 1000;
        ArrayList<Point3F> points = getPoints();
        ArrayList<Point3F> hull = new ArrayList<>();

        for (int i = 0; i < RECUR_COUNT; i++) {
            points.addAll(getPoints());
            hull = QuickHull.getConvexHull(points);
        }

        Assert.assertNotNull(hull);
    }

    @Test
    public void quickHull() {

        //zero-zero elimination
        ArrayList<Point3F> pointList = new ArrayList<>();
        pointList.add(new Point3F(-1f,-1f,0f));
        pointList.add(new Point3F(-1f,1f,0f));
        pointList.add(new Point3F(1f,1f,0f));
        pointList.add(new Point3F(1f,-1f,0f));
        pointList.add(new Point3F(0f,0f,0f));


        ArrayList<Point3F> p = QuickHull.getConvexHull(pointList);
        ArrayList<Point3F> expected = new ArrayList<>();
        expected.add(new Point3F(1f,-1f,0f));
        expected.add(new Point3F(-1f,-1f,0f));
        expected.add(new Point3F(-1f,1f,0f));
        expected.add(new Point3F(1f,1f,0f));

        Assert.assertEquals(expected,p);

    }

    @Test
    public void quickHullLessThanThreePoints() {
        ArrayList<Point3F> pointList = new ArrayList<>();
        pointList.clear();
        pointList.add(new Point3F(getRandom(),getRandom(),getRandom()));
        pointList.add(new Point3F(getRandom(),getRandom(),getRandom()));
        ArrayList<Point3F> actual = QuickHull.getConvexHull(pointList);
        Assert.assertEquals(pointList,actual);
    }

    @Test
    public void quickHul() {
        ArrayList<Point3F> pointList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            pointList.add(new Point3F(getRandom(),getRandom(),getRandom()));
        }

        ArrayList<Point3F> actual = QuickHull.getConvexHull(pointList);
        Assert.assertNotNull(actual);
    }

    private ArrayList<Point3F> getPoints() {
        ArrayList<Point3F> pointList = new ArrayList<>();
        for (int i = 0; i < LOOP_COUNT; i++) {
            pointList.add(new Point3F(
                    getRandom(),
                    getRandom(),
                    getRandom()
            ));
        }
        return pointList;
     }


    private float getRandom() {
        Random rd = new Random();
        return rd.nextFloat() - 0.5f;
    }
}
