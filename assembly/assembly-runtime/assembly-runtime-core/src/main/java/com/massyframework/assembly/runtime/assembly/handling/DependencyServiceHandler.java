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
package com.massyframework.assembly.runtime.assembly.handling;

import java.util.Collection;
import java.util.List;

import com.massyframework.assembly.service.DependencyServiceDefinition;
import com.massyframework.assembly.service.ServiceReference;

/**
 * 依赖服务处理器,跟踪服务发布事件，对服务进行依赖匹配
 *
 */
public interface DependencyServiceHandler {
	
	/**
	 * 所有依赖服务定义
	 * @return {@link List}， 依赖服务定义集合
	 */
	List<DependencyServiceDefinition<?>> getDependencyServiceDefinitions();
			
	/**
	 * 获取匹配依赖服务定义的服务引用
	 * @param definition 依赖服务定义
	 * @return {@link ServiceReference},如果依赖服务未被匹配，则可能返回null.
	 */
	<S> ServiceReference<S> findMatchedServiceReference(DependencyServiceDefinition<S> definition);
	
	/**
	 * 所有未匹配的依赖服务定义
	 * @return {@link List}, 依赖服务定义集合
	 */
	Collection<DependencyServiceDefinition<?>> getUnmatchDependencyServiceDefinitions();
	
	/**
	 * 是否所有的依赖都已经匹配
	 * @return {@link boolean},返回<code>true</code>表示全部匹配，否则返回<code>false</code>
	 */
	boolean isAllMatched();
}
