package triichat.testing;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        triichat.testing.servlet.ExampleServletTest.class,
        triichat.testing.servlet.MeServletTest.class,
        triichat.testing.servlet.GroupServletTest.class
})
/**
 * Created by Margret on 4/25/2016.
 */
public class AllServletTests {

}