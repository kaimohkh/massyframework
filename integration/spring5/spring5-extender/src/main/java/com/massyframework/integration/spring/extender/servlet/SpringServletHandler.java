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

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyContext;
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
import com.massyframework.assembly.util.ContainerUtils;
import com.massyframework.assembly.web.util.WebUtils;
import com.massyframework.integration.spring.extender.DependencyServiceBeanDefiner;


/**
 * 提供DispatcherServlet的装配件上下文处理
 */
public class SpringServletHandler extends DependencyServiceBeanDefiner 
	implements WebComponentHandler, ReadyingHandler, ActivationHandler{

	private volatile boolean hasEnvironment = true;
	private volatile ServiceRegistration<Servlet> servletRegistration;
	private volatile HandlerRegistration<AssemblyContext> contextRegistration;
	
	private volatile DispatcherServletEx servlet;
		
	/**
	 * 构造方法
	 */
	public SpringServletHandler() {
	}



	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.WebComponentHandler#registWebComponent()
	 */
	@Override
	public void registWebComponent() throws Exception {
		Assembly assembly = this.getAssembly();
		ServiceRepository repository =
				ServiceRepository.retrieveFrom(assembly);
		ServletContext servletContext = repository.findService(ServletContext.class);
		if (servletContext == null) {
			this.hasEnvironment = false;
			return;
		}
		
		this.registerServlet(servletContext);		
	}
	
	/**
	 * 注册DelegateServlet
	 * @param servletContext {@link ServletContext}
	 */
	protected void registerServlet(ServletContext servletContext) {
		Assembly assembly = this.getAssembly();
		WebUtils.addDelegateServlet(assembly, servletContext);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.WebComponentHandler#inited()
	 */
	@Override
	public boolean inited() {
		if (!this.hasEnvironment) return false;
		
		if (this.servletRegistration == null) {
			return false;
		}
		return this.servletRegistration.getSerivce(
				this.getAssembly()).getServletConfig() != null;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.ReadyingHandler#readying()
	 */
	@Override
	public synchronized void readying() throws ReadyingException {
		if (!this.hasEnvironment) {
			return;
		}
		
		this.exportService();
	}
	
	/**
	 * 输出服务
	 */
	protected synchronized void exportService() {
		if (this.servletRegistration == null) {
			Assembly assembly = this.getAssembly();
			ServiceRepository repository = ServiceRepository.retrieveFrom(assembly);
			ServiceRegistry registry = repository.getServiceRegistry();
			
			ServletBridge servlet = new ServletBridge();
			ServiceProperties props = new ServiceProperties();
			props.setObjectClass(Servlet.class);
			props.setName(assembly.getAssemblyConfig().getInitParameter("container.servlet.servlet-name"));
			this.servletRegistration = 
					registry.register(servlet , props);
		}
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.ReadyingHandler#unreadyed()
	 */
	@Override
	public void unreadyed() throws ReadyingException {
		this.unexportService();
	}

	/**
	 * 撤销服务输出
	 */
	protected synchronized void unexportService() {
		if (this.servletRegistration != null){
			this.servletRegistration.unregister();
			this.servletRegistration = null;
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
				this.servlet = new DispatcherServletEx(this, ContainerUtils.hasContainerConfigClasses(assembly));
				Servlet birdge = this.servletRegistration.getSerivce(assembly);
				servlet.init(birdge.getServletConfig());
							
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
			
			this.servlet.destroy();
			this.servlet = null;
		}
	}
	
	protected void doService(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		Servlet dispatcherServlet = this.servlet;
		if (dispatcherServlet != null) {
			dispatcherServlet.service(req, res);
		}
	}

	/**
	 * ServletBridge在DelegateServlet和DispatcherServletEx之间的连接桥
	 */
	private class ServletBridge implements Servlet {
		
		private ServletConfig config;

		@Override
		public void init(ServletConfig config) throws ServletException {
			this.config = config;	
			try {
				SpringServletHandler.this.getLifecycleManager().start();
			} catch (Exception e) {
				logError(e.getMessage(), e);
				throw new ServletException(e.getMessage(), e);
			}
		}
		

		@Override
		public ServletConfig getServletConfig() {
			return this.config;
		}

		@Override
		public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
			doService(req, res);
		}

		@Override
		public String getServletInfo() {
			return null;
		}

		@Override
		public void destroy() {
			try {
				SpringServletHandler.this.getLifecycleManager().stop();
			} catch (Exception e) {
				logError(e.getMessage(), e);
			}
		}
		
	}
}
