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
var glob_api_key = null;

function resetPage() {
	$(".sedro_version").html(getSedroVersion()); // add version
	
	$("#knowledge_text, #api_key").val(""); 
	$("#ctx, #persona, #username").val(""); 
	
	$("#pub_list").hide(); 
	$("#gen_persona, #gen_knowledge").hide(); 
	$("#thetenant").hide(); 
	$("#tenantchoice").show(); 
	
	$(".poolCount, .ctx, .ctxName").html(""); 
	
	$("#add_persona").hide().attr("data-h", "hide");
	$("#add_knowledge").hide().attr("data-h", "hide");
	
	$("#add_persona_bt, #add_knowledge_bt").html("Add");
	
	setStat("");
	g_msg_num = 1;
	clearErrors();
	
	$("#first_name").val(""); 
	$("#last_name").val(""); 
	$("#sex").val(""); 
	$("#email").val(""); 
	$("#form_content, #form_url").val(""); 
	$("#add_form").hide();
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
	$("#knowledge_text").removeClass("error");
	$("#form_content").removeClass("error");
	$("#username").removeClass("error");

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
			glob_api_key = data.info.sedro_access_key;
			$("#api_key").val(data.info.sedro_access_key); 
			setAPIKey(glob_api_key);
			sedroGetAccount(function (data) {
				if (data) showTenant(data.results[0]);
				else $("#xtenant_action").html("ERROR: Adding Account for: " + glob_api_key);
			});
		} else {
			glob_api_key = null;
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
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// persona generate
	$("#add_persona_bt").on('click', function (e) {		
		var h = $("#add_persona").attr("data-h");
		if (!h || h == "hide") {
			$("#add_persona").show().attr("data-h", "show");
			$("#add_persona_bt").html("Close");

		} else {
			$("#add_persona").hide().attr("data-h", "hide");
			$("#add_persona_bt").html("Add");

		}
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
			alert("CTX");
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

		$("#xpersona_action").html("Generating2 Persona: " + ctx + " / " + persona + " ...");
		var nvlist = {first_name:""+first_name, last_name: ""+last_name, sex: ""+sex, email: ""+email};
		sedroGeneratePersonaMap(ctx, nvlist, function (data) {
			$("#xpersona_action").html("Persona Saved: " + persona);
			$("#first_name, #last_name, #sex, #email").val("");
			getTenant();
		});

	});		
	$("#form_add_bt").on('click', function (e) {
		addPersonaForm();
	});
	$("#form_add_cancel_bt").on('click', function (e) {
		$("#form_content, #form_url").val(""); 
		$("#add_form").hide();
	});	
	$("#raw_persona_cancel_bt").on('click', function (e) {
		$("#raw_persona_data").val(""); 
		$("#raw_persona").hide();
	});		
	
	

	///////////////////////////////////////////////////////////////////////////////////////////////////
	// DB generate
	$("#add_knowledge_bt").on('click', function (e) {		
		var h = $("#add_knowledge").attr("data-h");
		
		var persona = $("#persona").val(); 
		if (persona && persona.length > 2) {
			$(".personaName").html(persona);
			$("#persona_select").val(persona);
		} else $(".personaName").html("...");

		if (!h || h == "hide") {
			$("#add_knowledge").show().attr("data-h", "show");
			$("#add_knowledge_bt").html("Close");

		} else {
			$("#add_knowledge").hide().attr("data-h", "hide");
			$("#add_knowledge_bt").html("Add");

		}
	});
	$("#db_generate_bt").on('click', function (e) {	
		var persona = $("#persona_select").val();
		if (!persona || persona.length < 2) {
			alert("no persona");
			return;
		}
		$("#persona").val(persona); 
		$(".personaName").html(persona);

		
		var content = $("#knowledge_text").val(); 
		if (!content || content.length < 10) {
			$("#knowledge_text").addClass("error");
			return;
		}
		var ctx = $("#ctx").val(); 
		var name = persona +"_info";  //%dbname%_info => ctx+"_"+persona +"_info"
		$("#xdb_action").html("Generating knowledge: " + ctx + " / " + name + " ...");
		sedroPersonaGenerateDb(ctx, persona, content, function (rctx, rname, rcontent, data) {
			if (data) {
				$("#xdb_action").html("Knowledge generated: " + rctx + " / " + rname);
				$("#knowledge_text").val(""); 
				getTenant();
			} else {
				$("#xdb_action").html("ERRORO: Knowledge generation: " + rctx + " / " + rname);
			}
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
	$("#xtenant_action").html("Added Tenant: " + name);
	
	var hh = "<div class='fLn'>&nbsp;Name: <b>" + tenant.name+"</b></div>";
	hh += "<div class='fLn'>&nbsp;Language: <b>" + tenant.language+"</b></div>";
	if (tenant.email) hh += "<div class='fLn'>&nbsp;Email: <b>" + tenant.email+"</b></div>";
	if (tenant.username) hh += "<div class='fLn'>&nbsp;Username: <b>" + tenant.username+"</b></div>";
	hh += "<div class='fLn'>&nbsp;Subscription: <b>" + tenant.subscription+"</b></div>";
	hh += "<div class='fLn'>&nbsp;&nbsp;&nbsp;Max persona: <b>" + tenant.max_persona+"</b> db: <b>" + tenant.max_db+"</b></div>";
		
	var pselect = "";
	if (tenant.personas) {
		hh += "<div class='fLn personas' style='margin-top:15px;padding-bottom:10px;border-bottom:solid 2px #555;font-size:18px'>&nbsp;Tenant Personas: " + tenant.personas.length+ "</div>";
		var hp = "";
		for (var i=0;i<tenant.personas.length;i++) {
			hp += "<div class='fLn' style='padding-top:8px;padding-bottom:4px;font-size:16px;margin-top:4px;border-bottom:solid 1px #555;'><span>&nbsp;&nbsp;&nbsp;&nbsp;";
			hp += "<b>" + tenant.personas[i]+"</b></span>";
			hp += "<div class='bslink' onClick='showChat(\""+tenant.personas[i]+"\");' style='width:90px;text-align:center;float:right;font-size:14px;margin-top:-5px;background:#666;color:#FFF;margin-right:10px;'>Chat</div>";		
			hp += "<div class='bslink' onClick='showPresonaForm(\""+tenant.personas[i]+"\");' style='width:90px;text-align:center;float:right;font-size:14px;margin-top:-5px;background:#666;color:#FFF;margin-right:10px;'>Add Form</div>";		
			hp += "<div class='bslink' onClick='showPersonaRaw(\""+tenant.personas[i]+"\");' style='width:90px;text-align:center;float:right;font-size:14px;margin-top:-5px;background:#666;color:#FFF;margin-right:10px;'>Raw</div>";				
			hp += "<div id='pform_"+tenant.personas[i]+"' class='fLn'></div>";
			hp += "</div>";
			pselect += "<option value='"+tenant.personas[i]+"'>"+tenant.personas[i]+"</option>";						
		}
		hh += hp;		
	}
	$("#persona_select").html(pselect);

	if (tenant.dbs) {
		hh += "<div class='fLn dbs' style='margin-top:10px;margin-bottom:4px;border-bottom:solid 2px #CCC;font-size:18px'>&nbsp;Persona Knowledge: " + tenant.dbs.length+ "</div>";
		var lh = "";
		for (var i=0;i<tenant.dbs.length;i++) {
			lh += "<div class='fLn' style='margin-top:4px;margin-bottom:4px;border-bottom:solid 1px #CCC'><span>&nbsp;&nbsp;&nbsp;&nbsp;";
			lh += "<b>" + tenant.dbs[i]+"</b></span>";
			lh += "<div class='bslink' onClick='removeTenantDb(\""+tenant.dbs[i]+"\");' style='width:70px;text-align:center;float:right;font-size:16px;margin-top:-5px;background:#666;color:#FFF;margin-right:10px;'>Remove</div>";		
			lh += "</div>";
		}
		hh += lh;
	}
	$("#xtenant_data").html(hh);
	/*
	if (tenant.pools) {
		var lh = "";
		for (var i=0;i<tenant.pools.length;i++) {
			lh += showPool(tenant.pools[i]);
		}
		$(".poolCount").html(tenant.pools.length);
		$("#xpool_data").html(lh);
	} else {
		$(".poolCount").html("No Depot Pools");
		$("#xpool_data").html("");
	}*/
	
	$("#xpool_action").html("");
	$(".ctx").html(tenant.ctx);
	$("#ctx").html(tenant.ctx);

	$(".ctxName").html(tenant.name);
	$("#thetenant, #gen_persona").show();
	$("#tenantchoice").hide();
	glob_max_persona = tenant.max_persona;
	$(".ctxSubscription").html(tenant.subscription);


	// get persona forms
	if (tenant.personas && tenant.personas.length > 0) {
		$("#gen_knowledge").show();
		glob_cur_persona = tenant.personas.length;
		for (var i=0;i<tenant.personas.length;i++) {
			showPersona(tenant.personas[i]);
		}
	} else {
		$("#gen_knowledge").hide();
	}
}
function showPersona(persona) {
	var ctx = $("#ctx").val(); 
	if (!persona) return;
	
	$("#xpool_action").html("Show persona Forms: " + ctx + " / " + persona + " ...");
	sedroPersonaGetForms(ctx, persona, function (rctx, rpersona, data) {
		if (data) {
			$("#xpool_action").html("Show persona forms: " + rctx + " / " + rpersona);
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
						dat += addShowForm("background", persona, data.results[0].active[i], false);
					}
				}
				if (data.results[0].action) {
					for (var i=0;i<data.results[0].action.length;i++) {
						dat += addShowForm("action", persona, data.results[0].action[i], false);
					}				
				}
			}
				
			// put it
			$("#pform_"+persona).html(dat);			
		} else $("#xpool_action").html("ERROR: getting persona forms: " + rctx + " / " + rpersona);
	});
}
function addShowForm(tag, persona, name, del) {
	var dat = "<div class='fLn' style='width:90%;padding-top:4px;padding-bottom:3px;margin-left:40px;position:relative;border-top:1px solid #CCC'>";
	if (tag == "load") dat += tag;
	else dat += "<b>"+tag+"</b>";
	dat += ":&nbsp;&nbsp;" + name;
	if (del) {
		dat += "<div class='bslink' onClick='sedroPersonaRForm(\""+persona+"\", \""+name+"\");' style='z-index:0;width:60px;text-align:center;font-size:12px;height:16px;min-height:16px;position:absolute;top:2px;right:10px;background:#666;color:#FFF;'>Remove</div>";		
		dat += "<div class='bslink' onClick='sedroPersonaRawForm(\""+persona+"\", \""+name+"\");' style='z-index:0;width:60px;text-align:center;font-size:12px;height:16px;min-height:16px;position:absolute;top:2px;right:80px;background:#666;color:#FFF;'>Raw</div>";		
	}
	dat += "</div>";
	return dat;
}

function showChat(persona) {
	clearErrors();
	var ctx = $("#ctx").val();
//	alert("start Chat: " + persona);
	window.open("/msg.html?persona="+persona+"&tenant="+ctx, "_blank");
	
}
function showPresonaForm(persona) {
	clearErrors();
	$("#form_content, #form_url").val(""); 
	$("#add_form").show();
	$("#form_persona").val(persona);
	$("#persona_form_show").html(persona);
}	
function addPersonaForm() {
	var ctx = $("#ctx").val();

	var ftype = $("#form_type").val(); // select
	var fmain = $("#form_main").val(); // select
	var persona = $("#form_persona").val();
	var fcontent = $("#form_content").val();

	var furl = $("#form_url").val();
	if (furl) {
		// do it
		sedroPersonaAddFormRemote(ctx, persona, furl, ftype, function (rctx, persona, url, data) {
			if (data) {
				//$("#xpool_action").html("removed persona form: " + rctx + " / " + persona);
				getTenant();
				$("#add_form").hide();
			} else {	
			}
		});
	} else {
		if (!fcontent) {
			$("#form_content").addClass("error");
			return;
		}
		// do it
		sedroPersonaAddForm(ctx, persona, fcontent, ftype, fmain, function (rctx, persona, data) {
			if (data) {
				//$("#xpool_action").html("removed persona form: " + rctx + " / " + persona);
				getTenant();
				$("#add_form").hide();
			} else {	
			}
		});
	}
}
function sedroPersonaRForm(persona, name) {
	var ctx = $("#ctx").val();
	$(".poolCount").html("Removeing persona Form["+persona+"] form["+name+"]...");
	sedroPersonaRemoveForm(ctx, persona, name, function (rctx, persona, data) {
		if (data) {
			$("#xpool_action").html("removed persona form: " + rctx + " / " + persona);
			getTenant();		
		} else {	
		}
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
		}
	});
}


// get raw persona
function showPersonaRaw(persona) {
	var ctx = $("#ctx").val();
	sedroGetPersonaRaw(ctx, persona, function (rctx, persona, data) {
		if (data) {
			$("#raw_persona_name").html(data.info.persona)
			$("#raw_persona_data").html(data.info.raw);
			$("#raw_persona").show();
		}
	});
}

function findPersonaPool(pools, persona) {
	if (!pools) return null;
	for (var i=0;i<pools.length;i++) {
		if (pools[i].persona == persona) return pools[i];
	}	
	return null;
}

function showPool(pool) {
	var hh = "<div class='fLn dpool' style='margin-top:4px;margin-bottom:4px;'><span>&nbsp;"
	hh += "<b>" + pool.name+"</b>";
	if (pool.persona) hh += " Persona: <b>" + pool.persona+"</b>";
	hh += " language: " + pool.language;
	hh += " [" + pool.min +" - " + pool.max +"]";
	hh += " Total: " + pool.total;
	hh += "</span>";
	hh += "<div class='bslink' onClick='removeDepotPool(\""+pool.persona+"\", \""+pool.name+"\");' style='width:70px;text-align:center;float:right;font-size:16px;margin-top:-5px;background:#666;color:#FFF;margin-right:10px;'>Remove</div>";		
	hh += "</div>";
	return hh;
}

function getDepotPools() {
	var ctx = $("#ctx").val();
	$(".poolCount").html("Getting Depot Pools...");
	sedroGetPool(ctx, function (rctx, data) {
		if (!data) $(".poolCount").html("ERROR: No Depot Pools");
		else if (data.results) {
			var lh = "";
			for (var i=0;i<data.results.length;i++) {
				lh += showPool(data.results[i]);
			}
			$(".poolCount").html(data.results.length);
			$("#xpool_data").html(lh);
		} else {
			$(".poolCount").html("No Depot Pools");
			$("#xpool_data").html("");
		}
	});
}
function removeDepotPool(persona, name) {
	var ctx = $("#ctx").val(); 
	if (persona) name = null;
	else persona = null;
	$("#xpool_action").html("Removing Depot Pool: " + ctx + " / " + persona + "/"+name+" ...");
	sedroRemovePool(ctx, persona, name, function (rctx, rpersona, data) {
		if (data) {
			$("#xpool_action").html("Removed Depot Pool: " + rctx + " / " + rpersona);
			getTenant();
		} else $("#xpool_action").html("ERROR: Removing Depot Pool: " + rctx + " / " + rpersona);
	});
}
function addDepotPool(persona) {
	var ctx = $("#ctx").val();
	$("#xpool_action").html("Adding Depot Pool: " + ctx + " / " + persona + " ...");
	sedroAddPool(ctx, persona, 1, 3, function (rctx, rpersona, data) {
		if (data) {
			$("#xpool_action").html("Added Depot Pool: " + rctx + " / " + rpersona);
			getTenant();
		}
		else $("#xpool_action").html("ERROR: Adding Depot Pool: " + rctx + " / " + rpersona);
	});
}
function removeTenantDb(name) {
	var ctx = $("#ctx").val();
	$("#xpool_action").html("Removing DB: " + ctx + " / " + name + " ...");
	sedroRemoveDb(ctx, name, function (rctx, rname, data) {
		if (data) {
			$("#xpool_action").html("Removed DB: " + rctx + " / " + rname);
			getTenant();
		}
		else $("#xpool_action").html("ERROR: Removing DB: " + rctx + " / " + rname);
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

// get a cookie
function getCookie(name) {
  var value = "; " + document.cookie;
  var parts = value.split("; " + name + "=");
  if (parts.length == 2) return parts.pop().split(";").shift();
}
	