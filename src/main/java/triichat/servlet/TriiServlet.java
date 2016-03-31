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
public class TriiServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        long triiID = Long.parseLong(request.getParameter("id"));

        JSONObject trii = new JSONObject();
        JSONArray messages = new JSONArray();

        // TODO: populate the JSON with values from the datastore. the following lines are placeholder
        messages.put(triiID * 10 + 1);
        messages.put(triiID * 10 + 2);
        messages.put(triiID * 10 + 3);

        try {
            trii.put("name", "Trii" + triiID);
            trii.put("messages", messages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().println(trii);
    }
}
