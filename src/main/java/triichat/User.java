package triichat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.cmd.Query;
/**
 * Created by Margret on 3/8/2016.
 */
@Entity
public class User {
	/*
	 * Adapter for com.google.appending.api.users.UserServlet to add our functionality
	 */
	@Id private String id;	//Objectify id uses user id from com.google.appengine.api.users.UserServlet
	//following fields can be gotten from com.google.appengine.api.users.UserServlet
    @Index private String name;
    private String email;
    private String federatedId;
    private String authDomain;
    @Load private Set<Ref<User>> contacts;
    @Load private Set<Ref<Group>> groups;

    private User(){
    	this.contacts = new HashSet<Ref<User>>();
        this.groups = new HashSet<Ref<Group>>();
    }
    /**
     * Creates and saves user
     * @param user
     * @return the newly instantiated and stored UserServlet
     */
    public static User createUser(com.google.appengine.api.users.User user) {
        User made = new User();
        made.id = user.getUserId(); 
        made.federatedId = user.getFederatedIdentity();
        made.authDomain = user.getAuthDomain();
        made.name = user.getNickname();
        OfyService.save(made);
        return made;
    }
    
    public void setName(String name){
    	this.name = name;
        OfyService.save(this);
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
        OfyService.save(this);
	}

	public String getName() {
		return name;
	}
	
	public String getFederatedId() {
		return federatedId;
	}
	public String getAuthDomain() {
		return authDomain;
	}
	public Set<Group> getGroups(){
		Set<Group> retval = new HashSet<Group>();
		for(Ref<Group> r : this.groups){
			retval.add(r.get());
		}
		return retval;
	}
	
	public Set<User> getContacts(){
		Set<User> retval = new HashSet<User>();
		for(Ref<User> r : this.contacts){
			retval.add(r.get());
		}
		return retval;
	}
	
	public void addGroup(Group group){
		this.groups.add(Ref.create(group));
        OfyService.save(this);
	}
	
	public void addContact(User contact){
		this.contacts.add(Ref.create(contact));
        OfyService.save(this);
	}
	
	/**
	 * Returns UserServlet id which is same as com.google.appengine.api.users.UserServlet.getUserId()
	 * @return
	 */
	public String getId(){
		return this.id;
	}
    
	/**
	 * Finds a user in datastore 
	 * @param user
	 * @return UserServlet
	 */
	public static User findUser(com.google.appengine.api.users.User user){
		String toFind = user.getUserId();
		Ref<User> found = OfyService.ofy().load().type(User.class).id(toFind);
		if(found == null){ 
			return null;
		}else{
			return found.get();
		}
	}
	
	/**
	 * UNTESTED
	 * Finds user in datastore by name. Expensive operation.
	 * @param name
	 * @return Set of users since multiple users can have same name
	 */
	public static List<User> findUserByName(String name){
		List<User> found = OfyService.ofy().load().type(User.class).filter("name",name).list();
		return found;
	}
	
}
