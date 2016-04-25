package triichat.testing;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        triichat.testing.model.AuthenticationTest.class,
        triichat.testing.model.GroupTest.class,
        triichat.testing.model.MessageTest.class,
        triichat.testing.model.TriiTest.class,
        triichat.testing.model.UserTest.class
})
/**
 * Created by Margret on 4/25/2016.
 */
public class AllModelTests {

}