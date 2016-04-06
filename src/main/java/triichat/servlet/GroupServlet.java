package triichat.servlet;

import com.googlecode.objectify.Objectify;
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
public class GroupServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        long groupID = Long.parseLong(request.getParameter("id"));
        Group currentGroup = OfyService.getGroup(groupID);

        JSONObject group = new JSONObject();
        JSONArray triis = new JSONArray();
        JSONArray members = new JSONArray();

        // TODO: test this code
        // populate the JSON with values from the datastore. the following lines are placeholder
        Set<Trii> triiSet = currentGroup.getTriis();
        Set<User> userSet = currentGroup.getUsers();

        for (Trii t : triiSet)
            triis.put(t.getId());

        for (User u : userSet)
            members.put(u.getId());

        try {
            group.put("name", currentGroup.getName());
            group.put("triis", triis);
            group.put("members", members);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().println(group);
    }
}
