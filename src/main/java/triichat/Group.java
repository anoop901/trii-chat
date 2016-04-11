package triichat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

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
    /**
     * Creates and saves group in datastore
     * @param name
     * @param users
     * @param triis 
     */
    private Group(String name, Set<User> users, Set<Trii> triis){
    	this.name = name;
    	this.users = new HashSet<Ref<User>>();
        this.triis = new HashSet<Ref<Trii>>();
        if(users != null){
            for(User u : users){
                this.users.add(Ref.create(u));
            }
        }
    	if(triis != null){
            for(Trii t : triis){
                this.triis.add(Ref.create(t));
            }
        }

        OfyService.save(this);
    }
    
    public static Group createGroup(String name, Set<User> users, Set<Trii> triis){
    	Group retval =  new Group(name,users,triis);
        if(retval.users == null) retval.users = new HashSet<Ref<User>>();
        if(retval.triis == null) retval.triis = new HashSet<Ref<Trii>>();
        for(User u : users){
            retval.addUser(u);
            u.addGroup(retval);
        }
        return retval;
    }
    
    public void addUser(User user){
        this.users.add(Ref.create(user));
        OfyService.save(this);
    }

    public void addTrii(Trii trii){
        if(this.triis == null) this.triis = new HashSet<Ref<Trii>>();
        this.triis.add(Ref.create(trii));
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
    
    public String getName(){
    	return this.name;
    }
    
    public Long getId(){
    	return this.id;
    }

}
