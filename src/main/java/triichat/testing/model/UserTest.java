package triichat.testing.model;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.*;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import triichat.db.OfyService;
import triichat.model.Group;
import triichat.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Margret on 4/21/2016.
 */
public class UserTest {

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


    @Test
    public void testSetName() throws Exception {
        theUser.setName("test");
        assertTrue(theUser.getName().equals("test"));
    }

    @Test
    public void testGetEmail() throws Exception {
        assertTrue(theUser.getEmail().equals(ENV_EMAIL));
    }

    @Test
    public void testSetEmail() throws Exception {
        theUser.setEmail("test@email.com");
        assertTrue(theUser.getEmail().equals("test@email.com"));
    }


    @Test
    public void testGetFederatedId() throws Exception {
        assertNotNull(theUser.getFederatedId());
    }

    @Test
    public void testGetAuthDomain() throws Exception {
        assertNotNull(theUser.getAuthDomain());
    }

    @Test
    public void testGetContacts() throws Exception {
        Set<User> found = theUser.getContacts();
        assertNotNull(found);
        assertTrue(found.isEmpty());
    }

    @Test
    public void testAddContact() throws Exception {
        fail();
    }

    @Test
    public void testFindUser() throws Exception {
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        User found = User.findUser(gUser);
        assertNotNull(found);
    }

    @Test
    public void testFindUserByName() throws Exception {
        List<User> found = User.findUserByName(theUser.getName());
        assertFalse(found.isEmpty());
        boolean match = false;
        for(User u : found){
            if(u.getId().equals(theUser.getId())){
                match = true;
            }
        }
        assertTrue(match);
    }

    @Test
    public void testFindUserByEmail() throws Exception {
        List<User> found = User.findUserByEmail(theUser.getEmail());
        assertFalse(found.isEmpty());
        boolean match = false;
        for(User u : found){
            if(u.getId().equals(theUser.getId())){
                match = true;
            }
        }
        assertTrue(match);
    }

    @Test
    public void testCreateGroup(){
        Set<User> users = new HashSet<>();
        users.add(theUser);
        Group made = Group.createGroup("group1",users);
        assertNotNull(made);
        Set<Group> groups = theUser.getGroups();
        assertFalse(groups.isEmpty());
        boolean match = false;
        for(Group g : groups){
            if(g.getId().equals(made.getId())){
                match = true;
            }
        }
        assertTrue(match);
        users = made.getUsers();
        match = false;
        for(User u : users){
            if(u.getId().equals(theUser.getId())){
                match = true;
            }
        }
        assertTrue(match);
    }

}