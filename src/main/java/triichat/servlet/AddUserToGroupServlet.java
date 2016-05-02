package triichat.servlet;

import org.json.JSONException;
import org.json.JSONObject;
import triichat.model.Group;
import triichat.db.OfyService;
import triichat.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Documentation of the client/server interface is in ClientServerInterface.txt
 * Created by anoop on 4/9/16.
 */
public class AddUserToGroupServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // "user" parameter contains user id of user to add
        // "group" parameter contains group id of group to add to

        long groupID = Long.parseLong(request.getParameter("group"));
        String userID = request.getParameter("user");

        Group group = OfyService.loadGroup(groupID);
        User user = OfyService.loadUser(userID);
        if(group == null || user == null){
            //TODO: Error Message
            System.out.println("ERROR: group or user is null");
            return;
        }
        group.addUser(user);

        response.setContentType("application/json");
        JSONObject userJSON = new JSONObject();
        try {
        	userJSON.put("id", userID);
            userJSON.put("name", user.getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().print(userJSON);

        // TODO: if user is active, send user a message saying they were added to the group - HOW?
        // TODO: also notify other users in group that a new user has been added
    }
}
