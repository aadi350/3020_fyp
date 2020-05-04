import android.content.Context;
import android.util.Log;

import com.google.ar.sceneform.math.Vector3;
import com.reactlibrary.Builder.DuffelBuilder;
import com.reactlibrary.Builder.objectBuilder;
import com.reactlibrary.FitCodes;
import com.reactlibrary.Object.ObjectCodes;
import com.reactlibrary.Point3F.Point3F;
import com.reactlibrary.Point3F.PointFilter;
import com.reactlibrary.SizeCheck.MinBoundingBox.TwoDimensionalOrientedBoundingBox;
import com.reactlibrary.SizeCheck.QuickSort;
import com.reactlibrary.SizeCheck.SizeCheckHandler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import Utility.Utility;
import de.javagl.obj.Obj;

import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})

public class SizeCheckTest {

    private final float MAX_VAL = 10.0f;
    private final float MIN_VAL = -10.0f;
    private final float[] LIM = {
            MIN_VAL,
            MAX_VAL
    };

    private ArrayList<Point3F> pointList;
    private Vector3 anchorPoint;
    private SizeCheckHandler handler;

    private Vector3[] comparisonDim;

    @Before
    public void setUp() {
        PowerMockito
                .mockStatic(Log.class);
        generatePoints();
    }

    private void generatePoints() {
        pointList = Utility.generatePoints(LIM);
        anchorPoint = new Vector3(0f,0f,0f);
    }

    @Test
    public void testCompareLimitsLarge() {
        PowerMockito
                .mockStatic(Log.class);
        handler = new SizeCheckHandler();
        FitCodes fitCode;

        comparisonDim = new Vector3[]{
                new Vector3(Vector3.zero()),
                new Vector3(
                        Math.abs(Utility.getRandom()),
                        Math.abs(Utility.getRandom()),
                        Math.abs(Utility.getRandom())
                )
        };

        fitCode = handler.compareLimits(comparisonDim[0], comparisonDim[1]);
        Assert.assertEquals(FitCodes.LARGE,fitCode);

        float randomLow = Math.abs(Utility.getRandom());
        float randomMid = randomLow + Math.abs(Utility.getRandom());
        float randomHigh = randomMid + Math.abs(Utility.getRandom());

        //1 dim larger
        comparisonDim = new Vector3[] {
                new Vector3(randomMid,randomMid,randomMid),
                new Vector3(randomHigh,randomMid,randomMid)
        };

        fitCode = handler.compareLimits(comparisonDim[0], comparisonDim[1]);
        Assert.assertEquals(FitCodes.LARGE,fitCode);

        //1 dim larger
        comparisonDim = new Vector3[] {
                new Vector3(randomMid,randomMid,randomMid),
                new Vector3(randomHigh,randomMid,randomLow)
        };

        fitCode = handler.compareLimits(comparisonDim[0], comparisonDim[1]);
        Assert.assertEquals(FitCodes.LARGE,fitCode);

        //1 dim larger
        comparisonDim = new Vector3[] {
                new Vector3(randomMid,randomMid,randomMid),
                new Vector3(randomHigh,randomLow,randomLow)
        };

        fitCode = handler.compareLimits(comparisonDim[0], comparisonDim[1]);
        Assert.assertEquals(FitCodes.LARGE,fitCode);

        //1 dim larger
        comparisonDim = new Vector3[] {
                new Vector3(randomMid,randomMid,randomMid),
                new Vector3(randomHigh,randomLow,randomLow)
        };

        fitCode = handler.compareLimits(comparisonDim[0], comparisonDim[1]);
        Assert.assertEquals(FitCodes.LARGE,fitCode);

        //1 dim larger
        comparisonDim = new Vector3[] {
                new Vector3(randomMid,randomMid,randomMid),
                new Vector3(randomLow,randomHigh,randomLow)
        };

        fitCode = handler.compareLimits(comparisonDim[0], comparisonDim[1]);
        Assert.assertEquals(FitCodes.LARGE,fitCode);

        //1 dim larger
        comparisonDim = new Vector3[] {
                new Vector3(randomMid,randomMid,randomMid),
                new Vector3(randomLow,randomLow,randomHigh)
        };

        fitCode = handler.compareLimits(comparisonDim[0], comparisonDim[1]);
        Assert.assertEquals(FitCodes.LARGE,fitCode);

    }

    @Test
    public void testCompareLimitsFits() {
        PowerMockito
                .mockStatic(Log.class);
        SizeCheckHandler handler = new SizeCheckHandler();
        FitCodes fitCode;

        float randomLow = Math.abs(Utility.getRandom());
        float randomMid = randomLow + Math.abs(Utility.getRandom());

        comparisonDim = new Vector3[] {
                new Vector3(randomMid,randomMid,randomMid),
                new Vector3(randomLow,randomLow,randomLow)
        };

        fitCode = handler.compareLimits(comparisonDim[0], comparisonDim[1]);
        Assert.assertEquals(FitCodes.FIT,fitCode);
    }

    @Test
    public void testCompareLimitsBoundary() {
        PowerMockito
                .mockStatic(Log.class);
        SizeCheckHandler handler = new SizeCheckHandler();
        FitCodes fitCode;

        float randomLow = Math.abs(Utility.getRandom());
        float randomMid = randomLow + Math.abs(Utility.getRandom());

        float DIM_BUFFER = 0.03f;

        comparisonDim = new Vector3[] {
                new Vector3(randomMid,randomMid,randomMid),
                new Vector3(randomMid + DIM_BUFFER,randomMid + DIM_BUFFER,randomMid + DIM_BUFFER)
        };

        fitCode = handler.compareLimits(comparisonDim[0], comparisonDim[1]);
        Assert.assertEquals(FitCodes.FIT,fitCode);
    }

    @Test
    public void testCheckIfFits_notEnoughPoints() {
        handler = new SizeCheckHandler();
        List<Point3F> list =  Utility.generatePoints(LIM).subList(0,2);
        pointList.clear();
        pointList.addAll(list);

    }

    @Test
    public void testPointListSame() {
        ArrayList<Point3F> pointsbefore = pointList;
        TwoDimensionalOrientedBoundingBox.getOBB(pointList);
        QuickSort q = new QuickSort();
        q.getHighestPoint(pointList);
        Assert.assertEquals(pointsbefore,pointList);
    }


}

