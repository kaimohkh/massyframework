module spring.extender {
	exports com.massyframework.integration.spring.extender;

	requires transitive massy.assembly.api;
	requires massy.io;
	requires massy.lang;
	requires massy.logging.api;
	requires massy.assembly.web.api;
	requires org.apache.commons.lang3;
	requires transitive javax.servlet.api;
		
	requires spring.aop;
	requires spring.core;
	requires spring.beans;
	requires spring.context;
	requires spring.context.support;
	requires spring.expression;
	requires spring.instrument;
	requires spring.jcl;
	requires spring.web;
	requires spring.webmvc;
	requires java.instrument;
	
	requires java.xml;
	
	provides com.massyframework.assembly.launching.Initializer
		with com.massyframework.integration.spring.extender.SpringResourceInitializer;
}