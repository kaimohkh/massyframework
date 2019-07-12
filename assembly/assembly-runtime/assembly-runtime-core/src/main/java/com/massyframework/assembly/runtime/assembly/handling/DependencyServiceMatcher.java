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
package com.massyframework.assembly.runtime.assembly.handling;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.massyframework.assembly.monitor.AssemblyTask;
import com.massyframework.assembly.monitor.AssemblyTaskMonitor;
import com.massyframework.assembly.monitor.AssemblyTaskPhases;
import com.massyframework.assembly.service.DependencyServiceDefinition;
import com.massyframework.assembly.service.Filter;
import com.massyframework.assembly.service.ServiceEvent;
import com.massyframework.assembly.service.ServiceListener;
import com.massyframework.assembly.service.ServiceReference;
import com.massyframework.assembly.service.ServiceRegistedEvent;
import com.massyframework.assembly.service.ServiceRepository;
import com.massyframework.assembly.spi.AssemblyStatus;
import com.massyframework.assembly.spi.DependencyServiceProvider;
import com.massyframework.assembly.spi.HandlerBase;
import com.massyframework.assembly.spi.LifecycleManager;

/**
 * 监听服务注册事件，执行服务匹配
 *
 */
final class DependencyServiceMatcher extends HandlerBase 
	implements DependencyServiceHandler, DependencyServiceProvider {

	//未匹配资源
	private final Set<DependencyServiceDefinition<?>> unmatchs =
			new HashSet<DependencyServiceDefinition<?>>();
	//已匹配资源
	private final Set<DependencyServiceDefinition<?>> matcheds =
				new HashSet<DependencyServiceDefinition<?>>();
	//已匹配关系
	private final Map<DependencyServiceDefinition<?>, ServiceReference<?>> matchedMap =
				new HashMap<DependencyServiceDefinition<?>, ServiceReference<?>>();
	
	private ListenerImpl listener;
		
	/**
	 * 构造方法
	 */
	public DependencyServiceMatcher() {

	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.HandlerBase#init()
	 */
	@Override
	protected synchronized void init() throws Exception {
		super.init();
		List<DependencyServiceDefinition<?>> dependencies = 
				this.getLifecycleManager().getAssemblyConfig().getDependencyServiceDefinitions();
		this.unmatchs.addAll(this.getLifecycleManager().getAssemblyConfig().getDependencyServiceDefinitions());
		if (this.listener == null){
			ServiceRepository repository =
					ServiceRepository.retrieveFrom(this.getAssembly());
			this.listener = new ListenerImpl(repository);
			repository.addListener(this.listener,
					repository.createFilter(null));

			for (DependencyServiceDefinition<?> definition: dependencies){
				ServiceReference<?> reference =
						definition.getFilterString() != null ?
								repository.findServiceRefernece(definition.getRequiredType(),definition.getFilterString()):
									repository.findServiceReference(definition.getRequiredType());
				if (reference != null){
					this.listener.doMatch(reference, definition);
				}
			}
		}
	}
	
	/**
	 * 析构方法
	 */
	@Override
	public synchronized void destroy() {
		if (this.listener != null){
			ServiceRepository repository =
					ServiceRepository.retrieveFrom(this.getAssembly());
			repository.removeListener(this.listener);
		}
		super.destroy();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.assembly.handling.DependencyServiceHandler#findMatchedServiceReference(com.massyframework.assembly.service.DependencyServiceDefinition)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public synchronized <S> ServiceReference<S> findMatchedServiceReference(DependencyServiceDefinition<S> definition) {
		return (ServiceReference<S>) this.matchedMap.get(definition);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.assembly.handling.DependencyServiceHandler#getDependencyServiceDefinitions()
	 */
	@Override
	public List<DependencyServiceDefinition<?>> getDependencyServiceDefinitions() {
		return this.getLifecycleManager().getAssemblyConfig().getDependencyServiceDefinitions();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.assembly.handling.DependencyServiceHandler#getUnmatchDependencyServiceDefinitions()
	 */
	@Override
	public synchronized Collection<DependencyServiceDefinition<?>> getUnmatchDependencyServiceDefinitions() {
		return Collections.unmodifiableCollection(this.unmatchs);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.assembly.handling.DependencyServiceHandler#isAllMatched()
	 */
	@Override
	public synchronized boolean isAllMatched() {
		return this.unmatchs.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.DependencyServiceProvider#getDependencyServices()
	 */
	@Override
	public Map<DependencyServiceDefinition<?>, Object> getDependencyServices() {
		if (this.getLifecycleManager().getAssemblyStatus() == AssemblyStatus.READY) {
			synchronized(this) {
				Map<DependencyServiceDefinition<?>, Object> result = new HashMap<>();
				if (this.unmatchs.isEmpty()) {
					ServiceRepository serviceRepository =
						ServiceRepository.retrieveFrom(this.getAssembly());
					for (Map.Entry<DependencyServiceDefinition<?>, ServiceReference<?>> entry: this.matchedMap.entrySet()) {
						ServiceReference<?> reference = entry.getValue();
						Object service = serviceRepository.getService(reference);
						result.put(entry.getKey(), service);
					}
					return result;
				}
			}
		}
		return Collections.emptyMap();
	}

	/**
	 * 根据<code>reference</code>获取被匹配的依赖服务定义
	 * @param reference 服务引用
	 * @return {@link DependencyServiceDefinition}
	 */
	@SuppressWarnings("unchecked")
	protected synchronized <S> DependencyServiceDefinition<S> getMatchedDefinition(ServiceReference<S> reference){
		if (this.matchedMap.containsValue(reference)){
			for (Map.Entry<DependencyServiceDefinition<?>, ServiceReference<?>> entry:
					this.matchedMap.entrySet()){
				if (entry.getValue() == reference){
					return (DependencyServiceDefinition<S>) entry.getKey();
				}
			}
		}

		return null;
	}
	
	/**
	 * 启动装配件
	 */
	protected void startAssembly(){
		if (this.isAllMatched()){
			try{
				LifecycleManager handler = this.getLifecycleManager();
				handler.start();
			}catch(Exception e){
				logError(e.getMessage(), e);
			}
		}
	}

	/**
	 * 启动装配件
	 */
	protected void stopAssembly(){
		try{
			this.getLifecycleManager().stop();
		}catch(Exception e){

		}
	}

	
	/**
	 * 服务监听器实现
	 */
	private class ListenerImpl implements ServiceListener {

		private Map<Class<?>, Map<DependencyServiceDefinition<?>, Filter>> filterMap;

		/**
		 * 构造方法
		 */
		public ListenerImpl(ServiceRepository repository) {
			this.filterMap = this.createFilterMap(repository);
		}

		/**
		 * 创建过滤Map
		 * @param repository 服务仓储
		 */
		protected Map<Class<?>, Map<DependencyServiceDefinition<?>, Filter>> createFilterMap(
				ServiceRepository repository){
			Map<Class<?>, Map<DependencyServiceDefinition<?>, Filter>> result = new HashMap<>();

			List<DependencyServiceDefinition<?>> definitions =
					getDependencyServiceDefinitions();
			for (DependencyServiceDefinition<?> definition: definitions){
				Class<?> type = definition.getRequiredType();
				Filter filter = repository.createFilter(definition.getFilterString());

				Map<DependencyServiceDefinition<?>, Filter> map = result.get(type);
				if (map == null){
					map = new HashMap<>();
					result.put(type, map);
				}
				map.put(definition, filter);
			}

			return result;
		}

		/**
		 * 触发服务事件
		 *
		 * @param event {@link ServiceEvent}
		 */
		@Override
		public void onChanged(ServiceEvent<?> event) {
			AssemblyTaskMonitor taskMonitor =
					getHandler(AssemblyTaskMonitor.class);
			AssemblyTask task =
					taskMonitor.startTask(getAssembly(), AssemblyTaskPhases.DEPENDENCY_MATCH);
			try {
				Class<?>[] objectTypes =
						(Class<?>[])event.getServiceReference().getObjectClasses();
				for (Class<?> type: objectTypes){
					Map<DependencyServiceDefinition<?>, Filter> map = this.filterMap.get(type);
					if (map != null){
						if (event instanceof ServiceRegistedEvent){
							this.doRegisted(event.getServiceReference(), map);
						}else{
							this.doUnregisting(event.getServiceReference(), map);
						}
					}
				}
				task.complete();
			}catch(Exception e) {
				task.complete(e);
				throw e;
			}finally {
				task = null;
			}
		}

		/**
		 * 服务注册处理
		 * @param reference
		 * @param map
		 */
		private void doRegisted(ServiceReference<?> reference,
								Map<DependencyServiceDefinition<?>, Filter> map){
			for (Map.Entry<DependencyServiceDefinition<?>, Filter> entry: map.entrySet()){
				if (entry.getValue().match(reference)){
					this.doMatch(reference, entry.getKey());
				}
			}
		}

		/**
		 * 执行匹配
		 * @param matchedReference 匹配的服务服务引用
		 * @param definition 定义
		 */
		public void doMatch(ServiceReference<?> matchedReference, DependencyServiceDefinition<?> definition){
			boolean matched = false;
			synchronized (DependencyServiceMatcher.this){
				if (matchedMap.putIfAbsent(definition, matchedReference) == null){
					matcheds.add(definition);
					unmatchs.remove(definition);
					
					logTrace("matched dependency: " + definition + ".");
					matched = true;
				}
			}

			if (matched){
				startAssembly();
			}
		}

		/**
		 * 服务注销处理
		 * @param reference
		 * @param map
		 */
		private void doUnregisting(ServiceReference<?> reference,
								   Map<DependencyServiceDefinition<?>, Filter> map){
			//准备注销
			DependencyServiceDefinition<?> definition =
					getMatchedDefinition(reference);
			if (definition != null){
				stopAssembly();
				synchronized (DependencyServiceMatcher.this){
					matchedMap.remove(definition);
					matcheds.remove(definition);
					unmatchs.add(definition);
				}
			}
		}
	}

}
