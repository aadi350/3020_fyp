package IntegrationTests;

import com.google.ar.sceneform.math.Vector3;
import com.reactlibrary.Point3F.Point3F;
import com.reactlibrary.Point3F.PointFilter;
import com.reactlibrary.SizeCheck.MinBoundingBox.QuickHull;
import com.reactlibrary.SizeCheck.MinBoundingBox.TwoDimensionalOrientedBoundingBox;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import Utility.Utility;

public class TwoDimensionalOrientedBoundingBoxTest {
    private final String mainDir = "D:\\3020\\Integration\\OBB\\";
    private static final int RECUR_COUNT = 10;
    private ArrayList<Point3F> pointList;
    private Vector3 refPoint = new Vector3(0f, 0f, 0f);

    @Before
    public void setUp() {
        pointList = Utility.generatePoints();
        Utility.writeOn();
    }

    @Test
    public void testFilteredBoxRecur() {
        for (int i = 0 ; i < RECUR_COUNT; i++) {
            ArrayList<Point3F> filteredPoints = PointFilter.filterGround(pointList,refPoint);
            filteredPoints = PointFilter.filterByRegion(filteredPoints,refPoint);
            filteredPoints = PointFilter.filterByConfidence(filteredPoints);

            //find convex hull
            ArrayList<Point3F> hull = QuickHull.getConvexHull(filteredPoints);

            //find min bounding box
            ArrayList<Point3F> box = TwoDimensionalOrientedBoundingBox.getOBB(hull).getPoints();

            final String filePathPoints = mainDir + "pointsRandom"+ i +".txt";
            final String filePathHull = mainDir + "pointsHull" + i + ".txt";
            final String filePathBox = mainDir + "pointsBox" + i + ".txt";

            Utility.writeFile(pointList,filePathPoints);
            Utility.writeFile(hull, filePathHull);
            Utility.writeFile(box, filePathBox);
            pointList.addAll(Utility.generatePoints());
        }
    }

    @Test
    public void testFilteredBoxSingle() {
        //filter input points
        ArrayList<Point3F> filteredPoints = PointFilter.filterPoints(pointList,refPoint);
        for (int i =0 ; i < 1000; i++) {
            filteredPoints.addAll(PointFilter.filterPoints(Utility.generatePoints(),refPoint));
        }
        System.out.println(filteredPoints.size());
        //find convex hull
        ArrayList<Point3F> hull = QuickHull.getConvexHull(filteredPoints);

        //find min bounding box
        ArrayList<Point3F> box = TwoDimensionalOrientedBoundingBox.getOBB(hull).getPoints();

        final String filePathPoints = mainDir + "pointsRandom.txt";
        final String filePathHull = mainDir + "pointsHull.txt";
        final String filePathBox = mainDir + "pointsBox.txt";

        Utility.writeFile(pointList,filePathPoints);
        Utility.writeFile(hull, filePathHull);
        Utility.writeFile(box, filePathBox);
    }
}
