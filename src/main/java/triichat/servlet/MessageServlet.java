package triichat.servlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import triichat.OfyService;

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
        triichat.Message triiMessage = OfyService.getMessage(messageID);
        JSONObject message = new JSONObject();

        // TODO: test this also does it want author id? or name?
        try {
            message.put("author", triiMessage.getAuthor().getId());
            message.put("body", triiMessage.getContent());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().println(message);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String messageBody = request.getParameter("body");
        Long triiID = Long.parseLong(request.getParameter("trii_id"));

        // TODO: insert this message into the database
        // TODO: notify any users who are listening to this trii
        System.out.println("trii: " + triiID + ", message: " + messageBody);
    }
}
