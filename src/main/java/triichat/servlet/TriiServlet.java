package triichat.servlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import triichat.model.Group;
import triichat.model.Message;
import triichat.db.OfyService;
import triichat.model.Trii;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Documentation of the client/server interface is in ClientServerInterface.txt
 * Created by anoop on 3/30/16.
 */
public class TriiServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        long triiID = Long.parseLong(request.getParameter("id"));
        Trii theTrii = OfyService.loadTrii(triiID);

        response.setContentType("application/json");
        JSONObject trii = new JSONObject();
        JSONArray messages = new JSONArray();

        // TODO: test this code and Trii.getMessages()
        // populate the JSON with values from the datastore

        Trii currentTrii = OfyService.loadTrii(triiID);

        Set<Message>  temp = currentTrii.getMessages();
        List<Message> messageSet = new ArrayList<Message>(temp);
        Collections.sort(messageSet);
        //TODO: pick one way of sending trii's messages
        //messages part is just ids - the old way
        /*
        for(Message m : messageSet){
            messages.put(m.getId());
        }
        */
        //messages part is all the parts of a message
        for(Message m : messageSet)
            try{
                JSONObject message = new JSONObject();
                JSONArray parents = new JSONArray();
                for(Message p : m.getParents()){
                    parents.put(p.getId());
                }
                JSONArray replies = new JSONArray();
                for(Message r : m.getReplies()){
                    replies.put(r.getId());
                }
                message.put("id", m.getId());
                message.put("author", m.getAuthor().getId());
                message.put("body", m.getContent());
                message.put("timestamp", m.getTimeStamp());
                message.put("parents", parents);
                message.put("replies", replies);
                messages.put(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        try {
        	trii.put("id", triiID);
            trii.put("name", currentTrii.getName());
            trii.put("messages", messages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.getWriter().println(trii);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // request parameter "name" contains the name of the new trii
        // request parameter "group" contains the group ID where this trii will be added
		resp.setContentType("application/html");
    	String command = req.getPathInfo();

        if (command == null) {
            // request parameter "name" contains the name of the new trii
            // request parameter "group" contains the group ID where this trii will be added
            // create a trii in the database within the specified group with no messages, with the specified name
            String name = req.getParameter("name");
            Long groupId = Long.parseLong(req.getParameter("group"));
            System.out.println("Creating trii with name=\"" + name + "\" and ID=" + groupId);
            if (name == null || groupId == null) {
                throw new IllegalArgumentException("POST /trii called with bad parameters");
            }
            Group group = OfyService.loadGroup(groupId);

            // create the trii
            Trii trii = Trii.createTrii(name, group);


            // return trii info in response

            resp.setContentType("application/json");
            JSONObject newTrii = new JSONObject();
            JSONArray messages = new JSONArray();

            Set<Message> messageSet = trii.getMessages();

            for (Message m : messageSet)
                messages.put(m.getId());

            try {
                newTrii.put("id", trii.getId());
                newTrii.put("name", trii.getName());
                newTrii.put("messages", messages);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            resp.getWriter().println(newTrii);

            // TODO: notify any active users in the group that a new trii has been created
        } else if (command.equals("/delete")) {
            Long triiId = Long.parseLong(req.getParameter("id"));
            OfyService.deleteTrii(triiId);
        } else if (command.equals("/edit")) {
            // TODO: Hook this up to an edit button on the client ui
            Long triiId = Long.parseLong(req.getParameter("id"));
            String triiName = req.getParameter("name");

            Trii trii = OfyService.loadTrii(triiId);
            trii.setName(triiName);
        }
    }
}