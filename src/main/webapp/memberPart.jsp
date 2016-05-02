<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="triichat.db.OfyService" %>

<div class="member">
<script>
 	//references to elements
 	var userListElem = $('#user-list');
 	var addUserToGroupButtonElem = $('#add-user-to-group-button');
</script>

<div id="addMember" class="overlay">
    <div class="popup">
      <h2>Who do you want to add to this group?</h2>
      <a class="close" onclick="$('#addMember').removeClass('visible');">&times;</a>
      <div class="content">
      	<form action="/add-user-to-group" id="addMemberForm">
		  <label for="name">User Name:</label>
		  <input type="text" name="name" placeholder="username" required><br>
		  <input type="submit" value="Add User">
		  <script>
			  // Attach a submit handler to the form
			  $('#addMemberForm').submit(function( event ) {
			  // Stop form from submitting normally
			  event.preventDefault();
			  // Get some values from elements on the page
			  var url = $( this ).attr( "action" );
			  var username = $( this ).find( "input[name='name']" ).val();
			  var data = $( this ).serializeArray();
			  //search for any users with this name
			  $.getJSON('/username-search', {name: username}, function (searchResults) {
			      var users = searchResults['users'];
			      // TODO: in case of multiple results allow the user to actually choose a user somehow, instead of random choice
			      if (users.length > 0) {
			          // randomly choose a user
			          var userID = users[Math.floor(Math.random() * users.length)];
			          var data = 
			          $.get('/add-user-to-group', {user: userID.toString(), group: selectedGroupID.toString()})
			          	  .done(function( user ) {
							  addMember(user); 
							  sendMessage(null,user['id'],null);
							  $('#addMember').removeClass('visible');
			        	  }, "json");
			      } else {
			          window.alert("There is no user with the name " + username);
			      }
 			  });
			});
		  </script> 
		</form>
      </div>
    </div>  
</div>

<script>
function createMemberList(members){
	// Create Member View
    var title = "<h3>Members</h3>";
    var button = $("<button>", {id:"add-user-to-group-button", onclick:"$('#addMember').addClass('visible');event.stopPropagation();", text:"Add Someone"});
    var ul = $("<ul>", {id:"user-list"});
    $('#group-members').append( title, button, ul );
    userListElem = ul;

    // iterate through list of users in group
    for (var i = 0; i < members.length; i++) {
        var userID = members[i];

        // get this user's name
        $.getJSON('/user', {id: userID}, function (user) {
        	addMember(user);
        });
    }
}

function addMember(user){
    var liElem = $('<li>');
    liElem.text(user['name']);
    $('#user-list').append(liElem);
}
</script>
</div>