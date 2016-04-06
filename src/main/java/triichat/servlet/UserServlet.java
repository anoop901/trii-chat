package triichat.servlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import triichat.Group;
import triichat.OfyService;
import triichat.Trii;
import triichat.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * Created by anoop on 3/30/16.
 */
public class UserServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: test this code
        // {"name" : String
        // "contacts": [UserID, ...]}

        String userID = request.getParameter("id");
        User currentUser = OfyService.getUser(userID);

        JSONObject user = new JSONObject();
        JSONArray contacts = new JSONArray();

        Set<User> contactsSet = currentUser.getContacts();
        for(User u : contactsSet)
            contacts.put(u.getId());

        try {
            user.put("name", "User" + userID);
            user.put("contacts", contacts);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().println(user);
    }
}
