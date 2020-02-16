import android.gesture.GestureUtils;
import android.gesture.OrientedBoundingBox;

import com.google.ar.sceneform.math.Vector3;

import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

public class SizeCheckHandlerTest {

    final int LOOP_COUNT = 1000000;
    final float Z_THRESH = 0.1f;
    final Vector3 REGION_LIMITS = new Vector3(0.8f,0.8f,0.8f);
    final float POINT_CONF_LIM = 0.7f;

    @Test
    public void getValidPointsTest() {

        GestureUtils gUtils = mock(GestureUtils.class);
        FloatBuffer pointBuffer = FloatBuffer.allocate(LOOP_COUNT);
        float[] floatList = new float[LOOP_COUNT];
        for (int i = 0; i < LOOP_COUNT; i++) {
            pointBuffer.put(generateRandomInRange(10f, -10f));
            floatList[i] = generateRandomInRange(10f, -10f);
        }
        Assert.fail();
    }


    float generateRandomInRange(Float max, Float min) {
        Random rd = new Random();
        return (min + rd.nextFloat()*(max - min));
    }
}
