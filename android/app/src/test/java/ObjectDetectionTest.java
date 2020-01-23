import com.helloarbridge4.SizeCheck.ObjectDetection;

import org.junit.Before;
import org.junit.Test;

import java.nio.FloatBuffer;

public class ObjectDetectionTest {
    private int POINT_NUM = 1000;
    private FloatBuffer buffer = FloatBuffer.allocate(POINT_NUM);
    private ObjectDetection testObj = ObjectDetection.getObjectDetector();

    @Before
    public FloatBuffer generatePointCloud() {

        for (int i = 0; i < POINT_NUM; i++) {
            buffer.put((float) Math.random());
        }
        return buffer;
    }

    @Test
    public void loadValidPoints() {
        testObj.loadFloatBuffer(buffer);
    }


}
