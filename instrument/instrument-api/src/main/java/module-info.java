module massy.instrument.api {
	exports com.massyframework.instrument;

	requires transitive java.instrument;
	requires java.management;
	requires jdk.attach;
}