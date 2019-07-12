/**
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
 * Author: 黄开晖（117227773@qq.com）
 * Date:   2019年6月22日
 */
package com.massyframework.assembly.launching;

/**
 * 启动期间发生的例外
 * @author  Huangkaihui
 *
 */
public class LaunchException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3840274244248722524L;

	/**
	 * 
	 */
	public LaunchException() {
		
	}

	/**
	 * @param message
	 */
	public LaunchException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public LaunchException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public LaunchException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public LaunchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
