package SizeCheck;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.test.InstrumentationRegistry;

import com.google.ar.sceneform.math.Vector3;
import com.reactlibrary.Builder.CarryOnBuilder;
import com.reactlibrary.Builder.DuffelBuilder;
import com.reactlibrary.Builder.ObjectSizes;
import com.reactlibrary.Builder.PersonalItemBuilder;
import com.reactlibrary.Builder.objectBuilder;
import com.reactlibrary.Object.ObjectCodes;
import com.reactlibrary.SizeCheck.SizeCheckHandler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ObjectBuilderTest {
    @Test
    public void testSetObjectCarryOnGetSize() {
        final float LENGTH = 0.35f;
        final float WIDTH = 0.23f;
        final float HEIGHT = 0.56f;

        Vector3 objectSize = new Vector3(LENGTH,WIDTH,HEIGHT);
        objectBuilder o = new CarryOnBuilder();

        Context context =
                InstrumentationRegistry
                        .getTargetContext()
                        .getApplicationContext();

        o.initBuilder(context);
        Assert.assertEquals(objectSize, ObjectSizes.getCarryOn());
    }

    @Test
    public void testSetObjectDuffelGetSize() {
        final float LENGTH = 0.56f;
        final float WIDTH = 0.23f;
        final float HEIGHT = 0.35f;

        Vector3 objectSize = new Vector3(LENGTH,WIDTH,HEIGHT);
        objectBuilder o = new DuffelBuilder();

        Context context =
                InstrumentationRegistry
                        .getTargetContext()
                        .getApplicationContext();

        o.initBuilder(context);
        Assert.assertEquals(objectSize,ObjectSizes.getDuffel());
    }
    @Test
    public void testSetObjectPersonalGetSize() {
        final float LENGTH = 0.33f;
        final float WIDTH = 0.15f;
        final float HEIGHT = 0.43f;

        Vector3 objectSize = new Vector3(LENGTH,WIDTH,HEIGHT);
        objectBuilder o = new PersonalItemBuilder();

        Context context =
                InstrumentationRegistry
                        .getTargetContext()
                        .getApplicationContext();

        o.initBuilder(context);
        Assert.assertEquals(objectSize, ObjectSizes.getPersonal());
    }

}
