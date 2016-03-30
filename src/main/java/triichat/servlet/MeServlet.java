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
public class MeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject me = new JSONObject();

        JSONArray groups = new JSONArray();

        // TODO: populate the JSON with values from the datastore. the following lines are placeholder
        groups.put(10);
        groups.put(20);
        groups.put(30);

        try {
            me.put("groups", groups);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().print(me);
    }
}
