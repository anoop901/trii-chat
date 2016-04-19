package triichat.db;

import java.util.List;

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
 * Objectify facade used to register all classes that will be stored.
 * Servlets should not use this class. If servlets want to load instances of model classes, use model's static methods.
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
     * see Objectify.ofy().load().type(__).filter(__,__).list()
     * @param type
     * @param condition
     * @param value
     * @return
     */
    public static List<Object> loadWithFilter(Class type, String condition, Object value){
    	return OfyService.load().type(type).filter(condition, value).list();
    }

    public static Group loadGroup(Long id) {
        return ofy().load().type(Group.class).id(id).now();
    }

    public static Message loadMessage(Long id){
        return ofy().load().type(Message.class).id(id).now();
    }

    public static Trii loadTrii(Long id){
        return ofy().load().type(Trii.class).id(id).now();
    }

    public static User loadUser(String id){
        return ofy().load().type(User.class).id(id).now();
    }

    public static void deleteGroup(Long id) {
        // TODO: Remove references to this group from users in it
        Group group = loadGroup(id);

        //don't delete users that are part of this group
        //delete triis
        for(Trii t : group.getTriis()){
            OfyService.deleteTrii(t.getId(),id);
        }

        // Remove references to this group from users in it
        // TODO: test groups are properly removed from user class
        for(User u: group.getUsers()) {
            u.removeGroup(group);
        }

        OfyService.ofy().delete().type(Group.class).id(id).now();
    }

    /**
     * Deletes message, but needs trii that it belongs to
     * @param id
     */
    public static void deleteMessage(Long id, Long triiId){
        //TODO: Remove refernce to this message from parents and replies

        OfyService.ofy().delete().type(Message.class).id(id).now();
    }

    /**
     * Deletes given trii and all messages inside it
     * @param id
     * @param groupID - Group that trii belongs to
     */
    public static void deleteTrii(Long id, Long groupID){
        //delete all messages in trii
        Trii trii = OfyService.loadTrii(id);
        for(Message m : trii.getMessages()){
            OfyService.deleteMessage(m.getId(),id);
        }
        //Remove reference to trii from group that includes it
        //search all groups for ref to this trii
        Group group = OfyService.loadGroup(groupID);
        group.removeTrii(id);
        //delete trii from datastore
        OfyService.ofy().delete().type(Trii.class).id(id).now();
    }

    public static void deleteUser(String id){
        User user = OfyService.loadUser(id);
        for(Group g : user.getGroups()){
            g.removeUser(user.getId());
        }
        OfyService.ofy().delete().type(User.class).id(id).now();
    }

}
