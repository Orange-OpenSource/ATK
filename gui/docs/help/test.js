function add_menu(){
menu = document.createElement("div");
menu.id="menu"
menu.innerHTML = "<h2>ATK</h2>\
	<ul>\
	<li><a href=\"index.html\">About ATK</a></li>\
	<li><a href=\"ATK_presentation.html\">Presentation</a></li>\
	<li><a href=\"ATK_script_tests.html\">Script Tests</a></li>\
	<li><a href=\"ATK_random_tests.html\">Random Tests</a></li>\
	<li><a href=\"ATK_mixscript_tests.html\">MixScript Tests</a></li>\
	<li><a href=\"ATK_device_detection.html\">Device detection</a></li>\
	<li><a href=\"ATK_script_language.html\">Script language</a></li>\
	</ul>\
	<h2>Monitoring</h2>\
	<ul>\
	<li><a href=\"ATK_test_configuration.html\">Monitoring configuration</a></li>\
	<li><a href=\"ATK_free_monitoring.html\">Free monitoring</a></li>\
	<li><a href=\"Monitor_android_phones.html\">Android phones</a></li>\
	<li><a href=\"Monitor_nokiaS60_phones.html\">Nokia S60 phones</a></li>\
	<li><a href=\"Monitor_mediatek_phones.html\">Mediatek phones</a></li>\
	<li><a href=\"Monitor_samsung_phones.html\">Samsung phones</a></li>\
	<li><a href=\"Monitor_se_phones.html\">SonyEricsson phones</a></li>\
	</ul>\
	<h2>Tools</h2>\
	<ul>\
	<li><a href=\"tool_script_recorder.html\">Script Recorder</a></li>\
	<li><a href=\"tool_graph_analyzer.html\">Graph Analyzer</a></li>\
	<li><a href=\"tool_screenshot_comparator.html\">Screenshot comparator</a></li>\
	<li><a href=\"tool_command_line.html\">Command Line tool</a></li>\
	</ul>\
	<h2>Settings</h2>\
	<ul>\
	<li><a href=\"settings_android.html\">Android</a></li>\
	<li><a href=\"settings_s60.html\">S60</a></li>\
	<li><a href=\"settings_se.html\">SonyEricsson</a></li>\
	</ul>\
	<h2>Configuration</H2>\
	<ul>\
	<li><a href=\"config_new_android.html\">Configuring a new Android device</a></li>\
	</ul>\
</ul>";

document.body.insertBefore(menu, document.body.firstChild);
}
window.onload = add_menu