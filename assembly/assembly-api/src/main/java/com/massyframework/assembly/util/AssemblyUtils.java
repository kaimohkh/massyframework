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
package com.massyframework.assembly.util;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyRepository;

/**
 * 装配件工具类
 */
public final class AssemblyUtils {
	
	private static final String QUOTE =  "\"";
	private static final String COLON =  ":";
	private static final String COMMA =  ",";
	
	public static final String WEB_RESOURCES = "/META-INF/resources";
	public static final String PATH_SEPARATOR = "/";
	
	/**
	 * 根据<code>assembly</code>获取内核装配件
	 * @param assembly {@link Assembly},装配件
	 * @return {@link Assembly}
	 */
	public static Assembly getKernelAssembly(Assembly assembly) {
		if (assembly.getAssemblyId() == 0) {
			return assembly;
		}
		
		AssemblyRepository repo = AssemblyRepository.retrieveFrom(assembly);
		return repo.getAssembly(0);
	}
	
	/**
	 * 在<code>assemblies</code>中查找能提供Web资源的装配件
	 * <br>web资源必须是存放在{@link #WEB_RESOURCES}目录下的资源。
	 * @param resourceName {@link String},资源名称
	 * @return {@link Assembly}, 未找到符合要求的资源，则返回null.
	 */
	public static Assembly findAssemblyWithWebResource(final String resourceName, List<Assembly> assemblies) {
		if (resourceName == null) return null;
		if (assemblies == null) return null;
		
		String path = resourceName;
		if (!path.startsWith(PATH_SEPARATOR)) {
			path = PATH_SEPARATOR.concat(path);
		}
		path = WEB_RESOURCES.concat(path);
		
		for (Assembly assembly: assemblies) {
			ClassLoader loader = assembly.getAssemblyClassLoader();
			URL url = loader.getResource(resourceName);
			if (url != null) {
				return assembly;
			}
		}
		
		return null;
	}

	/**
	 * 获取持有和<code>assembly</code>相同ClassLoader的装配件列表。
	 * <br>方法首先取得<code>assembly</code>的ClassLoader,然后迭代所有Assembly，检查与其ClassLoader相同的装配件。
	 * @param assembly {@link Assembly}
	 * @return {@link List}
	 */
	public static List<Assembly> getAssembliesWithSameClassLoader(Assembly assembly){
		if (assembly == null) return Collections.emptyList();
		
		ClassLoader classLoader = assembly.getAssemblyClassLoader();
		AssemblyRepository repo = AssemblyRepository.retrieveFrom(assembly);
		
		return getAssembliesWithClassLoader(classLoader, repo);
	}
	
	/**
	 * 获取由<code>classLoader/code>加载的装配件列表。
	 * <br>然后迭代所有Assembly，检查与其ClassLoader相同的装配件。
	 * @param assembly {@link Assembly}
	 * @return {@link List}
	 */
	public static List<Assembly> getAssembliesWithClassLoader(ClassLoader classLoader, AssemblyRepository repo){
		if (classLoader == null) return Collections.emptyList();
		if (repo == null) return Collections.emptyList();
			
		return repo.getAssemblies( a -> classLoader == a.getAssemblyClassLoader());
	}
	
	/**
	 * 拼接排序后的装配件符号名称
	 * @param assemblies {@link List},张配件列表
	 * @return {@link String}
	 */
	public static String getSymbolicNames(List<Assembly> assemblies) {
		Objects.requireNonNull(assemblies, "\"assemblies\" cannot be null.");
		int size = assemblies.size();
		if (size == 0) {
			throw new IllegalArgumentException("\"assemblies\" cannot be empty.");
		}
		StringBuilder builder = new StringBuilder();
		Collections.sort(assemblies);
		
		for (int i=0; i<size; i++) {
			Assembly assembly = assemblies.get(i);
			builder.append(assembly.getSymbolicName());
			if (i != size -1) {
				builder.append(",");
			}
		}
		return builder.toString();
	}
	
	/**
	 * 获取签名内容
	 * @param name {@link String}, 域名
	 * @param vendorId {@link long}, 分配给开发商的编号
	 * @param publicKey {@link String},公钥
	 * @return {@link String}, 待签名内容
	 */
	public static String getSignatureContext(String name, long vendorId, String publicKey) {
		Objects.requireNonNull(name, "\"name\" cannot be null.");
		Objects.requireNonNull(publicKey, "\"publicKey\" cannot be null.");
		
		StringBuilder builder = new StringBuilder();
		builder.append("{")
			.append(QUOTE).append("name").append(QUOTE)
				.append(COLON).append(QUOTE).append(name).append(QUOTE).append(COMMA)
			.append(QUOTE).append("vendorId").append(QUOTE)
				.append(COLON).append(vendorId).append(COMMA)
			.append(QUOTE).append("publicKey").append(QUOTE)
				.append(COLON).append(QUOTE).append(publicKey).append(QUOTE)
			.append("}");
		return builder.toString();
	}

}
