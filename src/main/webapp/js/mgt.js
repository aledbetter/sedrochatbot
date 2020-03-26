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

var glob_max_persona = 0;
var glob_cur_persona = 0;
var chbot_glob_api_key = null;

function resetPage() {
	$(".sedro_version").html(getSedroVersion()); // add version
		
	$("#thetenant").hide(); 
	$("#tenantchoice").show(); 
	
	$(".poolCount, .ctx, .ctxName").html(""); 
	$("#api_key, #ctx, #persona, #username").val(""); 
	$("#add_persona, #add_form, #add_qanda, #add_dbknow, #raw_persona").hide();
	
	setStat("");
	g_msg_num = 1;
	clearErrors();
	
	$("#first_name, #last_name, #sex, #email").val(""); 
	$("#form_content, #form_url").val(""); 
}
function setStat(stat) {
	$(".action").html("");
	$("#xaction").html(stat);
}
function clearErrors() {
	$("#ctx").removeClass("error");
	$("#first_name").removeClass("error");
	$("#last_name").removeClass("error");
	$("#email").removeClass("error");
	$("#username").removeClass("error");
	$("#form_content").removeClass("error");
	$("#qanda_question").removeClass("error");
	$("#qanda_answer").removeClass("error");
	$("#dbknow_content").removeClass("error");	
}

/////////////////////////////////////////////////////////////////
//SETUP RENDER PAGE
/////////////////////////////////////////////////////////////////
$(document).ready(function() {
	resetPage();
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Check auth cookie
	var cookie = getCookie("atok");
	if (!cookie || cookie == "") {
		if (window.location.href.indexOf(".html") > -1 && window.location.href.indexOf("index.html") == -1) {
			window.location.href = "/index.html";	
			return;
		}		
	}
	
	// get the key
	scsGetSettings(function(data) {
		if (data && data.info && data.info.sedro_access_key) {
			chbot_glob_api_key = data.info.sedro_access_key;
			$("#api_key").val(data.info.sedro_access_key); 
			setAPIKey(data.info.sedro_access_key);
			setAPIHost(data.info.sedro_host);
			sedroGetAccount(function (data) {
				if (data) showTenant(data.results[0]);
				else $("#xtenant_action").html("ERROR: Adding Account for: " + chbot_glob_api_key);
			});
		} else {
			chbot_glob_api_key = null;
			$("#api_key").val(""); 
			$("#xtenant_action").html("ERROR: RAPID API key not set ");
		}
	});
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Get or Add Tenant
	$("#tenant_get_bt").on('click', function (e) {
		clearErrors();
		var api_key = $("#api_key").val();
		if (!api_key || api_key.length < 10) {
			$("#api_key").addClass("error");
			return;
		}
		setAPIKey(api_key);
		sedroGetAccount(function (data) {
			if (data) showTenant(data.results[0]);
			else $("#xtenant_action").html("ERROR: Adding Account for: " + api_key);
		});
	});
	
	// raw persona
	$("#raw_persona_cancel_bt").on('click', function (e) {
		$("#raw_persona_data").val(""); 
		$("#raw_persona").hide();
	});	
	
	// FORMs
	$("#form_add_bt").on('click', function (e) {
		addPresonaForm();
	});
	$("#form_add_cancel_bt").on('click', function (e) {
		$("#form_content, #form_url").val(""); 
		$("#add_form").hide();
	});	
	
	// QandA
	$("#qanda_add_bt").on('click', function (e) {
		addQandA();
	});
	$("#qanda_add_cancel_bt").on('click', function (e) {
		$("#qanda_question, #qanda_answer, #qanda_answer_short").val(""); 
		$("#add_qanda").hide();
	});	

	// DBKnow
	$("#dbknow_add_bt").on('click', function (e) {
		addDBKnow();
	});
	$("#dbknow_add_cancel_bt").on('click', function (e) {
		$("#dbknow_content").val(""); 
		$("#add_dbknow").hide();
	});	
	// Persona	
	$("#add_persona_bt").on('click', function (e) {		
		$("#add_persona").show();
	});
	$("#persona_generate_cancel_bt").on('click', function (e) {
		$("#add_persona").hide();
	});
	$("#persona_generate_bt").on('click', function (e) {
		clearErrors();
		var ctx = $("#ctx").val(); 
		var persona = $("#first_name").val(); 
		var first_name = $("#first_name").val(); 
		var last_name = $("#last_name").val(); 
		var sex = $("#sex").val(); 
		var email = $("#email").val(); 
		var err = false;
		if (!ctx) {
			$("#ctx").addClass("error");
			err = true;
		}
		if (!persona) {
			$("#first_name").addClass("error");
			err = true;
		}
		if (!last_name) {
			$("#last_name").addClass("error");
			err = true;
		}
		if (!email) {
			$("#email").addClass("error");
			err = true;
		}
		if (err) return;		 
		
		persona = persona.toLowerCase();
		$(".personaName").html(persona);
		$(".ctx").html(ctx);	
		$("#persona").val(persona);

		var nvlist = {first_name:""+first_name, last_name: ""+last_name, sex: ""+sex, email: ""+email};
		sedroGeneratePersonaMap(ctx, nvlist, function (data) {
			$("#first_name, #last_name, #sex, #email").val(""); 
			getTenant();
			$("#add_persona").hide();
		});
	
	});	
});

function getTenant() {
	var ctx = $("#ctx").val();
	if (!ctx) {
		$("#ctx").addClass("error");
		return;
	}
	$("#xtenant_action").html("Getting Tenant: " + ctx + " ...");
	sedroGetAccount(function (data) {
		if (data && data.results) showTenant(data.results[0]);
		else $("#xtenant_action").html("ERROR: Getting Tenant: " + ctx);
	});		
}

function showTenant(tenant) {
	$("#ctx").val(tenant.ctx);
	$("#xtenant_action").html("");

	// header info
	var hh = "";
	if (tenant.username) hh += "<div class='fLn'>Username: <b>" + tenant.username+"</b></div>";
	if (tenant.name != tenant.username) hh += "<div class='fLn'>Name: <b>" + tenant.name+"</b></div>";
	hh += "<div class='fLn'>Language: <b>" + tenant.language+"</b></div>";
	if (tenant.email) hh += "<div class='fLn'>Email: <b>" + tenant.email+"</b></div>";
	hh += "<div class='fLn'>Subscription: <b>" + tenant.subscription+"</b> ";
	hh += "&nbsp;&nbsp;&gt;&gt;&nbsp;&nbsp;max persona: <b>" + tenant.max_persona+"</b> max db: <b>" + tenant.max_db+"</b>  max session: <b>" + tenant.max_session+"</b></div>";
	$("#tenant_header").html(hh);
	
	// persona info
	hh = "";	
	var pselect = "";
	if (tenant.personas) {
		$("#persona_count").html(""+tenant.personas.length);
		var hp = "";
		for (var i=0;i<tenant.personas.length;i++) {
			hp += "<div class='fLn'>";
			hp += "<div class='fLn' style='padding-top:8px;font-size:16px;margin-bottom:4px;border-top:solid 1px #555'><span>&nbsp;&nbsp;&nbsp;&nbsp;";			
			hp += "<b class='wordHover' onClick='showPersonaRaw(\""+tenant.personas[i]+"\");'>" + tenant.personas[i]+"</b></span>";					
			hp += "<div class='bslink' onClick='showChat(\""+tenant.personas[i]+"\");' style='width:90px;text-align:center;float:right;font-size:14px;margin-top:-5px;background:#666;color:#FFF;margin-right:10px;'>Chat</div>";		
			hp += "<div class='bslink' onClick='removePersona(\""+tenant.personas[i]+"\");' style='width:90px;text-align:center;float:right;font-size:14px;margin-top:-5px;background:#666;color:#FFF;margin-right:10px;'>Remove</div>";				
			hp += "<div class='bslink' onClick='sedroPersonaClearRForms(\""+tenant.personas[i]+"\");' style='width:90px;text-align:center;float:right;font-size:14px;margin-top:-5px;background:#666;color:#FFF;margin-right:10px;'>Clear Forms</div>";				
			hp += "<div class='bslink' onClick='showPresonaForm(\""+tenant.personas[i]+"\");' style='width:90px;text-align:center;float:right;font-size:14px;margin-top:-5px;background:#666;color:#FFF;margin-right:10px;'>Add Form</div>";		
			hp += "</div>";			
			hp += "<div class='fLn db_select_view'>";
				hp += "<div class='bslink' onClick='personaAddDB(\""+tenant.personas[i]+"\");' style='width:80px;text-align:center;float:right;font-size:14px;margin-top:3px;background:#666;color:#FFF;margin-right:10px;'>Add DB</div>";				
				hp += "<select id='"+tenant.personas[i]+"_db_select' class='db_select' style='width:200px;float:right;margin-top:0px;'></select>";
			hp += "</div>";
			hp += "<div class='fLn'>";
				hp += "<div id='pform_"+tenant.personas[i]+"' class='fLn' style='margin-top:5px'></div>";
				hp += "<div id='pdbs_"+tenant.personas[i]+"' class='fLn' style='margin-top:5px'></div>";
			hp += "</div></div>";			
			pselect += "<option value='"+tenant.personas[i]+"'>"+tenant.personas[i]+"</option>";						
		}
		hh += hp;		
	} else {
		$("#persona_count").html("0");
	}
	$("#tenant_personas").html(hh);
	
	var lh = "", dbselect = "";
	if (tenant.dbs) {
		$("#db_count").html(""+tenant.dbs.length);
		
		for (var i=0;i<tenant.dbs.length;i++) {
			lh += "<div class='fLn'>";
			lh += "<div class='fLn' style='padding-top:8px;font-size:16px;margin-bottom:4px;border-top:solid 1px #555'><span>&nbsp;&nbsp;&nbsp;&nbsp;";
			lh += "<b>" + tenant.dbs[i]+"</b></span>";
			lh += "&nbsp;&nbsp;&nbsp;&nbsp;<span id='dbobjects_"+tenant.dbs[i]+"'>...</span>";
			lh += "<div class='bslink' onClick='deleteTenantDb(\""+tenant.dbs[i]+"\");' style='width:70px;text-align:center;float:right;font-size:16px;margin-top:-5px;background:#666;color:#FFF;margin-right:10px;'>Delete</div>";		
			lh += "<div class='bslink' onClick='showAddQandA(\""+tenant.dbs[i]+"\");' style='width:90px;text-align:center;float:right;font-size:14px;margin-top:-5px;background:#666;color:#FFF;margin-right:10px;'>Add Q&A</div>";		
			lh += "<div class='bslink' onClick='showAddDBKnow(\""+tenant.dbs[i]+"\");' style='width:90px;text-align:center;float:right;font-size:14px;margin-top:-5px;background:#666;color:#FFF;margin-right:10px;'>Add Know</div>";		
			lh += "</div>";
			lh += "<div class='fLn'>";
				lh += "<div id='dbknowledge_"+tenant.dbs[i]+"' class='fLn' style='margin-top:5px'></div>";
				lh += "<div id='dbqanda_"+tenant.dbs[i]+"' class='fLn' style='margin-top:5px'></div>";			
			lh += "</div></div>";
			
			if (!tenant.dbs[i].endsWith("_info")) { // exclude default DBs
				dbselect += "<option value='"+tenant.dbs[i]+"'>"+tenant.dbs[i]+"</option>";	
			}
		}
	} else {
		$("#db_count").html("0");
	}
	
	$("#tenant_databases").html(lh);

	$(".ctx").html(tenant.ctx);
	$("#ctx").html(tenant.ctx);
	$(".ctxName").html(tenant.name);
	$("#thetenant, #mypool").show();
	$("#tenantchoice").hide();
	$("#persona_select").html(pselect);	

	$(".db_select_view").hide();
	$(".db_select").html(dbselect);
	if (dbselect != "") $(".db_select_view").show();

	// get persona forms
	if (tenant.personas) {
		for (var i=0;i<tenant.personas.length;i++) showPersona(tenant.personas[i]);
	}
	if (tenant.dbs) {
		for (var i=0;i<tenant.dbs.length;i++) showDb(tenant.dbs[i]);
	}
}
function showPersona(persona) {
	if (!persona) return;
	var ctx = $("#ctx").val(); 
	
	sedroPersonaGetForms(ctx, persona, function (rctx, rpersona, data) {
		if (data) {
			var dat = "";
			if (data.info && data.info.main) {
				dat += addShowForm("main", persona, data.info.main, false);
			}
			if (data.results) {
				if (data.results[0].load) {
					for (var i=0;i<data.results[0].load.length;i++) {
						dat += addShowForm("load", persona, data.results[0].load[i], true);
					}				
				}
				if (data.results[0].background) {
					for (var i=0;i<data.results[0].background.length;i++) {
						dat += addShowForm("background", persona, data.results[0].background[i], false);
					}
				}
				if (data.results[0].action) {
					for (var i=0;i<data.results[0].action.length;i++) {
						dat += addShowForm("action", persona, data.results[0].action[i], false);
					}				
				}
			}
			$("#pform_"+persona).html(dat);			
		} 
	});
	sedroGetPersonaDbs(ctx, persona, function (rctx, rpersona, data) {
		if (data) {
			var dat = "";
			if (data.list) {
				for (var i=0;i<data.list.length;i++) {
					dat += "<div class='fLn' style='width:90%;padding-top:5px;padding-bottom:5px;margin-left:40px;position:relative;border-top:1px solid #CCC'>";
					if (data.list[i].endsWith("_info")) { // default DB
						dat += "<b>database</b> [<b>default</b>]:</b> " + data.list[i]
						dat += "<div class='bslink' onClick='showAddQandA(\""+data.list[i]+"\");' style='z-index:0;width:60px;text-align:center;font-size:12px;height:16px;min-height:16px;position:absolute;top:3px;right:80px;background:#666;color:#FFF;'>Add Q&A</div>";		
						dat += "<div class='bslink' onClick='showAddDBKnow(\""+data.list[i]+"\");'  style='z-index:0;width:60px;text-align:center;font-size:12px;height:16px;min-height:16px;position:absolute;top:3px;right:10px;background:#666;color:#FFF;'>Add Know</div>";		
					} else {
						dat += "<b>database:</b> " + data.list[i];
						dat += "<div class='bslink' onClick='personaRemoveDB(\""+persona+"\", \""+data.list[i]+"\");' style='z-index:0;width:60px;text-align:center;font-size:12px;height:16px;min-height:16px;position:absolute;top:3px;right:10px;background:#666;color:#FFF;'>Remove</div>";		
					}
					dat += "</div>";
				}				
			}	
			$("#pdbs_"+persona).html(dat);			
		}
	});
}
function showDb(dbname) {
	if (!dbname) return;
	var ctx = $("#ctx").val(); 
	sedroGetDb(ctx, dbname, function (rctx, rdbname, data) {
		$("#dbobjects_"+dbname).html("empty");
		if (!data.results[0]) return;

		// objects
		if (data.results[0].objects) {
			$("#dbobjects_"+dbname).html("<b>"+data.results[0].objects+"</b> objects");
		}
		
		// knowledge
		if (data.results[0].knowledge) {
			var dat = "";
			for (var i=0;i<data.results[0].knowledge.length;i++) {
				dat += "<div class='fLn' style='width:90%;white-space: nowrap;overflow:hidden;padding-top:5px;padding-bottom:5px;margin-left:40px;position:relative;border-top:1px solid #CCC' title='"+data.results[0].knowledge[i]+"'>";
				dat += "<b>Knowledge:</b>&nbsp;&nbsp;" + data.results[0].knowledge[i];
				dat += "</div>";	
			}	
			$("#dbknowledge_"+dbname).html(dat);
		}
		// qanda
		if (data.results[0].qanda) {
			var dat = "";
			for (var i=0;i<data.results[0].qanda.length;i++) {
				var qa = data.results[0].qanda[i];
				dat += "<div class='fLn' style='width:90%;padding-top:5px;padding-bottom:5px;margin-left:40px;position:relative;border-top:1px solid #CCC'" +
						" title='classifier:"+qa.classifier+" act:"+qa.act+" short: "+qa.answer_short+"' answer: "+qa.answer+"' " +
						">";
				dat += "<b>Q&A ("+qa.object+")</b>&nbsp;&nbsp;" + qa.question;
				dat += "<div class='bslink' onClick='removeQandA(\""+dbname+"\", \""+qa.object+"\", \"" + qa.classifier + "\", \"" + qa.question + "\");' style='z-index:0;width:60px;text-align:center;font-size:12px;height:16px;min-height:16px;position:absolute;top:3px;right:10px;background:#666;color:#FFF;'>Remove</div>";		
				dat += "</div>";
			}				
			// put it
			$("#dbqanda_"+dbname).html(dat);
		}
	});
}
function addShowForm(tag, persona, name, del) {
	var dat = "<div class='fLn' style='width:90%;padding-top:5px;padding-bottom:5px;margin-left:40px;position:relative;border-top:1px solid #CCC'>";
	dat += "<b>form </b>";
	if (tag == "load") dat += "["+tag+"]";
	else dat += "[<b>"+tag+"</b>]";

	if (del) {
		dat += "&nbsp;&nbsp;<span class='wordHover' onClick='sedroPersonaRawForm(\""+persona+"\", \""+name+"\");'>" + name+"</span>";
		dat += "<div class='bslink' onClick='sedroPersonaRemoveRForm(\""+persona+"\", \""+name+"\");' style='z-index:0;width:60px;text-align:center;font-size:12px;height:16px;min-height:16px;position:absolute;top:3px;right:10px;background:#666;color:#FFF;'>Remove</div>";		
		dat += getTypeSelect(persona, name, tag);		
		dat += "<div class='bslink' onClick='sedroPersonaUpdateRForm(\""+persona+"\", \""+name+"\");' style='z-index:0;width:60px;text-align:center;font-size:12px;height:16px;min-height:16px;position:absolute;top:3px;right:80px;background:#666;color:#FFF;'>Update</div>";		
	} else {
		dat += "&nbsp;&nbsp;" + name;
	}
	dat += "</div>";
	return dat;
}
function getTypeSelect(persona, form, type) {
	var dat = "<select id='"+persona+"_"+form+"_type' style='width:120px;position:absolute;top:0px;right:225px'>";
	
	if (type == "load") dat += "<option value='load' selected>load</option>";
	else dat += "<option value='load'>load</option>";
	
	if (type == "main") dat += "<option value='main' selected>main</option>";
	else dat += "<option value='main'>main</option>";	

	if (type == "background") dat += "<option value='background' selected>background</option>";
	else dat += "<option value='background'>background</option>";	

	if (type == "action") dat += "<option value='action' selected>action</option>";
	else dat += "<option value='action'>action</option>";	
	dat += "</select>";
	return dat;
}

function showPresonaForm(persona) {
	clearErrors();
	$("#form_content, #form_url").val(""); 
	$("#add_form").show();
	$("#form_persona").val(persona);
	$("#persona_form_show").html(persona);
	scrollToId("#thetenant");
}	
function addPresonaForm() {
	var ctx = $("#ctx").val();
	var ftype = $("#form_type").val(); // select
	var persona = $("#form_persona").val();
	var fcontent = $("#form_content").val();
	var furl = $("#form_url").val();
	if (furl) {
		sedroPersonaAddFormRemote(ctx, persona, furl, ftype, function (rctx, persona, url, data) {
			if (data) {
				getTenant();
				$("#add_form").hide();
			}
		});
	} else {
		if (!fcontent) {
			$("#form_content").addClass("error");
			return;
		}
		sedroPersonaAddForm(ctx, persona, fcontent, ftype, function (rctx, persona, data) {
			if (data) {
				getTenant();
				$("#add_form").hide();
			}
		});
	}
}
function sedroPersonaRemoveRForm(persona, name) {
	var ctx = $("#ctx").val();
	$(".poolCount").html("Removeing persona Form["+persona+"] form["+name+"]...");
	sedroPersonaRemoveForm(ctx, persona, name, function (rctx, persona, data) {
		if (data) getTenant();
	});
}
function sedroPersonaClearRForms(persona) {
	var ctx = $("#ctx").val();
	$(".poolCount").html("Removeing all persona Form["+persona+"]...");
	sedroPersonaClearForms(ctx, persona, function (rctx, persona, data) {
		if (data) getTenant();
	});
}
function sedroPersonaUpdateRForm(persona, name) {
	var ctx = $("#ctx").val();
	var type = $("#pform_"+persona +" #"+persona+"_"+name+"_type").val();
	$(".poolCount").html("Updateing persona Form["+persona+"] form["+name+"] to " + type);
	sedroPersonaUpdateForm(ctx, persona, name, type, function (rctx, persona, data) {
		if (data) getTenant();		
	});
}
function sedroPersonaRawForm(persona, name) {
	var ctx = $("#ctx").val();
	sedroPersonaGetFormRaw(ctx, persona, name, function (rctx, persona, form, data) {
		if (data) {
			clearErrors();
			$("#form_url").val(""); 
			$("#form_content").val(data.info.raw); 
			$("#add_form").show();
			$("#form_persona").val(data.info.persona);
			$("#persona_form_show").html(data.info.persona + " form: " + data.info.form);	
			scrollToId("#thetenant");
		}
	});
}


//QandA
function showAddQandA(name) {
	clearErrors();
	$("#qanda_question, #qanda_answer, #qanda_answer_short").val(""); 
	$("#add_qanda").show();
	$("#qanda_dbname").val(name);
	$("#dbname_qanda_show").html(name);
	scrollToId("#thetenant");
}
function addQandA() {
	clearErrors();
	var ctx = $("#ctx").val();
	var dbname = $("#qanda_dbname").val();
	var qq = $("#qanda_question").val(); // select
	var qa = $("#qanda_answer").val();
	var qas = $("#qanda_answer_short").val();
	var err = false;
	if (!qq || qq == "") {
		$("#qanda_question").addClass("error");
		err = true;
	}
	if (!qa || qa == "") {
		$("#qanda_answer").addClass("error");
		err = true;
	}
	if (err) return;
	
	var ctx = $("#ctx").val();
	
	$("#xpool_action").html("Adding QandA: " + ctx + " / " + persona + " ...");
	var caller = null, caller_token = null, language = null;
	sedroAddQandA(ctx, dbname, qq, qa, qas, caller, caller_token, language, function (rctx, data) {
		if (data) {
			$("#add_qanda").hide();
			getTenant();
		} 
	});
}
function removeQandA(dbname, object, classifier, question) {
	var ctx = $("#ctx").val();
	sedroRemoveQandA(ctx, dbname, object, classifier, question, function (rctx, data) {
		if (data) {
			getTenant();
		} 
	});
}


//knowledge
function showAddDBKnow(name) {
	clearErrors();
	$("#dbknow_content").val(""); 
	$("#add_dbknow").show();
	$("#dbknow_dbname").val(name);
	$("#dbname_dbknow_show").html(name);
	scrollToId("#thetenant");
}
function addDBKnow() {
	clearErrors();
	var ctx = $("#ctx").val();
	var dbname = $("#dbknow_dbname").val();
	var know = $("#dbknow_content").val(); // select
	if (!know || know.length < 10) {
		$("#dbknow_content").addClass("error");
		return
	}
	var ctx = $("#ctx").val();
	sedroAddDbKnow(ctx, dbname, know, function (rctx, rname, rcontent, data) {
		if (data) {
			$("#add_dbknow").hide();
			getTenant();
		}
	});	
}


//get raw persona
function showPersonaRaw(persona) {
	var ctx = $("#ctx").val();
	sedroGetPersonaRaw(ctx, persona, function (rctx, persona, data) {
		if (data) {
			$("#raw_persona_name").html(data.info.persona)
			$("#raw_persona_data").html(data.info.raw);
			$("#raw_persona").show();
			scrollToId("#thetenant");
		}
	});
}
function removePersona(persona) {
	var ctx = $("#ctx").val();
	sedroRemovePersona(ctx, persona, function (rctx, persona, data) {
		getTenant();
	});
}
function personaAddDB(persona) {
	var ctx = $("#ctx").val();
	var dbname = $("#"+persona+"_db_select").val();
	sedroAddPersonaDb(ctx, persona, dbname, function (rctx, persona, data) {
		getTenant();
	});
}
function personaRemoveDB(persona, dbname) {
	var ctx = $("#ctx").val();
	sedroRemovePersonaDb(ctx, persona, dbname, function (rctx, persona, data) {
		getTenant();
	});
}
function deleteTenantDb(name) {
	var ctx = $("#ctx").val();
	sedroRemoveDb(ctx, name, function (rctx, rname, data) {
		if (data) getTenant();
	});
}


//////////////////////////////////////////////////////////////////
// UTILS
//////////////////////////////////////////////////////////////////

// Chat handoff
function showChat(persona) {
	clearErrors();
	var ctx = $("#ctx").val(); 
	url = "/msg?tenant="+ctx+"&persona="+persona;
	window.open(url, "_blank");
}

function scrollToId(id) {
	$([document.documentElement, document.body]).animate({
	    scrollTop: $(id).offset().top-20
	}, 200);
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

// get a cookie
function getCookie(name) {
  var value = "; " + document.cookie;
  var parts = value.split("; " + name + "=");
  if (parts.length == 2) return parts.pop().split(";").shift();
}
	