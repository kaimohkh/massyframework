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

/**
 * 装配件启停任务事件适配器，实现{@link AssemblyTaskListener}接口，提供按事件类型划分不同的事件处理过程。
 */
public abstract class AssemblyTaskEventAdapter implements AssemblyTaskListener {

	/**
	 * 
	 */
	public AssemblyTaskEventAdapter() {

	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.monitor.AssemblyStartTaskListener#onEvent(com.massyframework.assembly.monitor.AssemblyStartTaskEvent)
	 */
	@Override
	public final void onEvent(AssemblyTaskEvent event) {
		if (event instanceof AssemblyTaskStartedEvent) {
			this.doEvent((AssemblyTaskStartedEvent)event);
		}else {
			this.doEvent((AssemblyTaskComplatedEvent)event);
		}
	}
	
	/**
	 * 装配件启动任务开始事件处理
	 * @param event {@link AssemblyTaskStartedEvent}
	 */
	protected void doEvent(AssemblyTaskStartedEvent event) {
		
	}
	
	/**
	 * 装配件启动任务完成事件处理
	 * @param event {@link AssemblyTaskComplatedEvent}
	 */
	protected void doEvent(AssemblyTaskComplatedEvent event) {
		
	}

}
