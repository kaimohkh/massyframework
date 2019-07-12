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
package com.massyframework.assembly.web.processing;

import javax.servlet.ServletException;

import com.massyframework.assembly.web.ModularServletContext;

/**
 * Jsp资源处理仓储,提供按{@link ModularServletContext}归类管理对应的Jsp资源处理器
 */
public interface JspResourceProcessorRepository {
	
	/**
	 * 根据<code>ServletContext</code>获取对应的JspResourceProcessor
	 * @param servletContext {@link ModularServletContext}
	 * @return {@link JspResourceProcessor}
	 * @throws ServletException 实例化JspResourceProcessor时发生的例外
	 */
	JspResourceProcessor getJspResourceProcessor(ModularServletContext servletContext) throws ServletException;

}
