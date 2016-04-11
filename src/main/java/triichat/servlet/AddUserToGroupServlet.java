package triichat.servlet;

import triichat.Group;
import triichat.OfyService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Requests should have "user" parameter of user id of user to add (which is a string)
 * and "group" parameter of group to add to (which is a number (long))
 * Created by anoop on 4/9/16.
 */
public class AddUserToGroupServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // "user" parameter contains user id of user to add
        // "group" parameter contains group id of group to add to

        long groupID = Long.parseLong(request.getParameter("group"));
        String userID = request.getParameter("user");

        Group group = OfyService.getGroup(groupID);
        triichat.User user = OfyService.getUser(userID);
        if(group == null || user == null){
            //TODO: Error Message
            return;
        }
        group.addUser(user);
        user.addGroup(group);
        // TODO: if user is active, send user a message saying they were added to the group - HOW?
    }
}
