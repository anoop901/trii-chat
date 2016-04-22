package triichat.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import triichat.db.OfyService;

/**
 * One message with parent(s) and replies.
 * Created by Margret on 3/8/2016.
 */
@Entity
public class Message implements Comparable{
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
		OfyService.save(this);
    }
    
    public Long getId(){
    	return this.id;
    }
    
	public String getContent() {
		return content;
	}

	public Trii getTrii(){
		return OfyService.loadTrii(this.holder.getId());
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
	 * Gets all immediate replies
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

		if(this.replies.add(reply.getRef()))
            OfyService.save(this);
	}

    public void removeReply(Message reply) {
        if(this.replies == null) this.replies = new HashSet<Ref<Message>>();

        if(this.replies.remove(reply.getRef()))
            OfyService.save(this);
    }

    public Ref<Message> getRef() {
        Key<Message> key = Key.create(Message.class, this.getId());
        Ref<Message> ref = Ref.create(key);

        return ref;
    }
    
	public void addParent(Message parent){
        // create the HashSet if it does not exist
		if(this.parents == null)
            this.replies = new HashSet<Ref<Message>>();

		if(this.parents.add(parent.getRef()))
            OfyService.save(this);
	}

    public void removeParent(Message parent) {
        if(this.parents == null)
            this.replies = new HashSet<Ref<Message>>();

        if(this.parents.remove(parent.getRef()))
            OfyService.save(this);
    }

    /**
     * remove references to this Message from all parent and reply messages
     */
    public void unlink() {
        for (Ref<Message> r : this.replies) {
            Message reply = r.get();
            reply.removeParent(this);
            OfyService.save(reply);
        }

        for(Ref<Message> p : this.parents) {
            Message parent = p.get();
            parent.removeReply(this);
            OfyService.save(parent);
        }
    }

	public User getAuthor(){
		return OfyService.load().key(this.author).now();
	}

    /**
     * Returns a set containing all descendant messages
     * Adds the immediate children messages and calls getAllReplies for each immediate reply
     * @return
     */
	public Set<Message> getAllReplies() {
        // TODO: test if this works. may be deprecated by getMessages in Trii class
		Set<Message> retval = new HashSet<Message>();
		retval.add(this);
        for(Ref<Message> r : this.replies)
        {
            Message m = r.get();
            Set<Message> childrenReplies = m.getAllReplies();
            retval.addAll(childrenReplies);
        }

        return retval;
	}

	@Override
	public boolean equals(Object o){
		Message that = (Message) o;
		if(!this.getId().equals(that.getId())){return false;}
		return true;
	}

	@Override
	/**
	 * @see compareTo()
	 * Returns negative if o is timestamped before this.
	 * Returns positive if o is timestamped after this.
	 */
	public int compareTo(Object o) {
		Message that = (Message) o;
		if(this.equals(that)){return 0;}
		if(this.getTimeStamp().before(that.getTimeStamp())){return -1;}
		if(this.getTimeStamp().after(that.getTimeStamp())){return 1;}
		return 0;
	}
}
