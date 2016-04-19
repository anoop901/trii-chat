package triichat.db;

import java.util.List;
import java.util.Set;

import com.googlecode.objectify.*;
import com.googlecode.objectify.cmd.Loader;
import triichat.model.Group;
import triichat.model.Message;
import triichat.model.Trii;
import triichat.model.User;

/* Usage

 * Get the ofy() object
 Objectify objectify = OfyService.ofy();

 * Saving
 objectify.save().entity(greeting).now();

 * Loading
 List<Greeting> msgs = objectify.load().type(Greeting.class).list();

 * Filters
 Iterable<Key<Greeting>> keys = objectify.load().type(Greeting.class).filter("email", user.getEmail()).keys();

 * Deleting
 objectify.delete().keys(keys);

 */
/**
 * 
 * Objectify adapter used to register all classes that will be stored.
 * @author Matthew Zhan, Margret Tumbokon
 *
 */
public class OfyService {
	/*
	 * Adapter pattern or facade?
	 */
    static {
        // register all classes here
        ObjectifyService.register(Group.class);
        ObjectifyService.register(Message.class);
        ObjectifyService.register(Trii.class);
        ObjectifyService.register(User.class);
    }

    /**
     * Use in place of ObjectifyService.ofy()
     */
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }
    /**
     * Use in place of ObjectifyService.factory()
     */
    public static ObjectifyFactory factory(){
        return ObjectifyService.factory();
    }
    
    /**
     * Saves an entity.
     * @param entity
     * @return true if successful save
     */
    public static boolean save(Object entity){
        OfyService.ofy().save().entity(entity).now();
        if(OfyService.ofy().load().entity(entity).now() != null) return true;
        else return false;
    }
   
    /**
     * @return Loader for which you can start command sequence
     */
    public static Loader load(){
    	return OfyService.ofy().load();
    }
    
    /**
     * @see Objectify.ofy().load().type(__).filter(__,__).list()
     * @param type
     * @param condition
     * @param value
     * @return
     */
    public static List<Object> loadWithFilter(Class type, String condition, Object value){
    	return OfyService.load().type(type).filter(condition, value).list();
    }

    public static Group getGroup(Long id) {
        return ofy().load().type(Group.class).id(id).now();
    }

    public static Message getMessage(Long id){
        return ofy().load().type(Message.class).id(id).now();
    }

    public static Trii getTrii(Long id){
        return ofy().load().type(Trii.class).id(id).now();
    }

    public static User getUser(String id){
        return ofy().load().type(User.class).id(id).now();
    }

    public static void deleteGroup(Long id) {

        Group group = getGroup(id);
        //don't delete users that are part of this group
        //delete triis
        for(Trii t : group.getTriis()){
            OfyService.deleteTrii(t.getId());
        }

        // Remove references to this group from users in it
        // TODO: test groups are properly removed from user class
        for(User u: group.getUsers()) {
            u.removeGroup(group);
        }

        OfyService.ofy().delete().type(Group.class).id(id).now();
    }

    /**
     * Deletes a message and Trii reference to it and all message references to it
     * @param id
     */
    public static void deleteMessage(Long id){
        //TODO: Test reference deletion for trii, parents, and replies
        Message message = OfyService.getMessage(id);

        // Remove Trii reference
        Trii trii = message.getTrii();
        trii.removeMessage(message);

        // Remove reference to other messages: parents and replies
        message.unlink();

        OfyService.ofy().delete().type(Message.class).id(id).now();
    }

    /**
     * Deletes given trii and all messages inside it
     * @param id
     */
    public static void deleteTrii(Long id) {
        Trii trii = OfyService.getTrii(id);

        // remove the reference to this trii in the Group
        Group group = trii.getGroup();
        group.removeTrii(id);

        // remove all messages from this trii
        for(Message m : trii.getMessages()) {
            OfyService.deleteMessage(m.getId());
        }
    }


    public static void deleteUser(String id){
        User user = OfyService.getUser(id);
        for(Group g : user.getGroups()){
            g.removeUser(user.getId());
        }
        OfyService.ofy().delete().type(User.class).id(id).now();
    }
}
