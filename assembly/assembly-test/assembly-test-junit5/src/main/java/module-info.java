module massy.test.junit5 {
	exports com.massyframework.assembly.test.junit5;

	requires transitive massy.assembly.api;
	requires transitive massy.assembly.test.api;
	requires transitive org.junit.jupiter.api;
	requires transitive org.junit.platform.commons;
}