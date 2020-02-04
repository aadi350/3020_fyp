import android.gesture.GestureUtils;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class OrientedBoundingBoxTest {

    static GestureUtils gUtils = mock(GestureUtils.class);

    @Test
    public void testOrienter() {


        float[] originalPoints = {0f,0f,0f,1f,1f,0f,1f,1f};
//          TODO integrate PowerMock to test static classes
//        mock(GestureUtils.class).computeOrientedBoundingBox(originalPoints);
//        System.out.println("Centre: " + b);
//        assertNotNull(b);
    }
}
