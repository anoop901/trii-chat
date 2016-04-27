package triichat.model;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import triichat.db.OfyService;

/**
 * Created by Margret on 3/8/2016.
 */
@Entity
public class Group {
	@Id Long id = null;
	private String name;
	@Load private Set<Ref<User>> users;
	@Load private Set<Ref<Trii>> triis;

    private Group(){}
    /*
     * Creates and saves group in datastore
     * @param name
     * @param users
     */
    private Group(String name, Set<User> users){
    	this.name = name;
    	this.users = new HashSet<Ref<User>>();
        this.triis = new HashSet<Ref<Trii>>();
        if(users != null) {
            for (User u : users) {
                this.users.add(Ref.create(u));
            }
        }
        OfyService.save(this);
    }

    /**
     * Creates and saves group in datastore.
     * Also updates users that are in this group.
     * @param name
     * @param users
     * @return
     */
    public static Group createGroup(String name, Set<User> users){
        if(users.isEmpty()) throw new IllegalArgumentException("No users for this group");
    	Group retval =  new Group(name,users);
        if(retval.triis == null) retval.triis = new HashSet<Ref<Trii>>();
        for(User u : users){
            retval.addUser(u);
            u.addGroup(retval);
        }
        return retval;
    }

    /**
     * Adds existing user to this group
     * @param user
     */
    public void addUser(User user){
        this.users.add(user.getRef());
        user.addGroup(this);
        OfyService.save(this);
    }

    /**
     * Adds existing Trii to this group
     * @param trii
     */
    void addTrii(Trii trii){
        if(this.triis == null) this.triis = new HashSet<Ref<Trii>>();

        if(this.triis.add(trii.getRef()))
            OfyService.save(this);
    }



    public Set<Trii> getTriis(){
        Set<Trii> retval = new HashSet<Trii>();
        if (this.triis != null) {
            for(Ref<Trii> r : this.triis){
                retval.add(r.get());
            }
        }
        return retval;
    }

    /**
     * Gets a trii with the given name or returns null. 
     * If multiple triis with same name, then returns first one that matches.
     * @param name
     * @return
     */
    public Trii getTrii(String name){
        if(this.triis == null){return null;}
        for(Ref<Trii> r : this.triis){
        	Trii t = r.get();
        	if(t.getName().equalsIgnoreCase(name)){
        		return t;
        	}
        }
        return null;
    }

    public Set<User>  getUsers(){
        Set<User> retval = new HashSet<User>();
        if (this.users != null) {
            for(Ref<User> r : this.users){
                retval.add(r.get());
            }
        }
        return retval;
    }

    public void setName(String name){
        this.name = name;
        OfyService.save(this);
    }
    public String getName(){
    	return this.name;
    }
    
    public Long getId(){
    	return this.id;
    }

    public void removeTrii(Long id){
        if(this.triis.remove(Trii.getRef(id)))
            OfyService.save(this);
    }

    /**
     * Removes existing user to this group
     * @param user
     */
    public void removeUser(User user){
        if(this.users.remove(user.getRef())) {
            OfyService.save(this);
            user.removeGroup(this);
            OfyService.save(user);
        }
    }

    public void removeUser(String id){
        if(this.users.remove(User.getRef(id))) {
            OfyService.save(this);
            User user = OfyService.loadUser(id);
            user.removeGroup(this);
            OfyService.save(user);
        }
    }

    @Override
    public boolean equals(Object o){
        if(!o.getClass().equals(this.getClass())){
            return false;
        }
        Group that = (Group) o;
        if(that.getId().equals(this.getId())){
            return true;
        }
        return false;
    }
}
