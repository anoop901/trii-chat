package triichat.servlet;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import triichat.model.Group;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * Created by anoop on 3/30/16.
 */
public class MeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("application/json");
    	
    	JSONObject me = new JSONObject();
        JSONArray groups = new JSONArray();

        // TODO: test
        // Get user that's logged in

        UserService userService = UserServiceFactory.getUserService();
        User gUser = userService.getCurrentUser();
        triichat.model.User user = triichat.model.User.findUser(gUser);
        if(user == null){
            user = triichat.model.User.createUser(gUser);
        }
        // Get their groups and put group ids in the groups json array
        Set<Group> triiGroups = user.getGroups();
        for(Group g : triiGroups){
            groups.put(g.getId());
        }

        try {
            me.put("id", user.getId());
            me.put("groups", groups);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().print(me);
    }
}
