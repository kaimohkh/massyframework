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
package com.massyframework.integration.spring.extender.context;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.massyframework.integration.spring.extender.BeanDefiner;


/**
 *
 *
 */
public class SpringXmlWebApplicationContext extends XmlWebApplicationContext {
	
	private final BeanDefiner beanDefiner;
	private Resource[] resources;
	
	/**
	 * 
	 */
	public SpringXmlWebApplicationContext(BeanDefiner beanDefiner) {
		this.beanDefiner = Objects.requireNonNull(beanDefiner, "\"beanDefiner\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.context.support.XmlWebApplicationContext#loadBeanDefinitions(org.springframework.beans.factory.support.DefaultListableBeanFactory)
	 */
	@Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
		this.beanDefiner.praperaBeanFactory(beanFactory);
		super.loadBeanDefinitions(beanFactory);
		this.beanDefiner.postBeanFactory(beanFactory);
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.context.support.XmlWebApplicationContext#initBeanDefinitionReader(org.springframework.beans.factory.xml.XmlBeanDefinitionReader)
	 */
	@Override
	protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
		super.initBeanDefinitionReader(reader);
        reader.setBeanClassLoader(this.getClassLoader());
	}

	protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws IOException {
		Resource[] configLocations = this.getConfigResources();
		reader.loadBeanDefinitions(configLocations);
	}
	
	protected Resource[] getConfigResources() {
		return this.resources;
	}

	/**
	 * 设置配置资源
	 * @param resources {@link Resource}数组
	 */
	public void setConfigResources(Resource[] resources) {
		this.resources = resources;
	}
}
