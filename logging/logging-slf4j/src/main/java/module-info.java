module massy.logging.slf4j {

	requires massy.logging.api;
	requires slf4j.api;
	
	provides com.massyframework.logging.spi.LoggerProvider with
		com.massyframework.logging.slf4j.LoggerWrapperProvider;
}