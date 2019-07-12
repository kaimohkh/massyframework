module massy.assembly.launching.test {
	exports com.massyframework.assembly.launching.test;

	requires massy.assembly.api;
	requires massy.assembly.test.api;
	requires massy.assembly.launching.launcher
	requires massy.assembly.launching.instrument;
	requires massy.logging.api;
	requires org.apache.commons.codec;
	requires jdk.scripting.nashorn;
}