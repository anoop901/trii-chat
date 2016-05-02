<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="triichat.db.OfyService" %>
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
	 <link rel="stylesheet" href="stylesheets/layout.css">
	 <link rel="stylesheet" href="stylesheets/rappid.min.css" />
     <link rel="stylesheet" type="text/css" href="stylesheets/message.css" />
     <script src='/_ah/channel/jsapi'></script>
</head>
<body>

<%

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if(user != null){
%>

<main>
<div id="title" class="title">
    <div class="name">Welcome, <%= user.getNickname() %></div>
	<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Sign out</a>
    <h1>Trii Chat</h1>
</div>
<%
    	triichat.model.User triiUser = triichat.model.User.findUser(user);
    	if(triiUser != null){
%>

<div class="container clearfix">
	<%@ include file="groupPart.jsp" %>
	
	<%@ include file="triiPart.jsp" %>
</div>
</main>

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
var selectedMessageIDs = [];

//channel variables
var token; 
var channel;
var socket;

sendMessage = function(message) {
// 	path += '?g=' + state.game_key;
// 	if (opt_param) {
// 	  path += '&' + opt_param;
// 	}
// 	var xhr = new XMLHttpRequest();
// 	xhr.open('POST', path, true);
// 	xhr.send();
	$.post( "/me", { group_id: selectedGroupID, message: JSON.stringify("Hello World") } );
};
onOpened = function() {
	connected = true;
	sendMessage('opened');
// 	updateBoard();
};
	
onMessage = function(m) {
     newState = JSON.parse(m.data);
     alert(newState);
//     state.board = newState.board || state.board;
//     state.userX = newState.userX || state.userX;
//     state.userO = newState.userO || state.userO;
//     state.moveX = newState.moveX == 'true';
//     state.winner = newState.winner || "";
//     state.winningBoard = newState.winningBoard || "";
//     updateGame();
}

$(document).ready(function () {

	// request the list of groups from the server
	$.getJSON('/me', function (me) {
		
		// setup for channels
		token = me['token'];
		channel = new goog.appengine.Channel(token);
		var handler = {
			'onopen': function() {},
			'onmessage': onMessage,
			'onerror': function() {},
			'onclose': function() {}
		};
		socket = channel.open(handler);
		//socket.onopen = onOpened;
		socket.onmessage = onMessage;
		
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

	$("body").click(function(){
		$(".overlay").removeClass('visible');
	});
	$(".popup").click(function(e){
		e.stopPropagation();
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

<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Sign out</a>
<%	
    	}
%>

<%
    }else{
		response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
    }
%>
</body>
</html>
