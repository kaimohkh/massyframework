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
 * @日   期:  2019年2月23日
 */
package com.massyframework.assembly.web.runtime.pagemapping;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.massyframework.assembly.web.pagemapping.PageMappingEntry;
import com.massyframework.assembly.web.pagemapping.PageMappingRepository;

/**
 * 页面映射代理Servlet
 */
public final class PageMappingProxyServlet implements Servlet {
	
	public static final String COMMONS_URLPATTERN = "web.commons.urlpattern";
	public static final String DEFAULT_URLPATTREN = "/commons/*";
	
	private final PageMappingRepository repository;
	private ServletConfig config;

	/**
	 * 构造方法
	 * @param repository {@link PageMappingRepository}
	 */
	public PageMappingProxyServlet(PageMappingRepository repository) {
		this.repository = Objects.requireNonNull(
				repository, "\"repository\" cannot be null.");
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
		String requestURI = request.getRequestURI();
		PageMappingEntry entry = this.repository.findPageMappingEntry(requestURI);
		if (entry != null) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(entry.getPagePath());
			if (entry.isForward()) {
				dispatcher.forward(req, res);
			}else {
				dispatcher.include(req, res);
			}
		}else {
			HttpServletResponse response = (HttpServletResponse)res;
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
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
		
	}

}
