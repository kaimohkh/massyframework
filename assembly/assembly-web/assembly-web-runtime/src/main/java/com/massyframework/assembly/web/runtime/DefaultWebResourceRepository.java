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
 * @日   期:  2019年2月1日
 */
package com.massyframework.assembly.web.runtime;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.Framework;
import com.massyframework.assembly.FrameworkEventAdapter;
import com.massyframework.assembly.web.ModularServletContextRepository;
import com.massyframework.assembly.web.WebResourceRepository;

/**
 * Web资源仓储
 *
 */
final class DefaultWebResourceRepository extends FrameworkEventAdapter implements WebResourceRepository {
	
	private final AtomicReference<Map<String, ClassLoader>>  reference;

	/**
	 * 构造方法
	 */
	public DefaultWebResourceRepository(ModularServletContextRepository modularServletContextRepo) {
		this.reference = new AtomicReference<>();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.WebResourceRepository#findClassLoaderWithResource(java.lang.String)
	 */
	@Override
	public ClassLoader findClassLoaderWithResource(String resourceName) {
		Map<String, ClassLoader> resourceMap = this.reference.get();
		if (resourceMap != null) {
			return resourceMap.get(resourceName);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.FrameworkEventAdapter#doStarting(com.massyframework.assembly.Framework)
	 */
	@Override
	protected void doStarting(Framework framework) {
		List<Assembly> assemblies =
				framework.getAssemblyRepository().getAssemblies();
		Collections.sort(assemblies);
		new Thread(new Task(assemblies)).start();
		super.doStarting(framework);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.FrameworkEventAdapter#doStopped(com.massyframework.assembly.Framework)
	 */
	@Override
	protected void doStopped(Framework framework) {
		Map<String, ClassLoader> resourceMap = this.reference.getAndSet(null);
		resourceMap.clear();
		resourceMap = null;
		super.doStopped(framework);
	}
	
	/**
	 * 获取<code>url</code>对应的file文件
	 * @param url 
	 * @return
	 */
	public File getFile(URL url){
		try{
			if (this.isFileURL(url)){
				return new File(url.toURI());
			}
		}catch(Exception e){
			
		}
		return null;
	}
	
	/**
	 * 判断<code>url</code>是否来自于jar包
	 * @param url 
	 * @return
	 */
	public boolean isJarURL(URL url){
		return url.getProtocol().equals("jar");
	}
	
	/**
	 * 判断<code>url</code>是否来自于本地文件
	 * @param url 
	 * @return
	 */
	public boolean isFileURL(URL url){
		return url.getProtocol().equals("file");
	}

		
	private class Task implements Runnable {
		
		private List<Assembly> assemblies;
		
		/**
		 * 构造方法
		 * @param assemblies
		 */
		public Task(List<Assembly> assemblies) {
			this.assemblies = assemblies;
		}

		@Override
		public void run() {
		    Map<String, ClassLoader> resourceMap =
		    		new HashMap<String, ClassLoader>();
		    
		    for (Assembly assembly: assemblies) {
		    	ClassLoader classLoader =
		    			assembly.getAssemblyClassLoader();
		    	try {
			    	Enumeration<URL> em = classLoader.getResources(RESOURCES_PAHT);
			    	while (em.hasMoreElements()) {
			    		URL url = em.nextElement();
			    		File directory= getFile(url);
			    		if (directory != null) {
			    			this.scanDirectory(directory, PATH_SEPARATOR, classLoader, resourceMap);
			    		}else {
			    			JarFile jarFile = ((JarURLConnection)url.openConnection()).getJarFile();
			    			this.scanJarFile(jarFile, classLoader, resourceMap);
			    		}
			    	}
		    	}catch(Exception e) {
		    		
		    	}
		    }
		    
		    reference.set(resourceMap);
		}
		
		/**
		 * 扫描目录
		 * @param directory 目录
		 * @param classLoader 类加载器
		 * @param resoruceMap {@link Map},资源Map
		 */
		protected void scanDirectory(File directory, String parentName, ClassLoader classLoader, 
				Map<String, ClassLoader> resourceMap) {
			File[] files = directory.listFiles();
			for (File file: files) {
				if (file.isDirectory()) {
					String name = parentName.concat(file.getName()).concat(PATH_SEPARATOR);
					scanDirectory(file, name, classLoader, resourceMap);
				}else {
					String name = parentName.concat(file.getName());
					resourceMap.putIfAbsent(name, classLoader);
				}
				
			}
		}
		
		/**
		 * 扫描Jar文件
		 * @param jarFile {@link JarFile}
		 * @param classLoader 类加载器
		 * @param resourceMap 资源Map
		 */
		protected void scanJarFile(JarFile jarFile, ClassLoader classLoader, Map<String, ClassLoader> resourceMap) {
			String prefix= RESOURCES_PAHT.concat(PATH_SEPARATOR);
			int start = RESOURCES_PAHT.length();
			Enumeration<JarEntry> em = jarFile.entries();
			while (em.hasMoreElements()) {
				JarEntry entry = em.nextElement();
				String name = entry.getName();
				if (!entry.isDirectory()) {
					if (StringUtils.startsWith(name, prefix)) {
						name = StringUtils.substring(name, start);
						resourceMap.put(name, classLoader);
					}
				}
			}
		}
	}

}
