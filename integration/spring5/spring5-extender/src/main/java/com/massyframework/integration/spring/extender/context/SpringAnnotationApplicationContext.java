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

import java.util.Objects;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.massyframework.integration.spring.extender.BeanDefiner;


/**
 * @author huangkaihui
 *
 */
public class SpringAnnotationApplicationContext extends AnnotationConfigApplicationContext {

	private final BeanDefiner beanDefiner;
	
	/**
	 * 
	 */
	public SpringAnnotationApplicationContext(BeanDefiner beanDefiner) {
		this.beanDefiner = Objects.requireNonNull(beanDefiner, "\"beanDefiner\" cannot be null.");
	}

	/**
	 * @param beanFactory
	 */
	public SpringAnnotationApplicationContext(DefaultListableBeanFactory beanFactory,  BeanDefiner beanDefiner) {
		super(beanFactory);
		this.beanDefiner = Objects.requireNonNull(beanDefiner, "\"beanDefiner\" cannot be null.");
	}

	/**
	 * @param annotatedClasses
	 */
	public SpringAnnotationApplicationContext(BeanDefiner beanDefiner, Class<?>... annotatedClasses) {
		super(annotatedClasses);
		this.beanDefiner = Objects.requireNonNull(beanDefiner, "\"beanDefiner\" cannot be null.");
	}

	/**
	 * @param basePackages
	 */
	public SpringAnnotationApplicationContext(BeanDefiner beanDefiner, String... basePackages) {
		super(basePackages);
		this.beanDefiner = Objects.requireNonNull(beanDefiner, "\"beanDefiner\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.support.AbstractApplicationContext#prepareBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
	 */
	@Override
	protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		super.prepareBeanFactory(beanFactory);
		
		if (beanFactory instanceof DefaultListableBeanFactory){
			this.beanDefiner.praperaBeanFactory(
					(DefaultListableBeanFactory)beanFactory);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.support.AbstractApplicationContext#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
	 */
	@Override
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		super.postProcessBeanFactory(beanFactory);
		if (beanFactory instanceof DefaultListableBeanFactory){
			this.beanDefiner.postBeanFactory(
					(DefaultListableBeanFactory)beanFactory);
		}
	}
	
}
