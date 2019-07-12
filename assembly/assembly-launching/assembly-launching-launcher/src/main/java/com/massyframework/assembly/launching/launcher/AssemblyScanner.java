/**
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
 * Author: 黄开晖（117227773@qq.com）
 * Date:   2019年6月20日
 */
package com.massyframework.assembly.launching.launcher;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;

import com.massyframework.assembly.launching.LaunchException;

/**
 * 装配件扫描
 * @author  Huangkaihui
 */
final class AssemblyScanner {
	
	private static final String DIRECTORY    = "META-INF/assembly/";
	private static final String ASSEMBLY_XML = "assembly.xml";
	private static final String DOT          = ".";

	/**
	 * 
	 */
	public AssemblyScanner() {
		
	}
	
	/**
	 * 扫描装配件文件
	 * @param classLoader {@link ClassLoader},类加载器
	 * @return {@link LIst}
	 */
	public List<String> scanAssembly(ClassLoader classLoader) throws LaunchException {
		List<String> result = new ArrayList<>();
		
		try {
			Enumeration<URL> em = classLoader.getResources(DIRECTORY);
			while (em.hasMoreElements()) {
				URL url = em.nextElement();
				File file = this.getFile(url);
				if (file != null) {
					//文件目录
					this.scanDirectory(file, null, result);
				}else {
					JarFile jarFile = this.getJarFile(url);
					if (jarFile != null) {
						this.scanJarFile(jarFile, result);
					}
				}
			}
		} catch (IOException e) {
			throw new LaunchException("sanning assembly config file failed.", e);
		}
		return result;
	}
	
	/**
	 * 扫描文件目录
	 * @param directory
	 * @param name
	 * @param assemblyDirectory
	 */
	protected void scanDirectory(File directory, String name, 
			List<String> symbolicNames) {
		File[] files = directory.listFiles();
		for (File file: files) {
			if (file.isDirectory()) {
				String newName = name == null ? 
						directory.getName():
							name.concat(DOT).concat(directory.getName());
						
				this.scanDirectory(file, newName, symbolicNames);
			}else {
				if (ASSEMBLY_XML.equals(file.getName())) {
					symbolicNames.add(name);
				}
			}
		}
	}
	
	protected void scanJarFile(JarFile file, List<String> symbolicNames) {
		String SUFFIX = "/".concat(ASSEMBLY_XML);
		Enumeration<JarEntry> em = file.entries();
		while (em.hasMoreElements()) {
			JarEntry entry = em.nextElement();
			if (entry.isDirectory()) {
				continue;
			}
			String name = entry.getName();
			if (!name.startsWith(DIRECTORY)) {
				continue;
			}
			
			if (name.endsWith(SUFFIX)){
				String symbolicName = StringUtils.substring(name, DIRECTORY.length());
				symbolicName = StringUtils.replace(symbolicName, "/", DOT);
				symbolicNames.add(symbolicName);
			}
		}
	}

	/**
	 * 根据<code>url</code>获取对应的目录
	 * @param url {@link URL}
	 * @return {@link File},可能返回null
	 */
	protected File getFile(URL url) {
		if (url.getProtocol().equals("file")) {
			return new File(url.getFile());
		}
		return null;
	}
	
	/**
	 * 根据<code>url</code>获取对应的jar文件
	 * @param url {@link URL}
	 * @return {@link JarFile},可能返回null
	 * @throws IOException 
	 */
	protected JarFile getJarFile(URL url) throws IOException {
		if (url.getProtocol().equals("jar")) {
			return ((JarURLConnection)url.openConnection()).getJarFile();
		}
		return null;
	}
}
