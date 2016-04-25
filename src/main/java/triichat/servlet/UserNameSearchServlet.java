package triichat.servlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import triichat.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Documentation of the client/server interface is in ClientServerInterface.txt
 * Created by Margret on 4/5/2016.
 */
public class UserNameSearchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //write this
        String name = request.getParameter("name");

        response.setContentType("application/json");
        JSONObject found = new JSONObject();
        JSONArray foundUsers = new JSONArray();

        if(name != null){
            List<User> users = User.findUserByName(name);
            for(User u : users){
                foundUsers.put(u.getId());
            }
        }

        try {
            found.put("users", foundUsers);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().println(found);
    }
}
