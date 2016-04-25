package triichat.testing.servlet;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.*;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import com.meterware.httpunit.*;
import com.meterware.servletunit.*;

import java.io.InputStream;

import junit.framework.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import triichat.db.OfyService;
import triichat.model.User;

/**
 * Created by Margret on 4/24/2016.
 * Adapted from Httpunit tutorial @ http://httpunit.sourceforge.net/doc/tutorial/task1editor-initial.html
 */
public class ExampleServletTest extends TestCase{
    private static final String ENV_EMAIL = "bozo1@clown.com";
    private static final String ENV_USER_ID = "bozo1";
    private static final String ENV_AUTH_DOMAIN = "clown1.com";
    private static final boolean ENV_IS_ADMIN = true;
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
    User theUser;
    @Before
    public void setUp() {
        helper.setUp();
        OfyService ofyService = new OfyService(); //makes sure to register classes
        closeable = ObjectifyService.begin();
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        theUser = User.createUser(gUser);
    }

    @After
    public void tearDown() {
        helper.tearDown();
        closeable.close();
    }

    public static void main( String args[] ) {
        junit.textui.TestRunner.run( suite() );
    }

    public static TestSuite suite() {
        return new TestSuite( ExampleServletTest.class );
    }

    public ExampleServletTest( String s ) {
        super( s );
    }

    /**
     * http://stackoverflow.com/a/5445161
     * @param is - InputStream
     * @return the String
     */
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public void testGetForm() throws Exception {
        ServletRunner sr = new ServletRunner( "src/main/webapp/WEB-INF/web.xml" );       // (1) use the web.xml file to define mappings
        ServletUnitClient client = sr.newClient();               // (2) create a client to invoke the application

        WebResponse response = client.getResponse("http://localhost:4567/me");// (3) invoke the servlet w/o authorization
        System.out.println(response);
        System.out.println(response.getContentType());
        System.out.println(response.getResponseMessage());
        InputStream in = response.getInputStream();
        String str = convertStreamToString(in);
        System.out.println(str);
        JSONObject json = new JSONObject(str);
        JSONArray groups = json.getJSONArray("groups");
        String id = json.getString("id");
        System.out.println("ID: " + id);
        assertTrue(id.equals(theUser.getId()));
    }
}
