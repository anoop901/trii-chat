<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="triichat.OfyService" %>

<div class="trii">
<script>
 	//references to elements
	var createTriiButtonElem = $('#createTrii');
	var triiListElem = $('#trii-list');
	var triiMessagesElem = $('#trii-messages');
	var triiErrorElem = $('#trii-error');
</script>
 	
<div id="trii-view" class="trii-view">
	<div class="trii-header">
    	<h2 id="trii-name"></h2>
	</div>
    <div id="trii-messages" class="trii-messages"></div>
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
		  <script>
			// Attach a submit handler to the form
			$( "#createTriiForm" ).submit(function( event ) {
			  event.preventDefault();
			  var url = $( this ).attr( "action" );
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
		</form>
      </div>
    </div>    
</div>

<script>

function createTriiList(triis){
	// Create Trii View
    var title = "<h3 onclick='clearTriiSelection();'>Triis</h3>";
    var button = $("<button>", {id:"create-trii-button",  onclick:"$('#createTrii').addClass('visible');event.stopPropagation();", text:"New Trii"});
    var ul = $("<ul>", {id:"trii-list", class:"trii-list"});
    var error = $("<p>", {id:"trii-error"});
    $('#group-triis').append( title, button, ul, error );
    triiListElem = ul;

    // iterate through list of triis
    for (var i = 0; i < triis.length; i++) {
        var triiID = triis[i];
        // get this trii's name
        $.getJSON('/trii', {id: triiID}, (function (triiID, trii) {
        	addTrii(trii);
        }).bind(undefined, triiID));
    }
}

function addTrii(trii){
 	// create the <li> element containing this trii's name
    var liElem = $('<li>');
	var name = $("<h3>", {text: trii['name']});
	// when the <li> element is clicked...
    name.click(function (e) {
        clickedTrii(trii['id']);
    });
	var remove = $("<button>", {text: "Delete"});
	remove.click(function (e){
		$.post( '/trii/delete', {id: trii['id']}, function() {
  			liElem.remove();
  			if(trii['id'] == selectedTriiID){
  				clearTriiSelection();
  			}
  	  	});
	});
	liElem.append( remove, name );
    liElem.data('trii-id', trii['id']); // associate the trii id with the element
      
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
    	    var table = $("<ul>", {id:"message-table"});
    	    var form = $("<form>", {id:"message-send-form", class:"create-message"});
    	    var textbox = $("<textarea>", {id:"message-textbox"});
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
	var liElem = $('<li>');
    var dataElem = $('<div>', {class:"message-data"});
    var authorElem = $('<div>', {class:"message-data-name"});
    var contentElem = $('<div>', {class:"message my-message"});

    authorElem.text(message['author']);
    contentElem.text(message['body']);
    dataElem.append(authorElem);
    liElem.append(dataElem, contentElem);

    $('#message-table').append(liElem);
}
</script>
</div>