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
package com.massyframework.assembly.runtime.assembly;

import java.util.List;

import com.massyframework.assembly.AssemblyConfig;
import com.massyframework.assembly.runtime.service.ServiceAdmin;

/**
 * 缺省的装配件
 */
final class DefaultAssembly extends AssemblyBase {

	/**
	 * 构造方法
	 * @param id 装配件编号
	 * @param config {@link AssemblyConfig},装配件配置
	 * @param classLoader {@link ClassLoader},装配件的类加载器
	 * @param serviceAdmin {@link ServiceAdmin}, 服务管理器
	 * @param handlers {@link List}, 装配件的处理器例诶表
	 */
	DefaultAssembly(long id, AssemblyConfig config, ClassLoader classLoader, ServiceAdmin serviceAdmin, List<Object> handlers) {
		super(id, config, classLoader, serviceAdmin, handlers);
	}

	

}
