<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="triichat.db.OfyService" %>

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
				  sendMessage(trii['id'],null,null);
				  $('#createTrii').removeClass('visible');
			  }, "json");
			});
		  </script>  
		</form>
      </div>
    </div>    
</div>

<%@ include file="messagePart2.jsp" %>
    
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
	        createMessageView(trii['messages']);
	        triiMessagesElem = $('#trii-messages');
	
	    }).fail(function () {
	        // display an error message
	        triiListElem.empty();
	        triiErrorElem.text('[failed to get trii]');
	    });

		clearMessageSelection();
	}
}

function clearTriiSelection(){
	clearMessageSelection();
	selectedTriiID = undefined;
    $('#trii-name').text('---');
    triiMessagesElem.empty();
}
</script>
</div>