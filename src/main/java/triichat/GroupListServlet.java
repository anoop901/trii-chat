package triichat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class GroupListServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
		com.google.appengine.api.users.User user = userService.getCurrentUser();
		resp.setContentType("application/json");
		if(user == null){
			resp.getWriter().println("{\"groups\": [{\"id\": 10, \"name\": \"GOne\"}, {\"id\": 20, \"name\": \"GTwo\"}, {\"id\": 30, \"name\": \"GThree\"}]}");
		}else{
			User tuser = User.findUser(user);
			if(tuser == null){
				resp.getWriter().println("{\"groups\": [{\"id\": 10, \"name\": \"GOne\"}, {\"id\": 20, \"name\": \"GTwo\"}, {\"id\": 30, \"name\": \"GThree\"}]}");
			}
			String toPrint = "{\"groups\": [";
			Set<Group> groups = tuser.getGroups();
			boolean first = true;
			if(groups.isEmpty()){ //add a new Group "Alone" :-P
				Set<User> temp = new HashSet<User>();
				temp.add(tuser);
				Group g = Group.createGroup("Alone", temp, new HashSet<Trii>());
				tuser.addGroup(g);
				groups = tuser.getGroups();
			}
			for(Group g : groups){
				if(first){
					toPrint = toPrint + "{\"id\": " + g.getId() + ", \"name\": " + g.getName() +"}";
					first = false;
				}else{
					toPrint = toPrint + ", {\"id\": " + g.getId() + ", \"name\": " + g.getName() +"}";
				}
			}		
			toPrint = toPrint + "]}";
			//System.out.println(toPrint);
			//System.out.println("{\"groups\": [{\"id\": 10, \"name\": \"GOne\"}, {\"id\": 20, \"name\": \"GTwo\"}, {\"id\": 30, \"name\": \"GThree\"}]}");
			resp.getWriter().println(toPrint);
		}
	}
}
