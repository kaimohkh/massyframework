package com.massyframework.assembly.launching.launcher;

import org.junit.BeforeClass;
import org.junit.Test;

import com.massyframework.assembly.Framework;

public class DefaultLauncherTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testLaunch() {
		DefaultLauncher launcher =
				new DefaultLauncher();
		Framework framework = launcher.launch();
		System.out.println(framework);
	}

}
