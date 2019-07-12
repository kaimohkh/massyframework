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
 * @日   期:  2019年1月19日
 */
package com.massyframework.assembly.support;

import com.massyframework.logging.Logger;
import com.massyframework.logging.LoggerAware;

/**
 * 支持日志记录能力
 *
 */
public class LogSupporter implements LoggerAware{

	private volatile Logger logger;
	
	/**
	 * 构造方法 
	 */
	public LogSupporter() {

	}
	
	/**
     * 记录调试消息
     * @param message 消息
     */
    public void logDebug(String message) {
    	Logger logger = this.getLogger();
    	if (logger.isDebugEnabled()) {
    		logger.debug(message);
    	}
    }

    /**
     * 记录调试消息
     * @param message 消息
     * @param cause 例外
     */
    public void logDebug(String message, Throwable cause) {
    	Logger logger = this.getLogger();
    	if (logger.isDebugEnabled()) {
    		logger.debug(message, cause);
    	}
    }
    
    /**
     * 记录错误消息
     * @param message 消息
     */
    public void logError(String message) {
    	Logger logger = this.getLogger();
    	if (logger.isErrorEnabled()) {
    		logger.error(message);
    	}
    }

    /**
     * 记录错误消息
     * @param message 消息
     * @param cause 例外
     */
    public void logError(String message, Throwable cause) {
    	Logger logger = this.getLogger();
    	if (logger.isErrorEnabled()) {
    		logger.error(message, cause);
    	}
    }
	
    /**
     * 记录消息
     * @param message 消息
     */
    public void logInfo(String message) {
    	Logger logger = this.getLogger();
    	if (logger.isInfoEnabled()) {
    		logger.info(message);
    	}
    }

    /**
     * 记录消息
     * @param message 消息
     * @param cause 例外
     */
    public void logInfo(String message, Throwable cause) {
    	Logger logger = this.getLogger();
    	if (logger.isInfoEnabled()) {
    		logger.info(message, cause);
    	}
    }
    
    /**
     * 记录跟踪消息
     * @param message 消息
     */
    public void logTrace(String message) {
    	Logger logger = this.getLogger();
    	if (logger.isTraceEnabled()) {
    		logger.trace(message);
    	}
    }

    /**
     * 记录跟踪消息
     * @param message 消息
     * @param cause 例外
     */
    public void logTrace(String message, Throwable cause) {
    	Logger logger = this.getLogger();
    	if (logger.isTraceEnabled()) {
    		logger.trace(message, cause);
    	}
    }

    /**
     * 记录警告消息
     * @param message 消息
     */
    public void logWarn(String message) {
    	Logger logger = this.getLogger();
    	if (logger.isWarnEnabled()) {
    		logger.warn(message);
    	}
    }

    /**
     * 记录警告消息
     * @param message 消息
     * @param cause 例外
     */
    public void logWarn(String message, Throwable cause) {
    	Logger logger = this.getLogger();
    	if (logger.isWarnEnabled()) {
    		logger.warn(message, cause);
    	}
    }
    
    
    /* (non-Javadoc)
	 * @see com.massyframework.logging.LoggerAware#setLogger(com.massyframework.logging.Logger)
	 */
	@Override
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
     * 日志记录器
     * @return {@link Logger}
     */
    public Logger getLogger() {
    	return this.logger;
    }

}
