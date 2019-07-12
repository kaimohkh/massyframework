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
 * @日   期:  2019年1月17日
 */
package com.massyframework.logging;

import com.massyframework.logging.spi.LoggerProvider;

/**
 * 系统控制台日志记录器,将日志信息直接通过控制台输出
 *
 */
final class SystemConsoleLoggerFactory implements LoggerProvider {

	/**
	 * 构造方法
	 */
	public SystemConsoleLoggerFactory() {

	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.LoggerFactory#getLogger(java.lang.String)
	 */
	@Override
	public Logger getLogger(String name) {
		return new SystemConsoleLogger(name);
	}

}
