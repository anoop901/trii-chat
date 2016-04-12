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
	 <link rel="stylesheet" href="stylesheets/popup.css">
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
%>


<%@ include file="groupPart.jsp" %>

<%@ include file="memberPart.jsp" %>

<%@ include file="triiPart.jsp" %>

<script>

// references to some elements
var messageTableElem = $('#message-table');
var messageTextboxElem = $('#message-textbox');
var messageSendButtonElem = $('#message-send-button');

var userListElem = $('#user-list');
var addUserToGroupButtonElem = $('#add-user-to-group-button');

// variables for current selections
var selectedGroupID = undefined;
var selectedTriiID = undefined;

$(document).ready(function () {

	
	// request the list of groups from the server
	$.getJSON('/me', function (me) {
		
		// populate the group list <ul> element
	 	groupListElem.empty();
		groupListErrorElem.empty();

        // iterate through list of groupIDs
        var groups = me['groups'];
		for (var i = 0; i < groups.length; i++) {
			
			var groupID = groups[i];

            // request this group's name
			$.getJSON('/group', {id: groupID}, (function (groupID, group) {
				addGroup(group);
            }).bind(undefined, groupID)).fail(function () {
                // TODO: display an error message
            });
		}
	}).fail(function () {
        // display an error message
		groupListElem.empty();
		groupListErrorElem.text('[failed to get group list]');
	});

    
});


</script>

<%
    	}else{
%>
<p>You have not created an account with TriiChat. Would you like to create a TriiChat account?</p>
<form action="/userPage.jsp">
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
