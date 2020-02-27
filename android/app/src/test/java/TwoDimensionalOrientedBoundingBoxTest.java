import com.helloarbridge4.Point3F.Point3F;
import com.helloarbridge4.SizeCheck.TwoDimensionalOrientedBoundingBox;

import org.junit.Assert;
import org.junit.Test;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class TwoDimensionalOrientedBoundingBoxTest {
    private static final int RECUR_COUNT = 15;
    private final int LOOP_COUNT = 25;
    private final int POINT_COUNT = 25;



    @Test
    public void getMinimumBoundingRectangleKnown() {

        Point3F[] expected = {
                new Point3F(280f,-40f,0f),
                new Point3F(200f,200f,0f),
                new Point3F(-400f,0f,0f),
                new Point3F(-320f,-240f,0f)
        };

        ArrayList<Point3F> pointList = new ArrayList<>();
        pointList.add(new Point3F(-300f,-150f,0f));
        pointList.add(new Point3F(200f,200f,0f));
        pointList.add(new Point3F(100f,-100f,0f));
        pointList.add(new Point3F(-400f,0f,0f));

        Point3F[] actual = TwoDimensionalOrientedBoundingBox.getMinimumBoundingRectangle(pointList);

        int i = 0;
        for (Point3F p:actual) {
            Assert.assertEquals(expected[i].x,p.x,0.2);
            Assert.assertEquals(expected[i].y,p.y,0.2);
            i++;
        }

        System.out.println(pointList);
    }

    @Test
    public void getAllBoundingRectanglesTest() {
        ArrayList<Point3F> pointList = generatePoints();
        ArrayList<Point3F[]> rectArrayList = TwoDimensionalOrientedBoundingBox.getAllBoundingRectangles(pointList);
        Assert.assertNotNull(rectArrayList);
    }

    @Test
    public void getMinimumBoundingRectangleTest() {
        ArrayList<Point3F> pointList = new ArrayList<>(generatePoints());
        for (int i = 1; i  <= RECUR_COUNT;  i++) {
            pointList.addAll(generatePoints());
            try {
                Point3F[] rectMin = TwoDimensionalOrientedBoundingBox.getOBB(pointList);

                ArrayList<Point3F> OBBList = new ArrayList<>();
                Collections.addAll(OBBList,rectMin);

                final String filePathPoints = "C:\\Users\\aaadi\\Google Drive\\Documents\\School Work\\Year 4\\ECNG 3020\\points" +  RECUR_COUNT +".txt";
                final String filePathOBB = "C:\\Users\\aaadi\\Google Drive\\Documents\\School Work\\Year 4\\ECNG 3020\\OBB" + RECUR_COUNT + ".txt";
                writeFile(pointList,filePathPoints);
                writeFile(OBBList,filePathOBB);

                double minAreaActual = TwoDimensionalOrientedBoundingBox.getArea(rectMin);

                //Assert.assertEquals(minArea, minAreaActual,0.15);

            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(e.getLocalizedMessage());
            } catch (IndexOutOfBoundsException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }

    }


    public ArrayList<Point3F> generatePoints() {
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

    private float getRandom() {

        final float MIN = 2f;
        final float MAX = 4f;

        Random rd = new Random();
        return MIN + rd.nextFloat()*(MAX - MIN);
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


}
