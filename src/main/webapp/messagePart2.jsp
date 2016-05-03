<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="triichat.db.OfyService" %>

<div class="messages">

<script src="/js/loadash.min.js"></script>
<script src="/js/backbone-min.js"></script>
<script src="/js/rappid.js"></script>
<script src="/js/message.js"></script>
<script>
function createMessageView(messages){
	// Create Message Table
	//var button = '<button class="btn" id="btn-layout">layout</button>';
    var table = $("<div>", {id:"paper-holder",  class:"paper"});
    $('#trii-messages').append( table);
    

    createMessages();
    
   	if(!messages.length)
   		displayMessageForm();
    // iterate through list of messages
    for (var i = 0; i < messages.length; i++) {
      	addMessage(messages[i]);
    } 
    
    graphLayout.layout();
    paperScroller.centerContent();
    positionContent();
}

function displayMessageForm(){
	var form = $("<form>", {id:"message-send-form", class:"create-message"});
    var textbox = $("<textarea>", {id:"message-textbox"});
    var submit = $("<input>", {id:"message-send-button", type:"submit", value: "Send"});
	form.append(textbox, submit);
		form.submit(function(e) {
			e.preventDefault();
			var posting = $.post('/message', {
				body : textbox.val(), 
				trii_id : selectedTriiID,
				parent_id : JSON.stringify(selectedMessageIDs)
			}, function(message) {
				addMessage(message);
				sendMessage(selectedTriiID,null,message['id']);
				clearMessageSelection();
			}, "json");
		});
		$('#trii-messages').append(form);
	}

	function clearMessageSelection() {
		selectedMessageIDs = [];
		$('#message-send-form').remove();
	}
</script>

</div>