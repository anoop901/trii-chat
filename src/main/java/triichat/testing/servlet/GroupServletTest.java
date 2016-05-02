package triichat.testing.servlet;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.*;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import junit.framework.TestCase;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import triichat.db.OfyService;
import triichat.model.Group;
import triichat.model.User;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Tests the MeServlet Functionality
 * Created by Margret on 4/24/2016.
 * Adapted from Httpunit tutorial @ http://httpunit.sourceforge.net/doc/tutorial/task1editor-initial.html
 */
public class GroupServletTest extends TestCase{
    private static final String LOCALHOST = "http://localhost:4567";
    private static final String GROUP_SERVLET_MAPPING = "/group";
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
    public void testGetGroup() throws Exception{
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        User user = User.createUser(gUser);
        assertNotNull(user);
        Set<User> users = new HashSet<>(); users.add(user);
        String groupName = "GROUP1";
        Group group = Group.createGroup(groupName,users);
        String req = LOCALHOST + GROUP_SERVLET_MAPPING + "?id=" + group.getId();
        WebResponse response = client.getResponse(req);// (3) invoke the servlet w/o authorization
        String str = convertStreamToString(response.getInputStream());
        JSONObject json = new JSONObject(str);
        Long id = json.getLong("id");
        assertTrue(id.equals(group.getId()));
        String name = json.getString("name");
        assertTrue(name.equals(groupName));

    }

    @Test
    public void testMakeGroup() throws Exception{
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        User user = User.createUser(gUser);
        assertNotNull(user);
        Set<User> users = new HashSet<>(); users.add(user);
        String groupName = "GROUP1";
        String req = LOCALHOST + GROUP_SERVLET_MAPPING;
        WebRequest webRequest = new PostMethodWebRequest(req);
        webRequest.setParameter("name", groupName);
        WebResponse response = servletRunner.getResponse(webRequest);
        String str = convertStreamToString(response.getInputStream());
        assertNotNull(str);
        assertFalse(str.isEmpty());
        JSONObject json = new JSONObject(str);
        String retrievedName = json.getString("name");
        assertTrue(retrievedName.equals(groupName));

        //check updated user
        Group group = OfyService.loadGroup(json.getLong("id"));
        assertNotNull(group);
        Set<User> retUsers = group.getUsers();
        assertNotNull(retUsers);
        assertTrue(retUsers.size() == 1);
        assertTrue(retUsers.contains(user));
    }

    @Test
    public void testMakeRandomNumberOfGroups() throws Exception{
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        User user = User.createUser(gUser);
        assertNotNull(user);
        Set<User> users = new HashSet<>(); users.add(user);
        String req = LOCALHOST + GROUP_SERVLET_MAPPING;
        ArrayList<Long> allGroupIds = new ArrayList<>();
        for(int i = 0; i < max; i++){
            String groupName = "GROUP" + i;
            WebRequest webRequest = new PostMethodWebRequest(req);
            webRequest.setParameter("name", groupName);
            WebResponse response = servletRunner.getResponse(webRequest);
            String str = convertStreamToString(response.getInputStream());
            //Test that each group created successfully
            assertNotNull(str);
            assertFalse(str.isEmpty());
            JSONObject json = new JSONObject(str);
            String retrievedName = json.getString("name");
            assertTrue(retrievedName.equals(groupName));
            Long id = json.getLong("id");
            assertNotNull(id);
            allGroupIds.add(id);
        }
        //Test that all groups do exist
        user = User.createUser(gUser);
        Set<Group> usersGroups = user.getGroups();
        for(Group g : usersGroups){
            if(allGroupIds.contains(g.getId())){
                allGroupIds.remove(g.getId());
            }else{
                fail();//since the user has a group that it shouldn't have
            }
        }
        assertTrue(allGroupIds.isEmpty());//all ids should've been removed
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