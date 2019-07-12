/*
 * Copyright: 2018 smarabbit studio.
 *
 * Licensed under the Confluent Community License; you may not use this file
 * except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * @作   者： 黄开晖 (117227773@qq.com)
 * @日   期:  2019年2月2日
 */
package com.massyframework.assembly.runtime;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.Vector;

import com.massyframework.lang.support.OSInformation;


/**
 * 原生代码库
 *
 */
class NativeLibrary {

	private static List<String> libPaths;
	
	/**
	 * 加载动态库
	 * @throws MalformedURLException
	 */
	public static synchronized void loadLibrary() throws MalformedURLException {
		if (libPaths == null) {
			libPaths = new ArrayList<String>();
			libPaths.add("C:\\Users\\86135\\source\\repos\\massy\\x64\\Debug\\framework.dll");
			
			//libPaths = getNativeLibsPaths();
			for (String path: libPaths) {
				System.load(path);
			}
		}
	}
	
	/**
	 * 卸载动态库
	 */
	public static synchronized void unloadLibrary() {
		if (libPaths != null) {
			Collections.reverse(libPaths);
			for (String path: libPaths) {
				unloadLibrary0(NativeLibrary.class.getClassLoader(), path);
			}
			libPaths = null;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static synchronized void unloadLibrary0(ClassLoader classLoader) {
		try {
			Field field = ClassLoader.class.getDeclaredField("nativeLibraries");
			field.setAccessible(true);
			Vector<Object> libs = (Vector<Object>) field.get(classLoader);
			Iterator it = libs.iterator();
			while (it.hasNext()) {
				Object object = it.next();
				Method finalize = object.getClass().getDeclaredMethod("finalize");
				finalize.setAccessible(true);
				finalize.invoke(object);
			}
		}catch(Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static synchronized void unloadLibrary0(ClassLoader classLoader, String libName) {
		try {
			Field field = ClassLoader.class.getDeclaredField("nativeLibraries");
			field.setAccessible(true);
			Vector<Object> libs = (Vector<Object>) field.get(classLoader);
			Iterator it = libs.iterator();
			while (it.hasNext()) {
				Object object = it.next();
				Field[] fs = object.getClass().getDeclaredFields();
				for (int k = 0; k < fs.length; k++) {
					if (fs[k].getName().equals("name")) {
						fs[k].setAccessible(true);
						String dllPath = fs[k].get(object).toString();
						if (dllPath.endsWith(libName)) {
							Method finalize = object.getClass().getDeclaredMethod("finalize");
							finalize.setAccessible(true);
							finalize.invoke(object);
						}
					}
				}
			}
		}catch(Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	/**
	 * 获取原生共享库类库路径
	 * @return {@link List}
	 */
	private static List<String> getNativeLibsPaths() {
		String os = OSInformation.isWindows() ? "win32" : "linux";
		String resource = "META-INF/lib/".concat(os).concat("/");
		
		//临时目录
		File tempDir = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
		tempDir.deleteOnExit();
		tempDir.mkdirs();
		
		List<String> result = new ArrayList<String>();
		List<String> libNames = getNativeLibsNames();
		
		ClassLoader classLoader = NativeLibrary.class.getClassLoader();
		for (String libName: libNames) {
		    URL url = classLoader.getResource(resource.concat(libName));
		    result.add(copyToTempFile(tempDir, libName, url));
		}
				
		return result;		
	}
	
	private static List<String> getNativeLibsNames(){
		List<String> result = new ArrayList<>();
		if (OSInformation.isLinux()) {
			result.add("libframework.so");
		}else {
			result.add("libframework.dll");
		}
		
		return result;
	}
	
	/**
	 * 复制到临时文件
	 * @param directory {@link File},目标文件的文件夹
	 * @param fileName {@link String}, 复制后的文件名
	 * @param url  {@link URL}, 待复制的文件URL
	 * @return {@link String},复制后的文件路径
	 */
	private static String copyToTempFile(File directory, String fileName, URL source) {
		Objects.requireNonNull(source, "\"source\" cannot be null.");
		InputStream is = null;
		try {
			is = source.openStream();
			File tempFile = new File(directory, fileName);
			tempFile.deleteOnExit();
			Path result = tempFile.toPath();
			
			Files.copy(is, result);
			return result.toString();
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			if (is != null) {
				try {
					is.close();
				}catch(Exception e) {
					
				}
			}
		}
	}
}
