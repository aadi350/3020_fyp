import com.google.ar.core.PointCloud;
import com.google.ar.sceneform.math.Vector3;
import com.reactlibrary.Point3F.Point3F;
import com.reactlibrary.Point3F.PointFilter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import Utility.Utility;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PointFilterTest {
    private final String mainDir = "D:\\3020\\Unit\\PointFilter\\";
    private final float Z_THRESH = 0.05f;
    private Vector3 planePose = new Vector3(0f, 0f, 0f);

    //Ground filter
    final float[] GROUND_FAR_POS = {0.1f,10.0f};
    final float[] GROUND_BOUNDARY_HIGH = {0.05f,0.1f};
    final float[] GROUND_BOUNDARY_LOW = {0.0f,0.05f};
    final float[] GROUND_FAR_NEG = {-10.0f,0.0f};
    float[][] groundLimits = {
            GROUND_FAR_NEG,
            GROUND_BOUNDARY_LOW,
            GROUND_BOUNDARY_HIGH,
            GROUND_FAR_POS
    };

    //Confidence filter
    final float[] CONF_LOW = {0.0f, 0.5f};
    final float[] CONF_BOUNDARY_LOW = {0.5f,0.7f};
    final float[] CONF_BOUNDARY_HIGH = {0.7f, 0.8f};
    final float[] CONF_HIGH = {0.8f, 1.0f};
    float[][] confLimits = {
            CONF_HIGH,
            CONF_BOUNDARY_HIGH,
            CONF_BOUNDARY_LOW,
            CONF_LOW
    };


    //region filter
    Vector3 refPoint = new Vector3(0.0f, 0.0f, 0.0f);
    final float[] REGION_FAR = {-10.0f, 10.0f};
    final float[] REGION_BOUNDARY = {-0.55f, 0.55f};
    final float[] REGION_NEAR = {-0.4f, 0.4f};
    final float[] REGION_OUT = {0.6f,1.0f};
    float[][] regionLimits = {
            REGION_NEAR,
            REGION_BOUNDARY,
            REGION_FAR,
            REGION_OUT
    };

    private ArrayList<Point3F> pointList;
    private FloatBuffer pointBuffer;

    @Mock
    PointCloud pointCloudMock = mock(PointCloud.class);

    @Before
    public void setUp() {
        getPoints();
        getCloudBuffer();
        Utility.writeOn();
    }

    private void getPoints() {
        pointList = Utility.generatePoints();
    }

    private void getCloudBuffer() {
        //set up behaviour of PointCloud object
        pointBuffer = FloatBuffer.allocate(pointList.size()*4);
        for (Point3F p : pointList) {
            pointBuffer.put(p.x);
            pointBuffer.put(p.y);
            pointBuffer.put(p.z);
            pointBuffer.put(p.c);
        }
    }

    /*------------------------------------------UNIT TESTS----------------------------------------*/
    @Test
    public void testConvertBufferToArrayList() {
      when(pointCloudMock.getPoints())
                .thenReturn(pointBuffer);

        ArrayList<Point3F> convertedList = PointFilter.convertBufferToList(pointCloudMock.getPoints());

        boolean result = true;
        for (Point3F p : convertedList) {
            result = (
                    p.x == pointBuffer.get() &&
                    p.y == pointBuffer.get() &&
                    p.z == pointBuffer.get() &&
                    p.c == pointBuffer.get());
        }

        Assert.assertTrue(result);
     }

    @Test
    public void filterByConfidence() {
        final float POINT_CONF_LIM = PointFilter.getPointConfidenceMin();
        for (float[] p : confLimits) {
            pointList = Utility.generatePoints(p);
            ArrayList<Point3F> filteredPoints = PointFilter.filterByConfidence(pointList);

            for (Point3F point : filteredPoints) {
                Assert.assertTrue(point.c >= POINT_CONF_LIM);
            }
        }
    }

    @Test
    public void filterByRegion() {
        for (int partition = 0; partition < regionLimits.length; partition++) {
            pointList = Utility.generatePoints(regionLimits[partition]);
            refPoint = new Vector3(-0.11f,0.23f,0.1f);
            ArrayList<Point3F> closeList = PointFilter.filterByRegion(pointList, refPoint);

            Utility.writeFile(pointList,mainDir + "\\region\\pointsUnfiltered" + partition + ".txt");
            Utility.writeFile(closeList,mainDir + "\\region\\pointsRegionFiltered" + partition + ".txt");


            verifyRegionFilter(closeList,refPoint);
            System.out.println(closeList.size());
            closeList.clear();

        }
    }

    @Test
    public void filterByGround() {
        for (int partition = 0; partition < groundLimits.length; partition++) {
            pointList = Utility.generatePoints(groundLimits[partition]);
            ArrayList<Point3F> filteredList = PointFilter.filterGround(pointList, planePose);
            Assert.assertTrue(verifyGroundFilter(filteredList));

            Utility.writeFile(pointList,mainDir + "\\ground\\pointsUnfiltered" + partition + ".txt");
            Utility.writeFile(filteredList,mainDir + "\\ground\\pointsGroundFiltered" + partition + ".txt");
        }
    }
    /*------------------------------------LEVEL- 0 INTEGRATION TESTS--------------------------------*/

    @Test
    public void testFilterConfidenceWithGround() {
        for (int groundIterator = 0; groundIterator < groundLimits.length; groundIterator++) {
            pointList = Utility.generatePoints(groundLimits[groundIterator]);
            ArrayList<Point3F> groundList = PointFilter.filterGround(pointList, planePose);

            for (int confIterator = 0; confIterator < confLimits.length; confIterator++) {
                ArrayList<Point3F> filteredList = PointFilter.filterByConfidence(groundList);
                Assert.assertTrue(verifyGroundFilter(filteredList));
                Assert.assertTrue(verifyConfFilter(filteredList));
            }
        }
    }
    @Test
    public void testFilterGroundWithRegion() {
        for (int groundIterator = 0; groundIterator < groundLimits.length; groundIterator++) {
            pointList = Utility.generatePoints(groundLimits[groundIterator]);
            ArrayList<Point3F> groundList = PointFilter.filterGround(pointList, planePose);

            for (int regionIterator = 0; regionIterator < confLimits.length; regionIterator++) {
                ArrayList<Point3F> filteredList = PointFilter.filterByRegion(groundList,refPoint);
                Assert.assertTrue(verifyGroundFilter(filteredList));
                Assert.assertTrue(verifyRegionFilter(filteredList,refPoint));
            }
        }
    }

    @Test
    public void testfilterAll() {
        for (int groundIterator = 0; groundIterator < groundLimits.length; groundIterator++) {
            pointList = Utility.generatePoints(groundLimits[groundIterator]);
            ArrayList<Point3F> groundList = PointFilter.filterGround(pointList, planePose);

            for (int regionIterator = 0; regionIterator < confLimits.length; regionIterator++) {
                ArrayList<Point3F> regionList = PointFilter.filterByRegion(groundList,refPoint);
                for (int confIterator = 0; confIterator < confLimits.length; confIterator++) {
                    ArrayList<Point3F> confList = PointFilter.filterByConfidence(regionList);
                    Assert.assertTrue(verifyRegionFilter(confList,refPoint));
                    Assert.assertTrue(verifyGroundFilter(confList));
                    Assert.assertTrue(verifyConfFilter(confList));
                }
            }
        }
    }

    /*--------------------------------------VERIFY FUNCTIONS---------------------------------------*/
    private boolean verifyGroundFilter(ArrayList<Point3F> filteredList) {
        boolean pass = true;
        for (Point3F point : filteredList) {
            pass = point.y > Z_THRESH + planePose.y;
        }
        return pass;
    }

    private boolean verifyConfFilter(ArrayList<Point3F> filteredList) {
        boolean result = true;
        for (Point3F point : filteredList) {
           result = point.c >= PointFilter.getPointConfidenceMin();
        }
        return result;
    }

    private boolean verifyRegionFilter(ArrayList<Point3F> filteredList, Vector3 refPoint) {
        boolean result = true;
        for (Point3F point : filteredList) {

            float difX = Math.abs(point.x - refPoint.x);
            float difZ = Math.abs(point.z - refPoint.z);
            float dist = (float) Math.sqrt(difX * difX + difZ * difZ);
            result = dist <= 0.5f;
        }
        return result;
    }

}
