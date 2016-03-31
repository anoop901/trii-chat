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

    // references to some elements
    var groupListElem = $('#group-list');
    var groupListErrorElem = $('#group-list-error');
    var triiListElem = $('#trii-list');
	
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

                // create the <li> element containing this group's name
                var liElem = $('<li>');
                liElem.text(group['name']);
                liElem.data('group-id', groupID); // associate the group id with the element

                // when the <li> element is clicked...
                liElem.click(function (e) {
                    clickedGroup(groupID);
                });

                groupListElem.append(liElem);
            }).bind(undefined, groupID)).fail(function () {
                // TODO: display an error message
            });
		}
	}).fail(function () {
        // display an error message
		groupListElem.empty();
		groupListErrorElem.text('[failed to get group list]');
	});

    function clickedGroup(groupID) {
        // get group info
        $.getJSON('/group', {id: groupID}, function (group) {

            triiListElem.empty();

            var triis = group['triis'];
            for (var i = 0; i < triis.length; i++) {
                var triiID = triis[i];

                $.getJSON('/trii', {id: triiID}, (function (triiID, trii) {
                    var liElem = $('<li>');
                    liElem.text(trii['name']);
                    liElem.data('trii-id', triiID);

                    // when the <li> element is clicked...
                    liElem.click(function (e) {
                        alert('trii-id = ' + triiID);
                    });

                    triiListElem.append(liElem);
                }).bind(undefined, triiID));
            }
        });
    }
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
