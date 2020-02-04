import com.helloarbridge4.Point3F.Point3F;
import com.helloarbridge4.SizeCheck.QuickSort;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

public class QuickSortTest {


    private final float MIN = -10f, MAX = 10f;

    @Test
    public void sortByHeightNominal() {
        int NUM_POINTS_LIMIT = 10000;
        for (int NUM_POINTS = 0; NUM_POINTS < NUM_POINTS_LIMIT; NUM_POINTS+= 10) {

          Random rd = new Random();
          ArrayList<Point3F> pointList = new ArrayList<>();
          for (int i = 0; i < NUM_POINTS; i++) {
              pointList.add(new Point3F(
                              MIN + rd.nextFloat() * (MAX - MIN),
                              MIN + rd.nextFloat() * (MAX - MIN),
                              MIN + rd.nextFloat() * (MAX - MIN)
                      )

              );
          }

          QuickSort q = new QuickSort();
          ArrayList<Point3F> sortedList = q.sortByHeight(pointList);

          for (int i = 0; i < NUM_POINTS - 1; i++) {
              Assert.assertTrue(sortedList.get(i).z <= sortedList.get(i + 1).z);
          }
      }
    }

    @Test
    public void sortByHeightNullInput() {
        Random rd = new Random();
        ArrayList<Point3F> pointList = null;

        QuickSort q = new QuickSort();
        ArrayList<Point3F> sortedList = q.sortByHeight(pointList);

            Assert.assertNotNull(sortedList);
            Assert.assertTrue(sortedList.isEmpty());
    }

    @Test
    public void getHighestZ() {
        Random rd = new Random();
        int NUM_POINTS = 10000;
        float highZ = Float.NEGATIVE_INFINITY;
        ArrayList<Point3F> pointList = new ArrayList<>();
        for (int i = 0; i < NUM_POINTS; i++) {
            Point3F pointTemp = new Point3F(
                    MIN + rd.nextFloat() * (MAX - MIN),
                    MIN + rd.nextFloat() * (MAX - MIN),
                    MIN + rd.nextFloat() * (MAX - MIN)
            );
            pointList.add(pointTemp);
            if (highZ < pointTemp.z) highZ = pointTemp.z;
        }

        QuickSort q = new QuickSort();

        Assert.assertEquals(highZ, q.getHighestZ(pointList), 0.05f);
    }
}
