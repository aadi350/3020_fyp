import com.helloarbridge4.Point3F.Point3F;
import com.helloarbridge4.SizeCheck.MinBoundingBox.QuickHull;
import com.helloarbridge4.SizeCheck.MinBoundingBox.TwoDimensionalOrientedBoundingBox;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class TwoDimensionalOrientedBoundingBoxTest {
    private static final int RECUR_COUNT = 5;
    private final int LOOP_COUNT = 25;
    private int POINT_COUNT;


    @Test
    public void getMinimumBoundingRectangleTest() {
        ArrayList<Point3F> pointList = new ArrayList<>(generatePoints());
        POINT_COUNT = 5;
        pointList.addAll(generatePoints());

        for (int i = 1; i  <= RECUR_COUNT;  i++) {


            try {
                System.out.println(i);
                final String filePathPoints = "C:\\Users\\aaadi\\Google Drive\\Documents\\School Work\\Year 4\\ECNG 3020\\points" +  i +".txt";
                writeFile(pointList,filePathPoints);
                //ArrayList<Point3F> hull = QuickHull.getConvexHull(pointList);
                Point3F[] rectMin = TwoDimensionalOrientedBoundingBox.getOBB(pointList);
                for (Point3F p : pointList) {
                    System.out.println(p.toString());
                }
                ArrayList<Point3F> OBBList = new ArrayList<>();
                Collections.addAll(OBBList,rectMin);

                final String filePathOBB = "C:\\Users\\aaadi\\Google Drive\\Documents\\School Work\\Year 4\\ECNG 3020\\OBB" + i + ".txt";
                writeFile(OBBList,filePathOBB);

                //Assert.assertEquals(minArea, minAreaActual,0.15);

            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("OUT OF BOUNDS" + e.getLocalizedMessage());
            } catch (IndexOutOfBoundsException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }

    }


    public ArrayList<Point3F> generatePoints() {
        ArrayList<Point3F> pointList = new ArrayList<>();
        for (int i = 0; i <= POINT_COUNT; i++) {
            pointList.add(new Point3F(
                    getRandom(),
                    getRandom(),
                    getRandom()
            ));
        }
        return pointList;
    }

    private float getRandom() {

        final float MIN = -0.5f;
        final float MAX = 0.5f;

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
