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

import java.util.ArrayList;
import java.util.List;

import com.massyframework.assembly.AssemblyContext;
import com.massyframework.assembly.monitor.AssemblyTask;
import com.massyframework.assembly.monitor.AssemblyTaskMonitor;
import com.massyframework.assembly.monitor.AssemblyTaskPhases;
import com.massyframework.assembly.service.ExportServiceDefinition;
import com.massyframework.assembly.service.ServiceFactory;
import com.massyframework.assembly.service.ServiceRegistration;
import com.massyframework.assembly.service.ServiceRegistry;
import com.massyframework.assembly.service.ServiceRepository;
import com.massyframework.assembly.spi.HandlerBase;
import com.massyframework.assembly.spi.LifecycleEvetAdapter;
import com.massyframework.assembly.spi.LifecycleManager;

/**
 * 输出服务输出器
 *
 */
final class ExportServiceExporter extends HandlerBase {

	private volatile ListenerImpl listener;
	
	/**
	 * 构造方法
	 */
	public ExportServiceExporter() {
	}
	
	/**
	 * 初始化
	 *
	 * @throws Exception 装配件初始化抛出的例外
	 */
	@Override
	protected synchronized void init() throws Exception {
		super.init();
		if (this.listener == null){
			this.listener = new ListenerImpl();
			LifecycleManager handler = this.getLifecycleManager();
			handler.addListener(this.listener);
		}
	}

	/**
	 * 释放
	 */
	@Override
	public void destroy() {
		if (this.listener != null){
			LifecycleManager handler = this.getLifecycleManager();
			handler.removeListener(this.listener);
			this.listener = null;
		}
		super.destroy();
	}

	private class ListenerImpl extends LifecycleEvetAdapter {

		private List<ServiceRegistration<?>> registrations;

		public ListenerImpl(){
			this.registrations = new ArrayList<ServiceRegistration<?>>();
		}

		/**
		 * 已激活，装配件进入工作状态
		 */
		@Override
		public synchronized void onActivated() {
			super.onActivated();
			
			AssemblyTaskMonitor taskMonitor =
					getHandler(AssemblyTaskMonitor.class);
			AssemblyTask task =
					taskMonitor.startTask(getAssembly(), AssemblyTaskPhases.SERVICE_EXPORT);
			try {
				ServiceRepository repository =
						ServiceRepository.retrieveFrom(getAssembly());
				ServiceRegistry registry =
						repository.getServiceRegistry();
				AssemblyContext context =
						getHandler(AssemblyContext.class);
	
				List<ExportServiceDefinition> definitions=
						getLifecycleManager().getAssemblyConfig().getExportServiceDefinitions();
				for (ExportServiceDefinition definition : definitions) {
					Class<?>[] types = definition.getServiceProperties().getObjectClass();
					String cName = definition.getCName();
					if (cName == null) {
						cName = types[0].getName();
					}
					
					Object service = context.getComponent(cName);					
					if (ServiceFactory.class.isAssignableFrom(service.getClass())){
						ServiceFactory<?> factory = ServiceFactory.class.cast(service);
						this.registrations.add(
							registry.register(factory, definition.getServiceProperties()));
					}else{
						this.registrations.add(
							registry.register(service, definition.getServiceProperties()));
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
		 * 正在钝化，装配件准备退出工作状态
		 */
		@Override
		public synchronized void onInactivating() {
			AssemblyTaskMonitor taskMonitor =
					getHandler(AssemblyTaskMonitor.class);
			AssemblyTask task =
					taskMonitor.startTask(getAssembly(), AssemblyTaskPhases.SERVICE_UNEXPORT);
			try {
				if (!this.registrations.isEmpty()){
					for (ServiceRegistration<?> registration: this.registrations){
						registration.unregister();
					}
					this.registrations.clear();
				}
				task.complete();
			}catch(Exception e) {
				task.complete(e);
				throw e;
			}finally {
				task = null;
			}
			super.onInactivating();
		}
	}
}
