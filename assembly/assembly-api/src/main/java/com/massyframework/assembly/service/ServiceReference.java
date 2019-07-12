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
 * @日   期:  2019年1月13日
 */
package com.massyframework.assembly.service;

import java.util.List;

/**
 * 服务引用提供服务附加属性
 *
 */
public interface ServiceReference<S> {
	
	/**
	 * 装配件符号名称，其值为字符串
	 */
	static final String ASSEMBLY_SYMBOLICNAME = "service.assembly.symbolicName";

	/**
	 * 服务类型，其值为Class数组<br>
	 * 服务注册的必须项目
	 */
	static final String OBJECT_CLASS = "service.objectClass";

	/**
	 * 服务在装配件上下文中的名称， 其值为字符串<br>
	 * 该名称在装配件上下文中必须唯一，不能重复
	 */
	static final String CNAME = "service.cName";

	/**
	 * 服务说明，其值为字符串
	 */
	static final String DESCRIPTION = "service.description";

	/**
	 * 服务编号，由系统统一生成，值为long
	 */
	static final String ID = "service.id";

	/**
	 * 服务输出的名称，允许输出多个名称，值为字符串数组<br>
	 */
	static final String NAME = "service.name";

	/**
	 * 服务排名,其值为int。<br>
	 * 默认该值为0
	 */
	static final String RANKING = "service.ranking";
	
	/**
	 * 服务排名，最大值
	 */
	static final String RANKING_MAXVALUE = "2147483647";

	/**
	 * 服务的编号，由运行框架生成<br>
	 * 存放在服务属性中，键为{@link Service#ID}
	 * @return {@link long}
	 */
	default long getServiceId(){
		return this.getProperty(ID, Long.class).longValue();
	}
	
	/**
	 * 获取对象注册的类型
	 * @return {@link Class}数组
	 */
	default Class<?>[] getObjectClasses(){
		return (Class<?>[])this.getProperty(OBJECT_CLASS);
	}
	
	/**
	 * 获取服务属性
	 * @param propName 属性的键
	 * @return {@link Object}, 属性不存在返回null.
	 */
	Object getProperty(String propName);
	
	/**
	 * 获取服务属性
	 * @param propName 属性的键
	 * @param propType 属性的类型
	 * @return {@link P},属性不存在返回null.
	 */
	<P> P getProperty(String propName, Class<P> propType);
	
	/**
	 * 获取所有服务属性的键值
	 * @return {@link List}
	 */
	List<String> getPropertyNames();
}
