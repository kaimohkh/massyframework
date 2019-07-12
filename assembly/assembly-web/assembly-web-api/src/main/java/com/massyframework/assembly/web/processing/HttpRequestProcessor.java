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
package com.massyframework.assembly.web.processing;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Http请求处理器
 */
public interface HttpRequestProcessor {
	
	/**
	 * 初始化
	 * @param context {@link ServletContext}
	 * @throws ServletException {@link ServletException},发生非预期的例外
	 */
	void init(ServletContext context) throws ServletException;
	
	/**
	 * 判断<code>request</code>是否可支持处理
	 * @param request {@link HttpServletRequest},http请求
	 * @return {@link boolean},返回<code>true</code>表示支持
	 */
	boolean isSupport(HttpServletRequest request);
	
	/**
	 * 请求处理
	 * @param request {@link HttpServletRequest}, Http请求
	 * @param response {@link HttpServletResponse}, Http响应
	 * @param servletContext {@link ServletContext}, Servlet上下文
	 * @return {@link boolean},返回<code>true</code>表示已经执行了http处理，否则返回<code>false</code>
	 * @throws ServletException 处理请求发生例外
	 * @throws IOException 发生读写的例外
	 */
	void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
	
	/**
	 * 释放资源
	 */
	void destroy();
}
