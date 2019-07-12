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
 * @日   期:  2019年1月29日
 */
package com.massyframework.assembly;

import java.util.List;
import java.util.Objects;

import com.massyframework.assembly.service.DependencyServiceDefinition;
import com.massyframework.assembly.service.ServiceReference;

/**
 * 依赖管理
 */
public interface DependencyManager {

	/**
	 * 依赖服务定义列表
	 * @return {@link List},依赖服务定义列表
	 */
	List<DependencyServiceDefinition<?>> getDependencyServices();
	
	/**
	 * 根据<code>definition</code>查找匹配的服务引用
	 * @param definition {@link DependencyServiceDefinition},依赖服务定义
	 * @return
	 */
	<S> ServiceReference<S> getMatchedServiceReference(DependencyServiceDefinition<S> definition);
		
	/**
	 * 未匹配成功的依赖服务定义列表
	 * @return {@link List},依赖服务定义列表
	 */
	List<DependencyServiceDefinition<?>> getUnmatchDependencyServices();
	
	
	/**
	 * 所有依赖是否都匹配成功
	 * @return {@link boolean},返回<code>true</code>表示都匹配成功,返回<code>false</code>表示未全部匹配成功
	 */
	boolean allMatched();
	
	/**
	 * 从<code>assembly</code>中取回依赖管理
	 * @param assembly {@link Assembly},装配件
	 * @return {@link DependencyManager}
	 */
	static DependencyManager retrieveFrom(Assembly assembly) {
		Objects.requireNonNull(assembly, "\"assembly\" cannot be null.");
		return assembly.getAdapter(DependencyManager.class);
	}
}
