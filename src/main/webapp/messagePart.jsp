<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="triichat.db.OfyService" %>

<div class="messages">

<script>
function createMessageView(messages){
	// Create Message Table
    var table = $("<ul>", {id:"message-table"});
    var form = $("<form>", {id:"message-send-form", class:"create-message"});
    var textbox = $("<textarea>", {id:"message-textbox"});
    var submit = $("<input>", {id:"message-send-button", type:"submit", value: "Send"});
    form.append( textbox, submit );
    form.submit(function(e){
    	e.preventDefault();
    	  var posting = $.post( '/message', { body: textbox.val(), trii_id: selectedTriiID }, 
    			  function(message) {
    				  addMessage(message);
    				  textbox.val("");
    	  		  }, "json");
    });
    $('#trii-messages').append( table, form );
    

    // iterate through list of messages
    for (var i = 0; i < messages.length; i++) {
//        var messageID = messages[i];
//
//        // get this messages's author and body
//        $.getJSON('/message', {id: messageID}, (function (messageID, message) {
//			addMessage(message);
//        }).bind(undefined, messageID));

        addMessage(messages[i]);
    }
}

function addMessage(message){
	var liElem = $('<li>');
    var dataElem = $('<div>', {class:"message-data"});
    var authorElem = $('<div>', {class:"message-data-name"});
    var contentElem = $('<div>', {class:"message my-message"});

    authorElem.text(message['author']);
    $.get('/user', {id: message['author']}, function (user) {
        authorElem.text(user.name);
    });

    contentElem.text(message['body']);
    dataElem.append(authorElem);
    liElem.append(dataElem, contentElem);

    $('#message-table').append(liElem);
}
</script>
</div>