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

import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyContext;
import com.massyframework.assembly.initparam.InitParameters;
import com.massyframework.assembly.monitor.AssemblyTask;
import com.massyframework.assembly.monitor.AssemblyTaskMonitor;
import com.massyframework.assembly.monitor.AssemblyTaskPhases;
import com.massyframework.assembly.service.ServiceProperties;
import com.massyframework.assembly.service.ServiceRegistration;
import com.massyframework.assembly.service.ServiceRegistry;
import com.massyframework.assembly.service.ServiceRepository;
import com.massyframework.assembly.spi.ActivationHandler;
import com.massyframework.assembly.spi.HandlerRegistration;
import com.massyframework.assembly.spi.ReadyingException;
import com.massyframework.assembly.spi.ReadyingHandler;
import com.massyframework.assembly.spi.WebComponentHandler;
import com.massyframework.assembly.util.InitParametersUtils;
import com.massyframework.assembly.web.listener.DelegateListener;
import com.massyframework.integration.spring.extender.DependencyServiceBeanDefiner;


/**
 * 基于ContextLoaderListener的ServletContextListener容器处理
 */
public final class SpringListenerHandler extends DependencyServiceBeanDefiner
	implements WebComponentHandler, ReadyingHandler, ActivationHandler{

	private final String listenerName;
	private volatile ServiceRegistration<ServletContextListener> registration;
	
	private volatile HandlerRegistration<AssemblyContext> contextRegistration;
	private volatile ContextLoaderListenerEx contextLoaderListener;
	private volatile ServletContextEvent event;
	
	/**
	 * 
	 */
	public SpringListenerHandler() {
		this.listenerName = UUID.randomUUID().toString();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.WebComponentHandler#registWebComponent()
	 */
	@Override
	public void registWebComponent() throws Exception {	
		Assembly assembly = this.getAssembly();
		ServiceRepository repository =
				ServiceRepository.retrieveFrom(assembly);
		ServletContext servletContext = repository.getService(ServletContext.class);
		this.registerWebListener(servletContext);
	}
	
	/**
	 * 注册Web监听器
	 * @param servletContext {@link ServletContext},Servlet上下文
	 */
	private void registerWebListener(ServletContext servletContext){
		Assembly assembly = this.getAssembly();
		DelegateListener delegetListener =
				new DelegateListener(assembly.getSymbolicName(), this.listenerName);
		servletContext.addListener(delegetListener);
		
		//初始化参数
		InitParameters initParams = InitParametersUtils.subset(assembly.getAssemblyConfig(), "container.init");
		List<String> names = initParams.getInitParameterNames();
		for (String name: names) {
			servletContext.setInitParameter(name, initParams.getInitParameter(name));
		}
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.WebComponentHandler#inited()
	 */
	@Override
	public boolean inited() {		
		return this.event != null;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.ReadyingHandler#readying()
	 */
	@Override
	public void readying() throws ReadyingException {		
		// 输出ServletContextListener服务
		this.exportService();
	}
	
	/**
	 * 输出服务，服务输出后，DelegateListener才能和输出的服务挂接上
	 */
	protected synchronized void exportService() {
		if (this.registration == null) {
			//注册服务
			ServiceRepository repository = ServiceRepository.retrieveFrom(this.getAssembly());
			ServiceRegistry registry = repository.getServiceRegistry();
			
			ListenerBridge bridge = new ListenerBridge();
			ServiceProperties props = new ServiceProperties();
			props.setObjectClass(ServletContextListener.class);
			props.setName(this.listenerName);
			this.registration = 
					registry.register(bridge , props);
		}
	}
	
	/**
	 * 创建{@link ContextLoaderListenerEx}实例
	 * @return {@link ContextLoaderListenerEx}
	 */
	protected ContextLoaderListenerEx createContextLoaderListener() {
		return new ContextLoaderListenerEx(this);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.ReadyingHandler#unreadyed()
	 */
	@Override
	public void unreadyed() throws ReadyingException {		
		// 依赖服务未就绪
		this.unexportService();
	}
	
	/**
	 * 撤销服务输出
	 */
	protected synchronized void unexportService(){
		//注销
		if (this.registration != null){
			this.registration.unregister();
			this.registration = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.ActivationHandler#doStarting()
	 */
	@Override
	public synchronized void doStarting() throws Exception {		
		if (this.contextRegistration == null) {
			Assembly assembly = this.getAssembly();
			AssemblyTaskMonitor taskMonitor =
					getHandler(AssemblyTaskMonitor.class);
			AssemblyTask task = taskMonitor.startTask(assembly, AssemblyTaskPhases.CONTAINER_INITIALIZE);
			
			try {
				//创建ContextLoaderListenerEx
				this.contextLoaderListener = new ContextLoaderListenerEx(this);			
				contextLoaderListener.contextInitialized(this.event);
				
				//注册AssemblyContext
				this.contextRegistration = 
						this.getLifecycleManager().register(this.getAssemblyContext());	
				task.complete();
			}catch(Exception e) {
				task.complete(e);
				throw e;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.ActivationHandler#doStopped()
	 */
	@Override
	public synchronized void doStopped() throws Exception {
		if (this.contextRegistration != null) {		
			this.contextRegistration.unregister();
			this.contextRegistration = null;
			
			this.contextLoaderListener.contextDestroyed(this.event);
			this.contextLoaderListener= null;
		}
	}
	
	/**
	 * 初始化ServletContext
	 * @param sce {@link ServletContextEvent}
	 */
	protected void initServletContext(ServletContextEvent sce) {
		this.event = sce;
		try {
			this.getLifecycleManager().start();
		}catch(Exception e) {
			this.logError(e.getMessage(), e);
		}
	}
	
	/**
	 * 释放ServletContext
	 * @param sce {@link ServletContextEvent}
	 */
	protected void destortedServletContext(ServletContextEvent sce) {
		this.event = null;
	}
	
	private class ListenerBridge implements ServletContextListener {
		
		@Override
		public void contextInitialized(ServletContextEvent sce) {
			initServletContext(sce);
		}

		@Override
		public void contextDestroyed(ServletContextEvent sce) {
			destortedServletContext(sce);
		}
		
	}
}
