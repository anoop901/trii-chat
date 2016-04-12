<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="triichat.OfyService" %>

<script>
 	//references to elements
	var createTriiButtonElem = $('#createTrii');
	var triiListElem = $('#trii-list');
	var triiMessagesElem = $('#trii-messages');
	var triiErrorElem = $('#trii-error');
</script>
 	
<div id="trii-view">
    <h2 id="trii-name"></h2>
    <div id="trii-messages"></div>
    <p id="trii-error"></p>
</div>

<div id="createTrii" class="overlay">
    <div class="popup">
      <h2>Create a new Trii</h2>
      <a class="close" onclick="$('#createTrii').removeClass('visible')">&times;</a>
      <div class="content">
      	<form action="/trii" id="createTriiForm">
		  <label for="name">Trii Name:</label>
		  <input type="text" name="name" placeholder="NewTrii" required><br>
		  <input type="submit" value="Create Trii">
		</form>
      </div>
    </div>
</div>

<script>
// Attach a submit handler to the form
$( "#createTriiForm" ).submit(function( event ) {
  event.preventDefault();
  var $form = $( this ),
    url = $form.attr( "action" );
  
  // Get data from form
  var data = $( this ).serializeArray();
  data.push({name: 'group', value: selectedGroupID});
  
  // Send the data using post
  var posting = $.post( url, data, function( trii ) {
	  addTrii(trii);
	  $('#createTrii').removeClass('visible');
  }, "json");
});
</script>

<script>

function addTrii(trii){
	var liElem = $('<li>');
    liElem.text(trii['name']);
    liElem.data('trii-id', trii['id']);

    // when the <li> element is clicked...
    liElem.click(function (e) {
        clickedTrii(trii['id']);
    });

    triiListElem.append(liElem);
}

function clickedTrii(triiID) {
	if(triiID != selectedTriiID){
	    // get trii info
	    $.getJSON('/trii', {id: triiID}, function (trii) {
	
	        selectedTriiID = triiID;
	
	        $('#trii-name').text(trii['name']);
	
	        triiMessagesElem.empty();
	        
	     	// Create Message Table
    	    var table = $("<table>", {id:"message-table"});
    	    var form = $("<form>", {id:"message-send-form"});
    	    var textbox = $("<input>", {id:"message-textbox", type:"text"});
    	    var submit = $("<input>", {id:"message-send-button", type:"submit", value: "Send"});
    	    form.append( textbox, submit );
    	    form.submit(function(e){
    	    	e.preventDefault();
    	    	  var posting = $.post( '/message', { body: textbox.val(), trii_id: triiID }, 
    	    			  function(message) {
    	    				  addMessage(message);
    	    				  textbox.val("");
    	    	  		  }, "json");
    	    });
    	    $('#trii-messages').append( table, form );
    	    
    	    triiMessagesElem = $('#trii-messages');
	
	        // iterate through list of messages
	        var messages = trii['messages'];
	        for (var i = 0; i < messages.length; i++) {
	            var messageID = messages[i];
	
	            // get this messages's author and body
	            $.getJSON('/message', {id: messageID}, (function (messageID, message) {
					addMessage(message);
	            }).bind(undefined, messageID));
	        }
	
	    }).fail(function () {
	        // display an error message
	        triiListElem.empty();
	        triiErrorElem.text('[failed to get trii]');
	    });
	}
}

function clearTriiSelection(){
	selectedTriiID = undefined;
    $('#trii-name').text('---');
    triiMessagesElem.empty();
}

function addMessage(message){
	var trElem = $('<tr>');
    var td1Elem = $('<td>');
    var td2Elem = $('<td>');

    td1Elem.text(message['author']);
    td2Elem.text(message['body']);
    trElem.append(td1Elem, td2Elem);

    $('#message-table').append(trElem);
}
</script>