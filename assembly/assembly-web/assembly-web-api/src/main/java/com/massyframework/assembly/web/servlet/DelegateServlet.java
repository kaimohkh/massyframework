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
package com.massyframework.assembly.web.servlet;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyNotReadiedException;
import com.massyframework.assembly.service.ServiceReference;
import com.massyframework.assembly.service.ServiceRepository;
import com.massyframework.assembly.service.ServiceTracker;
import com.massyframework.assembly.service.ServiceTrackerCustomizer;
import com.massyframework.assembly.web.ModularServletContext;
import com.massyframework.assembly.web.processing.JspResourceProcessor;
import com.massyframework.assembly.web.processing.JspResourceProcessorRepository;
import com.massyframework.lang.util.ClassLoaderUtils;
import com.massyframework.logging.Logger;

/**
 * 为装配件内部的Servlet提供代理。<br>
 * 在运行框架启动阶段，先向ServletContext注册DelegateServlet，当装配件进入正常工作态后，
 * 才将Http请求转给装配件内的Servlet处理。
 */
public class DelegateServlet extends AbstractServlet implements ServiceTrackerCustomizer<Servlet>{
	
	private ServiceTracker<Servlet> tracker;
	private volatile ClassLoader classLoader;
	private volatile Servlet internelServlet;
	private volatile ServletException initException;
	private AtomicInteger invokeTimes;
	private volatile Logger logger;
	
	private JspResourceProcessorRepository jspResRepo;

	/**
	 * 构造方法
	 */
	public DelegateServlet() {
		this.invokeTimes = new AtomicInteger();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		Servlet servlet = this.internelServlet;
		if (servlet != null) {
			if (this.initException != null) {
				throw this.initException;
			}
			
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(this.classLoader);
			MassyHttpServletRequest request =
					new MassyHttpServletRequest((HttpServletRequest)req, this.getServletContext());
			MassyHttpServletResponse response =
					new MassyHttpServletResponse((HttpServletResponse)res);
			try {
				this.invokeTimes.incrementAndGet();				
				request.setAttribute(Assembly.class.getName(), this.getAssociatedAssembly());
				
				servlet.service(request, response);
				if (response.getStatus() == HttpServletResponse.SC_NOT_FOUND){
					//其他非Servlet处理的静态资源
					JspResourceProcessor processor =
							this.getJspResourceProcessorRepository().getJspResourceProcessor(
									(ModularServletContext)this.getServletContext());
					if (processor != null) {
						if (processor.isSupport(request)) {
							processor.service((HttpServletRequest)req, (HttpServletResponse)res);
						}else {
							response.commit();
						}
					}else {
						response.commit();
					}
				}else {
					response.commit();
				}
			}finally {
				this.invokeTimes.decrementAndGet();
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}else {
			throw new AssemblyNotReadiedException(this.getAssociatedAssembly());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.servlet.AbstractServlet#init()
	 */
	@Override
	protected synchronized void init() throws ServletException {
		super.init();
		if (this.tracker == null){
			Assembly assembly = this.getAssociatedAssembly();
			this.classLoader = assembly.getAssemblyClassLoader();
			this.logger = assembly.getLogger();
					
			String filterString = this.genericFilterString();	
			this.tracker = new ServiceTracker<Servlet>(
					assembly,
					Servlet.class,
					filterString,
					this);
			this.tracker.open();
		}
		
		this.logger.debug("init Servlet[" + this.getServletConfig().getServletName() + "].");
	}

	/**
	 * 获取JspResourceProcessorRepository.
	 * @return {@link JspResourceProcessorRepository}
	 */
	protected JspResourceProcessorRepository getJspResourceProcessorRepository() {
		if (this.jspResRepo == null) {
			ServiceRepository repo = ServiceRepository.retrieveFrom(this.getAssociatedAssembly());
			this.jspResRepo = repo.getService(JspResourceProcessorRepository.class);
		}
		return this.jspResRepo;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.servlet.AbstractServlet#destroy()
	 */
	@Override
	public synchronized void destroy() {
		if (this.tracker != null){
			this.tracker.close();
			this.tracker = null;
			
			if (this.internelServlet != null) {
				//等待调用全部完成
				while (this.invokeTimes.get() != 0) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				
				ClassLoader contextLoader =
						ClassLoaderUtils.setThreadContextClassLoader(this.classLoader);
				try {
					this.internelServlet.destroy();
				}finally {
					ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
				}
			}
			
			this.classLoader = null;
			this.internelServlet = null;
			this.initException = null;
		}
		this.invokeTimes = null;
		this.jspResRepo = null;
		
		super.destroy();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceTrackerCustomizer#addService(com.massyframework.assembly.service.ServiceReference, com.massyframework.assembly.service.ServiceRepository)
	 */
	@Override
	public synchronized Servlet addService(ServiceReference<Servlet> reference, ServiceRepository repository) {
		if (this.internelServlet == null) {
			Servlet servlet = repository.getService(reference);			
			ClassLoader contextLoader = 
					ClassLoaderUtils.setThreadContextClassLoader(this.classLoader);
			try {
				this.invokeTimes.incrementAndGet();
				servlet.init(this.getServletConfig());
				this.internelServlet = servlet;
				this.initException = null;
			}catch(ServletException e) {
				Logger logger = this.getAssociatedAssembly().getLogger();
				if (logger.isErrorEnabled()) {
					logger.error(e.getMessage(), e);
				}
				this.initException = e;
			}finally {
				this.invokeTimes.decrementAndGet();
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
			return servlet;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceTrackerCustomizer#removeService(com.massyframework.assembly.service.ServiceReference, java.lang.Object)
	 */
	@Override
	public synchronized void removeService(ServiceReference<Servlet> reference, Servlet service) {
		if (this.internelServlet != null) {
			this.internelServlet = null;
			this.initException = null;
			
			//等待调用全部完成
			while (this.invokeTimes.get() != 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(this.classLoader);
			try {
				service.destroy();
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}
	}
		
	/**
	 * 生成过滤字符串
	 * @return {@link String}
	 */
	protected String genericFilterString(){
		StringBuilder builder = new StringBuilder();
		builder.append("(&")
			.append("(").append(ServiceReference.ASSEMBLY_SYMBOLICNAME)
				.append("=").append(this.getAssociatedAssembly().getSymbolicName()).append(")")
			.append("(").append(ServiceReference.NAME)
				.append("=").append(this.getServletConfig().getServletName()).append(")")
			.append(")");
		return builder.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Assembly assembly = this.getAssociatedAssembly();
		return assembly == null ?
				this.getClass().getSimpleName() :
					this.getClass().getSimpleName() + "[assembly=" + assembly.getSymbolicName() + "]."; 
	}
}
