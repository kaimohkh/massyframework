/*
 * Copyright 2018 Confluent Inc.
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
package com.massyframework.assembly.runtime.service;

import com.massyframework.assembly.service.DependencyServiceDefinition;

/**
 * 依赖服务定义工具类，提供创建{@link DependencyServiceDefinition}的方法
 *
 */
public final class DependencyServiceDefinitionUtils {

	/**
	 * 创建依赖服务定义
	 * @param requiredType 要求的服务类型
	 * @param cName 依赖服务在容器中的名称
	 * @param filterString 依赖服务的过滤条件
	 * @return {@link DependencyServiceDefinition}
	 */
	public static <S> DependencyServiceDefinition<S> createDependencyServcieDefinition(
			Class<S> requiredType, String cName, String filterString){
		return new DefaultDependencyServiceDefinition<S>(requiredType, cName, filterString);
	}
}
