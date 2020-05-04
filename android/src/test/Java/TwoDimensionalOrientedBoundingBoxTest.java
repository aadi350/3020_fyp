import com.reactlibrary.Point3F.Point3F;
import com.reactlibrary.SizeCheck.MinBoundingBox.Rectangle;
import com.reactlibrary.SizeCheck.MinBoundingBox.TwoDimensionalOrientedBoundingBox;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Objects;

import Utility.Utility;


public class TwoDimensionalOrientedBoundingBoxTest {
    private static final int RECUR_COUNT = 5;
    private final String mainDir = "D:\\3020\\Unit\\OBB\\";
    private ArrayList<Point3F> pointList;

    @Before
    public void setUp() {
        Utility.writeOn();
    }

    @Test
    public void getMinimumBoundingRectangleTest() {
        //simulate iterative addition to point list
        for (int i = 1; i  <= RECUR_COUNT;  i++) {
            //add new set of input points to existing point set
            pointList = Utility.generatePoints();
        }

        //write points to text file
        final String filePathPoints = mainDir + "points.txt";
        Utility.writeFile(pointList,filePathPoints);


        //find OBB of points
        Rectangle rectMin = TwoDimensionalOrientedBoundingBox.getOBB(pointList);

        //set OBB List to rectMin
        ArrayList<Point3F> OBBList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Point3F boxPoint = Objects.requireNonNull(rectMin).getPoint(i);
            OBBList.add(boxPoint);
        }

        //string determines path of output file containing all points
        final String filePathOBB = mainDir + "OBB.txt";
        Utility.writeFile(OBBList,filePathOBB);
    }

}
