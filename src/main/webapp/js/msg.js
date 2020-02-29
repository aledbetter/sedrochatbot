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

var anz_in_progress = false;
var glob_chid = null;
var g_context = "itx";
var g_channel_type = "chat";
var g_language = "english";

var g_tenant = "";
var g_caller_token = null;
var g_msg_num = 1;


function resetPage() {
	$(".sedro_version").html(getSedroVersion()); // add version
	
	$("#interact_msg").html("");
	if ($("#interact_msg").length) $("#interact_msg").scrollTop($("#interact_msg")[0].scrollHeight);
	// switch to woke
	$(".notwoke").show();
	$(".woke").hide();
	$("#interact_text").val(""); 
	$("#interact_wd").show();
	$("#asktell_text").removeClass("warn").val(""); 
	glob_chid = null;
	g_msg_num = 1;
}

/////////////////////////////////////////////////////////////////
$(document).ready(function() {
	resetPage();
	
	// hangup handler
	byeHandler = function() {
		resetPage();
	}
	
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
		    anz_in_progress = false;
			postChatPoll(g_tenant, glob_chid, chatHandler);
	    }
        setTimeout(pollChid, 1000*30); // poll every 30 seconds
	}
	
	var g_tenant = getUrlParam('tenant');
	if (!g_tenant) {
		// FIXME get the callers tenant
		alert("No tenant selected: http://xxx/extern/msg?teant=XXX_TENANT_ID_XXX");
	}
	g_caller_token = getUrlParam('caller_token');
	if (g_caller_token) $("#caller_token").val(g_caller_token);
	
	// get the select list
	getPersonasSelect(g_tenant);
	var pers = getUrlParam('persona');
	if (pers) {
		$("#persona").val(pers);
		$("#wake_now_bt").click();
	}

	
	// get persona list select
	function getPersonasSelect(ctx) {
		sedroGetTenant(ctx, function (ctx, data) {
			var pselect = "";
			if (data) {
				if (data.results[0].personas) {
					for (var i=0;i<data.results[0].personas.length;i++) {
						pselect += "<option value='"+data.results[0].personas[i]+"'>"+data.results[0].personas[i]+"</option>";						
					}
				}
			}
			$("#persona").html(pselect);
		});			
	}
	

	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Add interactive message
	$("#wake_now_bt").on('click', function (e) {
		var persona = $("#persona").val(); 
		if (persona == "") {
			alert("select a persona");
			return;
		}
		$("#interact_msg").html("");
		$("#interact_msg").scrollTop($("#interact_msg")[0].scrollHeight);

		// switch to woke
		$(".notwoke, .askTell").hide();
		$(".woke").show();
		$(".personaName").html(persona);
		$("#xaction").html("&nbsp;Conversation");

		var author = null; // todo 		
		var ctoken = $("#caller_token").val(); 
		if (!ctoken || ctoken.length < 1) ctoken = null;
		
		// ready..
		postChatWake(g_tenant, persona, null, author, ctoken, g_context, g_channel_type, g_language, "false", chatHandler);
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
		postChatBye(g_tenant, glob_chid, chatHandler);
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
		postChatMsg(g_tenant, glob_chid, txt, chatHandler);
		waitChid();
		$("#interact_text").val(""); 
	});
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Ask API for command OR question
	$("#ask_now_bt").on('click', function (e) {
		var persona = $("#persona").val(); 
		if (persona == "") {
			alert("select a persona");
			return;
		}
		if (anz_in_progress) return;
		anz_in_progress = true;
		var txt = $("#asktell_text").val(); 
		if (!txt || txt.length < 1) {
			anz_in_progress = false;
			$("#asktell_text").addClass("warn");
			return;
		}

		// switch to woke
		$(".notwoke").hide();
		$(".askTell, .woke").show();
		$(".personaName").html(persona);
		$("#xaction").html("&nbsp;Ask");

		var ctoken = $("#caller_token").val(); 
		if (!ctoken || ctoken.length < 1) ctoken = null;
		$("#asktell_text").removeClass("warn"); 
		addLocalMsg(txt, "#asktell_response");
		
		// ready..
		postChatAsk(g_tenant, persona, txt, null, ctoken, g_context, g_channel_type, g_language, askTellHandler);
		waitChid();	
	});
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Tell API for command OR question
	$("#tell_now_bt").on('click', function (e) {
		var persona = $("#persona").val(); 
		if (persona == "") {
			alert("select a persona");
			return;
		}
		if (anz_in_progress) return;
		anz_in_progress = true;
		var txt = $("#asktell_text").val(); 
		if (!txt || txt.length < 1) {
			anz_in_progress = false;
			$("#asktell_text").addClass("warn");
			return;
		}

		// switch to woke
		$(".notwoke").hide();
		$(".askTell, .woke").show();
		$(".personaName").html(persona);
		$("#xaction").html("&nbsp;Tell");
		
		var ctoken = $("#caller_token").val(); 
		if (!ctoken || ctoken.length < 1) ctoken = null;
		$("#asktell_text").removeClass("warn");
		addLocalMsg(txt, "#asktell_response");
		
		// ready..
		postChatTell(g_tenant, persona, txt, null, ctoken, g_context, g_channel_type, g_language, askTellHandler);
		waitChid();	
	});
	
	// reset to base
	$("#reset_bt").on('click', function (e) {
		resetPage();
	});
});

// add local message
function addLocalMsg(msg, tag) {
	var cmsg = "<div class='c_msg' data-num='"+g_msg_num+"'>"+msg+"</div>";
	$(tag).append(cmsg) 
	g_msg_num++;
}

// handle chat responses
var chatHandler = function (words, resp) {
	if (!resp) {
		$("#resolve_list").show().html("<div class='fLn' style='margin-top:50px;color:red'>Error Retriving content</div>");
		glob_chid = null;
		return;
	}
	if (!resp.info) {
		$("#resolve_list").show().html("<div class='fLn' style='margin-top:50px;color:red'>Error Retriving content: "+resp.code+"</div>");
		glob_chid = null;
		return;
	}
	// interact_msg
	glob_persona_full_name = resp.info.persona_full_name;
	$(".personaFullName").html(glob_persona_full_name);
	
	glob_chid = resp.info.chid;

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


function addRemoteMsg(msg) {
	var cmsg = "<div class='s_msg' data-num='"+msg.num+"' data-qn='"+msg.qn+"' data-event='"+msg.event+"' data-time='"+msg.time+" 'data-from='"+msg.from+"' ";
	cmsg += " title='qn: "+msg.qn+" e: "+msg.event+" rply_type: "+msg.rply_type+" rply_base: "+msg.rply_base+" req_base: "+msg.req_base+" w: "+msg.pre_wait+" / "+msg.post_wait+" ' ";
	cmsg += ">"+msg.msg+"</div>";
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
    setTimeout(function () {	
		if (msg.msg != null) {
			$(tag).append(msg.msg); // write message
			$("#interact_msg").scrollTop($("#interact_msg")[0].scrollHeight);
		}
		waitPostMessage(tag, msg);
	}, (msg.pre_wait * 1000));
}
function waitPostMessage(tag, msg) {
    setTimeout(function () {
		if (msg.event == 'bye') {
			glob_chid = null;
			$("#interact_wd").hide();
			setTimeout(glob_resolve_bye_handler, glob_resolve_bye_time);		
		}
		processMsgs(tag);
	}, (msg.post_wait * 1000));
}


// handle ask AND tell resp[onses
var askTellHandler = function (words, resp) {
	if (!doc) {
		$("#asktell_response").html("<div class='fLn' style='margin-top:50px;color:red'>Error Retriving content</div>");
		glob_chid = null;
		return;
	}
	if (!resp.info) {
		$("#resolve_list").show().html("<div class='fLn' style='margin-top:50px;color:red'>Error Retriving content: "+resp.code+"</div>");
		glob_chid = null;
		return;
	}
	if (resp.list && resp.list.length > 0) {
		// first one will be the question in
		//addRemoteMsg(resp.list[1], "#asktell_response");
		for (var i=0;i<resp.list.length;i++) {
			if (resp.list[i].r == "false" || !resp.list[i].msg) continue; // only if remote add..
			addRemoteMsg(resp.list[i], "#asktell_response");
		}
		$("#asktell_response").scrollTop($("#asktell_response")[0].scrollHeight);
		
	} else {
		addRemoteMsg("No Response", "#asktell_response");
	}
	anz_in_progress = false;
}


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
