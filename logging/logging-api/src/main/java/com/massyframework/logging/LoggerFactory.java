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

import java.util.Iterator;
import java.util.ServiceLoader;

import com.massyframework.logging.spi.LoggerProvider;

/**
 * 日志记录器工厂
 */
public final class LoggerFactory {
	
	private static LoggerProvider INSTANCE;
	static {
		try {
			ServiceLoader<LoggerProvider> sl = 
				ServiceLoader.load(LoggerProvider.class);
			Iterator<LoggerProvider> it = sl.iterator();
			if (it.hasNext()) {
				INSTANCE = it.next();
			}else {
				INSTANCE = new SystemConsoleLoggerFactory();
			}
		}catch(Exception e) {
			e.printStackTrace();
			INSTANCE = new SystemConsoleLoggerFactory();
		}
	}
	
	/**
	 * 获取日志记录器
	 * @param name {@link String},日志名称
	 * @return {@link Logger}
	 */
	public static Logger getLogger(String name) {
		return INSTANCE.getLogger(name);
	}
	
	public static Logger getLogger(Class<?> clazz) {
		return INSTANCE.getLogger(clazz.getName());
	}

}
