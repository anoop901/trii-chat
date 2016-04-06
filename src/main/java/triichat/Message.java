package triichat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
/**
 * One message with parent(s) and replies.
 * Created by Margret on 3/8/2016.
 */
@Entity
public class Message {
	@Id private Long id;
    private String content;
    @Load private Set<Ref<Message>> parents;
    @Load private Set<Ref<Message>> replies;
    private Date timestamp;
	private Key<User> author;
    
    private Message(){}
    
    /**
     * Creates and saves message to datastore
     * @param content
     * @param parents
     */
    public static Message createMessage(String content, Set<Message> parents, User author){
    	return new Message(content,parents,author);
    }
    
    private Message(String content, Set<Message> parents,User author ){
    	this.content = content;
    	this.parents = new HashSet<Ref<Message>>();
    	for(Message p : parents){
    		Key<Message> k = Key.create(p);
    		Ref<Message> r = Ref.create(k);
    		this.parents.add(r);
    	}
    	this.replies = new HashSet<Ref<Message>>();
    	this.timestamp = new Date();
		this.author = Key.create(author);
        OfyService.save(this);
    }
    
    public Long getId(){
    	return this.id;
    }
    
	public String getContent() {
		return content;
	}
	
	public Set<Message> getParents() {
		Set<Message> retval = new HashSet<Message>();
		for(Ref<Message> r : this.parents){
			retval.add(r.get());
		}
		return retval;
	}
	public Set<Message> getReplies() {
		Set<Message> retval = new HashSet<Message>();
		for(Ref<Message> r : this.replies){
			retval.add(r.get());
		}
		return retval;
	}
	public Date getTimeStamp() {
		return timestamp;
	}
	
	public void addReply(Message reply){
		this.replies.add(Ref.create(reply));
        OfyService.save(this);
		
	}
    
	public void addParent(Message parent){
		this.parents.add(Ref.create(parent));
        OfyService.save(this);
		
	}

	public User getAuthor(){
		return OfyService.load().key(this.author).now();
	}
}
