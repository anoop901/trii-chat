package triichat.testing.servlet;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.*;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import junit.framework.TestCase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import triichat.db.OfyService;
import triichat.model.Group;
import triichat.model.Trii;
import triichat.model.User;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Tests the MeServlet Functionality
 * Created by Margret on 4/24/2016.
 * Adapted from Httpunit tutorial @ http://httpunit.sourceforge.net/doc/tutorial/task1editor-initial.html
 */
public class TriiServletTest extends TestCase{
    private static final String LOCALHOST = "http://localhost:4567";
    private static final String TRII_SERVLET_MAPPING = "/trii";
    private static final int TEST_MAX = 25;
    private static final String WEBXML_LOC = "src/main/webapp/WEB-INF/web.xml";
    private static final String ENV_EMAIL = "bozo1@clown.com";
    private static final String ENV_USER_ID = "bozo1";
    private static final String ENV_AUTH_DOMAIN = "clown1.com";
    private static final boolean ENV_IS_ADMIN = false;
    private final LocalUserServiceTestConfig userConfig = new LocalUserServiceTestConfig();
    private final LocalDatastoreServiceTestConfig dbConfig = new LocalDatastoreServiceTestConfig();
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(dbConfig,userConfig,new LocalDatastoreServiceTestConfig(),
                    new LocalTaskQueueTestConfig(),
                    new LocalBlobstoreServiceTestConfig(),
                    new LocalUserServiceTestConfig())
                    .setEnvIsAdmin(ENV_IS_ADMIN)
                    .setEnvIsLoggedIn(true)
                    .setEnvEmail(ENV_EMAIL)
                    //.setEnvModuleId(ENV_USER_ID)
                    .setEnvVersionId(ENV_USER_ID)
                    .setEnvAppId(ENV_USER_ID)
                    .setEnvAuthDomain(ENV_AUTH_DOMAIN)
            ;
    Closeable closeable;
    ServletUnitClient client;
    ServletRunner servletRunner;
    java.util.Random rand;
    int max=0;
    @Before
    public void setUp() {
        helper.setUp();
        OfyService ofyService = new OfyService(); //makes sure to register classes
        closeable = ObjectifyService.begin();
        try {
            servletRunner = new ServletRunner(WEBXML_LOC );// (1) use the web.xml file to define mappings
            client = servletRunner.newClient();               // (2) create a client to invoke the application
        } catch (Exception e) {
           fail(e.toString());
        }
        rand = new java.util.Random();
        max = rand.nextInt(TEST_MAX);
    }

    @After
    public void tearDown() {
        helper.tearDown();
        closeable.close();
    }

    @Test
    public void testGetTrii() throws Exception{
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        User user = User.createUser(gUser);
        assertNotNull(user);

        Set<User> users = new HashSet<>(); users.add(user);
        String groupName = "GROUP1";
        Group group = Group.createGroup(groupName,users);
        assertNotNull(group);

        String triiName = "TRII1";
        Trii trii = Trii.createTrii(triiName, group);

        WebRequest req = new GetMethodWebRequest(LOCALHOST + TRII_SERVLET_MAPPING);
        req.setParameter("id", trii.getId().toString());
        WebResponse resp = client.getResponse(req);
        JSONObject json = new JSONObject(convertStreamToString(resp.getInputStream()));
        Long retId = json.getLong("id");
        String retName = json.getString("name");
        JSONArray retMessages = json.getJSONArray("messages");

        assertTrue(retId.equals(trii.getId()));
        assertTrue(retName.equals(trii.getName()));
        assertTrue(retMessages.length() == 0);
    }


    /**
     * http://stackoverflow.com/a/5445161
     * @param is - InputStream
     * @return the String
     */
    static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
