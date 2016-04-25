package triichat.testing.servlet;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.*;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import triichat.db.OfyService;
import triichat.model.Group;
import triichat.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Tests the MeServlet Functionality
 * Created by Margret on 4/24/2016.
 * Adapted from Httpunit tutorial @ http://httpunit.sourceforge.net/doc/tutorial/task1editor-initial.html
 */
public class MeServletTest extends TestCase{
    private static final String LOCALHOST = "http://localhost:4567";
    private static final String ME_SERVLET_MAPPING = "/me";
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
    java.util.Random rand;
    int max=0;
    @Before
    public void setUp() {
        helper.setUp();
        OfyService ofyService = new OfyService(); //makes sure to register classes
        closeable = ObjectifyService.begin();
        try {
            ServletRunner sr = new ServletRunner(WEBXML_LOC );// (1) use the web.xml file to define mappings
            client = sr.newClient();               // (2) create a client to invoke the application
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
    public void testNoUserLoggedIn() throws Exception {
        WebResponse response = client.getResponse(LOCALHOST + ME_SERVLET_MAPPING);// (3) invoke the servlet w/o authorization
        String str = convertStreamToString(response.getInputStream());
        assertTrue(str.isEmpty());
    }

    @Test
    public void testMatchingUserId() throws Exception {
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        User user = User.createUser(gUser);

        WebResponse response = client.getResponse(LOCALHOST + ME_SERVLET_MAPPING);// (3) invoke the servlet w/o authorization
        String str = convertStreamToString(response.getInputStream());
        JSONObject json = new JSONObject(str);

        String id = json.getString("id");
        assertTrue(id.equals(user.getId()));
    }

    @Test
    public void testNewUserHasNoGroups() throws Exception{
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        User user = User.createUser(gUser);

        WebResponse response = client.getResponse(LOCALHOST + ME_SERVLET_MAPPING);// (3) invoke the servlet w/o authorization
        String str = convertStreamToString(response.getInputStream());
        JSONObject json = new JSONObject(str);
        JSONArray groups = json.getJSONArray("groups");
        assertTrue(groups.length() == 0);
    }

    @Test
    public void testUserHasAGroup() throws Exception{
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        User user = User.createUser(gUser);
        assertNotNull(user);
        Set<User> users = new HashSet<>(); users.add(user);
        Group group = Group.createGroup("group",users);
        assertNotNull(group);

        WebResponse response = client.getResponse(LOCALHOST + ME_SERVLET_MAPPING);// (3) invoke the servlet w/o authorization
        String str = convertStreamToString(response.getInputStream());
        JSONObject json = new JSONObject(str);
        JSONArray groups = json.getJSONArray("groups");
        assertTrue(groups.length() == 1);
        Long id = groups.getLong(0);
        assertTrue(id.equals(group.getId()));
    }

    @Test
    public void testUserHasMultipleGroups() throws Exception{
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        User user = User.createUser(gUser);
        assertNotNull(user);
        Set<User> users = new HashSet<>(); users.add(user);

        Group groups[] = new Group[max];
        for(int i = 0; i < max; i++){
            groups[i] = Group.createGroup("Group"+i, users);
        }

        WebResponse response = client.getResponse(LOCALHOST + ME_SERVLET_MAPPING);// (3) invoke the servlet w/o authorization
        String str = convertStreamToString(response.getInputStream());
        JSONObject json = new JSONObject(str);
        JSONArray groups_json = json.getJSONArray("groups");
        assertTrue(groups_json.length() == max);
        //Make sure ids returned match group ids of created groups
        for(int i = 0; i < max; i++){//For each group id, find match in groups[]
            Long id = groups_json.getLong(i);
            boolean match = false;
            for(int j = 0; j < max; j++){
                if(id.equals(groups[j].getId())){
                    match = true;
                }
            }
            assertTrue(match);
        }

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
