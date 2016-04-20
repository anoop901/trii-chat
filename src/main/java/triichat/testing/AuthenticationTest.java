package triichat.testing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.*;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import triichat.db.OfyService;

public class AuthenticationTest {
    private static final String OAUTH_CONSUMER_KEY = "notexample.com";
    private static final String OAUTH_EMAIL = "bozo@clown.com";
    private static final String OAUTH_USER_ID = "bozo";
    private static final String OAUTH_AUTH_DOMAIN = "clown.com";
    private static final boolean OAUTH_IS_ADMIN = true;

    private static final String ENV_EMAIL = "bozo1@clown.com";
    private static final String ENV_USER_ID = "bozo1";
    private static final String ENV_AUTH_DOMAIN = "clown1.com";
    private static final boolean ENV_IS_ADMIN = true;

    private final LocalUserServiceTestConfig userConfig = new LocalUserServiceTestConfig()
            .setOAuthConsumerKey(OAUTH_CONSUMER_KEY)
            .setOAuthEmail(OAUTH_EMAIL)
            .setOAuthUserId(OAUTH_USER_ID)
            .setOAuthAuthDomain(OAUTH_AUTH_DOMAIN)
            .setOAuthIsAdmin(OAUTH_IS_ADMIN)
            .setOAuthUserId(OAUTH_USER_ID);

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
    @Before
    public void setUp() {
        helper.setUp();
        OfyService ofyService = new OfyService();
        closeable = ObjectifyService.begin();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testIsAdmin() {
        UserService userService = UserServiceFactory.getUserService();
        assertTrue(userService.isUserAdmin());
    }

    @Test
    public void testLoggedIn(){
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        assertNotNull(gUser);
    }

    @Test
    public void testEmailAddressCorrect(){
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        assertEquals(gUser.getEmail(), ENV_EMAIL);
    }

    @Test
    public void testIDExists(){
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        assertNotNull(gUser.getUserId());
    }

    @Test
    public void testUserCreation(){
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        triichat.model.User.createUser(gUser);
    }

}
