<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="triichat.db.OfyService" %>

<div class="group">
<script>
 	//references to some elements
 	var createGroupButtonElem = $('#createGroup');
 	var groupListElem = $('#group-list');
 	var groupListErrorElem = $('#group-list-error');
 	var groupErrorElem = $('#group-error');
</script>

<div id="group-section" class="group-selection">
    <h2 id="group-header" onclick="clearGroupSelection();">Groups</h2>
    <button onclick="$('#createGroup').addClass('visible');event.stopPropagation();" id="create-group-button">Create Group</button>
    <ul id="group-list" class="group-list"></ul>
    <p id="group-list-error"></p>
    
    <div id="createGroup" class="overlay">
	    <div class="popup">
	      <h2>Create a new Group</h2>
	      <a class="close" onclick="$('#createGroup').removeClass('visible');">&times;</a>
	      <div class="content">
	      	<form action="/group" id="createGroupForm">
			  <label for="name">Group Name:</label>
			  <input type="text" name="name" placeholder="NewGroup" required><br>
			  <input type="submit" value="Create Group">
			  <script>
				// Attach a submit handler to the form
				$( "#createGroupForm" ).submit(function( event ) {
				  // Stop form from submitting normally
				  event.preventDefault();
				  // Get some values from elements on the page:
				  var url = $(this).attr( "action" );
				  // Get data from form
				  var data = $( this ).serializeArray();
				  // Send the data using post
				  var posting = $.post( url, data, function( group ) {
					  addGroup(group);
					  $('#createGroup').removeClass('visible');
				  }, "json");
				});
			  </script>
			</form>
	      </div>
	    </div>
	    
	    
	</div>
</div>

<div id="selected-group" class="group-details">
    <h2 id="group-name">---</h2>
    <div id="group-triis" class="trii-list"></div>
    <div id="group-members" class="member-list"></div>
    <p id="group-error"></p>
</div> 


<%@ include file="memberPart.jsp" %>
	

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
	// when the <li> element is clicked...
    name.click(function (e) {
        clickedGroup(group['id']);
    });
	var remove = $("<button>", {text: "Delete"});
	remove.click(function (e){
		var posting = $.post( '/group/delete', {id: group['id']} );
  		posting.done(function() {
  			liElem.remove();
  			if(group['id'] == selectedGroupID){
  				clearGroupSelection();
  			}
  	  	});
	});
	liElem.append( remove, name );
    liElem.data('group-id', group['id']); // associate the group id with the element
      
    $("#group-list").append(liElem);
}

function clickedGroup(groupID) {
    
    if(groupID != selectedGroupID){
    	// get group info
        $.getJSON('/group', {id: groupID}, function (group) {

        	clearGroupSelection();
            selectedGroupID = groupID;

            $('#group-name').text(group['name']);
            $('#trii-name').text('---');
            
            createMemberList(group['members']);
            createTriiList(group['triis']);
            
        }).fail(function () {
            // display an error message
            triiListElem.empty();
            groupErrorElem.text('[failed to get group]');
        });
    }
    
}

function clearGroupSelection(){
	clearTriiSelection();
    $('#trii-name').text('');
    
	selectedGroupID = undefined;
	$('#group-name').text('---');
    $('#group-members').empty();
    $('#group-triis').empty();
    groupErrorElem.empty();
}

</script>
</div>