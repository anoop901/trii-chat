<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="triichat.OfyService" %>

<script>
 	//references to some elements
 	var createGroupButtonElem = $('#createGroup');
 	var groupListElem = $('#group-list');
 	var groupErrorElem = $('#group-error');
 	var groupListErrorElem = $('#group-list-error');
</script>

<div id="group-list-section">
    <h2 id="group-header" onclick="clearGroupSelection();">Groups</h2>
    <a class="button" href="#createGroup" id="create-group-button">Create Group</a>
    <ul id="group-list"></ul>
    <p id="group-list-error"></p>
</div>

<div id="createGroup" class="overlay">
    <div class="popup">
      <h2>Create a new Group</h2>
      <a class="close" href="#">&times;</a>
      <div class="content">
      	<form action="/group" id="createGroupForm">
		  <label for="name">Group Name:</label>
		  <input type="text" name="name" placeholder="NewGroup" required><br>
		  <input type="submit" value="Create Group">
		</form>
      </div>
    </div>
</div>

<script>
// Attach a submit handler to the form
$( "#createGroupForm" ).submit(function( event ) {
 
  // Stop form from submitting normally
  event.preventDefault();
 
  // Get some values from elements on the page:
  var $form = $( this ),
    url = $form.attr( "action" );

  // Get data from form
  var data = $( this ).serializeArray();
  
  // Send the data using post
  var posting = $.post( url, data, function( group ) {
	  addGroup(group);
	  location.hash = "#";
  }, "json");
});
</script>

<div id="group-view">
    <h2 id="group-name">---</h2>
    <div id="group-members"></div>
    <div id="group-triis"></div>
</div> 


<script>
$(document).ready(function () {
    addUserToGroupButtonElem.click(function (e) {

        var newUserName = window.prompt("Who do you want to add to this group?");

        // search for any users with this name
        $.getJSON('/username-search', {name: newUserName}, function (searchResults) {
            var users = searchResults['users'];
            // TODO: in case of multiple results allow the user to actually choose a user somehow, instead of random choice
            if (users.length > 0) {
                // randomly choose a user
                var userID = users[Math.floor(Math.random() * users.length)];
                $.get('/add-user-to-group', {user: userID, group: selectedGroupID});
            } else {
                window.alert("There is no user with the name " + newUserName);
            }
        });
    });	
});

function addGroup(group){
	// create the <li> element containing this group's name
    var liElem = $('<li>');
	var name = $("<h3>", {text: group['name']});
    liElem.data('group-id', group['id']); // associate the group id with the element
	
	var remove = $("<p>", {text: "Delete"});
	remove.click(function (e){
		var posting = $.post( '/group/delete', {id: group['id']} );
  		posting.done(function() {
  			liElem.remove();
  			if(group['id'] == selectedGroupID){
  				clearGroupSelection();
  			}
  	  	});
	});
	liElem.append( name, remove );
    
    // when the <li> element is clicked...
    name.click(function (e) {
        clickedGroup(group['id']);
    });

    $("#group-list").append(liElem);
}

function clickedGroup(groupID) {
    
    if(groupID != selectedGroupID){
    	// get group info
        $.getJSON('/group', {id: groupID}, function (group) {

            selectedGroupID = groupID;
            selectedTriiID = undefined;

            $('#group-name').text(group['name']);
            $('#trii-name').text('---');
            triiMessagesElem.empty();

            $('#group-members').empty();
            $('#group-triis').empty();

            userListElem.empty();
            groupErrorElem.empty();
            
         	// Create Member View
    	    var title = "<h3>Members</h3>";
    	    var button = $("<a>", {id:"add-user-to-group-button", class:"button", href:"#addMember", text:"Add Someone"});
    	    var ul = $("<ul>", {id:"user-list"});
            $('#group-members').append( title, button, ul );
            
            // Create Trii View
    	    var title = "<h3 onclick='clearTriiSelection();'>Triis</h3>";
    	    var button = $("<a>", {id:"create-trii-button", class:"button", href:"#createTrii", text:"New Trii"});
    	    var ul = $("<ul>", {id:"trii-list"});
    	    var error = $("<p>", {id:"group-error"});
            $('#group-triis').append( title, button, ul, error );
            
            
            triiListElem = $('#trii-list');
            groupErrorElem = $('#group-error');
            

            // iterate through list of users in group
            var members = group['members'];
            for (var i = 0; i < members.length; i++) {
                var userID = members[i];

                // get this user's name
                $.getJSON('/user', {id: userID}, function (user) {
                	addMember(user);
                });
            }

            // iterate through list of triis
            var triis = group['triis'];
            for (var i = 0; i < triis.length; i++) {
                var triiID = triis[i];

                // get this trii's name
                $.getJSON('/trii', {id: triiID}, (function (triiID, trii) {
                	addTrii(trii);
                }).bind(undefined, triiID));
            }
        }).fail(function () {
            // display an error message
            triiListElem.empty();
            groupErrorElem.text('[failed to get group]');
        });
    }
    
}

function clearGroupSelection(){
	selectedGroupID = undefined;
	selectedTriiID = undefined;
	$('#group-name').text('---');
    $('#trii-name').text('');
    triiMessagesElem.empty();
    $('#group-members').empty();
    $('#group-triis').empty();
}

</script>