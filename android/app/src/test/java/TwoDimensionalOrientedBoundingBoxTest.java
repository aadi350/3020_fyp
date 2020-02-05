import com.helloarbridge4.SizeCheck.TwoDimensionalOrientedBoundingBox;

import org.junit.Assert;
import org.junit.Test;

public class TwoDimensionalOrientedBoundingBoxTest {

    @Test
    public void computeBox() {
        Assert.assertNotNull(TwoDimensionalOrientedBoundingBox.computeBox(null));
    }

}
