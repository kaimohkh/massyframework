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
 * @日   期:  2019年1月8日
 */
package com.massyframework.assembly.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyConfig;

/**
 * 容器工具类
 */
public abstract class ContainerUtils {
	
	/**
	 * 容器类型
	 */
	public static final String TYPE                           = "container.type";
	
	/**
	 * 独立容器
	 */
	public static final String TYPE_STANDALONE                = "standalone";
	
	/**
	 * 集成ServletContextListener的容器
	 */
	public static final String TYPE_SERVLETCONTEXTLISTENER    = "listener";
	
	/**
	 * 集成Filter的容器
	 */
	public static final String TYPE_FILTER                    = "filter";
	
	/**
	 * 集成Servlet的容器
	 */
	public static final String TYPE_SERVLET                   = "servlet";
	
	/**
	 * 容器的配置文件
	 */
	static final String CONTEXTCONFIGLOCATION                 = "container.contextConfigLocation";
	
	/**
	 * 容器的注解类
	 */
	public static final String CLASSES                               = "container.classes";
	
	/**
	 * jsp支持
	 * <br>在独立类加载器模式下生效，默认为false.
	 */
	static final String JSP_SUPPORT                           = "container.jsp.support";
	
	/**
	 * 判断容器类型是否为独立类型（非Web组件容器).<br>
	 * 根据<code>containerType</code>判断是否等于{@link #CONTAINER_TYPE_STANDALONE}或者.
	 * @param containerType {@link String}，容器类型
	 * @return {@link boolean},返回<code>true</code>表示是Filter.
	 */
	public static boolean hasStandalone(String containerType) {
		return containerType == null || TYPE_STANDALONE.equals(containerType);
	}
	
	/**
	 * 判断容器类型是否为servletcontextlistener.<br>
	 * 根据<code>containerType</code>判断是否等于{@link #CONTAINER_TYPE_SERVLETCONTEXTLISTENER}.
	 * @param containerType {@link String}，容器类型
	 * @return {@link boolean},返回<code>true</code>表示是.
	 */
	public static boolean hasServletContextListener(String containerType) {
		return TYPE_SERVLETCONTEXTLISTENER.equals(containerType);
	}

	/**
	 * 判断容器类型是否为Filter.<br>
	 * 根据<code>containerType</code>判断是否等于{@link #CONTAINER_TYPE_FILTER}.
	 * @param containerType {@link String}，容器类型
	 * @return {@link boolean},返回<code>true</code>表示是.
	 */
	public static boolean hasFiltert(String containerType) {
		return TYPE_FILTER.equals(containerType);
	}
	
	/**
	 * 判断容器类型是否为Servlet.<br>
	 * 根据<code>containerType</code>判断是否等于{@link #CONTAINER_TYPE_SERVLET}.
	 * @param containerType {@link String}，容器类型
	 * @return {@link boolean},返回<code>true</code>表示是.
	 */
	public static boolean hasServlet(String containerType) {
		return TYPE_SERVLET.equals(containerType);
	}
	
	/**
	 * 获取容器类型名称
	 * @param assemblyConfig {@link AssemblyConfig},装配件配置
	 * @return {@link String}，可能返回null.
	 */
	public static String getContainerType(AssemblyConfig assemblyConfig) {
		return assemblyConfig.getInitParameter(TYPE);
	}
	
	/**
	 * 是否定义由容器的配置类
	 * @param assembly {@link Assembly}
	 * @return {@link boolean},返回<code>true</code>表示有配置类，返回<code>false</code>表示没有
	 */
	public static boolean hasContainerConfigClasses(Assembly assembly) {
		return hasContainerConfigClasses(assembly.getAssemblyConfig());
	}
	
	/**
	 * 是否定义由容器的配置类
	 * @param assembly {@link AssemblyConfig}
	 * @return {@link boolean},返回<code>true</code>表示有配置类，返回<code>false</code>表示没有
	 */
	public static boolean hasContainerConfigClasses(AssemblyConfig config) {
		String classNames = 
				config.getInitParameter(CLASSES);
		return classNames != null;
	}
	
	/**
	 * 获取容器的配置类
	 * @param assembly {@link Assembly},容器
	 * @return {@link Class}数组
	 * @throws ClassNotFoundException 
	 */
	public static Class<?>[] getContainerConfigClasses(Assembly assembly){
		String classNames = 
				assembly.getAssemblyConfig().getInitParameter(CLASSES);
		if (classNames != null) {
			ClassLoader classLoader = assembly.getAssemblyClassLoader();
			List<Class<?>> classes = new ArrayList<Class<?>>();
    		String[] arr = StringUtils.split(classNames);
    		for (String className: arr) {
    			className = StringUtils.trim(className);
    			if (className.length()>0) {
    				Class<?> clazz;
					try {
						clazz = classLoader.loadClass(className);
						classes.add(clazz);
					} catch (ClassNotFoundException e) {
						assembly.getLogger().error(e.getMessage(), e);
					}
    			}
    		}
    		
    		return classes.toArray(new Class<?>[classes.size()]);
    	}
		
		return new Class<?>[0];
	}
		
	/**
	 * 判断是否需要支持jsp
	 * <br>仅在独立类加载器模式下，该属性才有效
	 * @param assembly {@link Assembly},装配件
	 * @return {@link boolean},返回<code>true</code>表示支持，否则<code>false</code>表示不支持
	 */
	public static boolean isSupportJsp(Assembly assembly) {
		return "true".equals(
				assembly.getAssemblyConfig().getInitParameter(JSP_SUPPORT,"false"));
	}
}
