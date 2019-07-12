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
package com.massyframework.assembly.web.filter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyNotReadiedException;
import com.massyframework.assembly.service.ServiceReference;
import com.massyframework.assembly.service.ServiceRepository;
import com.massyframework.assembly.service.ServiceTracker;
import com.massyframework.assembly.service.ServiceTrackerCustomizer;
import com.massyframework.lang.util.ClassLoaderUtils;
import com.massyframework.logging.Logger;

/**
 * 代理的Filter,为装配件内部的Filter提供代理。<br>
 * 在运行框架启动阶段，先向ServletContext注册DelegateFilter，当装配件进入正常工作态时，
 * 才将Http请求转给装配件内的Filter处理。
 */
public class DelegateFilter extends AbstractFilter implements ServiceTrackerCustomizer<Filter> {
	
	public static final String ALLOW_SKIP = "allowNotReadyToskip";

	private ServiceTracker<Filter> tracker;
	private boolean allowSkip = true;
	private volatile ClassLoader classLoader;
	private volatile Filter internelFilter;
	private volatile ServletException initException;
	
	//调用次数
	private AtomicInteger invokeTimes;
	
	/**
	 * 构造方法
	 */
	public DelegateFilter() {
		this.invokeTimes = new AtomicInteger();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (this.initException != null) {
			throw this.initException;
		}
		
		Filter filter = this.internelFilter;
		HttpServletRequest req = (HttpServletRequest)request;
		if (filter != null) {
			ClassLoader contextLaoder =
					ClassLoaderUtils.setThreadContextClassLoader(this.classLoader);
			try {
				//计数加1
				this.invokeTimes.incrementAndGet();
				filter.doFilter(request, response, chain);
			}finally {
				//计数减1
				this.invokeTimes.decrementAndGet();
				ClassLoaderUtils.setThreadContextClassLoader(contextLaoder);
			}
		}else {
			//检查是否可跳过
			if (allowNotReadiedToSkip()) {
				Logger logger = this.getAssociatedAssembly().getLogger();
				if (logger.isDebugEnabled()) {
					logger.debug("assembly is not readied, skip request: " + req.getRequestURI() + ".");
				}
				chain.doFilter(request, response);
			}else {
				throw new AssemblyNotReadiedException(this.getAssociatedAssembly());
			}
		}
	}
		
	/**
	 * 是否允许未就绪则跳过请求处理
	 * @return {@link boolean},返回<code>true</code>表示允许跳过
	 */
	protected boolean allowNotReadiedToSkip() {
		return this.allowSkip;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.filter.AbstractFilter#init()
	 */
	@Override
	protected synchronized void init() throws ServletException {
		super.init();
		if (this.tracker == null){
			super.init();
			Assembly assembly = this.getAssociatedAssembly();
			this.classLoader = assembly.getAssemblyClassLoader();
			if ("false".equalsIgnoreCase(this.getFilterConfig().getInitParameter(ALLOW_SKIP))){
				this.allowSkip = false;
			}
			
			String filterString = this.genericFilterString(assembly);	
			this.tracker = new ServiceTracker<Filter>(
					assembly,
					Filter.class,
					filterString,
					this);
			this.tracker.open();
		}
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.filter.AbstractFilter#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
		if (this.tracker != null){
			this.tracker.close();
			this.tracker = null;
			
			if (this.internelFilter != null) {
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
					this.internelFilter.destroy();
				}finally {
					ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
				}
			}
			
			this.classLoader = null;
			this.internelFilter = null;
			this.initException = null;
		}
		this.invokeTimes = null;
	}
	
	/**
	 * 生成过滤字符串
	 * @return {@link String}
	 */
	protected String genericFilterString(Assembly assembly){
		StringBuilder builder = new StringBuilder();
		builder.append("(&")
			.append("(").append(ServiceReference.ASSEMBLY_SYMBOLICNAME)
				.append("=").append(assembly.getSymbolicName()).append(")")
			.append("(").append(ServiceReference.NAME)
				.append("=").append(this.getFilterConfig().getFilterName()).append(")")
			.append(")");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceTrackerCustomizer#addService(com.massyframework.assembly.service.ServiceReference, com.massyframework.assembly.service.ServiceRepository)
	 */
	@Override
	public synchronized Filter addService(ServiceReference<Filter> reference, ServiceRepository repository) {
		if (this.internelFilter == null) {
			Filter result = repository.getService(reference);
						
			ClassLoader contextLoader = 
					ClassLoaderUtils.setThreadContextClassLoader(
							this.classLoader);
			try {
				this.invokeTimes.incrementAndGet();
				result.init(this.getFilterConfig());
				this.internelFilter = result;
				this.initException = null;
				return result;
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
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceTrackerCustomizer#removeService(com.massyframework.assembly.service.ServiceReference, java.lang.Object)
	 */
	@Override
	public synchronized void removeService(ServiceReference<Filter> reference, Filter service) {
		if (this.internelFilter == service) {
			this.internelFilter = null;
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
