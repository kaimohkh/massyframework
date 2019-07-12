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
 * @日   期:  2019年1月7日
 */
package com.massyframework.logging.slf4j;

import java.util.Objects;

import org.slf4j.LoggerFactory;

import com.massyframework.logging.Logger;

/**
 * 对Slf4j日志记录器的封装
 */
final class LoggerWrapper implements Logger {

	private final org.slf4j.Logger logger;

	/**
	 * 构造方法
	 * @param assembly 装配件
	 */
	public LoggerWrapper(String name) {
		Objects.requireNonNull(name, "\"name\" cannot be null.");
		this.logger =
				LoggerFactory.getLogger(name);		
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#isTraceEnabled()
	 */
	@Override
	public boolean isTraceEnabled() {
		return this.logger.isTraceEnabled();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#isDebugEnabled()
	 */
	@Override
	public boolean isDebugEnabled() {
		return this.logger.isDebugEnabled();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#isInfoEnabled()
	 */
	@Override
	public boolean isInfoEnabled() {
		return this.logger.isInfoEnabled();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#isWarnEnabled()
	 */
	@Override
	public boolean isWarnEnabled() {
		return this.logger.isWarnEnabled();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#isErrorEnabled()
	 */
	@Override
	public boolean isErrorEnabled() {
		return this.logger.isErrorEnabled();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#trace(java.lang.String)
	 */
	@Override
	public void trace(String message) {
		this.logger.trace(message);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#trace(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void trace(String message, Throwable cause) {
		this.logger.trace(message, cause);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#debug(java.lang.String)
	 */
	@Override
	public void debug(String message) {
		this.logger.debug(message);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#debug(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void debug(String message, Throwable cause) {
		this.logger.debug(message, cause);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#info(java.lang.String)
	 */
	@Override
	public void info(String message) {
		this.logger.info(message);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#info(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void info(String message, Throwable cause) {
		this.logger.info(message, cause);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#warn(java.lang.String)
	 */
	@Override
	public void warn(String message) {
		this.logger.warn(message);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#warn(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void warn(String message, Throwable cause) {
		this.logger.warn(message, cause);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#error(java.lang.String)
	 */
	@Override
	public void error(String message) {
		this.logger.error(message);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyLogger#error(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void error(String message, Throwable cause) {
		this.logger.error(message, cause);
	}

	
}
