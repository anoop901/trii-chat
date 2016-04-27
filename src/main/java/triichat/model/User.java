package triichat.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import triichat.db.OfyService;

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
    @Index private String email;
    private String federatedId;
    private String authDomain;
    @Load private Set<Ref<User>> contacts;
    @Load private Set<Ref<Group>> groups;

    private User(){
    	this.contacts = new HashSet<Ref<User>>();
        this.groups = new HashSet<Ref<Group>>();
    }

	public Ref<User> getRef() {
        Key<User> key = Key.create(User.class, this.getId());
        Ref<User> ref = Ref.create(key);
        return ref;
    }

    public static Ref<User> getRef(String userId) {
        Key<User> key = Key.create(User.class, userId);
        Ref<User> ref = Ref.create(key);
        return ref;
    }
    /**
     * Creates and saves user
     * @param user
     * @return the newly instantiated and stored UserServlet
     */
    public static User createUser(com.google.appengine.api.users.User user) {
		//try finding first
		User made = findUser(user);
		if(made != null){
			return made;
		}
		made = new User();
		if(user.getUserId() == null){
			made.id = user.getEmail();
		}else{
			made.id = user.getUserId();
		}
        made.federatedId = user.getFederatedIdentity();
        made.authDomain = user.getAuthDomain();
        made.name = user.getNickname();
		made.email = user.getEmail();
        OfyService.save(made);
        return made;
    }

	/**
	 * Creates a user out of the google account user that's logged in currently.
	 * If the triichat user already exists, returns that
	 * @param name
	 * @return null if no google account is logged in.
     */
	public static User createUser(String name){
		UserService userService = UserServiceFactory.getUserService();
		com.google.appengine.api.users.User gUser = userService.getCurrentUser();
		if(gUser == null){return null;}
		User user = User.findUser(gUser);
		if(user == null){
			user = User.createUser(gUser);
		}
		user.setName(name);
		return user;
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
	
	void addGroup(Group group){
		this.groups.add(Ref.create(group));
        OfyService.save(this);
	}
	
	public void addContact(User contact){
		this.contacts.add(Ref.create(contact));
        OfyService.save(this);
	}

	public void removeGroup(Group group){
		Ref<Group> groupRef = Ref.create(Key.create(Group.class,group.getId()));
		if(this.groups != null) this.groups.remove(groupRef);
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
		if(toFind == null){toFind = user.getEmail();}
		Result<User> found = OfyService.ofy().load().type(User.class).id(toFind);
		if(found == null){ 
			return null;
		}else{
			return found.now();
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

	/**
	 * UNTESTED
	 * Finds user in datastore by email. Expensive operation.
	 * @param email
	 * @return Set of users since multiple users can have same email (maybe)
	 */
	public static List<User> findUserByEmail(String email){
		return OfyService.ofy().load().type(User.class).filter("email", email).list();
	}

	@Override
	public boolean equals(Object o){
		if(!o.getClass().equals(this.getClass())){
			return false;
		}
		User that = (User) o;
		if(this.getId().equals(that.getId())){
			return true;
		}
		return false;
	}
	
}
