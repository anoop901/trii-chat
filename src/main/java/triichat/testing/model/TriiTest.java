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
import triichat.model.Message;
import triichat.model.Trii;
import triichat.model.User;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Margret on 4/21/2016.
 * Creation of a Trii in a Group is tested in GroupTest.
 * TriiTest tests correctness of that creation.
 */
public class TriiTest {

    private static final String ENV_EMAIL = "bozo1@clown.com";
    private static final String ENV_USER_ID = "bozo1";
    private static final String ENV_AUTH_DOMAIN = "clown1.com";
    private static final boolean ENV_IS_ADMIN = true;

    private static final String GROUP_NAME = "group1";

    private static final String TRII_NAME = "trii1";

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
    Trii theTrii;
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
        theTrii = Trii.createTrii(TRII_NAME, theGroup);
        assertNotNull(theTrii);
    }

    @After
    public void tearDown() {
        helper.tearDown();
        closeable.close();
    }

    @Test
    public void testGetName(){
        assertTrue(theTrii.getName().equals(TRII_NAME));
    }

    @Test
    public void testSetName(){
        theTrii.setName("test");
        assertTrue(theTrii.getName().equals("test"));
    }

    @Test
    public void testGetGroup(){
        Group group = theTrii.getGroup();
        assertTrue(group.getId().equals(theGroup.getId()));
        Trii foundTrii = theGroup.getTrii(TRII_NAME);
        assertNotNull(foundTrii);
        assertTrue(foundTrii.getId().equals(theTrii.getId()));
    }

    @Test
    public void testGetMessagesEmpty(){
        Set<Message> messages = theTrii.getMessages();
        assertNotNull(messages);
        assertTrue(messages.isEmpty());
    }

    @Test
    public void testGetRootEmpty(){
        assertNull(theTrii.getRoot());
    }

    @Test
    public void testCreateFirstMessage(){
        Message first = Message.createMessage("First", new HashSet<Message>(), theUser, theTrii);
        assertNotNull(first);
        Message found = theTrii.getRoot();
        //Check that root==first
        assertTrue(found.getId().equals(first.getId()));
        //Check that first is in set of messages
        Set<Message> foundSet = theTrii.getMessages();
        boolean match = false;
        for(Message m : foundSet){
            if(m.getId().equals(first.getId())){
                match = true;
            }
        }
        assertTrue(match);
        //Check that Message has correct reference to Trii
        Trii fromMessage = first.getTrii();
        assertNotNull(fromMessage);
        assertTrue(fromMessage.getId().equals(theTrii.getId()));
    }

}