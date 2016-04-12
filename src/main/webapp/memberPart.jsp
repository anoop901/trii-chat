<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="triichat.OfyService" %>

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
		</form>
      </div>
    </div>
</div>

 <script>
// Attach a submit handler to the form
$( "#addMemberForm" ).submit(function( event ) {
 
  // Stop form from submitting normally
  event.preventDefault();
 
  // Get some values from elements on the page
  var $form = $( this ),
    username = $form.find( "input[name='name']" ).val();
  
  //search for any users with this name
  $.getJSON('/username-search', {name: username}, function (searchResults) {
      var users = searchResults['users'];
      // TODO: in case of multiple results allow the user to actually choose a user somehow, instead of random choice
      if (users.length > 0) {
          // randomly choose a user
          var userID = users[Math.floor(Math.random() * users.length)];
          $.get('/add-user-to-group', {user: userID, group: selectedGroupID});
      } else {
          window.alert("There is no user with the name " + username);
      }
  });
  // Send the data using post
  var posting = $.post( url, data );
 
  // Put the results in a div
  posting.done(function( user ) {
	  addMember(user);
	  $('#addMember').removeClass('visible');
  });
});
</script> 

<script>
function addMember(user){
    var liElem = $('<li>');
    liElem.text(user['name']);
    $('#user-list').append(liElem);
}
</script>