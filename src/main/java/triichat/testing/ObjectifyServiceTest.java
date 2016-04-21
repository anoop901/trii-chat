package triichat.testing;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import triichat.db.OfyService;
import triichat.model.User;
import java.util.List;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Margret on 4/19/2016.
 */
public class ObjectifyServiceTest {

    // maximum eventual consistency (see https://cloud.google.com/appengine/docs/java/tools/localunittesting)
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

    private Closeable closeable;

    @Before
    public void setUp() {
        helper.setUp();
        OfyService ofyService = new OfyService();
        closeable = ObjectifyService.begin();
    }

    @After
    public void tearDown() {
        closeable.close();

        helper.tearDown();
    }

    @Test
    public void test1(){
        List<User> found = User.findUserByName("test");
        assertTrue(found.isEmpty());
    }
}
