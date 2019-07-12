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
 * @日   期:  2019年1月29日
 */
package com.massyframework.integration.spring.extender.servlet;

import java.util.Objects;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

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
 * 提供DispatcherServlet扩展
 */
public class DispatcherServletEx extends DispatcherServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 794069809658994938L;

	private final DependencyServiceBeanDefiner beanDefiner;
	private final boolean isAnnotated; //是否基于注解配置模式
	
	/**
	 * 构造方法，默认采用xml配置方式
	 * @param beanDefiner 依赖服务定义器
	 */
	public DispatcherServletEx(DependencyServiceBeanDefiner beanDefiner) {
		this(beanDefiner, false);
	}
	
	/**
	 * 构造方法
	 * @param beanDefiner 依赖服务定义器
	 * @param isAnnotated 是否采用注解配置
	 */
	public DispatcherServletEx(DependencyServiceBeanDefiner beanDefiner, boolean isAnnotated) {
		this.beanDefiner =
				Objects.requireNonNull(beanDefiner, "\"beanDefiner\" cannot be null.");
		this.isAnnotated = isAnnotated;
	}

	/**
	 * 构造方法， 默认采用xml配置方式
	 * @param webApplicationContext 父WebApplicationContext上下文
	 * @param beanDefiner 依赖服务定义器
	 */
	public DispatcherServletEx(WebApplicationContext webApplicationContext, 
			DependencyServiceBeanDefiner beanDefiner) {
		this(webApplicationContext, beanDefiner, false);
	}
	
	/**
	 * 构造方法
	 * @param webApplicationContext 父WebApplicationContext上下文
	 * @param beanDefiner 依赖服务定义器
	 * @param isAnnotated 是否采用注解配置
	 */
	public DispatcherServletEx(WebApplicationContext webApplicationContext, 
			DependencyServiceBeanDefiner beanDefiner, boolean isAnnotated) {
		super(webApplicationContext);
		this.beanDefiner =
				Objects.requireNonNull(beanDefiner, "\"beanDefiner\" cannot be null.");
		this.isAnnotated = isAnnotated;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.FrameworkServlet#createWebApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	protected WebApplicationContext createWebApplicationContext(ApplicationContext parent) {
		try {
			Assembly assembly = this.beanDefiner.getAssembly();
			if (this.isAnnotated) {
				SpringAnnotationWebApplicationContext result =
						new SpringAnnotationWebApplicationContext(this.beanDefiner);
				ClassLoader classLoader =
						assembly.getAssemblyClassLoader();
				result.setClassLoader(classLoader);
				result.setParent(parent);			
				Class<?>[] classes = ContainerUtils.getContainerConfigClasses(assembly);
				if (classes.length >0) {
					result.register(classes);
				}
				ProfileUtils.setProfiles(result, assembly);
				
				SpringContainer container =
						new SpringContainer(result);
				this.beanDefiner.bindSpringContainer(container);
				configureAndRefreshWebApplicationContext(result);
				return result;
			}else {
				SpringXmlWebApplicationContext result =
						new SpringXmlWebApplicationContext(this.beanDefiner);
				ClassLoader classLoader =
						assembly.getAssemblyClassLoader();
				result.setClassLoader(classLoader);
				result.setParent(parent);
				
				Resource[] resources =
						ConfigLocationsUtils.parseConfigLocationsToArray(assembly);
				result.setConfigResources(resources);
				
				SpringContainer container =
						new SpringContainer(result);
				this.beanDefiner.bindSpringContainer(container);
				configureAndRefreshWebApplicationContext(result);			
				return result;
			}
		}catch(Exception e) {
			throw new AssemblyException(e.getMessage(), e);
		}
	}
}
