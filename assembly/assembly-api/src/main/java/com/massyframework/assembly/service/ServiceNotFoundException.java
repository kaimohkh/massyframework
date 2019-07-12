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
 * @日   期:  2019年1月13日
 */
package com.massyframework.assembly.service;

/**
 * 服务未找到时抛出的例外
 *
 */
public final class ServiceNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2172608916497306479L;

	/**
	 * 构造方法
	 */
	public ServiceNotFoundException() {
	}

	/**
	 * 构造方法
	 * @param message
	 */
	public ServiceNotFoundException(String message) {
		super(message);
	}

	/**
	 * 构造方法
	 * @param cause
	 */
	public ServiceNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * 构造方法
	 * @param message
	 * @param cause
	 */
	public ServiceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 构造方法
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ServiceNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}


}
