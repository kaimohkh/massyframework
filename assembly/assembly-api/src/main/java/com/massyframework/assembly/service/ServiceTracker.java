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
 * @日   期:  2019年1月13日
 */
package com.massyframework.assembly.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.massyframework.assembly.Assembly;


/**
 * 服务事件追踪器，可以过滤去掉重复的服务注册和注销通知
 * @author huangkaihui
 */
public class ServiceTracker<T> {

	private final ServiceRepository serviceRepository;
	private final Class<T> serviceType;
	private final String filterString;
	protected final Map<ServiceReference<T>, T> serviceMap = 
			new HashMap<ServiceReference<T>, T>();
	private ServiceTrackerCustomizer<T> customizer;
	
	private ServiceListener listener;
	
	/**
	 * 构造方法
	 * @param assembly 装配件
	 * @param serviceType 监听的服务类型
	 * @param filterString 过滤条件
	 */
	public ServiceTracker(Assembly assembly, Class<T> serviceType, String filterString) {
		this(assembly, serviceType, filterString, null);
	}
	
	/**
	 * 构造方法
	 * @param assembly 装配件
	 * @param serviceType 监听的服务类型
	 * @param filterString 过滤条件
	 * @param customizer 自定义监听接口
	 */
	public ServiceTracker(Assembly assembly, Class<T> serviceType, String filterString, ServiceTrackerCustomizer<T> customizer){
		this(ServiceRepository.retrieveFrom(assembly), serviceType, filterString, customizer);
	}
	
	/**
	 * 构造方法
	 * @param serviceRepository 服务仓储
	 * @param serviceType 监听的服务类型
	 * @param filterString 过滤条件
	 */
	public ServiceTracker(ServiceRepository serviceRepository, Class<T> serviceType, String filterString) {
		this(serviceRepository, serviceType, filterString, null);
	}
	
	/**
	 * 构造方法
	 * @param serviceRepository 服务仓储
	 * @param serviceType 监听的服务类型
	 * @param filterString 过滤条件
	 * @param customizer 自定义监听接口
	 */
	public ServiceTracker(ServiceRepository serviceRepository, Class<T> serviceType, String filterString, ServiceTrackerCustomizer<T> customizer){
		if ((serviceType == null) && (filterString == null)){
			throw new IllegalArgumentException("serviceType and filterString cannot both be null.");
		}
	
		this.serviceRepository = Objects.requireNonNull(serviceRepository, "\"serviceRepository\" cannot be null.");
		this.serviceType = serviceType;
		this.filterString = filterString;
		this.customizer = customizer;
	}
	
	/**
	 * 开始监听
	 */
	public synchronized final void open(){
		if (this.listener == null){
			this.listener = new ListenerImpl();
			Filter filter = this.serviceType != null ?
					this.serviceRepository.createFilter(serviceType, filterString):
						this.serviceRepository.createFilter(filterString);
			this.serviceRepository.addListener(listener, filter);
			
			List<ServiceReference<T>> references = this.serviceType != null ?
					this.serviceRepository.getServiceReferences(serviceType,this.filterString) : 
						this.serviceRepository.getServiceReferences(filter);
			for (ServiceReference<T> reference: references){
				if (!serviceMap.containsKey(reference)){
					T service = this.addService(reference);
					if (service != null){
						this.serviceMap.put(reference, service);
					}
				}
			}
		}
	}
	
	/**
	 * 结束监听
	 */
	public synchronized final void close(){
		if (this.listener != null){
			this.serviceRepository.removeListener(this.listener);
			this.listener = null;
		}
	}
		
	/**
	 * 添加服务
	 * @param referencen 服务引用
	 * @return {@link T},返回使用的服务实例,如果不使用服务，则返回null.
	 */
	protected T addService(ServiceReference<T> reference){
		if (this.customizer != null){
			return this.customizer.addService(reference, this.serviceRepository);
		}
		return null;
	}
	
	/**
	 * 移除服务
	 * @param reference 服务引用
	 * @param service 服务实例
	 */
	protected void removeService(ServiceReference<T> reference, T service){
		if (this.customizer != null){
			this.customizer.removeService(reference, service);
		}
	}
	
	/**
	 * 服务仓储
	 * @return {@link ServiceRepository}
	 */
	public ServiceRepository getServiceRepository(){
		return this.serviceRepository;
	}

	private class ListenerImpl extends ServiceEventAdapter {
		
		public ListenerImpl(){
			
		}

		/* (non-Javadoc)
		 * @see com.massyframework.assembly.service.ServiceListenerAdapter#onRegistedEvent(com.massyframework.assembly.service.ServiceEvent)
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected synchronized void onRegistedEvent(ServiceRegistedEvent<?> event) {
			ServiceReference<T> reference = 
					(ServiceReference<T>)event.getServiceReference();
			if (!serviceMap.containsKey(reference)){
				T service = addService(reference);
				serviceMap.put(reference, service);
			}
		}

		/* (non-Javadoc)
		 * @see com.massyframework.assembly.service.ServiceListenerAdapter#onUnregistingEvent(com.massyframework.assembly.service.ServiceUnregistingEvent<?>)
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected synchronized void onUnregistingEvent(ServiceUnregistingEvent<?> event) {
			ServiceReference<T> reference = 
					(ServiceReference<T>)event.getServiceReference();
			T service = serviceMap.remove(reference);
			if (service != null){
				removeService(reference, service);
			}
		}
	}
}
