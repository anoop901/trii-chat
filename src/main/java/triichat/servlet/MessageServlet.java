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
public class MessageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        long messageID = Long.parseLong(request.getParameter("id"));

        JSONObject message = new JSONObject();

        // TODO: populate the JSON with values from the datastore. the following lines are placeholder

        try {
            message.put("author", messageID % 2);
            message.put("body", "This message has an ID of " + messageID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().println(message);
    }
}
