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
 * @日   期:  2019年1月16日
 */
package com.massyframework.assembly.runtime;

import java.util.List;

import com.massyframework.assembly.AssemblyRepository;
import com.massyframework.assembly.FrameworkListener;
import com.massyframework.assembly.monitor.AssemblyTaskMonitor;
import com.massyframework.assembly.runtime.service.ServiceAdmin;

/**
 *
 *
 */
final class DefaultFramework extends FrameworkBase {

	/**
	 * 构造方法
	 * @param assemblyRepo {@link AssemblyRepository},装配件仓储
	 * @param servieAdmin {@link ServiceAdmin},服务管理器
	 * @param taskMonitor {@link AssemblyTaskMonitor},装配件启停任务监视器
	 * @param listeners {@link List},FrameworkListener列表
	 */
	public DefaultFramework(AssemblyRepository assemblyRepo, ServiceAdmin serviceAdmin,
			AssemblyTaskMonitor taskMonitor, List<FrameworkListener> listeners) {
		super(assemblyRepo, serviceAdmin, taskMonitor, listeners);
	}

}
