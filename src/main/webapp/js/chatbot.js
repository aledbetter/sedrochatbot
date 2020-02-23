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

var glob_users = null;
$(document).ready(function() {

	$("#username, #password").removeClass("warn").val("");
	$("#loginerror").hide().val("");
	$("#show_settings").show();
	$("#update_settings").hide();
	$("#set_password").val(""); 
	$("#update_settings_bt").html("Update");
	$("#add_user_bt").html("Add User");
	$("#add_username").val("");
	$("#userInfo").hide();
	$("#set_password2, #set_password").removeClass("error");
	
	$("#userlist").show();

	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Check auth cookie
	var cookie = getCookie("atok");
	if (!cookie || cookie == "") {
		if (window.location.href.indexOf(".html") > -1 && window.location.href.indexOf("index.html") == -1) {
// FIXME only see login
			window.location.href = "/index.html";	
			return;
		}		
	}
	
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
		$("#set_password2, #set_password").removeClass("error");

		var sak = $("#set_sedro_access_key").val();
		var u = $("#set_username").val();
		var p = $("#set_password").val();
		var p2 = $("#set_password2").val();
		if ((p || p2) && p != p2) {
			$("#set_password2, #set_password").addClass("error");
			return;
		}
		var pi = $("#set_poll_interval").val();

		
		scsUpdateSettings(u, p, sak, pi, function(data) {
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
			$("#userlist").show();
		} else {
			$("#user_add").show();
			$("#user_add").attr("data-v", "hide");
			$("#add_user_bt").html("Cancel");
			$("#userlist").hide();
		}
	});
	$("#save_add_user_bt").on('click', function (e) {
		var u = $("#add_username").val();
		if (!u) return;
		var up = $("#user_sedro_persona").val();
		if (!up) return;
		
		scsAddUser(u, up, function(data) {
			getUsers();
			$("#add_user_bt").click();
			$("#add_username").val("");
		});
	});
	$("#save_user_bt").on('click', function (e) {
		if (!glob_edit_u) return;
		saveUser(glob_edit_u);
		$("#userlist").show();
	});
	$("#save_user_cancel_bt").on('click', function (e) {
		glob_edit_u = null;			
		$("#userInfo").hide();
		$("#userlist").show();

	});
	
	
	// if this is the server page
	if (window.location.href.indexOf("server.html") > -1) {
		getSettings();
		getUsers();
	}
});

function logout() {
	scsLogout(function(data) {
		window.location.href = "/index.html";
	});
}
	

function getSettings() {
	$("#setting_username, #setting_poll_interval, #setting_sedro_access_key, #setting_database_path").html("..."); 

	scsGetSettings(function(data) {
		$("#setting_poll_interval").html(data.info.poll_interval); 
		$("#set_poll_interval").val(data.info.poll_interval); 
		$("#setting_username").html(data.info.username); 
		$("#set_username").val(data.info.username); 
		
		if (data.info.database == true) {
			$("#setting_database_path").html(data.info.database_path); 
		} else {
			$("#setting_database_path").html("NONE"); 
		}
		if (data.info.sedro_access_key) {
			$("#set_sedro_access_key").val(data.info.sedro_access_key); 
			$("#setting_sedro_access_key").html(data.info.sedro_access_key); 
			glob_api_key = data.info.sedro_access_key;
			sedroGetPersonas(function (data) {
				if (data == null || data.code == 401) {
					window.location.href = "/index.html";
					return;
				}
				// persona select list
				if (data.list && data.list.length > 0) {
					var pselect = "";
					for (var i=0;i<data.list.length;i++) {
						pselect += "<option value='"+data.list[i]+"'>"+data.list[i]+"</option>";						
					}
					if (pselect == "") pselect = "<option value=''>No Personas</option>";
					$(".persona_list").html(pselect);
				}
			});
		} else {
			$("#set_sedro_access_key").val(""); 
			$("#setting_sedro_access_key").html("<span style='font-weight:bold;color:red'>KEY REQUIRED</span>"); 
			glob_api_key = null;
		}

	});
}

function getUsers() {
	$("#userlist").show();
	$("#userlist").html("Getting User List..."); 
	scsGetUsers(function(data) {
		if (data == null || data.code == 401) {
			window.location.href = "/index.html";
			return;
		}
		var usr = "";
		if (data.info && data.info.users) {
			glob_users = data.info.users;
			for (var i=0;i<data.info.users.length;i++) {
				usr += "<div class='fLn' id='ua_"+data.info.users[i].username+"' style='padding-top:10px;padding-bottom:10px;border-bottom:1px solid #555;position:relative;background:#FFF'>";				
					usr += "<div class='bslink' onClick='delUser(\""+data.info.users[i].username+"\");' style='width:70px;text-align:center;font-size:16px;position:absolute;right:100px;background:#EEE;'>Del</div>";	
					usr += "<div class='bslink' onClick='editUser(\""+data.info.users[i].username+"\");' style='width:70px;text-align:center;font-size:16px;position:absolute;right:180px;background:#EEE;'>Edit</div>";	
					usr += "<div class='fLn'>";
						usr += "<div style='padding-left:200px;text-align:left'><b>Username: " + data.info.users[i].username +"</b></div>";
						usr += "<div style='padding-left:200px;text-align:left'><b>Sedro Persona: " + data.info.users[i].sedro_persona +"</b></div>";
					usr += "</div>";				
					usr += "<div id='show_ua'>";
	
					// all the serviecs
					if (data.info.users[i].services) {
						for (var k=0;k<data.info.users[i].services.length;k++) {
							usr += "<h2 class='fLn'><b>Service: " + data.info.users[i].services[k].service + "</b></h2>";
							for (const property in data.info.users[i].services[k]) {
								if (property == "service") continue;
								usr += "<div class='fLn'>";
									usr += "<div style='padding-left:200px;text-align:left'><b>" + property + "</b>: "+data.info.users[i].services[k][property]+"</div>";
								usr += "</div>";				
							}
						}
					}
					
					usr += "</div>";				
				usr += "</div>";				
			}
		}
		//alert(usr);
		if (usr != "") $("#userlist").html(usr); 
		else $("#userlist").html("No Users"); 

	});
}

// Find user AND fill out the userInfo form
function getUserInfo(username) {
	if (!glob_users) return null;
	var user = null;
	for (var i=0;i<glob_users.length;i++) {
		if (glob_users[i].username == username) {
			user = glob_users[i];
			break;
		}
	}
	if (!user) return null;
	$(".username").html(username);
	$("#userlist").show();

	if (user.services) {
		// fill out the form for what we have
		for (var k=0;k<user.services.length;k++) {			
			for (const property in user.services[k]) {
				if (property == "service") continue;
				
				// get base from..
				//var template = $("#"+user.services[k].service+"_config");				
// allows only one of each... 	FIXME copy one for each instance	
				
				$("#"+user.services[k].service+"_"+property).val(user.services[k][property]);
				if (property == "id") {
					// existing....
					$("#"+user.services[k].service+"_"+property).hide();
				}
			}
		}
	}
	return user;
}

var glob_edit_u = null; 
function editUser(username) {
	if (glob_edit_u) {
		// one at a time only
		$("#userInfo").hide();
		if (glob_edit_u == username) {
			glob_edit_u = null;
			return;
		}
	}
	glob_edit_u = username;	
	
	$(".inform").val(""); 	// clear the forms
	$(".inform_id").val("new"); 	// clear the forms
	getUserInfo(username); 	// fill out the form
	
	$("#userlist").hide();
	$("#userInfo").show();

	// position form: FIXME
	
	
}

// save the user info
function saveUser(username) {
	
	var services = [];
	
	// Save twitter
	//var v_service = "twitter";
	var v_t_id = $("#twitter_id").val();
	var v_consumer_key = $("#twitter_consumer_key").val();
	var v_consumer_secret = $("#twitter_consumer_secret").val();
	var v_access_token = $("#twitter_access_token").val();
	var v_access_token_secret = $("#twitter_access_token_secret").val();		
	var twitter_serviceparams = {
			service: "twitter", 
			id: v_t_id, 
			consumer_key: v_consumer_key, 
			consumer_secret: v_consumer_secret, 
			access_token: v_access_token, 
			access_token_secret: v_access_token_secret};
	if (v_consumer_key && v_consumer_secret && v_access_token_secret && v_access_token) {
		if (v_consumer_key.length > 5 && v_consumer_secret.length > 5 && v_access_token_secret.length > 5 && v_access_token.length > 5) {
			services.push(twitter_serviceparams);
		}
	}

	// Save SMS
	var v_sms_id = $("#sms_id").val();
	var v_provider = $("#sms_provider").val();
	var v_account_sid = $("#sms_account_sid").val();
	var v_auth_token = $("#sms_auth_token").val();
	var v_phone_number = $("#sms_phone_number").val();

	var sms_serviceparams = {
			service: "sms", 
			id: v_sms_id, 
			provider: v_provider, 
			phone_number: v_phone_number, 
			account_sid: v_account_sid, 
			auth_token: v_auth_token};
	
	if (v_provider && v_account_sid && v_auth_token && v_phone_number) {
		if (v_provider.length > 3 && v_account_sid.length > 5 && v_auth_token.length > 5 && v_phone_number.length >= 10) {
			services.push(sms_serviceparams);
		}
	}	
	
	scsUpdateUser(username, services, function(data) {
		if (data == null || data.code == 401) {
			window.location.href = "/index.html";
			return;
		}
		glob_edit_u = null;
		$("#userInfo").hide();
		getUsers();
	});
	

}


function delUser(username) {
	scsDelUser(username, function(data) {
		if (data == null || data.code == 401) {
			window.location.href = "/index.html";
			return;
		}
		getUsers();
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

function scsUpdateSettings(username, password, sedro_access_key, poll_interval, cb) {	
	var dat = "{ "; 
    if (username) dat += "\"username\": \"" + username + "\"";
    if (password) dat += ", \"password\": \"" + password + "\"";
    if (poll_interval) dat += ", \"poll_interval\": \"" + poll_interval + "\"";
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

function scsAddUser(username, sedro_persona, cb) {	
	var dat = "{ "; 
    dat += "\"username\": \"" + username + "\"";
    dat += ", \"sedro_persona\": \"" + sedro_persona + "\"";
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


function scsUpdateUser(username, services, cb) {	
	if (!services || services.length < 1) return;
	
	var dat = "{"; 
// FIXME allow add / update / del service info	
	dat += "\"services\": [";
	
	// for each service set
	for (var i=0;i<services.length;i++) {
		var serviceparams = services[i];
		dat += "{";
		var service = serviceparams["service"];
		dat += "\"" + service + "\":{";
		var first = true;
		if (serviceparams) {
			for (const property in serviceparams) {
				if (!first) dat += ", ";
				else first = false;
				dat += "\""+property+"\": \"" + serviceparams[property] + "\"";
			}
		}
		dat += "}}";
		if (i != (services.length-1)) dat += ",";
	}
	dat += "]";
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

// get a cookie
function getCookie(name) {
  var value = "; " + document.cookie;
  var parts = value.split("; " + name + "=");
  if (parts.length == 2) return parts.pop().split(";").shift();
}
