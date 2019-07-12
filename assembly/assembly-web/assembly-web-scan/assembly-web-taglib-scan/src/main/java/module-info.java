module massy.assembly.web.taglib.scan {
	exports com.massyframework.assembly.web.taglib.scan;

	requires java.xml;
	requires javax.servlet.api;
	
	requires transitive massy.assembly.api;
	requires transitive massy.assembly.web.api;
	requires massy.logging.api;
	requires tomcat.api;
	requires tomcat.jasper;
	requires tomcat.juli;
	requires tomcat.util.scan;
	
	provides com.massyframework.assembly.launching.Initializer
		with com.massyframework.assembly.web.taglib.scan.TagLigScannerInitializer;
}