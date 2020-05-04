package Utility;

import com.reactlibrary.Point3F.Point3F;
import com.reactlibrary.SizeCheck.MinBoundingBox.Rectangle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public  class Utility {
    final static int POINT_COUNT = 10;
    private static boolean WRITE_FILE = false;

    public static void writeOn() {
        WRITE_FILE = true;
    }

    public static void writeOff() {
        WRITE_FILE = false;
    }

    public static ArrayList<Point3F> generatePoints(float[] limits) {
        ArrayList<Point3F> pointList = new ArrayList<>();
        for (int i = 0; i <= POINT_COUNT; i++) {
            pointList.add(new Point3F(
                    getRandom(limits),
                    getRandom(limits),
                    getRandom(limits),
                    getRandom(limits)
            ));
        }
        return pointList;
    }

    public static ArrayList<Point3F> generatePoints() {
        ArrayList<Point3F> pointList = new ArrayList<>();
        for (int i = 0; i <= POINT_COUNT; i++) {
            pointList.add(new Point3F(
                    getRandom(),
                    getRandom(),
                    getRandom(),
                    getRandom()
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
