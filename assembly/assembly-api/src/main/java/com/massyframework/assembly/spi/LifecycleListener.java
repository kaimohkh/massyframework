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
package com.massyframework.assembly.spi;

/**
 * 生命周期事件监听器
 *
 */
public interface LifecycleListener {

	/**
	 * 已准备就绪
	 */
	void onReadied();
	
	/**
	 * 已激活，装配件进入工作状态
	 */
	void onActivated();
	
	/**
	 * 正在钝化，装配件准备退出工作状态
	 */
	void onInactivating();
	
	/**
	 * 退出准备就绪
	 */
	void onUnreadying();
	
	/**
	 * 装配件准备卸载
	 */
	void onUninstalling();
}
