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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * Bean定义者
 */
public interface BeanDefiner {

	/**
	 * 加载Bean定义的预处理
	 * @param beanFactory bean工厂
	 * @throws BeansException 加载期间发生的例外
	 */
	void praperaBeanFactory(DefaultListableBeanFactory beanFactory) throws BeansException;
	
	/**
	 * 加载Bean定义的后置处理
	 * @param beanFactory bean工厂
	 * @throws BeansException 加载期间发生的例外
	 */
	void postBeanFactory(DefaultListableBeanFactory beanFactory) throws BeansException;
}
