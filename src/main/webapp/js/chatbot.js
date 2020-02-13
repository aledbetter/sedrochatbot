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
	$("#show_settings").show();
	$("#update_settings").hide();
	$("#set_password").val(""); 
	$("#update_settings_bt").html("Update");
	$("#add_user_bt").html("Add User");
	$("#add_username").val("");

	
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
	
	
	//////////////////////////////////////////////////
	// server page
	$("#update_settings_bt").on('click', function (e) {
		var v = $("#show_settings").attr("data-v");
		if (v == "hide") {
			$("#show_settings").show();
			$("#update_settings").hide();	
			$("#show_settings").attr("data-v", "show");
			$("#update_settings_bt").html("Update");
		} else {
			$("#show_settings").hide();
			$("#update_settings").show();
			$("#show_settings").attr("data-v", "hide");
			$("#update_settings_bt").html("Cancel");
		}		
	});
	$("#save_settings_bt").on('click', function (e) {
		var sak = $("#set_sedro_access_key").val();
		var u = $("#set_username").val();
		var p = $("#set_password").val();
		scsUpdateSettings(u, p, sak, function(data) {
			getSettings();
			$("#update_settings_bt").click();
		});
	});
	
	$("#add_user_bt").on('click', function (e) {
		var v = $("#user_add").attr("data-v");
		if (v == "hide") {
			$("#user_add").hide();
			$("#user_add").attr("data-v", "show");
			$("#add_user_bt").html("Add User");
		} else {
			$("#user_add").show();
			$("#user_add").attr("data-v", "hide");
			$("#add_user_bt").html("Cancel");
		}
	});
	$("#save_add_user_bt").on('click', function (e) {
		var u = $("#add_username").val();
		if (!u) return;
		scsAddUser(u, function(data) {
			getUsers();
			$("#add_user_bt").click();
			$("#add_username").val("");
		});
	});
	
	
	
	// if this is the server page
	if (window.location.href.indexOf("server.html") > -1) {
		getSettings();
		getUsers();
	}
	
});
	
function getSettings() {
	scsGetSettings(function(data) {
		$("#setting_poll_interval").html(data.info.poll_interval); 
		$("#setting_username").html(data.info.username); 
		$("#set_username").val(data.info.username); 
		if (data.info.sedro_access_key) {
			$("#set_sedro_access_key").val(data.info.sedro_access_key); 
			$("#setting_sedro_access_key").html(data.info.sedro_access_key); 
		} else {
			$("#set_sedro_access_key").val(""); 
			$("#setting_sedro_access_key").html("none"); 
		}

	});
}

function getUsers() {
	$("#userlist").html("No Users"); 
	scsGetUsers(function(data) {
		var usr = "";
		if (data.info && data.info.users) {
			for (var i=0;i<data.info.users.length;i++) {
				usr += "<div class'fLn' id='ua_"+data.info.users[i].username+"' style='padding-top:10px;padding-bottom:10px;border-bottom:1px solid #555;position:relative'>";				
				usr += "<div class='bslink' onClick='delUser(\""+data.info.users[i].username+"\");' style='width:70px;text-align:center;font-size:16px;position:absolute;right:100px;background:#EEE;'>Del</div>";	
				usr += "<div class='bslink' onClick='editUser(\""+data.info.users[i].username+"\");' style='width:70px;text-align:center;font-size:16px;position:absolute;right:180px;background:#EEE;'>Edit</div>";	
				usr += "<div class'fLn'><b>Username: " + data.info.users[i].username +"</b></div>";
				usr += "<div id='show_ua'>";

				// all the serviecs
				if (data.info.users[i].services) {
					for (var k=0;k<data.info.users[i].services.length;k++) {
// FIXME
					}
				}
				usr += "</div>";
				usr += "<div id='edit_ua' style='display:none'>";
// EDIT FIXME
				usr += "<div class='bslink' onClick='saveUser(\""+data.info.users[i].username+"\");' style='width:100px;text-align:center;font-size:16px;margin-left:440px;background:#EEE;'>Save</div>";	
				usr += "</div></div>";
			}
		}
		
		if (usr != "") $("#userlist").html(usr); 
	});
	
}

var glob_edit_u = null; 
function editUser(username) {
	if (glob_edit_u) {
		// one at a time only
		$("#ua_"+glob_edit_u+" #edit_ua").hide();
		$("#ua_"+glob_edit_u+" #show_ua").show();	
		if (glob_edit_u == username) {
			glob_edit_u = null;
			return;
		}
	}
	$("#ua_"+username+" #edit_ua").show();
	$("#ua_"+username+" #show_ua").hide();
	glob_edit_u = username;	
	
}
function saveUser(username) {
	// FIXME
}


function delUser(username) {
	scsDelUser(username, function(data) {
		getUsers()
	});
}








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