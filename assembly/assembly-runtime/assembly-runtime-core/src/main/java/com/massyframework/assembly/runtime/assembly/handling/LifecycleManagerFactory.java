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

import com.massyframework.assembly.AssemblyConfig;
import com.massyframework.assembly.runtime.assembly.AssemblyBase;
import com.massyframework.assembly.spi.LifecycleManager;

/**
 * 装配件生命周期工厂
 */
public final class LifecycleManagerFactory {

	/**
	 * 创建{@link LifecycleManager}
	 * @param assembly {@link AssemblyBase}, 装配件
	 * @param config {@link AssemblyConfig},装配件配置
	 * @param assemblyClassLoader {@link ClassLoader},装配件类加载器
	 * @return {@link LifecycleManager}
	 */
	public static LifecycleManager createLifecycleManager(
			AssemblyBase assembly, AssemblyConfig config) {
		return new DefaultLifecycleManager(assembly, config);
	}
}
