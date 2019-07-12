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
 * @日   期:  2019年1月24日
 */
package com.massyframework.assembly.spi;

import java.util.Map;

import com.massyframework.assembly.service.DependencyServiceDefinition;

/**
 * 依赖服务提供者
 */
public interface DependencyServiceProvider {

	/**
	 * 提供所有的依赖服务<br>
	 * 本方法仅在{@link LifecycleManager#getAssemblyStatus()} == {@link AssemblyStatus#READY}后才返回有效值。
	 * @return {@link Map},键为{@link DependencyServiceDefinition},值为服务实例
	 */
	Map<DependencyServiceDefinition<?>, Object> getDependencyServices();
}
