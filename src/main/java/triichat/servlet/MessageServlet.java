package triichat.servlet;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import triichat.Message;
import triichat.OfyService;
import triichat.Trii;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by anoop on 3/30/16.
 */
public class MessageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        long messageID = Long.parseLong(request.getParameter("id"));
        triichat.Message triiMessage = OfyService.getMessage(messageID);
        JSONObject message = new JSONObject();

        // TODO: test this also does it want author id? or name?
        try {
        	message.put("id", messageID);
            message.put("author", triiMessage.getAuthor().getId());
            message.put("body", triiMessage.getContent());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().println(message);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String messageBody = request.getParameter("body");
        Long triiID = Long.parseLong(request.getParameter("trii_id"));
        UserService userService = UserServiceFactory.getUserService();
        User gUser = userService.getCurrentUser();
        triichat.User user = triichat.User.findUser(gUser);
        if(user == null){
            user = triichat.User.createUser(gUser);
        }
        Trii trii = OfyService.getTrii(triiID);
        // get most recent message to make it the parent as default
        // TODO: eventually make this use actual parent
        Set<Message> parents = new HashSet<Message>();
        Message recent = getMostRecent(trii);
        if(recent != null){parents.add(recent);}
        // Create message
        Message newMessage = Message.createMessage(messageBody, parents, user,trii);
        // TODO: notify any users who are listening to this trii
        System.out.println("trii: " + triiID + ", message: " + messageBody);
        
        JSONObject message = new JSONObject();
        try {
        	message.put("id", newMessage.getId());
            message.put("author", newMessage.getAuthor().getId());
            message.put("body", newMessage.getContent());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().println(message);
    }

    private Message getMostRecent(Trii trii){
        Set<Message> all = trii.getMessages();
        if(all.isEmpty()){return null;}
        Message retval = trii.getRoot();
        for(Message m : all){
            if(retval.getTimeStamp().after(m.getTimeStamp())){
                retval = m;
            }
        }
        return retval;
    }
}
