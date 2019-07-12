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
 * @日   期:  2019年1月28日
 */
package com.massyframework.integration.spring.extender.listener;

import java.net.MalformedURLException;
import java.util.Objects;

import javax.servlet.ServletContext;

import org.springframework.core.io.Resource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyException;
import com.massyframework.assembly.util.ContainerUtils;
import com.massyframework.integration.spring.extender.ConfigLocationsUtils;
import com.massyframework.integration.spring.extender.DependencyServiceBeanDefiner;
import com.massyframework.integration.spring.extender.ProfileUtils;
import com.massyframework.integration.spring.extender.SpringContainer;
import com.massyframework.integration.spring.extender.context.SpringAnnotationWebApplicationContext;
import com.massyframework.integration.spring.extender.context.SpringXmlWebApplicationContext;


/**
 * ContextLoaderListener扩展
 */
public class ContextLoaderListenerEx extends ContextLoaderListener {

	private final DependencyServiceBeanDefiner beanDefiner;
	private final boolean isAnnotated;
	private volatile ServletContext servletContext;
	
	/**
	 * 构造方法，默认采用xml配置方式
	 * @param beanDefiner 依赖服务定义器
	 */
	public ContextLoaderListenerEx(DependencyServiceBeanDefiner beanDefiner) {
		this(beanDefiner, false);
	}
	
	/**
	 * 构造方法
	 * @param beanDefiner 依赖服务定义器
	 * @param isAnnotated 是否采用注解方式
	 */
	public ContextLoaderListenerEx(DependencyServiceBeanDefiner beanDefiner, boolean isAnnotated) {
		this.beanDefiner =
				Objects.requireNonNull(beanDefiner, "\"beanDefiner\" cannot be null.");
		this.isAnnotated = isAnnotated;
	}
	
	/**
	 * 构造方法，默认采用xml配置方式
	 * @param context 父WebApplicationContext
	 * @param beanDefiner 依赖服务定义器
	 */
	public ContextLoaderListenerEx(WebApplicationContext context, 
			DependencyServiceBeanDefiner beanDefiner) {
		this(context, beanDefiner, false);
	}
	
	/**
	 * 构造方法
	 * @param context 父WebApplicationContext
	 * @param beanDefiner 依赖服务定义器
	 * @param isAnnotated 是否采用注解方式
	 */
	public ContextLoaderListenerEx(WebApplicationContext context, 
			DependencyServiceBeanDefiner beanDefiner,
			boolean isAnnotated) {
		super(context);
		this.beanDefiner =
				Objects.requireNonNull(beanDefiner, "\"beanDefiner\" cannot be null.");
		this.isAnnotated = isAnnotated;
	}
			
	/* (non-Javadoc)
	 * @see org.springframework.web.context.ContextLoader#createWebApplicationContext(javax.servlet.ServletContext)
	 */
	@Override
	protected WebApplicationContext createWebApplicationContext(ServletContext sc) {
		if (this.isAnnotated) {
			SpringAnnotationWebApplicationContext result =
					new SpringAnnotationWebApplicationContext(this.beanDefiner);
			
			Assembly assembly = this.beanDefiner.getAssembly();
			ClassLoader classLoader = assembly.getAssemblyClassLoader();
			Class<?>[] classes = ContainerUtils.getContainerConfigClasses(assembly);
			if (classes.length >0) {
				result.register(classes);
			}
			result.setClassLoader(classLoader);
			ProfileUtils.setProfiles(result, assembly);
			
			SpringContainer container = new SpringContainer(result);
			this.beanDefiner.bindSpringContainer(container);
			return result;
		}else {
			SpringXmlWebApplicationContext result =
					new SpringXmlWebApplicationContext(this.beanDefiner);
			Assembly assembly = this.beanDefiner.getAssembly();
			ClassLoader classLoader = assembly.getAssemblyClassLoader();
			result.setClassLoader(classLoader);
			try {
				Resource[] resources = ConfigLocationsUtils.parseConfigLocationsToArray(assembly);
				result.setConfigResources(resources);
			} catch (MalformedURLException e) {
				throw new AssemblyException(e.getMessage(), e);
			}
			
			SpringContainer container = new SpringContainer(result);
			this.beanDefiner.bindSpringContainer(container);
			return result;
		}
	}

	public ServletContext getServletContext() {
		return this.servletContext;
	}
}
