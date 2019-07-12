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
 * @日   期:  2019年1月24日
 */
package com.massyframework.integration.spring.extender.standlone;

import org.springframework.core.io.Resource;
import org.springframework.web.context.ContextLoader;

import com.massyframework.assembly.ParameterNotFoundException;
import com.massyframework.integration.spring.extender.ConfigLocationsUtils;
import com.massyframework.integration.spring.extender.SpringContainer;
import com.massyframework.integration.spring.extender.context.SpringXmlApplicationContext;


/**
 *
 *
 */
public class SpringXmlHandler extends SpringHandler {

	/**
	 * 
	 */
	public SpringXmlHandler() {
	}

	
	/**
	 * 创建容器
	 * @param initParams 初始化参数
	 * @return {@link SpringContainer}， spring容器
	 * @throws ParameterNotFoundException 参数不存在时抛出的例外
	 */
	protected SpringContainer createContainer() throws Exception{
		Resource[] configResources = 
			ConfigLocationsUtils.parseConfigLocationsToArray(this.getAssembly());	
		if (configResources.length == 0l) {
			throw new ParameterNotFoundException("container." + ContextLoader.CONFIG_LOCATION_PARAM);
		}
				
		SpringXmlApplicationContext applicationContext = new SpringXmlApplicationContext(this);
		applicationContext.setClassLoader(this.getAssembly().getAssemblyClassLoader());
		
		applicationContext.setConfigResources(configResources);
		SpringContainer result = new SpringContainer(applicationContext);
		return result;
	}
}
