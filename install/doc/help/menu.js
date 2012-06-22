function add_menu(){
menu = document.createElement("div");
menu.id="menu"
menu.innerHTML = "<h2>ATK</h2>\
	<ul>\
	<li><a href=\"index.html\">About ATK</a></li>\
	<li><a href=\"ATK_presentation.html\">Presentation</a></li>\
	<li><a href=\"ATK_script_tests.html\">Script Tests</a></li>\
	<li><a href=\"ATK_random_tests.html\">Random Tests</a></li>\
	<li><a href=\"ATK_device_detection.html\">Device detection</a></li>\
	<li><a href=\"ATK_script_language.html\">Script language</a></li>\
	</ul>\
	<h2>Monitoring</h2>\
	<ul>\
	<li><a href=\"ATK_test_configuration.html\">Monitoring configuration</a></li>\
	<li><a href=\"ATK_free_monitoring.html\">Free monitoring</a></li>\
	<li><a href=\"Monitor_android_phones.html\">Android phones</a></li>\
	</ul>\
	<h2>Tools</h2>\
	<ul>\
	<li><a href=\"tool_script_recorder.html\">Script Recorder</a></li>\
	<li><a href=\"tool_graph_analyzer.html\">Graph Analyzer</a></li>\
	<li><a href=\"settings_android.html\">Android</a></li>\
	</ul>\
	<h2>Configuration</H2>\
	<ul>\
	<li><a href=\"config_new_android.html\">Configuring a new Android device</a></li>\
	</ul>\
</ul>";

document.body.insertBefore(menu, document.body.firstChild);
}
window.onload = add_menu