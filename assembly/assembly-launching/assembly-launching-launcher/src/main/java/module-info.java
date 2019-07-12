module massy.assembly.launching.launcher {
	exports com.massyframework.assembly.launching.launcher;

	requires massy.assembly.api;
	requires massy.logging.api;
	requires massy.lang;
	
	uses com.massyframework.assembly.launching.PresetHandler;
	uses com.massyframework.assembly.FrameworkFactory;
}