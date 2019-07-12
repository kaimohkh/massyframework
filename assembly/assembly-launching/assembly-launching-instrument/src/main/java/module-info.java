module massy.assembly.launching.instrument {
	exports com.massyframework.assembly.launching.instrument;

	requires massy.assembly.api;
	requires massy.instrument.api;
	requires java.instrument;
	
	provides com.massyframework.assembly.launching.PresetHandler with
		com.massyframework.assembly.launching.instrument.InstrumentPresetHandlder;
}