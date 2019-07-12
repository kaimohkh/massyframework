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
 * @日   期:  2019年2月3日
 */
package com.massyframework.instrument;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

/**
 * Agent Jar文件加载器
 * @author huangkaihui
 */
final class AgentJarLoader {

	private static final String FILENAME = "instrument-agent-2.3.0.9.jar";;
	
	private static URLClassLoader LOADER = null;
		
	/**
	 * 附加 agent jar.
	 * @param agentJarPath
	 * @throws IOException 
	 */
	public static void attachAgentJar() throws IOException{
		load();
		
		if (VirtualMachinUtils.getVMClass() == null){
			System.err.println("cannot found " + VirtualMachinUtils.VM_CLASSNAME + ".class.");
			return;
		}
					
		String agentJarPath = getAgentJarPath();
		System.out.println("load agent: " + agentJarPath + ".");
		
		try{
			VirtualMachinUtils.attach(agentJarPath);
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * 获取agent jar路径
	 * @return
	 * @throws IOException 
	 */
	protected static String getAgentJarPath() throws IOException{
		String tmpDir = System.getProperty("java.io.tmpdir") ;
		String pathSeparator = File.separator;
		if (!tmpDir.endsWith(pathSeparator)){
			tmpDir = tmpDir + pathSeparator;
		}
		File target = new File(tmpDir + FILENAME);
		if (target.exists()){
			return target.getAbsolutePath();
		}
		
		URL url = AgentJarLoader.class.getClassLoader().getResource("META-INF/lib/" + FILENAME);
		if (url != null) {
			InputStream is = null;
			try {
				is = url.openStream();
				Files.copy(is, target.toPath());
			}catch(IOException e) {
				if (is != null) {
					try {
						is.close();
					}catch(Exception ex) {
						
					}
				}
			}
		}
				
		return target.getPath();
	}
		
	/**
	 * 动态加载Tools.jar, 为下一步附加agent jar做准备
	 */
	protected synchronized static void load(){
		if (JavaVersions.VERSION >= 9) {
			return;
		}
		
		if (LOADER != null) return;
		
		String javaHome = System.getProperty("java.home");
		if (javaHome == null){
			throw new RuntimeException("JAVA_HOME not setting.");
		}
		
		String separator = System.getProperty("file.separator");
		String jre = separator + "jre";
		if (javaHome.endsWith(jre)){
			javaHome = javaHome.substring(0, javaHome.length() - 3);
		}
		if (!javaHome.endsWith(separator)) {
			javaHome = javaHome.concat(separator);
		}
		
		String path = javaHome +   "lib" + separator + "tools.jar";
		
		File file = new File(path);
		if (!file.exists()){
			throw new RuntimeException("cannot found tools.jar: " + path + ".");
		}
		
		URL url = null;
		try {
			url = file.toURI().toURL();
			LOADER = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}		
	}

}
