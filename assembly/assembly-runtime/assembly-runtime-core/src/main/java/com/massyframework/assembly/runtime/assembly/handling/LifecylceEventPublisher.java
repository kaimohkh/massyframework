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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.massyframework.assembly.spi.LifecycleListener;
import com.massyframework.assembly.support.LogSupporter;

/**
 * 生命周期事件发布
 *
 */
abstract class LifecylceEventPublisher extends LogSupporter{
	
	private final List<LifecycleListener> listeners = 
			new CopyOnWriteArrayList<LifecycleListener>();

	/**
	 * 
	 */
	public LifecylceEventPublisher() {

	}

	/**
	 * 添加监听器
	 * @param listener {@link LifecycleListener}，监听器
	 */
	public void addListener(LifecycleListener listener) {
		if (listener != null) {
			this.listeners.add(listener);
		}
	}
	
	/**
	 * 移除监听器
	 * @param listener {@link LifecycleListener},监听器
	 */
	public void removeListener(LifecycleListener listener) {
		if (listener != null) {
			this.listeners.remove(listener);
		}
	}
	
	/**
	 * 发布进入准备就绪
	 */
	protected synchronized void applyReadiedEvent(){
		for (LifecycleListener listener: this.listeners){
			try{
				listener.onReadied();
			}catch(Exception e){
				this.logWarn("onReadied failed: listener=" + listener + ".", e);
			}
		}
	}
	
	/**
	 * 发布装配件激活事件
	 */
	protected void applyActivatedEvent(){
		for (LifecycleListener listener: this.listeners){
			try{
				listener.onActivated();
			}catch(Exception e){
				this.logWarn("onActivated failed: listener=" + listener + ".", e);
			}
		}
		
	}
	
	/**
	 * 发布装配件钝化事件
	 */
	protected void applyInactivatingEvent(){		
		for (LifecycleListener listener: this.listeners){
			try{
				listener.onInactivating();
			}catch(Exception e){
				this.logWarn("onInactivating failed: listener=" + listener + ".", e);
			}
		}
	}
	
	/**
	 * 发布装配件退出就绪事件
	 */
	protected void applyUnreadyingEvent(){
		for (LifecycleListener listener: this.listeners){
			try{
				listener.onUnreadying();
			}catch(Exception e){
				this.logWarn("onUnreadying failed: listener=" + listener + ".", e);
			}
		}
	}
}
