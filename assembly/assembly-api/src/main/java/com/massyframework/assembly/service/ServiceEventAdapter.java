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
package com.massyframework.assembly.service;

/**
 * 服务事件适配器，根据事件类型提供不同处理的封装
 *
 */
public class ServiceEventAdapter implements ServiceListener {

	/**
	 * 
	 */
	public ServiceEventAdapter() {

	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceListener#onChanged(com.massyframework.assembly.service.ServiceEvent)
	 */
	@Override
	public final void onChanged(ServiceEvent<?> event) {
		if (event instanceof ServiceRegistedEvent) {
			this.onRegistedEvent((ServiceRegistedEvent<?>)event);
			return;
		}
		
		if (event instanceof ServiceUnregistingEvent) {
			this.onUnregistingEvent((ServiceUnregistingEvent<?>)event);
			return;
		}
	}
	
	/**
	 * 服务注册事件处理
	 * @param event {@link ServiceRegistedEvent},服务注册事件
	 */
	protected void onRegistedEvent(ServiceRegistedEvent<?> event) {
		
	}
	
	/**
	 * 服务注销事件处理 
	 * @param event {@link ServiceUnregistingEvent},服务注销事件
	 */
	protected void onUnregistingEvent(ServiceUnregistingEvent<?> event) {
		
	}

}
