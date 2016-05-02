package triichat.servlet;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import triichat.db.OfyService;
import triichat.model.Group;
import triichat.model.Trii;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

/**
 * Documentation of the client/server interface is in ClientServerInterface.txt
 * Created by anoop on 3/30/16.
 */
public class MeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("application/json");
    	
    	JSONObject me = new JSONObject();
        JSONArray groups = new JSONArray();

        // TODO: test
        // Get user that's logged in, or ignore request if not a TriiChat user
        UserService userService = UserServiceFactory.getUserService();
        User gUser = userService.getCurrentUser();
        triichat.model.User user = triichat.model.User.findUser(gUser);
        if(user == null){
            return; // ignore request
        }
        

        String userId = gUser.getUserId();
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        String token = channelService.createChannel(userId);
        
        // Get their groups and put group ids in the groups json array
        Set<Group> triiGroups = user.getGroups();

        for(Group g : triiGroups){
            groups.put(g.getId());
        }

        try {
            me.put("id", user.getId());
            me.put("token", token);
            me.put("groups", groups);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().print(me);
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	// get logged-in user
        UserService userService = UserServiceFactory.getUserService();
        User gUser = userService.getCurrentUser();
        triichat.model.User user = triichat.model.User.findUser(gUser);
        if (user == null) {
            user = triichat.model.User.createUser(gUser);
        }
        
        long groupID = Long.parseLong(req.getParameter("group_id"));
        String message = req.getParameter("message");
        Group currentGroup = OfyService.loadGroup(groupID);
        Set<triichat.model.User> users = currentGroup.getUsers();
        users.remove(user);
        sendUpdateToClients(users, message);
    }
    
    
    private void sendUpdateToUser(String userID, String message) {
		if (userID != null) {
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			channelService.sendMessage(new ChannelMessage(userID, message));
		}
	}
		
	public void sendUpdateToClients(Set<triichat.model.User> users, String message) {
		for(triichat.model.User user : users)
			sendUpdateToUser(user.getId() , message);
	}

}
