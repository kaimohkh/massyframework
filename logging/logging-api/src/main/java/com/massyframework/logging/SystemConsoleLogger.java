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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;


/**
 * 采用系统控制台作为日志输出
 *
 */
final class SystemConsoleLogger implements Logger {

	private final String name;
	
	private static final String TRACE = "[TRACE]";
	private static final String DEBUG = "[DEBUG]";
	private static final String INFO  = "[INFO]";
	private static final String WARN  = "[WRAN]";
	private static final String ERROR = "[ERROR]";
	private static final String SPACE = " ";
	
	/**
	 * 构造方法
	 */
	public SystemConsoleLogger(String name) {
		this.name = Objects.requireNonNull(name, "\"name\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#isTraceEnabled()
	 */
	@Override
	public boolean isTraceEnabled() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#isDebugEnabled()
	 */
	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#isInfoEnabled()
	 */
	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#isWarnEnabled()
	 */
	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#isErrorEnabled()
	 */
	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#trace(java.lang.String)
	 */
	@Override
	public void trace(String message) {
		String information = this.joint(TRACE, message);
		System.out.println(information);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#trace(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void trace(String message, Throwable cause) {
		
		String information = cause == null ?
			this.joint(TRACE, message) :
				this.joint(TRACE, message);
		System.out.println(information);	
	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#debug(java.lang.String)
	 */
	@Override
	public void debug(String message) {
		String information = this.joint(DEBUG, message);
		System.out.println(information);

	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#debug(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void debug(String message, Throwable cause) {
		String information = cause == null ?
			this.joint(DEBUG, message) :
				this.joint(DEBUG, message, cause);
		System.out.println(information);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#info(java.lang.String)
	 */
	@Override
	public void info(String message) {
		String information = this.joint(INFO, message);
		System.out.println(information);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#info(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void info(String message, Throwable cause) {
		
		String information = cause == null ?
			this.joint(INFO, message) :
				this.joint(INFO, message, cause);
		System.out.println(information);

	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#warn(java.lang.String)
	 */
	@Override
	public void warn(String message) {
		String information = this.joint(WARN, message);
		System.out.println(information);

	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#warn(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void warn(String message, Throwable cause) {
		String information = cause == null ?
			this.joint(WARN, message) :
				this.joint(WARN, message, cause);
		System.out.println(information);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#error(java.lang.String)
	 */
	@Override
	public void error(String message) {
		String information = this.joint(ERROR, message);
		System.err.println(information);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.Logger#error(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void error(String message, Throwable cause) {
		String information = cause == null ?
			this.joint(ERROR, message) :
				this.joint(ERROR, message, cause);
		System.err.println(information);
	}

	/**
	 * 拼装消息
	 * @param level 日志级别
	 * @param message 消息
	 * @return {@link String}
	 */
	private String joint(String level, String message) {
		return level.concat(SPACE).concat(this.name).concat(SPACE).concat(message);
	}
	
	/**
	 * 拼装消息
	 * @param level 日志级别
	 * @param message 消息
	 * @return {@link String}
	 */
	private String joint(String level, String message, Throwable cause) {
		StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            
            sw.append(level).append(SPACE)
            	.append(this.name).append(SPACE)
            	.append(message).append("\r\n");
            cause.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SystemConsoleLogger [name=" + name + "]";
	}	
}
