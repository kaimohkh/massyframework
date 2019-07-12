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
package com.massyframework.assembly.web;

import java.util.Set;

import javax.servlet.ServletContext;

import com.massyframework.assembly.Assembly;

/**
 * 基于独立类加载器封装的模块化Servlet上下文
 * <br>在这个ServletContext中，以下设置方法中属性名没有{@link #ROOT_PREFIX}前缀的，
 * 将被独立存储，有前缀的将去掉前缀，存入J2EE容器提供的ServletContext中。
 * <ul>
 * <li>{@link #setInitParameter(String, String)}</li>
 * <li>{@link #setAttribute(String, String)}</li>
 * </ul>
 * 同理，以下读取方法中，如果属性名有{@link #ROOT_PREFIX}的，直接从J2EE容器提供的ServletContext读取，否则，
 * 先从独立存储中读取，如果属性值为null,再从J2EE容器提供的ServletContext中读取：
 * <ul>
 * <li>{@link #getInitParameter(String)}</li>
 * <li>{@link #getAttribute(String)}</li>
 * </ul>
 */
public interface ModularServletContext extends ServletContext {
	
	static final String ROOT_PREFIX = "$.";

	/**
	 * 用同一类加载器加载的装配件
	 * @return {@link Set}, 装配件列表
	 */
	Set<Assembly> getAssociatedAssemblies();
}
