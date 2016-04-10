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
    <input type="button" id="create-group-button" value="Create Group">
    <ul id="group-list"></ul>
    <p id="group-list-error"></p>
</div>

<div id="group-view">
    <h2 id="group-name">---</h2>
    <h3>Members</h3>
    <ul id="user-list"></ul>
    <h3>Triis</h3>
    <ul id="trii-list"></ul>
    <p id="group-error"></p>
</div>

<div id="trii-view">
    <h2 id="trii-name">---</h2>
    <table id="message-table"></table>
    <p id="trii-error"></p>
    <form id="message-send-form">
        <input type="text" id="message-textbox">
        <input type="submit" id="message-send-button" value="Send">
    </form>
</div>

<script>
$(document).ready(function () {

    // references to some elements
    var groupListElem = $('#group-list');
    var groupListErrorElem = $('#group-list-error');
    var userListElem = $('#user-list');
    var triiListElem = $('#trii-list');
    var groupErrorElem = $('#group-error');
    var messageTableElem = $('#message-table');
    var messageTextboxElem = $('#message-textbox');
    var triiErrorElem = $('#trii-error');

    // variables for current selections
    var selectedGroupID = undefined;
    var selectedTriiID = undefined;
	
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

            selectedGroupID = groupID;

            $('#group-name').text(group['name']);

            userListElem.empty();
            triiListElem.empty();
            groupErrorElem.empty();

            // iterate through list of users in group
            var members = group['members'];
            for (var i = 0; i < members.length; i++) {
                var userID = members[i];

                // get this user's name
                $.getJSON('/user', {id: userID}, function (user) {
                    var liElem = $('<li>');
                    liElem.text(user['name']);
                    userListElem.append(liElem);
                });
            }

            // iterate through list of triis
            var triis = group['triis'];
            for (var i = 0; i < triis.length; i++) {
                var triiID = triis[i];

                // get this trii's name
                $.getJSON('/trii', {id: triiID}, (function (triiID, trii) {
                    var liElem = $('<li>');
                    liElem.text(trii['name']);
                    liElem.data('trii-id', triiID);

                    // when the <li> element is clicked...
                    liElem.click(function (e) {
                        clickedTrii(triiID);
                    });

                    triiListElem.append(liElem);
                }).bind(undefined, triiID));
            }
        }).fail(function () {
            // display an error message
            triiListElem.empty();
            groupErrorElem.text('[failed to get group]');
        });
    }

    function clickedTrii(triiID) {
        // get trii info
        $.getJSON('/trii', {id: triiID}, function (trii) {

            selectedTriiID = triiID;

            $('#trii-name').text(trii['name']);

            messageTableElem.empty();

            // iterate through list of messages
            var messages = trii['messages'];
            for (var i = 0; i < messages.length; i++) {
                var messageID = messages[i];

                // get this messages's author and body
                $.getJSON('/message', {id: messageID}, (function (messageID, message) {

                    var trElem = $('<tr>');
                    var td1Elem = $('<td>');
                    var td2Elem = $('<td>');

                    td1Elem.text(message['author']);
                    td2Elem.text(message['body']);
                    trElem.append(td1Elem, td2Elem);

                    messageTableElem.append(trElem);

                }).bind(undefined, messageID));
            }
        }).fail(function () {
            // display an error message
            triiListElem.empty();
            triiErrorElem.text('[failed to get trii]');
        });
    }

    $('#message-send-form').submit(function (e) {

        if (selectedTriiID !== undefined) {
            $.post('/message', {
                body: messageTextboxElem.val(),
                trii_id: selectedTriiID
            });

            messageTextboxElem.val("");
        }

        // return false to prevent refresh
        return false;
    });

    $('#create-group-button').click(function (e) {

        var newGroupName = window.prompt("Enter name of new group");
        if (newGroupName != null) {
            $.post('/group', {
                name: newGroupName
            });
        }
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
