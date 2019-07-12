/**
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
 * Author: 黄开晖（117227773@qq.com）
 * Date:   2019年6月29日
 */
package com.massyframework.assembly.runtime.assembly;

import java.util.List;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyConfig;
import com.massyframework.assembly.runtime.service.ServiceAdmin;

/**
 * 装配件工厂
 * @author  Huangkaihui
 *
 */
public abstract class AssemblyFactory {

	/**
	 * 创建装配件
	 * @param config {@link AssemblyConfig}
	 * @param classLoader {@link ClassLoader},类加载器
	 * @param serviceAdmin {@link ServiceAsdmin},服务管理器
	 * @param handlers {@link List}, 处理器集合
	 * @return {@link Assembly}
	 */
	public static AssemblyBase createAssembly(AssemblyConfig config, 
			ClassLoader classLoader, ServiceAdmin serviceAdmin, List<Object> handlers) {
		long id = AssemblyIDGenerator.generateID();
		return new DefaultAssembly(id, config, classLoader, serviceAdmin, handlers);
	}
	
}
