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


////////////////////////////////////////////////////////////////////////////
// EXTEND THIS FOR NEW SERVICES
////////////////////////////////////////////////////////////////////////////
function serviceSave(id, username, service) {
	var services = [];
	
	if (service == "twitter") {
		// Save twitter
		var v_t_id = $("#"+id+"_id").val();
		var v_consumer_key = $("#"+id+"_consumer_key").val();
		var v_consumer_secret = $("#"+id+"_consumer_secret").val();
		var v_access_token = $("#"+id+"_access_token").val();
		var v_access_token_secret = $("#"+id+"_access_token_secret").val();		
		var v_twitter_private = $("#"+id+"_doprivate").val();		
		var v_twitter_public = $("#"+id+"_dopublic").val();		
		
		var twitter_serviceparams = {
				service: "twitter", 
				id: v_t_id, 
				doprivate: v_twitter_private, 
				dopublic: v_twitter_public, 
				consumer_key: v_consumer_key, 
				consumer_secret: v_consumer_secret, 
				access_token: v_access_token, 
				access_token_secret: v_access_token_secret};
		if (v_consumer_key && v_consumer_secret && v_access_token_secret && v_access_token) {
			if (v_consumer_key.length > 5 && v_consumer_secret.length > 5 && v_access_token_secret.length > 5 && v_access_token.length > 5) {
				services.push(twitter_serviceparams);
			}
		}
		
	} else 	if (service == "sms") {
		// Save SMS
		var v_sms_id = $("#"+id+"_id").val();
		var v_provider = $("#"+id+"_provider").val();
		var v_account_sid = $("#"+id+"_account_sid").val();
		var v_auth_token = $("#"+id+"_auth_token").val();
		var v_phone_number = $("#"+id+"_phone_number").val();
		var v_sms_callback_url = $("#"+id+"_sms_callback_url").val();
			
		var sms_serviceparams = {
				service: "sms", 
				id: v_sms_id, 
				sms_callback_url: v_sms_callback_url, 			
				provider: v_provider, 
				phone_number: v_phone_number, 
				account_sid: v_account_sid, 
				auth_token: v_auth_token};
		
		if (v_provider && v_account_sid && v_auth_token && v_phone_number) {
			if (v_provider.length > 3 && v_account_sid.length > 5 && v_auth_token.length > 5 && v_phone_number.length >= 10) {
				services.push(sms_serviceparams);
			}
		}	
	}
	
	scsUpdateUser(username, services, function(data) {
		if (data == null || data.code == 401) {
			window.location.href = "/index.html";
			return;
		}
		getUsers();
	});
}



////////////////////////////////////////////////////////////////////////////
// Standard code follows
////////////////////////////////////////////////////////////////////////////
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
		var shost = $("#set_sedro_host").val();
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
		var cbup = $("#user_callback").val();
		if (cbup && cbup != "") return;				
		scsAddUser(u, up, cbup, function(data) {
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

function logout() {
	scsLogout(function(data) {
		window.location.href = "/index.html";
	});
}
	

function getSettings() {
	$("#setting_username, #setting_poll_interval, #setting_sedro_access_key, #setting_sedro_host, #setting_database_path").html("..."); 

	scsGetSettings(function(data) {
		if (data == null || data.code == 401) {
			window.location.href = "/index.html";
			return;
		}
		$("#setting_poll_interval").html(data.info.poll_interval); 
		$("#set_poll_interval").val(data.info.poll_interval); 
		$("#setting_username").html(data.info.username); 
		$("#set_username").val(data.info.username); 
		
		if (data.info.database == true) {
			$("#setting_database_path").html(data.info.database_path); 
		} else {
			$("#setting_database_path").html("NONE"); 
		}
		if (data.info.sedro_host) {
			$("#set_sedro_host").val(data.info.sedro_host); 
			$("#setting_sedro_host").html(data.info.sedro_host); 			
		}
		if (data.info.sedro_access_key) {
			$("#set_sedro_access_key").val(data.info.sedro_access_key); 
			$("#setting_sedro_access_key").html(data.info.sedro_access_key); 
			glob_api_key = data.info.sedro_access_key;
			setAPIKey(data.info.sedro_access_key);
			setAPIHost(data.info.sedro_host);
			sedroGetPersonas(function (data) {
				var pselect = "";
				var pUser = "";
				// persona select list
				if (data.list && data.list.length > 0) {
					for (var i=0;i<data.list.length;i++) {
						pselect += "<option value='"+data.list[i]+"'>"+data.list[i]+"</option>";						
						pUser += "<div class='fLn persona'>"+data.list[i]+"</div>";						
					}
				}
				if (pselect == "") {
						pselect = "<option value=''>No Personas</option>";
						pUser += "<div class='fLn persona' style='color:red'>No Personas</div>";						
				}
				$(".persona_list").html(pselect);
				$(".user_persona_list").html(pUser);			
			});
			// get message callbacks 
			scsMessageCallback(function (data) {
				// persona select list
				if (data.list && data.list.length > 0) {
					var pselect = "<option value=''>No Callback</option>";
					for (var i=0;i<data.list.length;i++) {
						pselect += "<option value='"+data.list[i]+"'>"+data.list[i]+"</option>";						
					}
					$(".callback_list").html(pselect);
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
				usr += "<div class='fLn' id='ua_"+data.info.users[i].username+"' style='padding-top:10px;padding-bottom:10px;margin-bottom:10px;border-bottom:1px solid #555;position:relative;background:#F7F7F7'>";				
					usr += "<div class='bslink' onClick='delUser(\""+data.info.users[i].username+"\");' style='width:70px;text-align:center;font-size:16px;position:absolute;right:10px;background:#EEE;'>Del</div>";	
					usr += "<div style='width:180px;text-align:center;font-size:16px;position:absolute;top:8px;right:190px'>" +
							"<select id='"+data.info.users[i].username+"_service' style='width:160px;'>" +
								"<option value='twitter'>Twitter</option>" +
								"<option value='sms'>SMS</option></select></div>";	

					usr += "<div class='bslink' onClick='addService(\""+data.info.users[i].username+"\");' style='width:100px;text-align:center;font-size:16px;position:absolute;right:90px;background:#EEE;'>Add Service</div>";	
					usr += "<div class='fLn' style='padding-bottom:10px;'>";
						usr += "<div style='text-align:left'><b>Username: " + data.info.users[i].username +"</b></div>";
						usr += "<div style='text-align:left'><b>Sedro Persona: " + data.info.users[i].sedro_persona +"</b></div>";
						if (data.info.users[i].callback) usr += "<div style='text-align:left'><b>Message Cb: " + data.info.users[i].callback +"</b></div>";
					usr += "</div>";				
					usr += "<div id='show_ua'>";
					
					usr += "<div class='fLn' id='"+data.info.users[i].username+"_add_service' style='display:none'></div>";
	
					// all the serviecs
					if (data.info.users[i].services) {
						for (var k=0;k<data.info.users[i].services.length;k++) {
							usr += "<div class='fLn' style='position:relative;border-top:1px solid #CCC'>";
							usr += "<h2 class='fLn' style='margin-bottom:0px;'><b>Service: " + data.info.users[i].services[k].service + "</b></h2>";

							var sid = null;
							var service = null;
							for (const property in data.info.users[i].services[k]) {
								if (property == "service") {
									service = data.info.users[i].services[k][property];
									continue;
								}
								if (property == "id") sid = data.info.users[i].services[k][property];
								usr += "<div class='fLn'>";
									usr += "<div style='text-align:left'><b>" + property + "</b>: "+data.info.users[i].services[k][property]+"</div>";
								usr += "</div>";				
							}
							usr += "<div class='bslink' onClick='delService(\""+sid+"\");' style='width:70px;text-align:center;font-size:16px;position:absolute;top:14px;right:10px;background:#EEE;'>Del</div>";	
							usr += "<div class='bslink' onClick='editService(\""+data.info.users[i].username+"\", \""+sid+"\");' style='width:70px;text-align:center;font-size:16px;position:absolute;top:14px;right:90px;background:#EEE;'>Edit</div>";	
							
							usr += "<div class='fLn' id='"+sid+"_edit' style='display:none'>";
							// make form for service ID
							var template = $("#"+service+"_config").html();
							template = template.replaceAll("=\""+service+"_", "=\""+sid+"_");
							template = template.replaceAll("x"+service, sid);
							template = template.replaceAll("xusername", data.info.users[i].username);
							usr += template;	
							usr += "</div>";
							
							usr += "</div>";
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
				if (!user.services[k][property] || user.services[k][property] == "") continue;
				$("#"+user.services[k].id+"_"+property).val(user.services[k][property]);
			}
		}
	}
	return user;
}

function delService(id) {
	scsDelUser(id, function(data) {
		if (data == null || data.code == 401) {
			window.location.href = "/index.html";
			return;
		}
		getUsers();
	});
}

function addService(username) {
	var service = $("#"+username+"_service").val();
	// make form for service ID
	var template = $("#"+service+"_config").html();
	template = template.replaceAll("=\""+service+"_", "=\""+username+"_");
	template = template.replaceAll("x"+service, username);
	template = template.replaceAll("xusername", username);

	$("#"+username+"_add_service").html(template).show();
	$("#"+username+"_add_service .inform_id").val("new"); 
	$("#"+username+"_add_service #the_id").hide();

}
function editService(username, id) {
	$("#"+id+"_edit").show();	
	$(".inform").val(""); 	// clear the forms
	$(".inform_id").val("new"); 	// clear the forms
	getUserInfo(username); 	// fill out the form		
}
function serviceCancel(id) {
	$("#"+id+"_add_service").hide();
	$("#"+id+"_edit").hide();
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


////////////////////////////////////////////////////////////////////////////
// API to ChatServer
////////////////////////////////////////////////////////////////////////////
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
function scsMessageCallback(cb) {
	$.ajax({url: "/api/1.0/callbacks", type: 'GET', dataType: "json", contentType: 'application/json', 
	  success: function(data){
		  cb(data);
	  }, error: function(xhr) {
		  cb(null);
	  }
	});
}

function scsAddUser(username, sedro_persona, msg_callback, cb) {	
	var dat = "{ "; 
    dat += "\"username\": \"" + username + "\"";
    dat += ", \"sedro_persona\": \"" + sedro_persona + "\"";
    if (msg_callback) dat += ", \"callback\": \"" + msg_callback + "\"";
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
function scsDelService(id, cb) {	
	$.ajax({url: "/api/1.0/service/"+id+"/del", type: 'POST', async: true, contentType: 'application/json', 
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

String.prototype.replaceAll = function(search, replacement) {
    var target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};
