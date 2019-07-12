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
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import com.massyframework.assembly.AssemblyActivedEvent;
import com.massyframework.assembly.AssemblyEvent;
import com.massyframework.assembly.AssemblyListener;
import com.massyframework.assembly.AssemblyReadiedEvent;
import com.massyframework.assembly.AssemblyUnactivingEvent;
import com.massyframework.assembly.AssemblyUnreadingEvent;
import com.massyframework.logging.Logger;
import com.massyframework.logging.LoggerAware;

/**
 * 提供发布装配件事件的能力
 */
class AssemblyEventPublisher implements LoggerAware, ExecutorServiceAware{
	
	private volatile ExecutorService executor;
	private Logger logger;
	private List<AssemblyListener> listeners;

	/**
	 * 构造方法
	 */
	public AssemblyEventPublisher() {
		this.listeners = new CopyOnWriteArrayList<>();
	}
		
	/**
	 * 添加事件监听器
	 * @param listener {@link AssemblyListener},装配件事件监听器
	 */
	public void addListener(AssemblyListener listener) {
		if (listener != null) {
			this.listeners.add(listener);
		}
	}
	
	/**
	 * 移除事件监听器
	 * @param listener {@link AssemblyListener},装配件事件监听器
	 */
	public void removeListener(AssemblyListener listener) {
		if (listener != null) {
			this.listeners.remove(listener);
		}
	}
	
	/**
	 * 发布激活事件
	 * @param event {@link AssemblyActivedEvent}
	 */
	public void applyEvent(AssemblyActivedEvent event) {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			executor.execute(new Task(event));
		}else {
			new Thread(new Task(event)).start();
		}
	}
	
	/**
	 * 发布就绪事件
	 * @param event {@link AssemblyReadiedEvent}
	 */
	public void applyEvent(AssemblyReadiedEvent event) {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			executor.execute(new Task(event));
		}else {
			new Thread(new Task(event)).start();
		}
	}
	
	/**
	 * 发布撤销激活事件
	 * @param event {@link AssemblyUnactivingEvent}
	 */
	public void applyEvent(AssemblyUnactivingEvent event) {
		this.doSendEvent(event);
	}
	
	/**
	 * 发布退出激活事件
	 * @param event {@link AssemblyUnreadingEvent}
	 */
	public void applyEvent(AssemblyUnreadingEvent event) {
		this.doSendEvent(event);
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.logging.LoggerAware#setLogger(com.massyframework.logging.Logger)
	 */
	@Override
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.ExecutorServiceAware#setExecutorService(java.util.concurrent.ExecutorService)
	 */
	@Override
	public void setExecutorService(ExecutorService executor) {
		this.executor = executor;
	}
	
	protected ExecutorService getExecutorService() {
		return this.executor;
	}

	/**
	 * 发送事件
	 * @param event {@link AssemblyEvent}
	 */
	protected void doSendEvent(AssemblyEvent event) {
		for (AssemblyListener listener: this.listeners) {
			try {
				listener.onChanged(event);
			}catch(Exception e) {
				if (logger != null) {
					if (logger.isErrorEnabled()) {
						logger.error(listener.getClass().getName() + " onChanged() falied.", e);
					}
				}
			}
		}
	}
	
	private class Task implements Runnable {
		
		private AssemblyEvent event;
		
		public Task(AssemblyEvent event) {
			this.event = Objects.requireNonNull(event, "\"event\" cannot be null.");
		}

		@Override
		public void run() {
			doSendEvent(event);			
		}
		
	}

}
