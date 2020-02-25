import com.helloarbridge4.Point3F.Point3F;
import com.helloarbridge4.SizeCheck.TwoDimensionalOrientedBoundingBox;
import com.mathworks.toolbox.javabuilder.MWException;

import org.junit.Assert;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Random;


public class TwoDimensionalOrientedBoundingBoxTest {

    final int POINT_COUNT = 10;
    final float MAX = 5f;
    final float MIN = -5f;


    @Test
    public void getMinimumBoundingRectangleKnown() {

        Point3F[] expected = {
                new Point3F(280f,-40f,0f),
                new Point3F(200f,200f,0f),
                new Point3F(-400f,0f,0f),
                new Point3F(-320f,-240f,0f)
        };

        ArrayList<Point3F> pointList = new ArrayList<>();
        pointList.add(new Point3F(-300f,-150f,0f));
        pointList.add(new Point3F(200f,200f,0f));
        pointList.add(new Point3F(100f,-100f,0f));
        pointList.add(new Point3F(-400f,0f,0f));

        Point3F[] actual = TwoDimensionalOrientedBoundingBox.getMinimumBoundingRectangle(pointList);

        int i = 0;
        for (Point3F p:actual) {
            Assert.assertEquals(expected[i].x,p.x,0.2);
            Assert.assertEquals(expected[i].y,p.y,0.2);
            i++;
        }

        System.out.println(pointList);
    }

    @Test
    public void getAllBoundingRectanglesTest() {
        ArrayList<Point3F> pointList = generatePoints();
        ArrayList<Point3F[]> rectArrayList = TwoDimensionalOrientedBoundingBox.getAllBoundingRectangles(pointList);
        Assert.assertNotNull(rectArrayList);
    }

    @Test
    public void getMinimumBoundingRectangleTest() {
        ArrayList<Point3F> pointList = new ArrayList<>(generatePoints());
        pointList.addAll(generatePoints());
        try {
            ArrayList<Point3F[]> rectList = TwoDimensionalOrientedBoundingBox.getAllBoundingRectangles(pointList);
            Point3F[] rectMin = TwoDimensionalOrientedBoundingBox.getMinimumBoundingRectangle(pointList);
            double minArea = Double.MAX_VALUE;
            for (Point3F[] rect : rectList) {
                if (TwoDimensionalOrientedBoundingBox.getArea(rect) < minArea) {
                    minArea = TwoDimensionalOrientedBoundingBox.getArea(rect);
                }
            }
            double minAreaActual = TwoDimensionalOrientedBoundingBox.getArea(rectMin);

            Assert.assertEquals(minArea, minAreaActual,0.1);

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e.getLocalizedMessage());
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getLocalizedMessage());
        }

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

    float generateRandomInRange(Float max, Float min) {
        Random rd = new Random();
        return (min + rd.nextFloat()*(max - min));
    }


}
