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
 * Date:   2019年6月2日
 */
package com.massyframework.assembly.launching.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import com.massyframework.assembly.Framework;
import com.massyframework.assembly.launching.LaunchContext;
import com.massyframework.assembly.launching.PresetHandler;
import com.massyframework.logging.Logger;

/**
 * 加载默认的配置文件，并将配置参数设置到LaunchContext中
 * @author  Huangkaihui
 *
 */
final class ConfigurationHandler implements PresetHandler {
	
	private static final String FILE = "META-INF/massy/etc/massy.properties";

	/**
	 * 构造方法
	 */
	public ConfigurationHandler() {
	}

	@Override
	public void init(LaunchContext context) throws Exception {
		Logger logger = context.getLogger(); 
		
		try {
			Properties props = this.loadConfigFile();
				
			Enumeration<Object> em = props.keys();
			while (em.hasMoreElements()) {
				String name = em.nextElement().toString();
				String value = props.getProperty(name);
				if (!context.setInitParameter(name, value)) {
					if (logger.isWarnEnabled()) {
						logger.warn("set init parameter failed: name=" + 
								name + ", value=" + value + ".");
					}
				}else {
					if (logger.isTraceEnabled()) {
						logger.trace("set init parameter success: name=" +
								name + ", value=" + value + ".");
					}
				}
			}
			
			String userId = context.getInitParameter(Framework.USER_ID);
			if (userId == null) {
				context.setInitParameter(Framework.USER_ID, "0");
			}
			String userName = context.getInitParameter(Framework.USER_NAME);
			if (userName== null) {
				context.setInitParameter(Framework.USER_NAME, System.getProperty("user.name"));
			}
		}catch(IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("load config file with massyframework is failed.", e);
			}
		}
	}
	
	@Override
	public int getOrdered() {
		return -1;
	}

	/**
	 * 加载配置文件
	 * @return {@link Properties}
	 * @throws IOException, 文件读取发生的例外
	 */
	protected Properties loadConfigFile() throws IOException{
		Properties result = new Properties();
		ClassLoader classLoader = this.getClass().getClassLoader();
		URL url = classLoader.getResource(FILE);
		if (url != null) {
			InputStream input = null;
			try {
				input = url.openStream();
				result.load(input);
			}finally {
				if (input != null) {
					try {
						input.close();
					}catch(IOException e) {
						
					}
				}
			}
		}
		
		return result;
	}
	
	
}
