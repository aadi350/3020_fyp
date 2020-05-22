package UITests;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import com.reactlibrary.ARActivity;
import com.reactlibrary.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class testObjectManipulation {

    public final int AR_LAYOUT = R.layout.ar_layout;

    @Rule
    public ActivityTestRule<ARActivity> activityRule
            = new ActivityTestRule<>(ARActivity.class);

    @Test
    public void testRotate() {
        onView(withId(AR_LAYOUT))
                .perform(click())
                .check(matches(isDisplayed()));
    }
}
