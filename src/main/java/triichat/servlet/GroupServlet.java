package triichat.servlet;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.sun.javaws.exceptions.InvalidArgumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import triichat.model.Group;
import triichat.db.OfyService;
import triichat.model.Trii;
import triichat.model.User;

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
public class GroupServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        long groupID = Long.parseLong(request.getParameter("id"));
        Group currentGroup = OfyService.getGroup(groupID);
        
        response.setContentType("application/json");
        JSONObject group = new JSONObject();
        JSONArray triis = new JSONArray();
        JSONArray members = new JSONArray();

        // TODO: test this code
        // get group's triis and users from the datastore
        Set<Trii> triiSet = currentGroup.getTriis();
        Set<User> userSet = currentGroup.getUsers();

        // add them to the JSON object

        for (Trii t : triiSet)
            triis.put(t.getId());

        for (User u : userSet)
            members.put(u.getId());

        try {
        	group.put("id", groupID);
            group.put("name", currentGroup.getName());
            group.put("triis", triis);
            group.put("members", members);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().println(group);
    }

    /**
     * Create a group in the database containing only the currently logged-in user,
     * and with no trees, and the name given as the "name" parameter in the request
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	String command = req.getPathInfo();

        if (command == null) { // Creates a group with the given name containing only the logged-in user and no triis.

            String name = req.getParameter("name");
            if(name == null){
                System.err.println("error: POST /group called without name parameter");
                return;
            }

            // Get user that's logged in, or ignore request if not a TriiChat user
            UserService userService = UserServiceFactory.getUserService();
            com.google.appengine.api.users.User gUser = userService.getCurrentUser();
            User user = User.findUser(gUser);
            if(user == null){
                return; // ignore request
            }

            // create group
            Set<User> users = new HashSet<User>();
            users.add(user); // users contains only logged-in user
            Group currentGroup = Group.createGroup(name,users);

            // TODO: notify the user


            // return group info in response

            resp.setContentType("application/json");
            JSONObject group = new JSONObject();
            JSONArray triis = new JSONArray();
            JSONArray members = new JSONArray();

            // populate the JSON with values from the datastore
            Set<Trii> triiSet = currentGroup.getTriis();
            Set<User> userSet = currentGroup.getUsers();

            for (Trii t : triiSet)
                triis.put(t.getId());

            for (User u : userSet)
                members.put(u.getId());

            try {
                group.put("id", currentGroup.getId());
                group.put("name", currentGroup.getName());
                group.put("triis", triis);
                group.put("members", members);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            resp.getWriter().println(group);

        } else if (command.equals("/delete")) {
            long groupID = Long.parseLong(req.getParameter("id"));
            OfyService.deleteGroup(groupID);
        }
    }
}
