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
package com.massyframework.assembly.web.runtime;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.massyframework.assembly.web.ModularServletContext;
import com.massyframework.assembly.web.ModularServletContextRepository;
import com.massyframework.assembly.web.WebResourceRepository;
import com.massyframework.assembly.web.processing.JspResourceProcessor;
import com.massyframework.assembly.web.processing.JspResourceProcessorRepository;
import com.massyframework.logging.Logger;
import com.massyframework.logging.LoggerFactory;

/**
 * 提供Jsp支持的Servlet
 */
class JspSupportServlet implements Servlet {

	private final Logger logger = LoggerFactory.getLogger(JspSupportServlet.class.getName());
	private final ModularServletContextRepository modularServletContextRepo;
	private JspResourceProcessorRepository jspResProcRepo;
	private WebResourceRepository webResRepo;
	private ServletConfig config;
	
		
	/**
	 * 构造方法
	 */
	public JspSupportServlet(ModularServletContextRepository modularServletContextRepo,
			WebResourceRepository webResRepo,
			JspResourceProcessorRepository jspResProcRepo) {
		this.modularServletContextRepo = 
				Objects.requireNonNull(modularServletContextRepo, "\"modularServletContextRepo\" cannot be null.");
		this.webResRepo =  Objects.requireNonNull(webResRepo, "\"webResRepo\" cannot be null.");
		this.jspResProcRepo = Objects.requireNonNull(jspResProcRepo, "\"jspResProcRepo\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		this.config = config;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#getServletConfig()
	 */
	@Override
	public ServletConfig getServletConfig() {
		return this.config;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		
		if (logger.isTraceEnabled()) {
			logger.trace("receive http request: " + request.getRequestURI() + ".");
		}
		
		ServletContext context = req.getServletContext();
		
		JspResourceProcessor processor = null;
		if (context instanceof ModularServletContext) {
			processor = this.jspResProcRepo.getJspResourceProcessor((ModularServletContext)context);
		}else {
			String requestURI = request.getRequestURI();
			processor = this.findJspResourceProcessor(requestURI);
		}
		
		if (processor != null) {
			processor.service(request, response);
		}
	}
	
	/**>
	 * 根据<code>requestURI</code>查找匹配的JspResourceProcessor
	 * @param requestURI {@link String},请求资源URI
	 * @return {@link JspResourceProcessor}
	 * @throws ServletException
	 */
	protected JspResourceProcessor findJspResourceProcessor(String requestURI) throws ServletException {
		ClassLoader classLoader = this.webResRepo.findClassLoaderWithResource(requestURI);
		if (classLoader != null) {
			ModularServletContext context = this.modularServletContextRepo.getModularServletContext(classLoader);
			return this.jspResProcRepo.getJspResourceProcessor(context);
		}
		return null;
	}
		
	/**
	 * 执行jsp服务请求
	 * @param request {@link HttpServletRequest}
	 * @param response {@link HttpServletResponse}
	 * @param servletContext {@link ServletContext}
	 */
	protected void doService(HttpServletRequest request, HttpServletResponse response, 
			ModularServletContext servletContext) throws ServletException, IOException{
		JspResourceProcessor processor = this.jspResProcRepo.getJspResourceProcessor(servletContext);
		processor.service(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#getServletInfo()
	 */
	@Override
	public String getServletInfo() {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#destroy()
	 */
	@Override
	public void destroy() {
		if (this.jspResProcRepo instanceof DefaultJspResourceProcessorRepository) {
			((DefaultJspResourceProcessorRepository)this.jspResProcRepo).destroy();
		}
	}

}
