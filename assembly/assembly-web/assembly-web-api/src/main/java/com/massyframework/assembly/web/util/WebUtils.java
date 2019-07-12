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
package com.massyframework.assembly.web.util;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import javax.servlet.DispatcherType;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.apache.commons.lang3.StringUtils;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyConfig;
import com.massyframework.assembly.AssemblyException;
import com.massyframework.assembly.Framework;
import com.massyframework.assembly.ParameterNotFoundException;
import com.massyframework.assembly.service.ServiceReference;
import com.massyframework.assembly.initparam.InitParameters;
import com.massyframework.assembly.util.InitParametersUtils;
import com.massyframework.assembly.web.filter.DelegateFilter;
import com.massyframework.assembly.web.listener.DelegateListener;
import com.massyframework.assembly.web.servlet.DelegateServlet;

/**
 * Framework工具类
 *
 */
public final class WebUtils {
	
	public static final String URL_PATTERN   = "url-pattern";
	public static final String DISPATCHER    = "dispatcher";
	public static final String ISMATCHAFTER  = "isMatchAfter";
	public static final String SERVLET_NAME  = "servlet-name";
	public static final String FILTER_NAME   = "filter-name";
	
	public static final String CLASS = "class";
	public static final String ASYNC_SUPPORTED = "async-supported";
	
	public static final String LOAD_ON_STARTUP = "load-on-startup";
	
	public static final String INIT = "init";
	
	/**
	 * 添加DelegateListener
	 * @param assembly 装配件
	 * @param servletContext {@link ServletContext},Servlet上下文
	 * @param listenerName {@link String},监听器名称
	 * @param listenerName 监听器名称
	 */
	public static void  addDelegateListener(Assembly assembly, ServletContext servletContext, String listenerName) {
		Objects.requireNonNull(assembly, "\"assembly\" cannot be null.");
		Objects.requireNonNull(servletContext, "\"servletContext\" cannot be null.");
		Objects.requireNonNull(listenerName, "\"listenerName\" cannot be null.");
		
		DelegateListener listener = new DelegateListener(assembly.getSymbolicName(), listenerName);
		servletContext.addListener(listener);
	}
	
	/**
	 * 添加DelegateFilter，并返回{@link FilterRegistration.Dynamic}
	 * @param assembly 装配件
	 * @param initParams container.filter前缀过滤后的初始化参数
	 * @param servletContext Servlet上下文
	 * @return {@link FilterRegistration.Dynamic}
	 */
	public static FilterRegistration.Dynamic addDelegateFilter(Assembly assembly, 
			ServletContext servletContext) throws ParameterNotFoundException{
		Objects.requireNonNull(assembly, "\"assembly\" cannot be null.");
		Objects.requireNonNull(servletContext, "\"servletContext\" cannot be null.");
		
		AssemblyConfig config = assembly.getAssemblyConfig();
		InitParameters initParams = InitParametersUtils.subset(config, "container.filter");
		
		String filterName = initParams.getInitParameter(FILTER_NAME);
		if (filterName == null) {
			throw new ParameterNotFoundException("container.filter." + FILTER_NAME);
		}
		
		//注意：在tomcat中，直接使用DelegateFilter类型注册，在实例化DelegateFilter时会报找不到类的异常.
		FilterRegistration.Dynamic result =
				servletContext.addFilter(filterName, DelegateFilter.class);
		
		//匹配设定
		String text = initParams.getInitParameter(ISMATCHAFTER, "true");
		boolean isMatchAfter = Boolean.parseBoolean(text);
		
		text = initParams.getInitParameter(DISPATCHER);
		EnumSet<DispatcherType> set = null;
		if (text != null) {
			set = EnumSet.noneOf(DispatcherType.class);
			String[] dispatchers = StringUtils.split(text, ",");
			for (String dispatcher: dispatchers) {
				DispatcherType type = DispatcherType.valueOf(dispatcher);
				if (type != null) {
					set.add(type);
				}
			}
		}else {
			set = EnumSet.allOf(DispatcherType.class);
		}
		
		text = initParams.getInitParameter(URL_PATTERN);
		if (text != null) {
			String[] patterns = StringUtils.split(text, ",");
			result.addMappingForUrlPatterns(set, isMatchAfter, patterns);
		}else {
			text = initParams.getInitParameter(SERVLET_NAME);
			if (text != null) {
				String[] servletNames = StringUtils.split(text, ",");
				result.addMappingForServletNames(set, isMatchAfter, servletNames);
			}else {
				throw new AssemblyException("filter is must mapping to url-pattern or servlet-name.");
			}
		}
		
		//异步支持
		String asyncSupport = initParams.getInitParameter(ASYNC_SUPPORTED, "true");
		result.setAsyncSupported(Boolean.parseBoolean(asyncSupport));
		
		//初始化参数
		InitParameters subParams = InitParametersUtils.subset(initParams, INIT);
		List<String> keys = subParams.getInitParameterNames();
		for (String key: keys) {
			result.setInitParameter(key, subParams.getInitParameter(key));
		}
		
		result.setInitParameter(ServiceReference.ASSEMBLY_SYMBOLICNAME, assembly.getSymbolicName());
		
		return result;
	}
	
	/**
	 * 添加DelegateServlet
	 * @param assembly {@link Assembly},装配件
	 * @param servletContext {@link ServletContext},Servlet上下文
	 * @return {@link ServletRegistration.Dynamic}
	 * @throws AssemblyException
	 */
	public static ServletRegistration.Dynamic addDelegateServlet(Assembly assembly, 
			ServletContext servletContext) throws ParameterNotFoundException{
		Objects.requireNonNull(assembly, "\"assembly\" cannot be null.");
		Objects.requireNonNull(servletContext, "\"servletContext\" cannot be null.");
		
		AssemblyConfig config = assembly.getAssemblyConfig();
		InitParameters initParams = InitParametersUtils.subset(config, "container.servlet");
		String servletName = initParams.getInitParameter(SERVLET_NAME);
		if (servletName == null) {
			throw new ParameterNotFoundException("container.servlet." + SERVLET_NAME);
		}
		
		//注意：在tomcat中，如果直接使用DelegateServlet类型注册Servlet，会报找不到类型的异常
		ServletRegistration.Dynamic result =
				servletContext.addServlet(servletName, DelegateServlet.class);
		
		//匹配
		String text = initParams.getInitParameter(URL_PATTERN);
		if (text == null) {
			throw new ParameterNotFoundException("container.servlet." + URL_PATTERN);
		}
		String[] patterns = StringUtils.split(text, ",");
		result.addMapping(patterns);

		
		//异步支持
		String asyncSupport = initParams.getInitParameter(ASYNC_SUPPORTED, "true");
		result.setAsyncSupported(Boolean.parseBoolean(asyncSupport));
		
		//加载次序
		text = initParams.getInitParameter(LOAD_ON_STARTUP);
		if (text != null) {
			result.setLoadOnStartup(Integer.parseInt(text));
		}
		
		//初始化参数
		InitParameters subParams = InitParametersUtils.subset(initParams, INIT);
		List<String> keys = subParams.getInitParameterNames();
		for (String key: keys) {
			result.setInitParameter(key, subParams.getInitParameter(key));
		}
		result.setInitParameter(
				ServiceReference.ASSEMBLY_SYMBOLICNAME, 
				assembly.getSymbolicName());
		
		return result;
	}
		
	/**
	 * 从<code>servletContext</code>中取回{@link Framework}
	 * @param servletContext {@link ServletContext},Servlet上下文
	 * @return {@link Framework}, 可能返回null.
	 */
	public static Framework retrieveFramework(ServletContext servletContext) {
		if (servletContext == null) return null;
		
		Object result = servletContext.getAttribute(Framework.class.getName());
		if (result != null) {
			if (result instanceof Framework) {
				return Framework.class.cast(result);
			}
		}
		
		return null;
	}
	
	/**
	 * 从<code>config</code>中取回关联的{@link Assembly}
	 * @param config {@link ServletConfig}
	 * @return {@link Assembly}
	 * @throws ParameterNotFoundException 如果未设置符号名称，则抛出例外
	 */
	public static Assembly getAssociatedAssembly(ServletConfig config) throws ParameterNotFoundException{
		String symbolicName = getSymbolicName(config);
		Framework framework = retrieveFramework(config.getServletContext());
		return framework.getAssemblyRepository().getAssembly(symbolicName);
	}
	
	/**
	 * 从<code>config</code>中取回关联的{@link Assembly}
	 * @param config {@link FilterConfig},过滤器配置
	 * @return {@link Assembly}
	 * @throws ParameterNotFoundException 如果未设置符号名称，则抛出例外
	 */
	public static Assembly getAssociatedAssembly(FilterConfig config) throws ParameterNotFoundException{
		String symbolicName = getSymbolicName(config);
		if (symbolicName != null) {
			Framework framework = retrieveFramework(config.getServletContext());
			if (framework != null) {
				return framework.getAssemblyRepository().getAssembly(symbolicName);
			}
		}
		
		return null;
	}
	
	/**
	 * 从<code>config</code>中取回装配件符号名称
	 * @param config {@link ServletConfig},servlet配置
	 * @return {@link String}
	 * @throws ParameterNotFoundException 如果未设置符号名称，则抛出例外
	 */
	public static String getSymbolicName(ServletConfig config) throws ParameterNotFoundException{
		String result = config.getInitParameter(ServiceReference.ASSEMBLY_SYMBOLICNAME);
		if (result == null) {
			throw new ParameterNotFoundException(ServiceReference.ASSEMBLY_SYMBOLICNAME);
		}
		return result;
	}
	
	/**
	 * 从<code>config</code>中取回装配件符号名称
	 * @param config {@link FilterConfig},Filter配置
	 * @return {@link String}
	 * @throws ParameterNotFoundException 如果未设置符号名称，则抛出例外
	 */
	public static String getSymbolicName(FilterConfig config) throws ParameterNotFoundException{
		String result = config.getInitParameter(ServiceReference.ASSEMBLY_SYMBOLICNAME);
		if (result == null) {
			throw new ParameterNotFoundException(ServiceReference.ASSEMBLY_SYMBOLICNAME);
		}
		return result;
	}
}
