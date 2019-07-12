module massy.assembly.api {
	exports com.massyframework.assembly;
	exports com.massyframework.assembly.container;
	exports com.massyframework.assembly.crypto;
	exports com.massyframework.assembly.domain;
	exports com.massyframework.assembly.initparam;
	exports com.massyframework.assembly.launching;
	exports com.massyframework.assembly.monitor;
	exports com.massyframework.assembly.protocols.assembly;
	exports com.massyframework.assembly.protocols.factory;
	exports com.massyframework.assembly.service;
	exports com.massyframework.assembly.spi;
	exports com.massyframework.assembly.support;
	exports com.massyframework.assembly.util;
	
	requires transitive massy.lang;
	requires transitive massy.io;
	requires transitive massy.logging.api;
	requires transitive org.apache.commons.lang3;
	requires transitive java.xml;
}