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
package com.massyframework.assembly.web.servlet;

import java.util.Objects;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * HttpServletRequest请求
 */
public class MassyHttpServletRequest extends HttpServletRequestWrapper {
	
	private final ServletContext servletContext;

	/**
	 * 构造方法
	 * @param request
	 */
	public MassyHttpServletRequest(HttpServletRequest request, ServletContext servletContext) {
		super(request);
		this.servletContext = 
				Objects.requireNonNull(servletContext, "\"servletContext\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequestWrapper#getServletContext()
	 */
	@Override
	public ServletContext getServletContext() {
		return this.servletContext;
	}

}
