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
import triichat.model.Trii;
import triichat.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Margret on 4/21/2016.
 */
public class GroupTest {

    private static final String ENV_EMAIL = "bozo1@clown.com";
    private static final String ENV_USER_ID = "bozo1";
    private static final String ENV_AUTH_DOMAIN = "clown1.com";
    private static final boolean ENV_IS_ADMIN = true;
    private static final String GROUP_NAME = "group1";

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
    Group theGroup;
    @Before
    public void setUp() {
        helper.setUp();
        OfyService ofyService = new OfyService(); //makes sure to register classes
        closeable = ObjectifyService.begin();
        //Create a user to test with
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser = userService.getCurrentUser();
        theUser = User.createUser(gUser);
        //Create a group for the user to belong to
        Set<User> users = new HashSet<>();
        users.add(theUser);
        theGroup = Group.createGroup(GROUP_NAME,users);
        assertNotNull(theGroup);
        Set<Group> groups = theUser.getGroups();
        assertFalse(groups.isEmpty());
        boolean match = false;
        for(Group g : groups){
            if(g.getId().equals(theGroup.getId())){
                match = true;
            }
        }
        assertTrue(match);
    }

    @After
    public void tearDown() {
        helper.tearDown();
        closeable.close();
    }

    @Test
    public void testGetName(){
        assertTrue(theGroup.getName().equals(GROUP_NAME));
    }

    @Test
    public void testSetName(){
        theGroup.setName("test");
        assertTrue(theGroup.getName().equals("test"));
    }

    @Test
    public void testGetTriisWhenEmpty(){
        Set<Trii> triis = theGroup.getTriis();
        assertNotNull(triis);
        assertTrue(triis.isEmpty());
    }

    @Test
    public void testGetUsers(){
        Set<User> users = theGroup.getUsers();
        assertNotNull(users);
        assertFalse(users.isEmpty());
        boolean match = false;
        for(User u : users){
            if(u.getId().equals(theUser.getId())){
                match = true;
            }
        }
        assertTrue(match);
    }

    @Test
    public void testGetUsersHaveGroup(){
        Set<Group> groups = theUser.getGroups();
        boolean match = false;
        assertNotNull(groups);
        assertFalse(groups.isEmpty());
        for(Group g : groups){
            if(g.getId().equals(theGroup.getId())){
                match = true;
            }
        }
        assertTrue(match);
    }

    @Test
    public void testCreateTrii(){
        Trii trii = Trii.createTrii("trii1", theGroup);
        assertNotNull(trii);
        Group group = trii.getGroup();
        assertTrue(group.getId().equals(theGroup.getId()));
        Set<Trii> triiSet = theGroup.getTriis();
        assertNotNull(triiSet);
        assertFalse(triiSet.isEmpty());
        boolean match = false;
        for(Trii t : triiSet){
            if(t.getId().equals(trii.getId())){
                match = true;
            }
        }
        assertTrue(match);
    }

    @Test
    public void testGetTriiByName(){
        String triiName = "trii1";
        Trii trii = Trii.createTrii(triiName, theGroup);
        assertNotNull(trii);
        Group group = trii.getGroup();
        assertTrue(group.getId().equals(theGroup.getId()));
        Trii found = theGroup.getTrii(triiName);
        assertTrue(found.getId().equals(trii.getId()));
    }

}