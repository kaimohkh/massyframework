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
 * @日   期:  2019年1月6日
 */
package com.massyframework.assembly.container;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 存放组件的容器，组件通过名称进行区分
 *
 */
public interface Container {
	
	/**
	 * 判断<code>cName</code>名称的组件是否存在
	 * @param cName {@link String},组件名称
	 * @return {@link boolean},返回<code>true</code>表示存在，否则返回<code>false</code>
	 */
	boolean containsComponent(String cName);
	
	/**
	 * 按<Code>cName</code>获取组件实例
	 * @param cName 组件在容器中的名称
	 * @return {@link Object}
	 * @throws ComponentsException 发生异常时抛出的例外
	 */
	Object getComponent(String cName) throws ComponentsException;
	
	/**
	 * 按<Code>componentType</code>获取组件实例<br>
	 * 本方法实际调用{@link #getComponent(componentType.getName, componentType)}
	 * @param componentType 组件类型
	 * @return {@link Object}
	 * @throws ComponentsException 发生异常时抛出的例外
	 */
	default <T> T getComponent(Class<T> componentType) throws ComponentsException{
		return this.getComponent(componentType.getName(), componentType);
	}
	
	/**
	 * 按<Code>cName</code>获取组件实例,并按<code>componentType</code>类型返回
	 * @param cName {@link String},组件在容器中的名称
	 * @param componentType {@link Class},组件的类型
	 * @return {@link T}
	 * @throws ComponentsException 发生异常时抛出的例外
	 */
	default <T> T getComponent(String cName, Class<T> componentType) throws ComponentsException{
		Object result = this.getComponent(cName);
		return componentType.cast(result);
	}
		
	/**
	 * 获取所有派生于<code>componentType</code>的实例<br>
	 * @param componentType 要求组件实现的类型
	 * @return {@link Map}
	 */
	<T> Map<String, T> getComponentsOfType(Class<T> componentType);
	
	/**
	 * 获取所有在类上注解<code>annoType</code>的组件实例
	 * @param annoType 注解，仅支持注解在类型上的注解。
	 * @return {@link Map}
	 */
	Map<String, Object> getComponentsWithAnnotation(Class<? extends Annotation> annoType);
}
