package triichat.model;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import triichat.db.OfyService;

import java.util.*;

/**
 * Created by matthewzhan on 3/8/16.
 */
@Entity
public class Trii {
	@Id private Long id;
    String name;
    @Load Ref<Message> root;
    @Load Set<Ref<Message>> all;
    @Load Ref<Group> group; //that this trii is part of

    private Trii(){
        this.name = null;
        this.group = null;
        this.all = new HashSet<Ref<Message>>();
        this.root = null;
    }
    
    /**
     * Creates and saves Trii in datastore. Can use null firstMessage to create empty trii.
     * @param name
     * @param group
     *
     */
    public static Trii createTrii(String name, Group group){
    	Trii retval =  new Trii(name, group);
        group.addTrii(retval);
        return retval;
    }
    private Trii(String name, Group group)
    {
        this.name = name;
        this.all = new HashSet<Ref<Message>>();
        this.root = null;
        this.group = Ref.create(Key.create(Group.class, group.getId()));
        OfyService.save(this);
    }

    public Group getGroup(){
        if(this.group == null){return null;}
        else{return this.group.get();}
    }

    public Message getRoot()
    {
        if(this.root == null) return null;
        else return root.get();
    }
    
    public String getName(){
    	return name;
    }
    
    public Long getId(){
    	return id;
    }
    
    /**
     * Sets and saves name with Trii
     * @param name
     */
    public void setName(String name){
    	this.name = name;
        OfyService.save(this);
    }

    /**
     * Returns a Set containing all messages within this Trii.
     * Unsorted
     * @return
     */
    public Set<Message> getMessages()
    {
        Set<Message> retval = new HashSet<Message>();
        if(this.all == null){
            this.all = new HashSet<>();
        }
        for(Ref<Message> r : this.all){
            retval.add(r.get());
        }
        return retval;
    }

    /**
     * Adds a message to a trii
     * @param m
     */
    public void addMessage(Message m){
        Key<Message> key = Key.create(Message.class, m.getId());
        Ref<Message> ref = Ref.create(key);
        if(this.all == null){
            this.all = new HashSet<>();
        }
        if(this.all.contains(ref)){return;}//already has it
        this.all.add(ref);
        if(this.root == null){
            this.root = ref;
        }
        OfyService.save(this);
        return;
    }

}
