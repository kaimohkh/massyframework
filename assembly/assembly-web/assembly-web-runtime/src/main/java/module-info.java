module massy.assembly.web.runtime {
	exports com.massyframework.assembly.web.runtime;
	exports com.massyframework.assembly.web.runtime.resource;
	exports com.massyframework.assembly.web.runtime.pagemapping;

	requires transitive javax.servlet.api;
	requires transitive massy.assembly.api;
	requires transitive massy.assembly.web.api;
	requires massy.lang;
	requires massy.logging.api;
	requires org.apache.commons.lang3;
	
	provides com.massyframework.assembly.launching.Initializer
		with com.massyframework.assembly.web.runtime.WebResourceInitializer;
}