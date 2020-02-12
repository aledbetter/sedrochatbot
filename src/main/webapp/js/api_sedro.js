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
var sedro_api_version =  "A.6265677228"; // **VERSION** keep in sync with pom.xml
var sedro_api_prod = true; // is in production (for tracking)
if (location.hostname === "localhost" || location.hostname === "127.0.0.1" || location.hostname === "") {
	sedro_api_prod = false;
}

function isProd() {
	return sedro_api_prod; 
}
function getSedroVersion() {
	return sedro_api_version; 
}

$(document).ready(function() {
	$(".sedro_version").html(getSedroVersion()); // add version
});
var glob_api_host_persona = "inteligent-chatbots.p.rapidapi.com";
var glob_api_key = null;

// set the API Key for all the calls
function setAPIKey(key) {
	glob_api_key = key;
}

function getUrl(url) {
	return "https://"+glob_api_host_persona+url;
}

////////////////////////////////////////////////////////////////////////////////////////
// CHAT
// WAKE -> MSG/POLL -> BYE
//
function postChatWake(ctx, persona, txt, user, caller_token, context, channel_type, language, save, max_qn, cb) {
    if (!persona) return;
    
	var ind = "{ \"text\": \"";
    if (txt) ind += txt.escapeSpecialChars();
    else ind += " ";
    ind += "\", \"event\": \"wake\""; 
    ind += ", \"persona\": \"" + persona  + "\""; 
    if (user) ind += ", \"user\": \"" + user.escapeSpecialChars() + "\""; 
    if (caller_token) ind += ", \"caller_token\": \"" + caller_token + "\""; 
    if (context) ind += ", \"context\": \"" + context  + "\""; 
    if (language) ind += ", \"language\": \"" + language  + "\""; 
    if (channel_type) ind += ", \"channel_type\": \"" + channel_type  + "\""; 
    if (save) ind += ", \"save\": \"" + save + "\"";  
    if (max_qn) ind += ", \"max_qn\": \"" + max_qn + "\"";
    ind += "}";
    postChat_api(ctx, "/persona/chat/wake", txt, ind, cb);
}
function postChatPoll(ctx, chid, cb) {
    var ind = "{ \"chid\": \"" + chid  + "\", \"event\": \"poll\"}";
    postChat_api(ctx, "/persona/chat/poll", null, ind, cb);
}
function postChatMsg(ctx, chid, txt, cb) {
    var ind = "{ \"text\": \"";
    if (txt) ind += txt.escapeSpecialChars();
    else ind += " ";
    ind += "\", \"chid\": \"" + chid  + "\"}";
    postChat_api(ctx, "/persona/chat/msg", txt, ind, cb);
}
function postChatBye(ctx, chid, cb) {
    var ind = "{ \"chid\": \"" + chid  + "\", \"event\": \"bye\"}";
    postChat_api(ctx, "/persona/chat/bye", null, ind, cb);
}

//single get response OR do command, no session
function postChatAsk(ctx, persona, txt, user, caller_token, context, channel_type, language, cb) {
    if (!persona) return;
	var ind = "{ \"text\": \"";
    if (txt) ind += txt.escapeSpecialChars();
    else ind += " ";
    ind += "\", \"event\": \"ask\""; 
    ind += ", \"persona\": \"" + persona  + "\""; 
    if (user) ind += ", \"user\": \"" + user.escapeSpecialChars() + "\""; 
    if (caller_token) ind += ", \"caller_token\": \"" + caller_token + "\""; 
    if (context) ind += ", \"context\": \"" + context  + "\""; 
    if (language) ind += ", \"language\": \"" + language  + "\""; 
    if (channel_type) ind += ", \"channel_type\": \"" + channel_type  + "\""; 
    ind += "}";
    postChat_api(ctx, "/persona/ask", txt, ind, cb);
}
// single add to knowledge
function postChatTell(ctx, persona, txt, user, caller_token, context, channel_type, language, cb) {
    if (!persona) return;
	var ind = "{ \"text\": \"";
    if (txt) ind += txt.escapeSpecialChars();
    else ind += " ";
    ind += "\", \"event\": \"ask\""; 
    ind += ", \"persona\": \"" + persona  + "\""; 
    if (user) ind += ", \"user\": \"" + user.escapeSpecialChars() + "\""; 
    if (caller_token) ind += ", \"caller_token\": \"" + caller_token + "\""; 
    if (context) ind += ", \"context\": \"" + context  + "\""; 
    if (language) ind += ", \"language\": \"" + language  + "\""; 
    if (channel_type) ind += ", \"channel_type\": \"" + channel_type  + "\""; 
    ind += "}";
    postChat_api(ctx, "/persona/tell", txt, ind, cb);
}
function postChat_api(ctx, turl, txt, ind, cb) {
	if (!glob_api_key) return;
	$.ajax({url: getUrl(turl), type: 'POST', dataType: "json", crossDomain: true, contentType: 'application/json', data: ind, 
	  headers: { 'x-rapidapi-host': glob_api_host_persona, 'x-rapidapi-key': glob_api_key},
	  success: function(data){
		  if (data.code == 200) {
			  cb(txt, data);
		  } else {
			  cb(txt, null);
		  }
	  }, error: function(xhr) {
		  cb(txt, null);
	  }
	});	
}



////////////////////////////////////////////////////////////////////////////////////
// ACCOUNT / PERSONA
////////////////////////////////////////////////////////////////////////////////////
function sedroGetAccount(cb) {
	if (!glob_api_key) return;
	var turl = "/tenant/get";
	$.ajax({url: getUrl(turl), type: 'POST', async: true, crossDomain: true, contentType: 'application/x-www-form-urlencoded', 
	  headers: { 'accept': 'application/json', 'x-rapidapi-host': glob_api_host_persona, 'x-rapidapi-key': glob_api_key},
	  "data": {},
	  success: function(data){
		  cb(data);
	  }, error: function(xhr) {
		  cb(null);
	  }
	});
}

function sedroGetPersonas(cb) {
	if (!glob_api_key) return;
	var turl = "/tenant/personas";
	$.ajax({url: getUrl(turl), type: 'GET', dataType: "json", crossDomain: true, contentType: 'application/json', 
	  headers: { 'x-rapidapi-host': glob_api_host_persona, 'x-rapidapi-key': glob_api_key},
	  success: function(data){
		  cb(data);
	  }, error: function(xhr) {
		  cb(null);
	  }
	});
}

// send full complete map
function sedroGeneratePersona(definition, cb) {
	if (!glob_api_key) return;
	var turl = "/tenant/persona/add";
	var ind = "{ \"definition\": \"" + definition.escapeSpecialChars() + "\"}"; 
	$.ajax({url: getUrl(turl), type: 'POST', dataType: "json", crossDomain: true, contentType: 'application/json', data: ind, 
	  headers: { 'x-rapidapi-host': glob_api_host_persona, 'x-rapidapi-key': glob_api_key},
	  success: function(data){
		  cb(ctx, data);
	  }, error: function(xhr) {
		  cb(ctx, null);
	  }
	});	
}
// send name/value sets[first_name/last_name/email/sex]
function sedroGeneratePersonaMap(ctx, nvlist, cb) {
	if (!glob_api_key) return;
	if (!nvlist && nvlist.length > 0) return;
	var turl = "/tenant/persona/add";
	var ind = "{ \"params\":[";
	var fi = true;
	for (const property in nvlist) {
		if (!fi) ind += ",";
		ind += "{\"n\":\"" + property  + "\", \"v\":\""+nvlist[property].escapeSpecialChars()+"\"}"; 		
		fi = false;
	}	
	ind += "]}"; 
	$.ajax({url: getUrl(turl), type: 'POST', dataType: "json", crossDomain: true, contentType: 'application/json', data: ind, 
	  headers: { 'x-rapidapi-host': glob_api_host_persona, 'x-rapidapi-key': glob_api_key},
	  success: function(data){
		  cb(ctx, data);
	  }, error: function(xhr) {
		  cb(ctx, null);
	  }
	});	
}

function sedroRemovePersona(ctx, persona, cb) {
	if (!glob_api_key) return;
	var turl = "/tenant/persona/remove/"+persona;
	$.ajax({url: getUrl(turl), type: 'POST', dataType: "json", crossDomain: true, contentType: 'application/json', 
	  headers: { 'x-rapidapi-host': glob_api_host_persona, 'x-rapidapi-key': glob_api_key},
	  success: function(data){
		  cb(ctx, data);
	  }, error: function(xhr) {
		  cb(ctx, null);
	  }
	});	
}
// get raw persona defintion
function sedroGetPersonaRaw(ctx, persona, cb) {
	if (!glob_api_key) return;
	var turl = "/tenant/persona/"+persona+"/getraw";
	$.ajax({url: getUrl(turl), type: 'GET', dataType: "json", crossDomain: true, contentType: 'application/json',
		  headers: { 'x-rapidapi-host': glob_api_host_persona, 'x-rapidapi-key': glob_api_key},
	  success: function(data){
		  cb(ctx, persona, data);
	  }, error: function(xhr) {
		  cb(ctx, persona, null);
	  }
	});
}
function sedroPersonaGenerateDb(ctx, persona, content, cb) {
	if (!glob_api_key) return;
	var turl = "/tenant/persona/knowledge";
	var ind = "{ \"content\": \"" + content.escapeSpecialChars() + "\""; 
    ind += ", \"persona\": \"" + persona + "\"}";
	$.ajax({url: getUrl(turl), type: 'POST', dataType: "json", crossDomain: true, data: ind,
		  headers: { 'x-rapidapi-host': glob_api_host_persona, 'x-rapidapi-key': glob_api_key},
	  success: function(data){
		  cb(ctx, persona, content, data);
	  }, error: function(xhr) {
		  cb(ctx, persona, content, null);
	  }
	});	
}

// FORMS
function sedroPersonaGetForms(ctx, persona, cb) {
	if (!glob_api_key || !persona) return;
	var turl = "/tenant/persona/"+persona+"/forms";
	$.ajax({url: getUrl(turl), type: 'GET', dataType: "json", crossDomain: true, contentType: 'application/json', 
		  headers: { 'x-rapidapi-host': glob_api_host_persona, 'x-rapidapi-key': glob_api_key},
	  success: function(data){
		  cb(ctx, persona, data);
	  }, error: function(xhr) {
		  cb(ctx, persona, null);
	  }
	});
}
// add form embeded: load/action/active/null
function sedroPersonaAddForm(ctx, persona, form, type, main, cb) {
	if (!glob_api_key) return;
	if (!persona || !name) return;
	var turl = "/tenant/persona/"+persona+"/form/add";
	var ind = "{ \"form\": \"" + form.escapeSpecialChars() + "\""; 
    if (type) ind += ", \"type\": \"" + type + "\"";
    if (main == "true") ind += ", \"main\": \"true\"";
    ind += "}";
	$.ajax({url: getUrl(turl), type: 'POST', dataType: "json", crossDomain: true, data: ind, 
		  headers: { 'x-rapidapi-host': glob_api_host_persona, 'x-rapidapi-key': glob_api_key},
	  success: function(data){
		  cb(ctx, persona, data);
	  }, error: function(xhr) {
		  cb(ctx, persona, null);
	  }
	});	
}
function sedroPersonaAddFormRemote(ctx, persona, url, cb) {
	if (!glob_api_key) return;
	if (!persona || !url) return;
	var turl = "/tenant/persona/"+persona+"/form/add";
	var ind = "{\"url\": \"" + url + "\"}";
	$.ajax({url: getUrl(turl), type: 'POST', dataType: "json", crossDomain: true, data: ind, 
		  headers: { 'x-rapidapi-host': glob_api_host_persona, 'x-rapidapi-key': glob_api_key},
	  success: function(data){
		  cb(ctx, persona, url, data);
	  }, error: function(xhr) {
		  cb(ctx, persona, url, null);
	  }
	});	
}
//get raw Form defintion
function sedroPersonaGetFormRaw(ctx, persona, form, cb) {
	if (!glob_api_key) return;
	form = form.escapeSpecialChars();
	var turl = "/tenant/persona/"+persona+"/form/"+form+"/getraw";
	$.ajax({url: getUrl(turl), type: 'GET', dataType: "json", crossDomain: true, contentType: 'application/json', 
		  headers: { 'x-rapidapi-host': glob_api_host_persona, 'x-rapidapi-key': glob_api_key},
	  success: function(data){
		  cb(ctx, persona, form, data);
	  }, error: function(xhr) {
		  cb(ctx, persona, form, null);
	  }
	});
}
function sedroPersonaRemoveForm(ctx, persona, form, cb) {
	if (!glob_api_key) return;
	if (!persona || !form) return;
	var turl = "/tenant/persona/"+persona+"/form/"+form+"/remove";
	$.ajax({url: getUrl(turl), type: 'POST', dataType: "json", crossDomain: true, 
		  headers: { 'x-rapidapi-host': glob_api_host_persona, 'x-rapidapi-key': glob_api_key},
	  success: function(data){
		  cb(ctx, persona, data);
	  }, error: function(xhr) {
		  cb(ctx, persona, null);
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

