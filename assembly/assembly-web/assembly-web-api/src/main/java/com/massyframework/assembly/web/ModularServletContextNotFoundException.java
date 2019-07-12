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

import com.massyframework.assembly.AssemblyException;

/**
 * 模块化Servlet上下文没有找到时抛出例外
 *
 */
public class ModularServletContextNotFoundException extends AssemblyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4872671206648277771L;
	
	/**
	 * 构造方法
	 */
	public ModularServletContextNotFoundException() {

	}

	/**
	 * 构造方法
	 * @param message {@link string},消息
	 */
	public ModularServletContextNotFoundException(String message) {
		super(message);
	}

	/**
	 * 构造方法
	 * @param cause {@link Throwable},例外
	 */
	public ModularServletContextNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * 构造方法
	 * @param message {@link String},消息
	 * @param cause {@link Throwable},例外
	 */
	public ModularServletContextNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
