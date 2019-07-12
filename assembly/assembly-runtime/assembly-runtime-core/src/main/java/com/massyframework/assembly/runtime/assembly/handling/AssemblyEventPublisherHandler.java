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
package com.massyframework.assembly.runtime.assembly.handling;

import com.massyframework.assembly.AssemblyListener;

/**
 * 装配件事件发布处理器
 *
 */
public interface AssemblyEventPublisherHandler {
	
	/**
	 * 添加事件监听器
	 * @param listener {@link AssemblyListener},装配件事件监听器
	 */
	void addListener(AssemblyListener listener);
	
	/**
	 * 发布就绪事件
	 */
	void postReadiedEvent();

	/**
	 * 发布退出就绪事件
	 */
	void sendUnreadingEvent();
	
	/**
	 * 发布激活事件
	 */
	void postActivedEvent();
	
	/**
	 * 发送退出激活事件
	 */
	void sendUnactivingEvent();
	
	/**
	 * 移除事件监听器
	 * @param listener {@link AssemblyListener},装配件事件监听器
	 */
	void removeListener(AssemblyListener listener);
}
