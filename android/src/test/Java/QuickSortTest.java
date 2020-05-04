import com.reactlibrary.Point3F.Point3F;
import com.reactlibrary.SizeCheck.QuickSort;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import Utility.Utility;

public class QuickSortTest {

    private ArrayList<Point3F> pointList;

    @Before
    public void getPoints() {
        pointList = Utility.generatePoints();
    }

    @Test
    public void sortByHeightNominal() {
        int NUM_POINTS_LIMIT = 10000;
            QuickSort q = new QuickSort();
            ArrayList<Point3F> sortedList = q.sortByHeight(pointList);

            for (int i = 0; i < sortedList.size() - 1; i++) {
                Assert.assertTrue(sortedList.get(i).y <= sortedList.get(i + 1).y);
            }
    }

    @Test
    public void sortByHeightNullInput() {
        QuickSort q = new QuickSort();
        ArrayList<Point3F> sortedList = q.sortByHeight(null);

        Assert.assertNotNull(sortedList);
        Assert.assertTrue(sortedList.isEmpty());
    }

    @Test
    public void getHighestZ() {
        float highZ = Float.NEGATIVE_INFINITY;
        for (Point3F p : pointList) {
            if (highZ < p.y) highZ = p.y;
        }

        QuickSort q = new QuickSort();
        Assert.assertEquals(highZ, q.getHighestPoint(pointList), 0.05f);
    }
}
