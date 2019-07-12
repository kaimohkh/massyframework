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
package com.massyframework.integration.spring.extender;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.context.ConfigurableApplicationContext;

import com.massyframework.assembly.container.ComponentsException;
import com.massyframework.assembly.container.Container;

/**
 * 提供对Spring ApplicationContext的容器封装
 */
public final class SpringContainer implements Container {

	private final ConfigurableApplicationContext applicationContext;
	
	/**
	 * 
	 */
	public SpringContainer(ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.container.Container#getComponent(java.lang.String)
	 */
	@Override
	public Object getComponent(String cName) throws ComponentsException {
		try {
			return this.getApplicationContext().getBean(cName);
		}catch(Exception e) {
			throw new ComponentsException(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.Container#getComponentsOfType(java.lang.Class)
	 */
	@Override
	public <T> Map<String, T> getComponentsOfType(Class<T> componentType) {
		return this.getApplicationContext().getBeansOfType(componentType);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.Container#getComponentsWithAnnotation(java.lang.Class)
	 */
	@Override
	public Map<String, Object> getComponentsWithAnnotation(Class<? extends Annotation> annoType) {
		return this.getApplicationContext().getBeansWithAnnotation(annoType);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.ComponentProvider#containsComponent(java.lang.String)
	 */
	@Override
	public boolean containsComponent(String cName) {
		return this.getApplicationContext().containsBean(cName);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.ComponentProvider#getComponent(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> T getComponent(String cName, Class<T> componentType) throws ComponentsException {
		try {
			return this.getApplicationContext().getBean(cName, componentType);
		}catch(Exception e) {
			throw new ComponentsException(e.getMessage(), e);
		}
	}

	public ConfigurableApplicationContext getApplicationContext() {
		return this.applicationContext;
	}
}
