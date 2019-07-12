package com.massyframework.assembly.launching.test;


import org.junit.Assert;
import org.junit.Test;

import com.massyframework.assembly.Framework;
import com.massyframework.assembly.crypto.Keys;
import com.massyframework.assembly.crypto.RsaHelper;
import com.massyframework.assembly.test.FrameworkUtils;
import com.massyframework.assembly.util.AssemblyUtils;

public class LauncherTestCase {

	@Test
	public void test() throws Throwable{
		Framework framework =
				FrameworkUtils.getFramework();
		framework.start();
		
		RsaHelper helper = new RsaHelper();
		Keys keys = helper.genericKeys(1024, 
				"com.massyframework".concat("#")
					.concat(Long.toString(System.currentTimeMillis())));
		
		System.out.println("private key");
		System.out.println(keys.getPrivateKey());
		System.out.println("public key");
		System.out.println(keys.getPublicKey());
		
		String signContext = 
			AssemblyUtils.getSignatureContext(
				"com.massyframework", 10000, keys.getPublicKey());
		String signData = helper.sign(
				signContext.getBytes(), keys.getPrivateKey());
		
		System.out.println("sign data");
		StringBuilder builder = new StringBuilder();
		builder.append(signData);
		int size = builder.length();
		for (int i=64; i<size; i+=65) {
			builder.insert(i, "\r");
		}
		System.out.println(builder.toString());
		
		boolean verified =
			helper.verify(signContext.getBytes(), signData, keys.getPublicKey());
		Assert.assertTrue(verified);
	}
}
