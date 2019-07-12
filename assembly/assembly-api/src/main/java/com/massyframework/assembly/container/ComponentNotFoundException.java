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
 * @日   期:  2019年1月6日
 */
package com.massyframework.assembly.container;

/**
 * 未找到组件时抛出的例外
 *
 */
public class ComponentNotFoundException extends ComponentsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6637045992847749756L;

	/**
	 * 构造方法
	 */
	public ComponentNotFoundException() {
	}

	/**
	 * 构造方法
	 * @param message 信息
	 */
	public ComponentNotFoundException(String message) {
		super(message);
	}

	/**
	 * 构造方法
	 * @param cause 例外
	 */
	public ComponentNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * 构造方法
	 * @param message 信息
	 * @param cause 例外
	 */
	public ComponentNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
