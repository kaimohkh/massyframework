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
 * @日   期:  2019年2月21日
 */
package com.massyframework.assembly;

/**
 * 提供对不同装配件事件处理的封装
 */
public abstract class AssemblyEventAdapter implements AssemblyListener {

	/**
	 * 构造方法
	 */
	public AssemblyEventAdapter() {
		
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyListener#onChanged(com.massyframework.assembly.AssemblyEvent)
	 */
	@Override
	public final void onChanged(AssemblyEvent event) {
		if (event instanceof AssemblyReadiedEvent) {
			this.onEvent((AssemblyReadiedEvent)event);
			return;
		}
		
		if (event instanceof AssemblyUnreadingEvent) {
			this.onEvent((AssemblyUnreadingEvent)event);
			return;
		}
		
		if (event instanceof AssemblyActivedEvent) {
			this.onEvent((AssemblyActivedEvent)event);
			return;
		}
		
		if (event instanceof AssemblyUnactivingEvent) {
			this.onEvent((AssemblyUnactivingEvent)event);
			return;
		}
	}
	
	/**
	 * 就绪事件处理
	 * @param event {@link AssemblyReadiedEvent}
	 */
	protected void onEvent(AssemblyReadiedEvent event) {
		
	}
	
	/**
	 * 退出就绪事件处理
	 * @param event {@link AssemblyUnreadingEvent}
	 */
	protected void onEvent(AssemblyUnreadingEvent event) {
		
	}
	
	/**
	 * 激活事件处理
	 * @param event {@link AssemblyActivedEvent}
	 */
	protected void onEvent(AssemblyActivedEvent event) {
		
	}
	
	/**
	 * 退出激活事件处理
	 * @param event {@link AssemblyUnactivingEvent}
	 */
	protected void onEvent(AssemblyUnactivingEvent event) {
		
	}

}
