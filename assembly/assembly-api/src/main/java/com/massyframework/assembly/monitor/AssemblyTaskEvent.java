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
 * @日   期:  2019年1月26日
 */
package com.massyframework.assembly.monitor;

import java.util.Objects;

/**
 * 装配件启停任务事件
 *
 */
public abstract class AssemblyTaskEvent {
	
	private final AssemblyTask task;

	/**
	 * 构造方法
	 * @param task {@link AssemblyTask}
	 */
	public AssemblyTaskEvent(AssemblyTask task) {
		this.task = Objects.requireNonNull(
				task, "\"stratTask\" cannot be null.");
	}
	
	/**
	 * 启停任务
	 * @return {@link AssemblyTask}
	 */
	public AssemblyTask getAssemblyTask(){
		return this.task;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	
}
