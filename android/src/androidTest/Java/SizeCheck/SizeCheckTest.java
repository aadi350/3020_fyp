package SizeCheck;

import android.util.Log;

import com.google.ar.sceneform.math.Vector3;
import com.reactlibrary.FitCodes;
import com.reactlibrary.Object.ObjectCodes;
import com.reactlibrary.Point3F.Point3F;
import com.reactlibrary.SizeCheck.SizeCheckHandler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SizeCheckTest {
    SizeCheckHandler handler;
    ArrayList<Point3F> pointList;
    Vector3 nodePosition;

    @Before
    public void setUp() {
        handler = new SizeCheckHandler();
        nodePosition = new Vector3(Vector3.zero());
        pointList = new ArrayList<>(Utility.generatePoints());
}

    @Test
    public void testGetFitCode_emptyInput(){
        handler.updateAnchor(nodePosition);
        handler.setObject(ObjectCodes.CARRYON);
        handler.loadPointList(new ArrayList<Point3F>());

        Assert.assertNotNull(handler.checkIfFits());
        Assert.assertEquals(FitCodes.NONE,handler.checkIfFits());
    }

    @Test
    public void testGetFitCode_fits() {
        float[] LIM = new float[] {-0.1f,0.1f};
        pointList = Utility.generatePoints(LIM);
        handler.updateAnchor(nodePosition);
        handler.setObject(ObjectCodes.CARRYON);
        handler.loadPointList(pointList);

        int DELAY_COUNT = 200;
        FitCodes f = null;
        //override delay count
        for (int i = 0; i < DELAY_COUNT + 500; i++) {
            f = handler.checkIfFits();
            handler.loadPointList(Utility.generatePoints(LIM));
        }

        Assert.assertEquals(FitCodes.FIT,f);
    }

    @Test
    public void testGetFitCode_large() {
        float[] LIM = new float[] {-0.5f,0.5f};
        pointList = Utility.generatePoints(LIM);
        handler.updateAnchor(nodePosition);
        handler.setObject(ObjectCodes.CARRYON);
        handler.loadPointList(pointList);

        int DELAY_COUNT = 200;
        FitCodes f = null;
        //override delay count
        for (int i = 0; i < DELAY_COUNT + 500; i++) {
            f = handler.checkIfFits();
            handler.loadPointList(Utility.generatePoints(LIM));
            Log.d("fitCode", String.valueOf(handler.getPointList().size()));
            Log.d("fitCode", String.valueOf(handler.getBoxDim()));
        }

        Assert.assertEquals(FitCodes.LARGE,f);
    }

}



 class Utility {
    final static int POINT_COUNT = 50;
    private static boolean WRITE_FILE = false;

    public static void writeOn() {
        WRITE_FILE = true;
    }

    public static void writeOff() {
        WRITE_FILE = false;
    }

    public static ArrayList<Point3F> generatePoints(float[] limits) {
        ArrayList<Point3F> pointList = new ArrayList<>();
        Random rd = new Random();
        for (int i = 0; i <= POINT_COUNT; i++) {
            pointList.add(new Point3F(
                    getRandom(limits),
                    0.2f,
                    getRandom(limits),
                    1f
            ));
        }
        return pointList;
    }

    public static ArrayList<Point3F> generatePoints() {
        ArrayList<Point3F> pointList = new ArrayList<>();
        for (int i = 0; i <= POINT_COUNT; i++) {
            pointList.add(new Point3F(
                    getRandom(),
                    Math.abs(getRandom()) + 0.05f,
                    getRandom(),
                   1f
            ));
        }
        return pointList;
    }

    public static float getRandom() {
        final float MIN = -1.0f;
        final float MAX = 1.0f;

        Random rd = new Random();
        return MIN + rd.nextFloat()*(MAX - MIN);
    }

    private static float getRandom(float[] limits) {
        final float MIN = limits[0];
        final float MAX = limits[1];

        Random rd = new Random();
        return MIN + rd.nextFloat()*(MAX - MIN);
    }

    public static void writeFile(ArrayList<Point3F> pointList, String filePath) {
        if (WRITE_FILE) {
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

}

