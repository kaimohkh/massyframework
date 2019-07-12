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
 * Date:   2019年6月8日
 */
package com.massyframework.assembly.launching.servlet;

import java.util.Objects;

import javax.servlet.ServletContext;

import com.massyframework.assembly.Framework;
import com.massyframework.assembly.launching.LaunchContext;
import com.massyframework.assembly.launching.PresetHandler;
import com.massyframework.assembly.service.ServiceProperties;

/**
 * Servlet上下文注册器
 * @author  Huangkaihui
 *
 */
final class ServletContextRegister implements PresetHandler {
	
	private final ServletContext servletContext;

	/**
	 * 
	 */
	public ServletContextRegister(ServletContext servletContext) {
		this.servletContext =
			Objects.requireNonNull(servletContext, "\"servletContext\" cannot be null.");
	}

	@Override
	public void init(LaunchContext context) throws Exception {
		//设置运行在J2EE模式下
		context.setInitParameter(Framework.ENVIRONMENT, Framework.ENVIRONMENT_J2EE);
		
		//注入ServletContext服务
		ServiceProperties props = new ServiceProperties();
		props.setObjectClass(ServletContext.class);
		props.setDescription("Servlet上下文");
		props.setTopRanking();
		
		context.addComponent(
				ServletContext.class.getName(), 
				this.servletContext, 
				props);
	}

	@Override
	public int getOrdered() {
		return -1;
	}

	
}
