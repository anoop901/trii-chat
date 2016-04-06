package triichat;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by matthewzhan on 3/8/16.
 */
@Entity
public class Trii {
	@Id private Long id;
    String name;
    @Load Ref<Message> root;

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
     * Returns a Set containing all messages within this Trii
     * @return
     */
    public Set<Message> getMessages()
    {
        Set<Message> retval = this.root.get().getAllReplies();
        retval.add(this.root.get());
        return retval;
    }


}
