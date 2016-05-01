package triichat.servlet;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import triichat.model.Message;
import triichat.db.OfyService;
import triichat.model.Trii;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Documentation of the client/server interface is in ClientServerInterface.txt
 * Created by anoop on 3/30/16.
 */
public class MessageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        long messageID = Long.parseLong(request.getParameter("id"));
        Message triiMessage = OfyService.loadMessage(messageID);
        
        response.setContentType("application/json");  
        JSONObject message = new JSONObject();
        JSONArray parents = new JSONArray();
        for(Message p : triiMessage.getParents()){
            parents.put(p.getId());
        }
        JSONArray replies = new JSONArray();
        for(Message r : triiMessage.getReplies()){
            replies.put(r.getId());
        }
        // TODO: test this
        try {
        	message.put("id", messageID);
            message.put("author", triiMessage.getAuthor().getName());
            message.put("body", triiMessage.getContent());
            message.put("timestamp", triiMessage.getTimeStamp());
            message.put("parents", parents);
            message.put("replies", replies);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().println(message);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("application/html");
		String command = request.getPathInfo();

        if (command == null) {
            // the message body
            String messageBody = request.getParameter("body");
            // the ID of the trii
            Long triiID = Long.parseLong(request.getParameter("trii_id"));
            // the ID of the parent message (as String and Long). both will be null if parameter is unspecified
            String parentMessageIDsStr = request.getParameter("parent_id");

            // get logged-in user
            UserService userService = UserServiceFactory.getUserService();
            User gUser = userService.getCurrentUser();
            triichat.model.User user = triichat.model.User.findUser(gUser);
            if (user == null) {
                user = triichat.model.User.createUser(gUser);
            }

            // trii to add message to
            Trii trii = OfyService.loadTrii(triiID);

            // if parent_id parameter is specified,
            Set<Message> parents = new HashSet<Message>();
            if (parentMessageIDsStr != null) {
            	parentMessageIDsStr = parentMessageIDsStr.replaceAll("[\"\\[\\]]", "");
                for (String parentMessageIDStr : parentMessageIDsStr.split(",")) {
                    Long parentMessageID = Long.parseLong(parentMessageIDStr);
                    Message parent = OfyService.loadMessage(parentMessageID);
                    parents.add(parent);
                }

            }else{//use a default parent (the most recent) if none defined
                Message parent = getMostRecent(trii);
                if(parent != null){parents.add(parent);}
            }

            // Create message
            Message newMessage = Message.createMessage(messageBody, parents, user, trii);

            // TODO: notify any users who are listening to this trii
            System.out.println("trii: " + triiID + ", message: " + messageBody);

            response.setContentType("application/json");
            JSONObject message = new JSONObject();
            JSONArray parentsJSON = new JSONArray();
            for(Message p : parents){
                parentsJSON.put(p.getId());
            }
            JSONArray repliesJSON = new JSONArray();
            try {
                message.put("id", newMessage.getId());
                message.put("author", newMessage.getAuthor().getName());
                message.put("body", newMessage.getContent());
                message.put("timestamp", newMessage.getTimeStamp());
                message.put("parents", parentsJSON);
                message.put("replies", repliesJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            response.getWriter().println(message);
        } else if (command.equals("/delete")) {
            long messageID = Long.parseLong(request.getParameter("id"));
            OfyService.deleteMessage(messageID);
        } else if (command.equals("/edit")) {
            long messageID = Long.parseLong(request.getParameter("id"));
        }
    }

    private Message getMostRecent(Trii trii){
        Set<Message> all = trii.getMessages();
        if(all.isEmpty()){return null;}
        Message retval = trii.getRoot();
        for(Message m : all){
            if(m.getTimeStamp().after(retval.getTimeStamp())){
                retval = m;
            }
        }
        return retval;
    }
}
