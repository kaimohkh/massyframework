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
 * @日   期:  2019年1月15日
 */
package com.massyframework.assembly;

/**
 * 装配件基础例外
 *
 */
public class AssemblyException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7501996718078882565L;

	/**
	 * 
	 */
	public AssemblyException() {
	}

	/**
	 * @param message
	 */
	public AssemblyException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public AssemblyException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AssemblyException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public AssemblyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
