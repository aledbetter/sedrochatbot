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


//FIXME these must go
var glob_chid = null;
var anz_in_progress = false;
var g_chat_id = null;
var g_language = "english";
var g_msg_num = 1;
var glob_from_full_name = null;
var glob_from_name = null;

//get location for chatbot APIs
var glatitude = null;
var glongitude = null;	
if (navigator.geolocation) {
	navigator.geolocation.getCurrentPosition(function(position) {
		glatitude = position.coords.latitude;
		glongitude = position.coords.longitude;
	});
}

function resetPage() {
	$(".sedro_version").html(getSedroVersion()); // add version	
	$("#interact_msg").html("");
	if ($("#interact_msg").length) $("#interact_msg").scrollTop($("#interact_msg")[0].scrollHeight);
	// switch to woke
	$(".notwoke").show();
	$(".woke").hide();
	$("#interact_text").val(""); 
	$("#interact_wd").show();
	glob_chid = null;
	g_msg_num = 1;
}
var glob_bye_delay = (1000 * 10);
var glob_bye_handler = function() {
	resetPage();
}


/////////////////////////////////////////////////////////////////
$(document).ready(function() {
	resetPage();
	//setAPIChatHost("/api/1.0");
	
	// hangup handler
	byeHandler = function() {
		resetPage();
	}
		
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// get the chat ID
	g_chat_id = getUrlParam('id');
	if (!g_chat_id) {
		alert("No Chat ID");
		return;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Add interactive message
	$("#wake_now_bt").on('click', function (e) {
		$("#interact_msg").html("");
		$("#interact_msg").scrollTop($("#interact_msg")[0].scrollHeight);

		// switch to woke
		$(".notwoke").hide();
		$(".woke").show();
		$("#xaction").html("&nbsp;Conversation");
		$("#interact_wd").show();

		// ready..
		scsChatWake(g_chat_id, g_language, chatHandler);
		// clear it
		$("#interact_text").val(""); 
		waitChid();	
	});

	$("#bye_now_bt").on('click', function (e) {		
		var txt = $("#interact_text").val(); 
		if (txt && txt.length > 0) {
			addLocalMsg(txt, "#interact_msg");
			$("#interact_msg").scrollTop($("#interact_msg")[0].scrollHeight);
		}
		// ready..
		$("#interact_wd").hide();
		scsChatBye(glob_chid, chatHandler);
	});
	
	// bye if they leave and are in conversation
	$(window).unload(function() {
		if (glob_chid) scsChatBye(glob_chid, chatHandler);
	});
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Add interactive message
	$("#send_now_bt").on('click', function (e) {
		if (anz_in_progress) return;
		anz_in_progress = true;
		var txt = $("#interact_text").val(); 
		if (!txt || txt.length < 1) {
			anz_in_progress = false;
			return;
		}
		addLocalMsg(txt, "#interact_msg");
		$("#interact_msg").scrollTop($("#interact_msg")[0].scrollHeight);

		// ready..
		scsChatMsg(glob_chid, txt, chatHandler);
		waitChid();
		$("#interact_text").val(""); 
	});
});


/////////////////////////////////////////////////////////////
// RENDER MESSAGES
// add local message
function addLocalMsg(msg, tag) {
	var cmsg = "<div class='c_msg' data-num='"+g_msg_num+"'>"+msg+"</div>";
	$(tag).append(cmsg) 
	g_msg_num++;
}

// handle chat responses
var chatHandler = function (resp) {
	if (!resp) {
		glob_chid = null;
		$("#interact_msg").html("Error Connecting...");
		$("#interact_wd").hide();
		return;
	}
	if (resp.code == 429 || !resp.info) {
		glob_chid = null;
		$("#interact_msg").html("too many sessions");
		$("#interact_wd").hide();
		return;
	}
	
	// interact_msg
	if (resp.info.from) {
		glob_from_name = resp.info.from;
		$(".fromName").html(glob_from_name);
	}
	if (resp.info.from_full_name) {
		glob_from_full_name = resp.info.from_full_name;
		$(".fromFullName").html(glob_from_full_name);
	}
	glob_chid = resp.info.call_id;
	//$("#interact_wd").show();

	if (resp.list && resp.list.length > 0) {
		for (var i=0;i<resp.list.length;i++) {
			if (resp.list[i].r == "false" || !resp.list[i].msg) continue; // only if remote add..
			addMsg(addRemoteMsg(resp.list[i]), resp.list[i].pre_wait, resp.list[i].post_wait, resp.list[i].event);
		}
	}
	anz_in_progress = false;
	
	if (!glob_chid) addMsg(null, 0, 0, "bye");
	// process the messages
	processMsgs("#interact_msg");
}

//do the poll for new messages
function pollChatMsg() {
	if (anz_in_progress) return;
    anz_in_progress = false;
	scsChatPoll(glob_chid, chatHandler);
}

function addRemoteMsg(msg) {
	var cmsg = "<div class='s_msg' data-num='"+msg.num+"' data-qn='"+msg.qn+"' data-event='"+msg.event+"' data-time='"+msg.time+" 'data-from='"+msg.from+"' ";
	cmsg += " title='qn: "+msg.qn+" e: "+msg.event+" rply_type: "+msg.rply_type+" rply_base: "+msg.rply_base+" req_base: "+msg.req_base+" w: "+msg.pre_wait+" / "+msg.post_wait+" ' ";
	cmsg += ">"+updateNewLine(msg.msg)+"</div>";
	if (g_msg_num < msg.num) g_msg_num = msg.num;
	return cmsg;
}

// message queue to post messages with correct delay and possible retraction
var gmsg_que = [];
function addMsg(msgHtml, pre_wait, post_wait, event) {
	var m = {msg:msgHtml, pre_wait:pre_wait, post_wait:post_wait, event:event};
	gmsg_que.push(m);
}
function processMsgs(tag) {
	if (gmsg_que.length < 1) return;
	var m = gmsg_que.shift();
	waitPreWriteMessage(tag, m);
}
function waitPreWriteMessage(tag, msg) {
	if (msg.pre_wait > 0) {
		var mt = "...";
		if (msg.event == 'typing' && msg.msg != null) mt = msg.msg;
		$(tag).append("<div class='s_msg s_typeing' style='font-weight:bold'>"+mt+"</div>");
		$("#interact_msg").scrollTop($("#interact_msg")[0].scrollHeight);
	} 
    setTimeout(function () {	
		if (msg.event == 'typing') {												
			pollChatMsg();	// make callback 
			return;
		}
		if (msg.msg != null) {
			$(tag + " .s_typeing").hide();
			$(tag).append(msg.msg); // write message
			$("#interact_msg").scrollTop($("#interact_msg")[0].scrollHeight);
		}
		waitPostMessage(tag, msg);
	}, (msg.pre_wait * 100));
}
function waitPostMessage(tag, msg) {
    setTimeout(function () {
		if (msg.event == 'bye') {
			glob_chid = null;
			$("#interact_wd").hide();
			setTimeout(glob_bye_handler, glob_bye_delay);		
		}
		processMsgs(tag);
	}, (msg.post_wait * 100));
}
//new line for web chat
function updateNewLine(text) {
	return text.replace(/\\n/g, "<br>");
}


/////////////////////////////////////////////////////////////
// local Chat API
function scsChatWake(chat_id, language, cb) {
	var d = new Date();
	var calltime = d.toISOString(); // "yyyy-MM-dd'T'HH:mm:ss.SSSZ"; 

	var ind = "{ \"chat_id\": \"" + chat_id + "\""; 	
    ind += ", \"time\": \"" + calltime + "\"";
    if (glatitude) ind += ", \"latitude\": \"" + glatitude + "\"";
    if (glongitude) ind += ", \"longitude\": \"" + glongitude + "\"";    
	var tzn = Intl.DateTimeFormat().resolvedOptions().timeZone;
    ind += ", \"timezone\": \"" + tzn + "\"";	
    ind += ", \"language\": \"" + language + "\"}";
    
	$.ajax({url: "/api/1.0/chat/wake", type: 'POST', dataType: "json", crossDomain: true, contentType: 'application/json', data: ind, 
		success: function(data){
			cb(data);
		}, error: function(xhr) {
			cb(null);
		}
	});
}
function scsChatMsg(call_id, text, cb) {
	var ind = "{ \"call_id\": \"" + call_id + "\", \"text\": \"" + text.escapeSpecialChars() + "\"}";
	$.ajax({url: "/api/1.0/chat/msg", type: 'POST', dataType: "json", crossDomain: true, contentType: 'application/json', data: ind, 
		success: function(data){
			cb(data);
		}, error: function(xhr) {
			cb(null);
		}
	});
}
function scsChatPoll(call_id, cb) {
	var ind = "{ \"call_id\": \"" + call_id + "\"}";
	$.ajax({url: "/api/1.0/chat/poll", type: 'POST', dataType: "json", crossDomain: true, contentType: 'application/json', data: ind, 
		success: function(data){
			cb(data);
		}, error: function(xhr) {
			cb(null);
		}
	});
}
function scsChatBye(call_id, cb) {
	var ind = "{ \"call_id\": \"" + call_id + "\"}";
	$.ajax({url: "/api/1.0/chat/bye", type: 'POST', dataType: "json", crossDomain: true, contentType: 'application/json', data: ind, 
		success: function(data){
			if (data.code == 200) cb(data);
			else cb(null);
		}, error: function(xhr) {
			cb(null);
		}
	});
}


/////////////////////////////////////////////////////////////
// UTILS
var getUrlParam = function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
        }
    }
};

function waitChid() {
    if (anz_in_progress == true) {
        setTimeout(waitChid, 50);//wait 50 millisecnds then recheck
        return;
    }
    pollChidBase(true);
}
// poll for async messages
function pollChid() {
	pollChidBase(false);
}
// polling every 30 seconds when no waiting for call...
function pollChidBase(init) {
	if (glob_chid == null) return; // end
    if (!(anz_in_progress || init)) {
    	pollChatMsg();
    }
    setTimeout(pollChid, 1000*30); // poll every 30 seconds
}
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
