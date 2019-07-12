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
 * @日   期:  2019年1月27日
 */
package com.massyframework.assembly.runtime.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.monitor.AssemblyTask;
import com.massyframework.assembly.monitor.AssemblyTaskMonitor;
import com.massyframework.assembly.monitor.AssemblyTaskPhases;
import com.massyframework.assembly.runtime.ExecutorServiceAware;
import com.massyframework.assembly.service.Filter;
import com.massyframework.assembly.service.ServiceEvent;
import com.massyframework.assembly.service.ServiceListener;
import com.massyframework.assembly.service.ServiceRegistedEvent;
import com.massyframework.assembly.service.ServiceUnregistingEvent;
import com.massyframework.logging.Logger;
import com.massyframework.logging.LoggerAware;

/**
 * 服务事件发布者
 */
class ServiceEventPublisher implements LoggerAware, ExecutorServiceAware{
	
	private AssemblyTaskMonitor taskMonitor;
	private volatile Logger logger;
	private volatile ExecutorService executor;
	private List<ListenerWrapper> listeners =
			new CopyOnWriteArrayList<>();

	/**
	 * 
	 */
	public ServiceEventPublisher(AssemblyTaskMonitor taskMonitor) {
		this.taskMonitor = Objects.requireNonNull(taskMonitor, "\"taskMonitor\" cannot be null.");
	}
	
	/**
     * 添加服务事件监听器和对应的筛选器
     * @param listener 服务事件监听器
     * @param filter 服务事件筛选器
     */
    public void addListener(ServiceListener listener, Filter filter) {
    	ListenerWrapper wrapper = new ListenerWrapper(listener, filter);
    	this.listeners.add(wrapper);
    }
        
    /**
     * 移除服务事件监听器
     * @param listener 服务事件监听器
     */
    public void removeListener(ServiceListener listener) {
    	Optional<ListenerWrapper> optional =
    			this.listeners.stream()
    				.filter( wrapper -> wrapper.listener == listener)
    				.findFirst();
    	if (optional.isPresent()) {
    		this.listeners.remove(optional.get());
    	}
    }
    
    /**
     * 发布服务注册事件
     * @param assembly {@link Assembly},注册服务的装配件
     * @param event {@link ServiceRegistedEvent}
     */
    public void applyEvent(Assembly assembly, ServiceRegistedEvent<?> event) {
    	AssemblyTask task = this.taskMonitor.startTask(assembly, AssemblyTaskPhases.SERVICE_UNEXPORT);
    	EventPublisherTask publisherTask = new EventPublisherTask(event, task);
    	ExecutorService executor = this.getExecutorService();
    	if (executor != null) {
    		executor.execute(publisherTask);
    	}else {
    		new Thread(publisherTask).start();
    	}
    }
    
    /**
     * 发布服务注销事件
     * @param assembly {@link Assembly},撤销服务的装配件
     * @param event {@link ServiceUnregistingEvent}
     */
    public void applyEvent(Assembly assembly, ServiceUnregistingEvent<?> event) {
    	AssemblyTask task = this.taskMonitor.startTask(assembly, AssemblyTaskPhases.SERVICE_UNEXPORT);
    	this.doSendEvent(event);
    	task.complete();
    }
    
    /**
     * 发送事件
     * @param event {@link ServiceEvent}
     */
    protected void doSendEvent(ServiceEvent<?> event) {
    	this.listeners.stream()
    		.forEach( listener -> listener.onChanged(event));
    }
        

    /* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.ExecutorServiceAware#setExecutorService(java.util.concurrent.ExecutorService)
	 */
	@Override
	public void setExecutorService(ExecutorService executor) {
		this.executor = executor;
	}
	
	/**
	 * 执行服务
	 * @return {@link ExecutorService}
	 */
	protected ExecutorService getExecutorService() {
		return this.executor;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.logging.LoggerAware#setLogger(com.massyframework.logging.Logger)
	 */
	@Override
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	/**
	 * 日志记录器
	 * @return {@link Logger}
	 */
	protected Logger getLogger() {
		return this.logger;
	}


	private class EventPublisherTask implements Runnable{
		
		private ServiceEvent<?> event;
		private AssemblyTask assemblyTask;
		
		public EventPublisherTask(ServiceEvent<?> event, AssemblyTask assemblyTask) {
			this.event = Objects.requireNonNull(event, "\"event\" cannot be null.");
			this.assemblyTask = Objects.requireNonNull(assemblyTask, "\"assemblyTask\" cannot be null.");
		}

		@Override
		public void run() {
			doSendEvent(this.event);
			this.assemblyTask.complete();
		}
		
	}
	/**
	 * 服务事件监听封装器
	 */
	private class ListenerWrapper implements ServiceListener {
    	
    	private final ServiceListener listener;
    	private final Filter filter;
    	
    	public ListenerWrapper(ServiceListener listener, Filter filter) {
    		this.listener = Objects.requireNonNull(listener, "\"listener\" cannot be nul.");
    		this.filter = Objects.requireNonNull(filter, "\"filter\" cannot be null.");
    	}

		@Override
		public void onChanged(ServiceEvent<?> event) {
			if (filter.match(event.getServiceReference())) {
				try {
					this.listener.onChanged(event);
				}catch(Exception e) {
					Logger logger = getLogger();
					if (logger != null) {
						if (logger.isErrorEnabled()) {
							logger.error(this.listener.getClass().getName() + " onChanged() failed.", e);
						}
					}
				}
			}
		}
    	
    }
}
