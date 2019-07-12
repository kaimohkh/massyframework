module massy.assembly.web.api {
	exports com.massyframework.assembly.web;
	exports com.massyframework.assembly.web.filter;
	exports com.massyframework.assembly.web.listener;
	exports com.massyframework.assembly.web.pagemapping;
	exports com.massyframework.assembly.web.processing;
	exports com.massyframework.assembly.web.servlet;
	exports com.massyframework.assembly.web.spi;
	exports com.massyframework.assembly.web.util;

	requires transitive javax.servlet.api;
	requires transitive massy.assembly.api;
	requires massy.lang;
	requires massy.logging.api;
	requires org.apache.commons.lang3;
}