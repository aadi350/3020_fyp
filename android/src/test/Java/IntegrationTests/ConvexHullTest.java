package IntegrationTests;

import com.google.ar.sceneform.math.Vector3;
import com.reactlibrary.Point3F.Point3F;
import com.reactlibrary.Point3F.PointFilter;
import com.reactlibrary.SizeCheck.MinBoundingBox.QuickHull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import Utility.Utility;

public class ConvexHullTest {
    private final String mainDir = "D:\\3020\\Integration\\hull\\";
    private static final int RECUR_COUNT = 10;
    private ArrayList<Point3F> pointList;
    private Vector3 refPoint = new Vector3(0f, 0f, 0f);
    @Before
    public void setUp() {
        pointList = Utility.generatePoints();
        Utility.writeOn();
    }

    @Test
    public void testFilteredHullRecur() {
        for (int i = 0 ; i < RECUR_COUNT; i++) {
            testFilteredHullSingle();
            pointList.addAll(Utility.generatePoints());
        }
    }

    @Test
    public void testFilteredHullSingle() {
        //filter input points
        ArrayList<Point3F> filteredPoints = PointFilter.filterGround(pointList,refPoint);
        filteredPoints = PointFilter.filterByRegion(filteredPoints,refPoint);
        filteredPoints = PointFilter.filterByConfidence(filteredPoints);

        //find convex hull
        ArrayList<Point3F> hull = QuickHull.getConvexHull(filteredPoints);

        final String filePathPoints = mainDir + "pointsRandom.txt";
        final String filePathHull = mainDir + "pointsHull.txt";

        Utility.writeFile(pointList,filePathPoints);
        Utility.writeFile(hull, filePathHull);
    }
}
