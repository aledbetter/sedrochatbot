 /*************************************************************************
 * Ledbetter CONFIDENTIAL
 * __________________
 * 
 * [2018] - [2020] Aaron Ledbetter
 * All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains
 * the property of Aaron Ledbetter. The intellectual and technical 
 * concepts contained herein are proprietary to Aaron Ledbetter and 
 * may be covered by U.S. and Foreign Patents, patents in process, 
 * and are protected by trade secret or copyright law. 
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Aaron Ledbetter.
 */


////////////////////////////////////////////////////////////////////////////////////
// Doc open
$(document).ready(function() {

	$("#username, #password").removeClass("warn").val("");
	$("#loginerror").hide().val("");

	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Add interactive message
	$("#login_bt").on('click', function (e) {
		var username = $("#username").val(); 
		var password = $("#password").val(); 

		$("#username, #password").removeClass("warn");

		var fail = false;
		if (!username || username.length < 3) {
			$("#username").addClass("warn");
			fail = true;
		}
		if (!password || password == "") {
			$("#password").addClass("warn");
			fail = true;
		}
		if (fail) {
			$("#password").val(""); 
			return;
		}
		scsLogin(username, password, function(data) {
			$("#username, #password").removeClass("warn").val("");
			if (data.code == 200) {
				window.location.href = "/server.html";
			} else {
				$("#loginerror").show().val("Incorrect username or password");
			}
		});
	});
	
	$("#logout_bt").on('click', function (e) {
		scsLogout(username, password, function(data) {
			window.location.href = "/index.html";
		});
	});
	
	
	
});
	










////////////////////////////////////////////////////////////////////////////////////
// API to ChatServer
////////////////////////////////////////////////////////////////////////////////////
function scsLogin(username, password, cb) {	
	var dat = "{ "; 
    dat += "\"username\": \"" + username + "\"";
    dat += ", \"password\": \"" + password + "\"";
    dat += "}";
	
	$.ajax({url: "/api/1.0/login", type: 'POST', async: true, data: dat, contentType: 'application/json', 
	  success: function(data){
		  cb(data);
	  }, error: function(xhr) {
		  cb(null);
	  }
	});
}

function scsLogout(cb) {
	$.ajax({url: "/api/1.0/logout", type: 'GET', dataType: "json", contentType: 'application/json', 
	  success: function(data){
		  cb(data);
	  }, error: function(xhr) {
		  cb(null);
	  }
	});
}

function scsGetSettings(cb) {
	$.ajax({url: "/api/1.0/settings", type: 'GET', dataType: "json", contentType: 'application/json', 
	  success: function(data){
		  cb(data);
	  }, error: function(xhr) {
		  cb(null);
	  }
	});
}

function scsUpdateSettings(username, password, sedro_access_key, cb) {	
	var dat = "{ "; 
    if (username) dat += "\"username\": \"" + username + "\"";
    if (password) dat += ", \"password\": \"" + password + "\"";
    if (sedro_access_key) dat += ", \"sedro_access_key\": \"" + sedro_access_key + "\"";
    dat += "}";
	
	$.ajax({url: "/api/1.0/settings", type: 'POST', async: true, data: dat, contentType: 'application/json', 
	  success: function(data){
		  cb(data);
	  }, error: function(xhr) {
		  cb(null);
	  }
	});
}

function scsGetUsers(cb) {
	$.ajax({url: "/api/1.0/users", type: 'GET', dataType: "json", contentType: 'application/json', 
	  success: function(data){
		  cb(data);
	  }, error: function(xhr) {
		  cb(null);
	  }
	});
}

function scsGetUser(user, cb) {
	$.ajax({url: "/api/1.0/users/"+user, type: 'GET', dataType: "json", contentType: 'application/json', 
	  success: function(data){
		  cb(data);
	  }, error: function(xhr) {
		  cb(null);
	  }
	});
}

function scsAddUser(username, cb) {	
	var dat = "{ "; 
    dat += "\"username\": \"" + username + "\"";
    dat += "}";
	
	$.ajax({url: "/api/1.0/user/add", type: 'POST', async: true, data: dat, contentType: 'application/json', 
	  success: function(data){
		  cb(data);
	  }, error: function(xhr) {
		  cb(null);
	  }
	});
}
function scsDelUser(username, cb) {	
	$.ajax({url: "/api/1.0/user/"+username+"/del", type: 'POST', async: true, contentType: 'application/json', 
	  success: function(data){
		  cb(data);
	  }, error: function(xhr) {
		  cb(null);
	  }
	});
}


function scsUpdateUser(username, service, serviceparams, cb) {	
	if (!service) return;
	var dat = "{ "; 
// FIXME allow add / update / del service info	
	//services [ 
	   // dat += "{ \"name\": \"" + servicename + "\"";
	   // dat += "\"username\": \"" + username + "\"";
	   // dat += "\"username\": \"" + username + "\"";
	   // dat += "\"username\": \"" + username + "\"";
	// dat += "}";
	// ]
    dat += "}";
	
	$.ajax({url: "/api/1.0/user/"+username, type: 'POST', async: true, data: dat, contentType: 'application/json', 
	  success: function(data){
		  cb(data);
	  }, error: function(xhr) {
		  cb(null);
	  }
	});
}



/////////////////////////////////////////////////////////////////////
//utiity
String.prototype.escapeSpecialChars = function() {
return this
.replace(/[\\]/g, '\\\\')
.replace(/[\/]/g, '\\/')
.replace(/[\f]/g, '\\f')
.replace(/[\n]/g, '\\n')
.replace(/[\r]/g, '\\r')
.replace(/[\t]/g, '\\t')
.replace(/[\"]/g, '\\"');
};