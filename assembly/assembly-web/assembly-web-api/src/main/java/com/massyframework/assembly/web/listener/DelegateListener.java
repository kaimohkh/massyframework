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
package com.massyframework.assembly.web.listener;

import java.util.Objects;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.Framework;
import com.massyframework.assembly.service.ServiceReference;
import com.massyframework.assembly.service.ServiceRepository;
import com.massyframework.assembly.service.ServiceTracker;
import com.massyframework.assembly.service.ServiceTrackerCustomizer;
import com.massyframework.assembly.web.util.WebUtils;
import com.massyframework.lang.util.ClassLoaderUtils;

/**
 * 为装配件内部的ServletContextListener提供代理。<br>
 * 在运行框架启动阶段，先向ServletContext注册DelegateListener，当装配件进入正常工作态时，
 * 才将事件转给装配件内的ServletContextListener处理。
 */
public class DelegateListener implements 
	ServletContextListener, 
	ServletContextAttributeListener, 
	HttpSessionActivationListener,
	HttpSessionAttributeListener, 
	HttpSessionBindingListener, 
	HttpSessionIdListener, 
	HttpSessionListener,
	ServiceTrackerCustomizer<ServletContextListener> {

	private final String symbolicName;
	private final String name;
	private volatile Assembly assembly;
	private volatile ServletContext context;
	
	private volatile ServletContextListener listener;
	private ServiceTracker<ServletContextListener> tracker;
	
	/**
	 * 构造方法
	 * @param symbolicName 符号名称
	 * @param name 名称
	 */
	public DelegateListener(String symbolicName, String name) {
		this.symbolicName = Objects.requireNonNull(symbolicName, "\"symbolicName\" cannot be null.") ;
		this.name = Objects.requireNonNull(name, "\"name\" cannot be null.") ;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public synchronized void contextInitialized(ServletContextEvent sce) {
		sce.getServletContext().log(this + " contextInitialized.");
		this.context = sce.getServletContext();
		if (this.tracker == null){
			try{
				Framework framework = this.retrieveFramework(sce.getServletContext());
				this.assembly = framework.getAssemblyRepository().getAssembly(this.symbolicName);
				
				String filterString = this.genericFilterString();
				this.tracker = new ServiceTracker<ServletContextListener>(
						this.assembly, 
						ServletContextListener.class, 
						filterString, 
						this);
				this.tracker.open();
			}catch(Exception e){
				sce.getServletContext().log("servlet context initialized failed", e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public synchronized void contextDestroyed(ServletContextEvent sce) {
		if (this.tracker != null){
			this.tracker.close();
			this.tracker = null;
		}
		this.context = null;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextAttributeListener#attributeAdded(javax.servlet.ServletContextAttributeEvent)
	 */
	@Override
	public void attributeAdded(ServletContextAttributeEvent event) {
		ServletContextAttributeListener listener = this.asInterface(ServletContextAttributeListener.class);
		if (listener != null){
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
			try {
				listener.attributeAdded(event);
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextAttributeListener#attributeRemoved(javax.servlet.ServletContextAttributeEvent)
	 */
	@Override
	public void attributeRemoved(ServletContextAttributeEvent event) {
		ServletContextAttributeListener listener = this.asInterface(ServletContextAttributeListener.class);
		if (listener != null){
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
			try {
				listener.attributeRemoved(event);
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextAttributeListener#attributeReplaced(javax.servlet.ServletContextAttributeEvent)
	 */
	@Override
	public void attributeReplaced(ServletContextAttributeEvent event) {
		ServletContextAttributeListener listener = this.asInterface(ServletContextAttributeListener.class);
		if (listener != null){
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
			try {
				listener.attributeReplaced(event);
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionActivationListener#sessionWillPassivate(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionWillPassivate(HttpSessionEvent se) {
		HttpSessionActivationListener listener = this.asInterface(HttpSessionActivationListener.class);
		if (listener != null){
			
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
			try {
				listener.sessionWillPassivate(se);
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionActivationListener#sessionDidActivate(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDidActivate(HttpSessionEvent se) {
		HttpSessionActivationListener listener = this.asInterface(HttpSessionActivationListener.class);
		if (listener != null){
			
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
			try {
				listener.sessionDidActivate(se);
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionAttributeListener#attributeAdded(javax.servlet.http.HttpSessionBindingEvent)
	 */
	@Override
	public void attributeAdded(HttpSessionBindingEvent event) {
		HttpSessionAttributeListener listener = this.asInterface(HttpSessionAttributeListener.class);
		if (listener != null){
			
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
			try {
				listener.attributeAdded(event);
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionAttributeListener#attributeRemoved(javax.servlet.http.HttpSessionBindingEvent)
	 */
	@Override
	public void attributeRemoved(HttpSessionBindingEvent event) {
		HttpSessionAttributeListener listener = this.asInterface(HttpSessionAttributeListener.class);
		if (listener != null){
		
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
			try {
				listener.attributeRemoved(event);
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionAttributeListener#attributeReplaced(javax.servlet.http.HttpSessionBindingEvent)
	 */
	@Override
	public void attributeReplaced(HttpSessionBindingEvent event) {
		HttpSessionAttributeListener listener = this.asInterface(HttpSessionAttributeListener.class);
		if (listener != null){
		
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
			try {
				listener.attributeReplaced(event);
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		HttpSessionBindingListener listener = this.asInterface(HttpSessionBindingListener.class);
		if (listener != null){
			
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
			try {
				listener.valueBound(event);
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		HttpSessionBindingListener listener = this.asInterface(HttpSessionBindingListener.class);
		if (listener != null){
		
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
			try {
				listener.valueUnbound(event);
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionIdListener#sessionIdChanged(javax.servlet.http.HttpSessionEvent, java.lang.String)
	 */
	@Override
	public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
		HttpSessionIdListener listener = this.asInterface(HttpSessionIdListener.class);
		if (listener != null){
			
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
			try {
				listener.sessionIdChanged(event, oldSessionId);
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		HttpSessionListener listener = this.asInterface(HttpSessionListener.class);
		if (listener != null){
			
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
			try {
				listener.sessionCreated(se);
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSessionListener listener = this.asInterface(HttpSessionListener.class);
		if (listener != null){
			
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
			try {
				listener.sessionDestroyed(se);
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}
	}
	
		
	/**
	 * 将委托服务转换为指定接口实例，不支持可返回null.
	 * @param interfaceType 接口类型
	 * @return {@link I}, 可以返回null.
	 */
	protected <I> I asInterface(Class<I> interfaceType){
		if (this.listener != null){
			if (interfaceType.isAssignableFrom(listener.getClass())){
				return interfaceType.cast(listener);
			}
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceTrackerCustomizer#addService(com.massyframework.assembly.service.ServiceReference, com.massyframework.assembly.service.ServiceRepository)
	 */
	@Override
	public ServletContextListener addService(ServiceReference<ServletContextListener> reference, ServiceRepository repository) {
		Object value = reference.getProperty(ServiceReference.NAME);
		if (value instanceof String) {
			if (this.name.equals(value.toString())){
				ClassLoader contextLoader =
						ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
				try {
					this.listener = (ServletContextListener) repository.getService(reference);
					this.listener.contextInitialized(new ServletContextEvent(this.context));
					return this.listener;
				}finally {
					ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
				}
			}
		}else {
			String[] values = (String[])value;
			for (String text: values) {
				if (this.name.equals(text)){
					ClassLoader contextLoader =
							ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
					try {
						this.listener = (ServletContextListener) repository.getService(reference);
						this.listener.contextInitialized(new ServletContextEvent(this.context));
						return this.listener;
					}finally {
						ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
					}
				}
			}
		}
		
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceTrackerCustomizer#removeService(com.massyframework.assembly.service.ServiceReference, java.lang.Object)
	 */
	@Override
	public void removeService(ServiceReference<ServletContextListener> reference, ServletContextListener service) {
		if (this.listener == service){
			this.listener.contextDestroyed(new ServletContextEvent(this.context));
			this.listener = null;
		}
	}

	/**
	 * 取回运行框架
	 * @return {@link Framework}
	 * @throws ServletException
	 */
	protected Framework retrieveFramework(ServletContext context) throws ServletException{
		Framework result = WebUtils.retrieveFramework(context);
		if (result == null) {
			throw new ServletException("cannot load Framework instance.");
		}
		return result;
	}
	
	/**
	 * 生成过滤字符串
	 * @return {@link String}
	 */
	protected String genericFilterString(){
		StringBuilder builder = new StringBuilder();
		builder.append("(&")
			.append("(").append(ServiceReference.ASSEMBLY_SYMBOLICNAME)
				.append("=").append(this.symbolicName).append(")")
			.append("(").append(ServiceReference.NAME)
				.append("=").append(this.name).append(")")
			.append(")");
		return builder.toString();
	}
			
	/**
	 * 获取服务仓储
	 * @return {@link ServiceRepository}
	 * @throws ServletException Servlet发生的例外
	 */
	protected ServiceRepository getServiceRepository() throws ServletException{
		return ServiceRepository.retrieveFrom(this.getAssembly());
	}
	
	protected Assembly getAssembly(){
		return this.assembly;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DelegateListener [symbolicName=" + symbolicName + ", name=" + name + "]";
	}
	
}

