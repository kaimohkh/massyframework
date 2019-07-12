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
package com.massyframework.assembly.web;

import com.massyframework.assembly.Assembly;

/**
 * 模块化Servlet上下文仓储
 */
public interface ModularServletContextRepository {
	
	/**
	 * 根据<code>assembly</code>判断是否存有对应的{@link ModularServletContext}
	 * @param assembly {@link Assembly}
	 * @return {@link boolean},返回<code>true</code>表示存有，否则返回<code>false</code>
	 */
	boolean containsModularServletContext(Assembly assembly);
	
	/**
	 * 根据<code>classLoader</code>判断是否存有对应的{@link ModularServletContext}
	 * @param classLoader {@link ClassLoader},类加载器
	 * @return {@link boolean},返回<code>true</code>表示存有，否则返回<code>false</code>
	 */
	boolean containsModularServletContext(ClassLoader classLoader);
	
	/**
	 * 按<code>assembly</code>获取{@link ModularServletContext}
	 * <br>如果不存在，则创建一个新的{@link ModularServletContext}
	 * @param assembly {@link assembly},装配件
	 * @return {@link ModularServletContext}
	 */
	ModularServletContext getModularServletContext(Assembly assembly);

	/**
	 * 按<code>classLoader</code>获取{@link ModularServletContext}
	 * <br>如果不存在，则创建一个新的{@link ModularServletContext}
	 * @param assembly {@link assembly},装配件
	 * @return {@link ModularServletContext}
	 */
	ModularServletContext getModularServletContext(ClassLoader classLoader);
}
