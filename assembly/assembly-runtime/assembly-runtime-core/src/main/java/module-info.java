module massy.assembly.runtime.core {
	exports com.massyframework.assembly.runtime.support;
	exports com.massyframework.assembly.runtime.assembly.handling;
	exports com.massyframework.assembly.runtime.config;
	exports com.massyframework.assembly.runtime.domain;
	exports com.massyframework.assembly.runtime.service;
	exports com.massyframework.assembly.runtime;
	exports com.massyframework.assembly.runtime.assembly;

	requires transitive java.xml;
	requires transitive massy.assembly.api;
	requires transitive massy.lang;
	requires transitive massy.io;
	requires transitive massy.logging.api;
	requires transitive org.apache.commons.lang3;
	
	provides com.massyframework.assembly.FrameworkFactory with
		com.massyframework.assembly.runtime.DefaultFrameworkFactory;
}