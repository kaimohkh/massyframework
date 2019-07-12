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
 * @日   期:  2019年1月27日
 */
package com.massyframework.assembly.web.runtime.resource;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;

import com.massyframework.logging.Logger;

/**
 * 提供基于装配件进行资源定位
 *
 */
final class ClassLoaderResourceLocator implements ResourceLocator {
	
	public static final String RESOURCES_PATH = "/META-INF/resources";
	private static final String PATH_SEPARATOR = "/";
	
	private final ClassLoader classLoader;
	private final List<URL> jarUrls = new ArrayList<URL>();
	private final Map<String, SoftReference<Set<String>>> pathsMap =
			new ConcurrentHashMap<>();

	/**
	 * 构造方法
	 * @param classLoader {@link ClassLoader},类加载器
	 */
	public ClassLoaderResourceLocator(ClassLoader classLoader) {
		this.classLoader = Objects.requireNonNull(classLoader, "\"classLoader\" cannot be null.");
		
		this.recursiveJars(this.classLoader, jarUrls);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.ResourceLocator#getResource(java.lang.String, javax.servlet.ServletContext)
	 */
	@Override
	public URL getResource(String path, ServletContext servletContext, Logger logger) throws MalformedURLException {
		if (path == null) return null;
		Objects.requireNonNull(servletContext, "\"servletContext\" cannot be null.");
		
		URL result = null;
		try {
			result = servletContext.getResource(path);
		}catch(MalformedURLException e) {
			
		}
				
		if (result == null) {
			String effectivePath = this.getEffectivePath(path);
			result = this.classLoader.getResource(effectivePath);
			if (result == null) {
				if (logger.isTraceEnabled()) {
					logger.trace("cannot found resource [\"" + effectivePath + "\"] from " + this.classLoader + ".");
				}
				effectivePath = RESOURCES_PATH.concat(effectivePath);
				result = this.classLoader.getResource(effectivePath);
			}
			
			if (logger.isTraceEnabled()) {
				if (result == null) {
					logger.trace("cannot found resource [\"" + effectivePath + "\"] from " + this.classLoader + ".");
				}else {
					logger.trace("found resource [\"" +  effectivePath + "\"] from " + this.classLoader  + ".");
				}
			}
		}
		
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.ResourceLocator#getResourcePaths(java.lang.String, javax.servlet.ServletContext)
	 */
	@Override
	public Set<String> getResourcePaths(String path, ServletContext servletContext) {
		if (path == null) return null;
		Objects.requireNonNull(servletContext, "\"servletContext\" cannot be null.");
		
		String effectivePath = this.getEffectivePath(path);
		if (StringUtils.endsWith(effectivePath, PATH_SEPARATOR)) {
			effectivePath = effectivePath.concat(PATH_SEPARATOR);
		}
		
		Set<String> result = null;
		SoftReference<Set<String>> sr = this.pathsMap.get(effectivePath);
		if (sr != null) {
			result = sr.get();
		}
		if (result != null) {
			return result;
		}
		
		result = servletContext.getResourcePaths(effectivePath);
		result = result == null ? new LinkedHashSet<>() : new LinkedHashSet<>(result);
		
		try {
			Enumeration<URL> em = classLoader.getResources(effectivePath);
			this.doScan(em, effectivePath, result);
			
			em = classLoader.getResources(RESOURCES_PATH.concat(effectivePath));
			this.doScan(em, effectivePath, result);
		} catch (IOException e) {
			
		}
		
		sr = new SoftReference<>(result);
		this.pathsMap.putIfAbsent(effectivePath, sr);
		return result;
	}
	
	/**
	 * 列斯资源
	 * @param em 
	 * @param name
	 * @param paths
	 */
	protected void doScan(Enumeration<URL> em, String path, Set<String> paths) {
		while (em.hasMoreElements()) {
			URL url = em.nextElement();

			if (this.isFileURL(url)) {
				File dir = this.getFile(url);
				this.doScanFileSystem(path, dir, paths);
			}else {
				try {
					JarURLConnection connection =(JarURLConnection) url.openConnection();
					JarFile jarFile = connection.getJarFile();
					this.doScanJarFile(path, jarFile, paths);
				}catch(Exception e) {
					
				}
			}
			
		}
	}
	
	/**
	 * 扫描文件目录
	 * @param path 路径
	 * @param directory 对应的目录
	 * @param paths 存放下级文件的set
	 */
	protected void doScanFileSystem(String path, File directory, Set<String> paths) {
		File[] files = directory.listFiles();
		for (File file: files) {
			String resource = path + file.getName();
			paths.add(resource);
		}
	}
	
	/**
	 * 扫描Jar文件
	 * @param basePackage 基本包
	 * @param jarFile Jar文件
	 * @param paths 存放下级完呢键的set
	 */
	protected void doScanJarFile(String basePackage, JarFile jarFile, Set<String> paths) {
		int length = basePackage.length();
		Enumeration<JarEntry> em = jarFile.entries();
		while (em.hasMoreElements()) {
			JarEntry entry = em.nextElement();
			String name = entry.getName();			
			if (name.startsWith(basePackage)) {
				String suffix = StringUtils.substring(name, length);
				int pos = suffix.indexOf("/");
				if (pos == -1) {
					paths.add(name);
				}else {
					if (pos == suffix.length() -1) {
						paths.add(name);
					}
				}
			}
		}
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
	
	/**
	 * 有效的路径
	 * @param path 路径
	 * @return {@link String}
	 */
	protected String getEffectivePath(String path) {
		String result = null;
		if (path.length() == 1) {
			result = path;
		}else {
			if (!path.startsWith(PATH_SEPARATOR)) {
				result = PATH_SEPARATOR.concat(path);
			}else {
				result = path;
			}
		}
		
		return result;
	}

	
	/**
	 * 递归查找jar
	 * @param loader 类加载器
	 * @param jarUrls jarUrls
	 */
	private void recursiveJars(ClassLoader classLoader, List<URL> jarUrls){
		if (classLoader instanceof URLClassLoader){
			URLClassLoader urlLoader = (URLClassLoader)classLoader;
			
			URL[] urls = urlLoader.getURLs();
			if (urls != null){
				for (URL url: urls){
					jarUrls.add(url);
				}
			}
		}			
		ClassLoader parent = classLoader.getParent();
		if (parent != null){
			recursiveJars(parent, jarUrls);
		}
	}
}
