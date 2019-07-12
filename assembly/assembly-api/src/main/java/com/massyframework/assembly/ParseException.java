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
 * @日   期:  2019年1月18日
 */
package com.massyframework.assembly;

/**
 * 解析发生异常时抛出的例外
 */
public class ParseException extends AssemblyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9108624647536555787L;

	/**
	 * 构造方法
	 */
	public ParseException() {
	}

	/**
	 * 构造方法
	 * @param message 消息
	 */
	public ParseException(String message) {
		super(message);
	}

	/**
	 * 构造方法
	 * @param cause 例外
	 */
	public ParseException(Throwable cause) {
		super(cause);
	}

	/**
	 * 构造方法
	 * @param message 消息
	 * @param cause 例外
	 */
	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}
}
