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
 * @日   期:  2019年1月9日
 */
package com.massyframework.assembly.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 服务属性
 */
public final class ServiceProperties {

	private Map<String, Object> properties;
	
	/**
	 * 构造方法
	 */
	public ServiceProperties() {
		this(new HashMap<String, Object>());
	}
	
	/**
	 * 构造方法
	 * @param props {@link Map}
	 */
	public ServiceProperties(Map<String, Object> props) {
		this.properties=Objects.requireNonNull(props, "\"props\" cannot be null.");
	}
	
	/**
	 * 服务名称
	 * @return {@link String}数组
	 */
	public String[] getName() {
		return this.getProperty(ServiceReference.NAME, String[].class);
	}
	
	/**
	 * 设置服务名称
	 * @param name {@link String},服务名称
	 */
	public void setName(String name) {
		if (name == null) return;
		this.setProperty(ServiceReference.NAME, new String[] {name});
	}
	
	/**
	 * 设置服务名称
	 * @param names {@link String}数组
	 */
	public void setName(String[] names) {
		if (names == null) return;
		this.setProperty(ServiceReference.NAME, names);
	}
	
	/**
	 * 注册的服务类型
	 * @return {@link Class}数组
	 */
	public Class<?>[] getObjectClass(){
		return (Class<?>[])this.getProperty(ServiceReference.OBJECT_CLASS);
	}
	
	/**
	 * 设置注册的服务类型
	 * @param clazz {@link Class},服务类型
	 */
	public void setObjectClass(Class<?> clazz) {
		if (clazz == null) return;
		this.setProperty(ServiceReference.OBJECT_CLASS, new Class<?>[] {clazz});
	}
	
	/**
	 * 设置注册的服务类型
	 * @param classes {@link Class}数组,服务类型
	 */
	public void setObjectClass(Class<?>[] classes) {
		if (classes == null) return;
		this.setProperty(ServiceReference.OBJECT_CLASS, classes);
	}
	
	/**
	 * 服务说明
	 * @return {@link String}
	 */
	public String getDescription() {
		return this.getProperty(ServiceReference.DESCRIPTION, String.class);
	}
	
	/**
	 * 设置服务说明
	 * @param description {@link String},说明
	 */
	public void setDescription(String description) {
		this.setProperty(ServiceReference.DESCRIPTION, description);
	}
	
	/**
	 * 服务排名
	 * @return {@link Integer}
	 */
	public Integer getRanking() {
		return this.getProperty(ServiceReference.RANKING, 0, Integer.class);
	}
	
	/**
	 * 设置顶部排名
	 */
	public void setTopRanking() {
		this.setProperty(ServiceReference.RANKING, Integer.MAX_VALUE);
	}

	/**
	 * 获取服务属性
	 * @param name {@link String},属性名
	 * @return {@link Object},属性值，属性不存在可以返回null.
	 */
	public final Object getProperty(String name) {
		return this.properties.get(name);
	}
	
	/**
	 * 设置属性值
	 * @param name 属性名
	 * @param value 属性值
	 */
	public final void setProperty(String name, Object value) {
		this.properties.put(name, value);
	}
	
	/**
	 * 按<code>propType</code>获取属性值
	 * @param name {@link String},属性名
	 * @param propType {@link Class},属性类型
	 * @return {@link T},属性不存在可以返回null.
	 */
	public final <P> P getProperty(String name, Class<P> propType) {
		Object result = this.getProperty(name);
		if (result == null) {
			return null;
		}else {
			return propType.cast(result);
		}
	}
	
	/**
	 * 按<code>propType</code>获取属性值,属性不存在则返回<code>defaultValue</code>
	 * @param name {@link String},属性名
	 * @param defaultValue {@link T}，缺省值
	 * @param propType {@link Class},属性类型
	 * @return {@link T}
	 */
	protected final <P> P getProperty(String name, P defaultValue, Class<P> propType) {
		Object result = this.getProperty(name);
		if (result == null) {
			return defaultValue;
		}else {
			return propType.cast(result);
		}
	}
	
	/**
	 * Map属性
	 * @return {@link Map}
	 */
	public Map<String, Object> getProperties(){
		return this.properties;
	}
}
