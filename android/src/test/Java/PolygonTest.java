import com.reactlibrary.Point3F.Point3F;
import com.reactlibrary.SizeCheck.MinBoundingBox.Polygon;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import Utility.Utility;

public class PolygonTest {
    Polygon polygon;

    ArrayList<Point3F> pointList;
    private final String mainDir = "D:\\3020\\Unit\\Polygon\\";

    @Before
    public void setUp() {
        pointList = Utility.generatePoints();
        Utility.writeOff();

    }
    /*----------------------------UNIT TESTS----------------------------*/
    @Test
    public void testCalculateCenter() {
        polygon = new Polygon(pointList);

        final String filePathPoints = mainDir + "pointsRandom.txt";
        final String filePathPoly = mainDir + "pointsPoly.txt";

        Utility.writeFile(pointList,filePathPoints);
        Utility.writeFile(polygon.getPoints(),filePathPoly);
    }

    @Test
    public void testAddSinglePoint() {
        //checks if new point added to Polygon data structure
        //exists after adding another newer point
        polygon = new Polygon(pointList);

        Point3F oldPoint = new Point3F(
                Utility.getRandom(),
                Utility.getRandom(),
                Utility.getRandom()
        );

        polygon.addPoint(oldPoint);

        Point3F newPoint = new Point3F(
                Utility.getRandom(),
                Utility.getRandom(),
                Utility.getRandom()
        );
        polygon.addPoint(newPoint);

        ArrayList<Point3F> polyList = polygon.getPoints();

        Assert.assertTrue( polyList.contains(oldPoint));
        Assert.assertTrue(polyList.contains(newPoint));
    }
}
