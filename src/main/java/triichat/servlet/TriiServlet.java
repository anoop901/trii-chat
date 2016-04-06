package triichat.servlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import triichat.Message;
import triichat.OfyService;
import triichat.Trii;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * Created by anoop on 3/30/16.
 */
public class TriiServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        long triiID = Long.parseLong(request.getParameter("id"));
        Trii theTrii = OfyService.getTrii(triiID);

        JSONObject trii = new JSONObject();
        JSONArray messages = new JSONArray();

        // TODO: test this code and Trii.getMessages()
        // populate the JSON with values from the datastore. the following lines are placeholder

        Trii currentTrii = OfyService.getTrii(triiID);

        Set<Message> messageSet = currentTrii.getMessages();

        for(Message m : messageSet)
            messages.put(m.getId());

        try {
            trii.put("name", currentTrii.getName());
            trii.put("messages", messages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().println(trii);
    }
}
