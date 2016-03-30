<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="triichat.OfyService" %>
<%--
  Created by IntelliJ IDEA.
  UserServlet: anoop
  Date: 2/9/16
  Time: 19:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Home</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
</head>
<body>
<%

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if(user != null){
%>
<div id="title">
    <div>Trii Chat</div>
    <div>Welcome, <%= user.getNickname() %></div>
</div>
<%
    	triichat.User triiUser = triichat.User.findUser(user);
    	if(triiUser != null){
    		//go to user page
%>

<div id="group-list-section">
<h2>Groups</h2>
<ul id="group-list">
</ul>
<p id="group-list-error"></p>
</div>

<div id="trii-list-section">
<h2>Triis</h2>
<ul id="trii-list">
</ul>
</div>

<div id="trii-view">
<h2>Trii</h2>
<table id="message-table">
</table>
</div>

<script>
$(document).ready(function () {
	
	// request the list of groups from the server
	$.getJSON('/me', function (me) {
		
		// populate the group list <ul> element
		var groupListElem = $('#group-list');
		groupListElem.empty();
        $('group-list-error').empty();
		for (var i = 0; i < me.groups.length; i++) {
			
			var groupID = me.groups[i];

			$.getJSON('/group', {id: groupID}, (function (groupID, group) {
                var liElem = $('<li>');
                liElem.text(group.name);
                liElem.data('group-id', groupID); // associate the group id with the element

                // when the <li> element is clicked...
                liElem.click(function (e) {
                    alert('group-id = ' + $(e.target).data('group-id'));
                });
                groupListElem.append(liElem);
            }).bind(undefined, groupID));
		}
	}).fail(function () {
		var groupListElem = $('#group-list');
		groupListElem.empty();

		var groupListErrorElem = $('#group-list-error');
		groupListErrorElem.text('[failed to get group list]');
	});
});
</script>

<%
    	}else{
%>
<p>You have not created an account with TriiChat. Would you like to create a TriiChat account?</p>
<form action="userPage.jsp">
    <input type="submit" value="Create TriiChat User">
</form>
<%	
    	}
%>
<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Sign out</a></p>

<%
    }else{
		response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
    }
%>
</body>
</html>
