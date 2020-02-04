import com.helloarbridge4.Point3F.Point3F;
import com.helloarbridge4.SizeCheck.ConvexHull;

import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Random;

public class ConvexHullTest {
    float MAX = 10f;
    float MIN = -10f;
    @Test
    public void quickHull() {

        //zero-zero elimination
        ArrayList<Point3F> pointList = new ArrayList<>();
        pointList.add(new Point3F(-1f,-1f,0f));
        pointList.add(new Point3F(-1f,1f,0f));
        pointList.add(new Point3F(1f,1f,0f));
        pointList.add(new Point3F(1f,-1f,0f));
        pointList.add(new Point3F(0f,0f,0f));


        ConvexHull q = new ConvexHull();
        ArrayList<Point3F> p = q.quickHull(pointList);
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
        ConvexHull q = new ConvexHull();
        ArrayList<Point3F> actual = q.quickHull(pointList);
        Assert.assertEquals(pointList,actual);
    }

    @Test
    public void quickHul() {
        ArrayList<Point3F> pointList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            pointList.add(new Point3F(getRandom(),getRandom(),getRandom()));
        }

        ConvexHull q = new ConvexHull();
        ArrayList<Point3F> actual = q.quickHull(pointList);
        Assert.assertNotNull(actual);
    }

    private float getRandom() {
        Random rd = new Random();
        return rd.nextFloat() - 0.5f;
    }
}
