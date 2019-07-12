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
 * @日   期:  2019年1月25日
 */
package com.massyframework.assembly.monitor;

import com.massyframework.assembly.Assembly;

/**
 * 装配件启停任务监视器,监视装配件启动和停止的执行过程
 */
public interface AssemblyTaskMonitor {
	
	/**
	 * 添加事件监听器
	 * @param listener {@link AssemblyTaskListener}
	 */
	void addListener(AssemblyTaskListener listener);

	/**
	 * 开始执行任务
	 * @param assembly  {@link Assembly},执行启停的装配件
	 * @param phases {@link AssemblyTaskPhases},启停任务阶段
	 * @return {@link AssemblyTask}
	 */
	<T> AssemblyTask startTask(Assembly assembly, AssemblyTaskPhases phases);
	
	/**
	 * 移除事件监听器
	 * @param listener {@link AssemblyTaskListener}
	 */
	void removeListener(AssemblyTaskListener listener);
}
