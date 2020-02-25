import com.helloarbridge4.Point3F.Point3F;
import com.helloarbridge4.SizeCheck.SizeCheckHandler;
import com.helloarbridge4.SizeCheck.TwoDimensionalOrientedBoundingBox;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

public class MATLABOutputVerification {

    int POINT_COUNT = 1000;
    int POINT_COUNT_SCALER = 10;
    int POINT_COUNT_MAX = 1000000;

    final float MAX = 10.0f;
    final float MIN = -10.0f;

    @Test
    public void integrateBasic() {

        ArrayList<Point3F> pointList = new ArrayList<>();
        pointList.clear();
        pointList.addAll(generatePoints());



//        for (POINT_COUNT= 10; POINT_COUNT <= POINT_COUNT_MAX; POINT_COUNT = POINT_COUNT * POINT_COUNT_SCALER) {
//            for (int i = 0; i < 10; i++) {
//                pointList.clear();
//                pointList = new ArrayList<>(generatePoints());

                //generate input for MATLAB
                double[] x = new double[pointList.size()];
                double[] y = new double[pointList.size()];

                int j = 0;
                for (Point3F point: pointList) {
                    x[j] = (double) point.x;
                    y[j] = (double) point.y;
                    j++;
                }

                try {
                    double[] xVals = {0.0,1.0,2.0,3.0};
                    double[] yVals = {1.0,2.0,3.0,4.0};
                    MWNumericArray xVal = new MWNumericArray(xVals,MWClassID.DOUBLE);
                    MWNumericArray yVal = new MWNumericArray(yVals,MWClassID.DOUBLE);


                    Point3F[] rectMin = TwoDimensionalOrientedBoundingBox.getMinimumBoundingRectangle(pointList);
                    SizeCheckHandler box = new SizeCheckHandler();
                    float[] LW = box.getBoxDimLW(rectMin);
                    float rectL = LW[0];
                    float rectW = LW[1];

                    //writeBoxDifference w = new writeBoxDifference();
//                    l.loadFromJava(POINT_COUNT, x,y,rectL, rectW);


                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Exception: " + e.getLocalizedMessage());
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Exception: " + e.getLocalizedMessage());
                }
//                catch (MWException e) {
//                    System.out.println("MATLAB Exception: " + e.getLocalizedMessage());
//                }
//            }
//        }

        Assert.assertFalse(false);

    }


    public ArrayList<Point3F> generatePoints() {
        ArrayList<Point3F> pointList = new ArrayList<Point3F>(POINT_COUNT);

        for (int i = 0 ; i < POINT_COUNT; i++) {
            pointList.add(new Point3F(
                    generateRandomInRange(MAX,MIN),
                    generateRandomInRange(MAX,MIN),
                    generateRandomInRange(MAX,MIN)
            ));
        }

        return pointList;
    }

    public float generateRandomInRange(Float max, Float min) {
        Random rd = new Random();
        return (min + rd.nextFloat()*(max - min));
    }

}
