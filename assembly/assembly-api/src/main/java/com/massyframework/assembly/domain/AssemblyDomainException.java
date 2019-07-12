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
 * @日   期:  2019年3月31日
 */
package com.massyframework.assembly.domain;

/**
 * 装配域例外的基类
 *
 */
public abstract class AssemblyDomainException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1456960863931651763L;

	/**
	 * 构造方法
	 */
	public AssemblyDomainException() {
		
	}

	/**
	 * 构造方法
	 * @param message 消息
	 */
	public AssemblyDomainException(String message) {
		super(message);
	}

	/**
	 * 构造方法
	 * @param cause 例外
	 */
	public AssemblyDomainException(Throwable cause) {
		super(cause);
	}

	/**
	 * 构造方法
	 * @param message 消息
	 * @param cause 例外
	 */
	public AssemblyDomainException(String message, Throwable cause) {
		super(message, cause);
	}

}
