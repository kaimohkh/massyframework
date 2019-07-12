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
package com.massyframework.integration.spring.extender.factory;

import com.massyframework.assembly.AssemblyConfig;
import com.massyframework.assembly.spi.ContainerTypeNotSupportException;
import com.massyframework.assembly.spi.Handler;
import com.massyframework.assembly.spi.HandlerFactory;
import com.massyframework.assembly.util.ContainerUtils;
import com.massyframework.integration.spring.extender.listener.SpringListenerHandler;
import com.massyframework.integration.spring.extender.servlet.SpringServletHandler;
import com.massyframework.integration.spring.extender.standlone.SpringXmlHandler;


/**
 * Spring5 处理器工厂
 *
 */
public class SpringContainerHandlerFactory implements HandlerFactory {

	/**
	 * 容器处理器工厂
	 */
	public SpringContainerHandlerFactory() {
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.HandlerFactory#createHandler(com.massyframework.assembly.AssemblyConfig)
	 */
	@Override
	public Handler createHandler(AssemblyConfig config) throws ContainerTypeNotSupportException{
		//Spring容器仅支持：
		//1、独立的xml文件
		//2、Web容器内嵌组件，包括： ContextLoaderListener和DispatcherServlet
		String containerType = ContainerUtils.getContainerType(config);
		if (ContainerUtils.hasStandalone(containerType)) {
			return new SpringXmlHandler();
		}else {
			if (ContainerUtils.hasServletContextListener(containerType)) {
				return new SpringListenerHandler();
			}
			
			if (ContainerUtils.hasServlet(containerType)) {
				return new SpringServletHandler();
			}
		}
		
		//不支持Filter
		throw new ContainerTypeNotSupportException(config.getInitParameter(containerType));
	}

}
