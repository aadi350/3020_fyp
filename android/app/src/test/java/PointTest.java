import com.google.ar.sceneform.math.Vector3;
import com.helloarbridge4.SizeCheck.Point;

import org.junit.Test;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class PointTest {

    private final int NUM_POINTS = 1000;
    private final Float POINT_THRESH = 0.7f;


    public List<Point> generatePoints() {
        List<Point> pointList = new ArrayList<Point>();
        for (int i = 0; i < NUM_POINTS; i++) {
            pointList.add(new Point(
                    (float) Math.random(),
                    (float) Math.random(),
                    (float) Math.random()
            ));
        }
        return pointList;
    }

    public FloatBuffer generateFloadBuffer() {
        FloatBuffer b = FloatBuffer.allocate(NUM_POINTS);
        for (int i = 0; i < NUM_POINTS; i++) {
            b.put((float) Math.random());
        }
        return b;
    }

    @Test
    public void loadValidPoints() {

        for (int i = 0; i < NUM_POINTS; i++) {
            float c = (float) Math.random();
            assertSame(Point.isValid(c), (c > POINT_THRESH));
        }
    }

    @Test
    public void filterByDistanceTo() {

        float X_THRESH = 0.8f, Y_THRESH = 0.8f, Z_THRESH = 0.8f;
        //Testing null input conditions
        Vector3 v = new Vector3(Vector3.zero());
        List<Point> p = generatePoints();
        assertFalse(Point.filterByDistanceTo(null,null));
        for (Point point : p) {
            Vector3 difference = Vector3.subtract(new Vector3(point.getX(),point.getY(),point.getZ()),v);
            Boolean isTrue = (
                    difference.x < X_THRESH &&
                            difference.y < Y_THRESH &&
                            difference.z < Z_THRESH
                    );
            boolean actuallyTrue = Point.filterByDistanceTo(v,point);
            assertSame(isTrue, actuallyTrue);
        }
    }

    @Test
    public void getXYZ() {
        List<Point> p = generatePoints();
        for (Point point : p) {
            assertNotNull(point.getXYZ());

            assertNotNull(point.getZ());
            assertNotNull(point.getY());
            assertNotNull(point.getX());
        }
    }

}
