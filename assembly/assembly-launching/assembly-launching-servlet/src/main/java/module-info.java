module massy.assembly.launching.servlet {
	exports com.massyframework.assembly.launching.servlet;

	requires java.instrument;
	requires javax.servlet.api;
	requires massy.assembly.api;
	requires massy.assembly.launching.launcher;
	
	
	provides javax.servlet.ServletContainerInitializer with
		com.massyframework.assembly.launching.servlet.FrameworkWebLauncher;
}