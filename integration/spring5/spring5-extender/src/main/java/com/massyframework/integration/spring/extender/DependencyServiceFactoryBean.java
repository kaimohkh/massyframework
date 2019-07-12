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
package com.massyframework.integration.spring.extender;

import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * 依赖服务工厂Bean
 */
public class DependencyServiceFactoryBean<T> extends AbstractFactoryBean<T> {

	private Class<T> serviceType;
	private T service;

	public DependencyServiceFactoryBean(){
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.AbstractFactoryBean#createInstance()
	 */
	@Override
	protected T createInstance() throws Exception {
		return this.service;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.AbstractFactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return this.serviceType;
	}
	
	/**
	 * 设置对象类型
	 * @param serviceType
	 */
	public void setObjectType(Class<T> serviceType){
		this.serviceType = serviceType;
	}
	
	/**
	 * 设置服务实例
	 * @param service 服务实例
	 */
	public void setObject(T service){
		this.service = service;
	}
}
