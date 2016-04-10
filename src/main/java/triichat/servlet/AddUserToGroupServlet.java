package triichat.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by anoop on 4/9/16.
 */
public class AddUserToGroupServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // "user" parameter contains user id of user to add
        // "group" parameter contains group id of group to add to
        // TODO: add user to group in the database
        // TODO: if user is active, send user a message saying they were added to the group
    }
}
