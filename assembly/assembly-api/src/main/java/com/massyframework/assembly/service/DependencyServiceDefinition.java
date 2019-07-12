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
 * @日   期:  2019年1月18日
 */
package com.massyframework.assembly.service;

/**
 * 标记对外部服务的依赖约定
 *
 */
public interface DependencyServiceDefinition<S> {

	/**
	 * 依赖服务被注入后，在装配件上下文中的名称
	 * @return {@link String}, 可能为null.
	 */
	String getCName();
	
	/**
	 * 依赖服务的类型
	 * @return {@link Class}
	 */
	Class<S> getRequiredType();
	
	/**
	 * 服务筛选条件
	 * @return {@link String}
	 */
	String getFilterString();
}
