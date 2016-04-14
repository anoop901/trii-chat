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
	@Load private Key<User> author;
	@Load private Key<Trii> holder; //Trii that holds this message

    private Message(){
		this.content = null;
		this.parents = new HashSet<>();
		this.replies = new HashSet<>();
		this.timestamp = null;
		this.author = null;
		this.holder = null;
	}
    
    /**
     * Creates and saves message to datastore and associated trii
     * @param content
     * @param parents, this set can be empty but cannot be null
	 * @param author
	 * @param trii
     */
    public static Message createMessage(String content, Set<Message> parents, User author, Trii trii){
		Message retval = new Message(content,parents,author,trii);
		if(!parents.isEmpty()){
			for(Message p : parents){
				p.addReply(retval);
			}
		}
		trii.addMessage(retval);
		OfyService.save(retval);
		return retval;
    }
    
    private Message(String content, Set<Message> parents,User author,Trii trii ){
    	this.content = content;
    	this.parents = new HashSet<Ref<Message>>();
		if(!parents.isEmpty()) {
			for (Message p : parents) {
				if (p != null) {
					Key<Message> k = Key.create(p);
					Ref<Message> r = Ref.create(k);
					this.parents.add(r);
				}
			}
		}
    	this.replies = new HashSet<Ref<Message>>();
    	this.timestamp = new Date();
		this.author = Key.create(author);
		this.holder = Key.create(Trii.class, trii.getId());
    }
    
    public Long getId(){
    	return this.id;
    }
    
	public String getContent() {
		return content;
	}

	public Trii getTrii(){
		return OfyService.getTrii(this.holder.getId());
	}

	public Set<Message> getParents() {
		Set<Message> retval = new HashSet<Message>();
		if(this.parents == null) this.replies = new HashSet<Ref<Message>>();
		for(Ref<Message> r : this.parents){
			retval.add(r.get());
		}
		return retval;
	}

	/**
	 * Gets all immeadiate replies
	 * @return
     */
	public Set<Message> getReplies() {
		Set<Message> retval = new HashSet<Message>();
		if(this.replies == null) this.replies = new HashSet<Ref<Message>>();
		for(Ref<Message> r : this.replies){
			retval.add(r.get());
		}
		return retval;
	}
	public Date getTimeStamp() {
		return timestamp;
	}
	
	public void addReply(Message reply){
		if(this.replies == null) this.replies = new HashSet<Ref<Message>>();
		this.replies.add(Ref.create(reply));
        OfyService.save(this);
		
	}
    
	public void addParent(Message parent){
		if(this.parents == null) this.replies = new HashSet<Ref<Message>>();
		this.parents.add(Ref.create(parent));
        OfyService.save(this);
		
	}

	public User getAuthor(){
		return OfyService.load().key(this.author).now();
	}

    /**
     * Returns a set containing all descendant messages
     * Adds the immediate children messages and calls getAllReplies for each immediate reply
	 * TODO: Make this actually get all kids not just first kid's kid etc.
     * @return
     */
	public Set<Message> getAllReplies() {
		Set<Message> retval = new HashSet<Message>();

        for(Ref<Message> r : this.replies)
        {
            Message m = r.get();
            // add the immediate child and all its child messages
            retval.add(m);

            Set<Message> childrenReplies = m.getAllReplies();
            retval.addAll(childrenReplies);
        }

        return retval;
	}
}
