import com.reactlibrary.Point3F.Point3F;
import com.reactlibrary.SizeCheck.MinBoundingBox.QuickHull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;

import Utility.Utility;

public class QuickHullTest {
    private final String mainDir = "D:\\3020\\Unit\\QuickHull\\";
    private ArrayList<Point3F> pointList;

    @Before
    public void setUp() {
        pointList = Utility.generatePoints();
        Utility.writeOn();
    }

    /*----------------------------UNIT TESTS----------------------------*/
    @Test
    public void testCrossProduct() {
        int RECUR_COUNT = 100000;
        for (int i = 0; i < RECUR_COUNT; i++) {
            singleCrossProduct();
        }
    }

    private void singleCrossProduct() {
        //provides all possible combinations of three points' orientation
        //for testing output of pointLocation()
        //called multiple times with random input
        final int LEFT_TURN = 1, RIGHT_TURN = -1;
        float randomLow = Math.abs(Utility.getRandom());
        float randomMid = randomLow + Math.abs(Utility.getRandom());
        float randomHigh = randomMid + Math.abs(Utility.getRandom());

        Point3F randomPoint = new Point3F(
                Utility.getRandom(),
                Utility.getRandom(),
                Utility.getRandom()
        );

        Point3F[] rightTurnRight = {
                randomPoint,
                new Point3F(randomPoint.x + randomLow, randomPoint.y + 0.0f, randomPoint.z + randomLow),
                new Point3F(randomPoint.x + randomHigh, randomPoint.y, randomPoint.z + randomMid)
        };

        Assert.assertEquals(RIGHT_TURN,QuickHull.pointLocation(
                rightTurnRight[0],
                rightTurnRight[1],
                rightTurnRight[2]
        ));

        Point3F[] leftTurnLeft = {
                randomPoint,
                new Point3F(randomPoint.x - randomLow, randomPoint.y, randomPoint.z - randomLow),
                new Point3F(randomPoint.x - randomMid, randomPoint.y, randomPoint.z - randomHigh)
        };

        Assert.assertEquals(LEFT_TURN,QuickHull.pointLocation(
                leftTurnLeft[0],
                leftTurnLeft[1],
                leftTurnLeft[2]
        ));

        Point3F[] leftTurnRight = {
                randomPoint,
                new Point3F(randomPoint.x + randomMid, randomPoint.y, randomPoint.z + randomMid),
                new Point3F(randomPoint.x - randomMid, randomPoint.y, randomPoint.z + randomHigh)
        };

        Assert.assertEquals(LEFT_TURN,QuickHull.pointLocation(
                leftTurnRight[0],
                leftTurnRight[1],
                leftTurnRight[2]
        ));

        Point3F[] rightTurnLeft = {
                randomPoint,
                new Point3F(randomPoint.x - randomMid, randomPoint.y, randomPoint.z + randomMid),
                new Point3F(randomPoint.x + randomMid, randomPoint.y, randomPoint.z + randomHigh)
        };

        Assert.assertEquals(RIGHT_TURN,QuickHull.pointLocation(
                rightTurnLeft[0],
                rightTurnLeft[1],
                rightTurnLeft[2]
        ));

        Point3F[] collinear = {
                randomPoint,
                new Point3F(randomPoint.x + randomMid, randomPoint.y, randomPoint.z + randomMid),
                new Point3F(randomPoint.x + randomHigh, randomPoint.y, randomPoint.z + randomHigh)
        };
    }

    /*--------------------------LEVEL-0 INTEGRATION--------------------------*/
    @Test
    public void repeatedQuickHull() {
        //Test to generate output which is graphed in MATLAB
        pointList.clear();
        final int RECUR_COUNT = 10;
        for (int i = 0; i < RECUR_COUNT; i++) {
//            getQuickHull(i);
            final String filePathPoints = mainDir + "pointsRandom"+ i +".txt";
            final String filePathHull = mainDir + "pointsHull"+ i +".txt";
            float[] f = {-0.9f, -0.5f };
            pointList.addAll(Utility.generatePoints(f));
            if (i % 3 == 0) {
                Utility.writeFile(pointList,filePathPoints);
                ArrayList<Point3F> hull =  QuickHull.getConvexHull(pointList);
                Utility.writeFile(hull, filePathHull);
            }

        }
    }


    @Test
    public void testQuickHullLessThanThreePoints() {
        ArrayList<Point3F> shortList = new ArrayList<>();
        shortList.add(new Point3F(Utility.getRandom(),Utility.getRandom(),Utility.getRandom()));
        shortList.add(new Point3F(Utility.getRandom(), Utility.getRandom(), Utility.getRandom()));
        ArrayList<Point3F> actual = QuickHull.getConvexHull(shortList);
        Assert.assertEquals(shortList,actual);
    }


    public void getQuickHull(int i) {
        final String filePathPoints = mainDir + "pointsRandom"+ i +".txt";
        final String filePathHull = mainDir + "pointsHull"+ i +".txt";
        float[] f = {-0.9f, -0.5f };
        pointList.addAll(Utility.generatePoints(f));
        Utility.writeFile(pointList,filePathPoints);
        ArrayList<Point3F> hull =  QuickHull.getConvexHull(pointList);
        Utility.writeFile(hull, filePathHull);
    }
}
