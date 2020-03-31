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


var chbot_glob_api_key = null;

/////////////////////////////////////////////////////////////////
//SETUP RENDER PAGE
/////////////////////////////////////////////////////////////////
$(document).ready(function() {
	glob_chat_url = "/msg_test.html";
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Check auth cookie
	var cookie = getCookie("atok");
	if (!cookie || cookie == "") {
		if (window.location.href.indexOf(".html") > -1 && window.location.href.indexOf("index.html") == -1) {
			window.location.href = "/index.html";	
			return;
		}		
	}
	$("#message_here").html("loading...");
	$("#message_here").show();
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// get info from the server
	$.ajax({url: "/api/1.0/settings", type: 'GET', dataType: "json", contentType: 'application/json', 
		  success: function(data){
			if (data && data.info && data.info.sedro_access_key) {
				
				chbot_glob_api_key = data.info.sedro_access_key;
				$("#api_key").val(data.info.sedro_access_key); 
				setAPIKey(data.info.sedro_access_key);
				setAPIHost(data.info.sedro_host);
				$("#message_here").html("keyset loading tenant...");
				var ctx = null;
				sedroGetAccount(null, function (ctx, data) {
					if (data) {
						showTenant(data.results[0]);
						$("#message_here").html("").hide();
					} else {
						$("#message_here").html("ERROR: Getting Account for: " + chbot_glob_api_key);
					}
				});
			} else {
				chbot_glob_api_key = null;
				$("#api_key").val(""); 
				$("#message_here").html("ERROR: RAPID API key not set ");
			}
		  }, error: function(xhr) {
			$("#message_here").html("ERROR: RAPID API key not set ");
		  }
	});
});


