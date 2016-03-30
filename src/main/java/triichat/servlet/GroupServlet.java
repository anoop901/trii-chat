package triichat.servlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by anoop on 3/30/16.
 */
public class GroupServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        long groupID = Long.parseLong(request.getParameter("id"));

        JSONObject group = new JSONObject();
        JSONArray triis = new JSONArray();
        JSONArray members = new JSONArray();

        // TODO: populate the JSON with values from the datastore. the following lines are placeholder
        triis.put(groupID + 1);
        triis.put(groupID + 2);
        triis.put(groupID + 3);
        members.put(groupID + 4);
        members.put(groupID + 5);
        members.put(groupID + 6);

        try {
            group.put("name", "Group" + groupID);
            group.put("triis", triis);
            group.put("members", members);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().println(group);
    }
}
