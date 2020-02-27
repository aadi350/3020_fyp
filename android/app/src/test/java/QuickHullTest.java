import com.helloarbridge4.Point3F.Point3F;
import com.helloarbridge4.SizeCheck.QuickHull;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class QuickHullTest {
    float MAX = 0.1f;
    float MIN = -0.1f;
    final int POINT_COUNT = 100;

    @Test
    public void repeatedQuickHull() {
        int RECUR_COUNT = 10;
        String filePathPoints = "C:\\Users\\aaadi\\Google Drive\\Documents\\School Work\\Year 4\\ECNG 3020\\points";
        String filePathHull = "C:\\Users\\aaadi\\Google Drive\\Documents\\School Work\\Year 4\\ECNG 3020\\output";
        ArrayList<Point3F> points = getPoints();
        ArrayList<Point3F> hull = new ArrayList<>();

        for (int i = 1; i <= RECUR_COUNT; i++) {

            try {
                points.addAll(getPoints());
                hull = QuickHull.getConvexHull(points);
                writeFile(points,filePathPoints + String.valueOf(i) + ".txt");
                writeFile(hull,filePathHull + String.valueOf(i) + ".txt");
            } catch (NullPointerException e) {
                System.out.println(e.getLocalizedMessage());
            }
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

    public static void writeFile(ArrayList<Point3F> pointList, String filePath) {
        try {

            BufferedWriter out = new BufferedWriter(new FileWriter(filePath, false));

            for (Point3F point : pointList) {
                out.write(point.toString());
            }
            out.close();
        }catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
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
        for (int i = 0; i < POINT_COUNT; i++) {
            pointList.add(new Point3F(
                    getRandom(),
                    getRandom(),
                    getRandom()
            ));
        }
        return pointList;
     }

     @Test
     public void printPoints() {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for (int i = 0; i < POINT_COUNT; i++){
            float f = getRandom();
            if (f < min) min = f;
            if (f > max) max = f;
            System.out.println(f);
        }

        Assert.assertFalse(min <= MIN);
        Assert.assertTrue(max <= MAX);
     }

    private float getRandom() {
        Random rd = new Random();
        return MIN + rd.nextFloat()*(MAX - MIN);
    }
}
