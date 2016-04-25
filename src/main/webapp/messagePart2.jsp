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
	var button = '<button class="btn" id="btn-layout">layout</button>';
    var table = $("<div>", {id:"paper-holder",  class:"paper"});
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
    $('#trii-messages').append( button, table, form );
    

    createMessages();
    
    // iterate through list of messages
    for (var i = 0; i < messages.length; i++) {
        //        var messageID = messages[i];
        //
		//      // get this messages's author and body
		//      $.getJSON('/message', {id: messageID}, (function (messageID, message) {
		//			addMessage(message);
		//      }).bind(undefined, messageID));
      	addMessage(messages[i]);
    }
}

</script>

</div>