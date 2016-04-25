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
 * TriiServlet already tests creating a first message,
 * so MessageTest expands on that by creating more messages.
 */
public class MessageTest {

    private static final String ENV_EMAIL = "bozo1@clown.com";
    private static final String ENV_USER_ID = "bozo1";
    private static final String ENV_AUTH_DOMAIN = "clown1.com";
    private static final boolean ENV_IS_ADMIN = true;

    private static final String GROUP_NAME = "group1";

    private static final String TRII_NAME = "trii1";

    private static final String FIRST_MESSAGE_CONTENT = "FIRST";

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
        //Create a Trii to test message creation on
        theTrii = Trii.createTrii(TRII_NAME, theGroup);
        assertNotNull(theTrii);
        //Create a first message in the Trii (the root message)
        Message first = Message.createMessage(FIRST_MESSAGE_CONTENT, new HashSet<Message>(), theUser, theTrii);
        assertNotNull(first);
        Message found = theTrii.getRoot();
        assertTrue(found.getId().equals(first.getId()));
        Set<Message> foundSet = theTrii.getMessages();
        match = false;
        for(Message m : foundSet){
            if(m.getId().equals(first.getId())){
                match = true;
            }
        }
        assertTrue(match);
        Trii fromMessage = first.getTrii();
        assertNotNull(fromMessage);
        assertTrue(fromMessage.getId().equals(theTrii.getId()));
    }

    @After
    public void tearDown() {
        helper.tearDown();
        closeable.close();
    }

    //TEST HELPER FUNCTIONS
    private static Message createReply(Set<Message> parents, String content, User user, Trii trii){
        Message retval = Message.createMessage(content,parents,user,trii);
        return retval;
    }
    private static Message createReply(Message parent, String content, User user, Trii trii){
        Set<Message> parents = new HashSet<>();
        parents.add(parent);
        Message retval = Message.createMessage(content,parents,user,trii);
        return retval;
    }
    //END TEST HELPER FUNCTIONS

    @Test
    public void testCreateSecondMessageNotNull(){
        Message first = theTrii.getRoot();
        Message second = createReply(first, "Second", theUser,theTrii);
        assertNotNull(second);
    }

    @Test
    public void testCreateSecondMessageParentIsFirst(){
        Message first = theTrii.getRoot();
        Message second = createReply(first, "Second", theUser,theTrii);
        assertNotNull(second);
        Set<Message> parents = second.getParents();
        boolean match = false;
        for(Message p : parents){
            if(p.getId().equals(first.getId())){
                match = true;
            }
        }
        assertTrue(match);
    }

    @Test
    public void testCreateSecondMessageIsReplyToFirst(){
        Message first = theTrii.getRoot();
        Message second = createReply(first, "Second", theUser,theTrii);
        assertNotNull(second);
        Set<Message> replies = first.getReplies();
        boolean match = false;
        for(Message p : replies){
            if(p.getId().equals(second.getId())){
                match = true;
            }
        }
        assertTrue(match);
    }

    @Test
    public void testCreateSecondMessageTimeStampAfterFirst(){
        Message first = theTrii.getRoot();
        Message second = createReply(first, "Second", theUser,theTrii);
        assertTrue(first.getTimeStamp().before(second.getTimeStamp()));
    }

    @Test
    public void testRootGetContent(){
        assertNotNull(theTrii.getRoot().getContent().equals(FIRST_MESSAGE_CONTENT));
    }

    @Test
    public void testRootGetAuthor(){
        User author = theTrii.getRoot().getAuthor();
        assertNotNull(author);
        assertTrue(author.getId().equals(theUser.getId()));
    }

}