package triichat;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

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
    private Trii(){}
    
    /**
     * Creates and saves Trii in datastore
     * @param name
     * @param firstMessage 
     */
    public static Trii createTrii(String name, Message firstMessage){
    	return new Trii(name, firstMessage);
    }
    private Trii(String name, Message firstMessage)
    {
        this.name = name;
        this.root = Ref.create(firstMessage);
        this.all = new TreeSet<>();
        all.add(root);
        OfyService.save(this);
    }

    public Message getRoot()
    {
        return root.get();
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
        Set<Message> retval = new TreeSet<Message>();
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
        if(this.all.contains(ref)){return;}//already has it
        this.all.add(ref);
        OfyService.save(this);
        return;
    }

}
