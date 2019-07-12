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
 * @日   期:  2019年4月6日
 */
package com.massyframework.assembly.domain;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.SymbolicName;
import com.massyframework.assembly.domain.Vendor;

/**
 * 装配件定义
 */
public final class AssemblyDefinition {
	
	public static final String DEFAULT_HOME = "META-INF/assembly/";
	public static final String ASSEMBLY_FILE = "assembly.xml";

	private final SymbolicName symbolicName;
	private final ClassLoader assemblyClassLoader;
	private final String home;
	private final String path;
	private final URL assemblyXmlUrl;
	
	/**
	 * 从<code>assembly</code>中取回装配件配置定义
	 * @param assembly {@link Assembly}
	 * @return {@link AssemblyDefinition},可能返回null.
	 */
	public static AssemblyDefinition retrieveFrom(Assembly assembly) {
		return assembly.getAttribute(AssemblyDefinition.class.getName(), AssemblyDefinition.class);
	}
	
	/**
	 * 构造方法
	 * @param symbolicName {@link SymblicName},装配件的符号名称
	 * @param assemblyClassLoader {@link ClassLoader},装配件的类加载器
	 */
	public AssemblyDefinition(SymbolicName symbolicName, ClassLoader assemblyClassLoader) {
		this(symbolicName, DEFAULT_HOME, null, assemblyClassLoader);
	}
	
	/**
	 * 构造方法
	 * @param symbolicName {@link SymblicName},装配件的符号名称
	 * @param home {@link String}, 装配件的根目录
	 * @param assemblyClassLoader {@link ClassLoader},装配件的类加载器
	 */
	public AssemblyDefinition(SymbolicName symbolicName, String home, ClassLoader assemblyClassLoader) {
		this(symbolicName, home, null, assemblyClassLoader);
	}
	
	/**
	 * 构造方法
	 * @param symbolicName {@link SymbolicName},符号名称
	 * @param home {@link String},装配件的根目录
	 * @param assemblyXmlUrl {@link URL},装配件配置文件的路径，为null采用默认路径
	 * @param assemblyClassLoader {@link ClassLoader},装配件的类加载器
	 */
	public AssemblyDefinition(SymbolicName symbolicName, String home, URL assemblyXmlUrl, ClassLoader assemblyClassLoader) {
		this.symbolicName = Objects.requireNonNull(symbolicName, "\"symbolicName\" cannot be null.");
		this.assemblyClassLoader = Objects.requireNonNull(assemblyClassLoader, "\"assemblyClassLoader\" cannot be null.");
		
		Objects.requireNonNull(home, "\"home\" cannot be null.");
		this.home = home.endsWith("/") ? home : home + "/";
		this.assemblyXmlUrl = assemblyXmlUrl;
		StringBuilder builder = new StringBuilder();
		builder.append(this.home)
			.append(Long.toString(symbolicName.getDomain().getVendor().getId()))
			.append("/")
			.append(StringUtils.replace(symbolicName.getName(), SymbolicName.DOT, "/"))
			.append("/");
		this.path = builder.toString();
	}

	/**
	 * 装配件定义的根目录
	 * @return the home
	 */
	public String getHome() {
		return home;
	}

	/**
	 * 装配件符号名称
	 * @return the assemblyName
	 */
	public SymbolicName getSymbolicName() {
		return this.symbolicName;
	}

	/**
	 * 加载装配件服务组件的类加载器
	 * @return the assemblyClassLoader
	 */
	public ClassLoader getAssemblyClassLoader() {
		return assemblyClassLoader;
	}

	/**
	 * 生产装配件的供应商
	 * @return the vendor
	 */
	public Vendor getVendor() {
		return this.symbolicName.getDomain().getVendor();
	}
	
	/**
	 * 装配件assembly.xml的路径，可能为null.
	 * @return {@link URL}
	 */
	public URL getAssemblyURL() {
		if (this.assemblyXmlUrl != null) {
			return this.assemblyXmlUrl;
		}
		
		return this.getResource(ASSEMBLY_FILE);
	}
	
	/**
	 * 获取配置目录下的资源<br>
	 * 配置目录是指: DEFAULT_HOME + Long.toString(vendor.getId()) + "/" + name.replace(".","/") + "/" 
	 * 
	 * @param name {@link String},资源名称
	 * @return {@link URL}
	 */
	public URL getResource(String resourceName) {
		if (resourceName == null) return null;
		
		Path path = Paths.get(this.path, resourceName);
		String name = path.toString();
		URL result = this.assemblyClassLoader.getResource(name);
		return result;
	}
}
