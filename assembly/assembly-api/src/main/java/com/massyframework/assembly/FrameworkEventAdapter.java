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
package com.massyframework.assembly;

import com.massyframework.assembly.service.ServiceRepository;

/**
 * 实现{@link FrameworkListener}，封装事件处理方法<br>
 * 目前仅支持{@link FrameworkStatus#STARTING}、{@link FrameworkStatus#STARTED}、{@link FrameworkStatus#STOPPING}、{@link FrameworkStatus#STOPPED}四种事件
 */
public class FrameworkEventAdapter implements FrameworkListener {

	/**
	 * 构造方法 
	 */
	public FrameworkEventAdapter() {
	
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.FrameworkListener#onEvent(com.massyframework.assembly.FrameworkEvent)
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public void onEvent(FrameworkEvent event) {
		Framework framework = event.getFramework();
		switch (framework.getStatus()) {
			case STARTING: {
				this.doStarting(framework);
				break;
			}
			case STARTED: {
				this.doStarted(framework);
				break;
			}
			case STOPPING: {
				this.doStopping(framework);
				break;
			}
			case STOPPED:{
				this.doStopped(framework);
				break;
			}
		}
	}
	
	/**
	 * 开始启动
	 * @param framework {@link Framework}
	 */
	protected void doStarting(Framework framework) {
		
	}
	
	/**
	 * 启动完成
	 * @param framework  {@link Framework}
	 */
	protected void doStarted(Framework framework) {
		
	}
	
	/**
	 * 开始停止
	 * @param framework  {@link Framework}
	 */
	protected void doStopping(Framework framework) {
		
	}
	
	/**
	 * 停止完成
	 * @param framework  {@link Framework}
	 */
	protected void doStopped(Framework framework) {
		
	}
	
	/**
	 * 获取内核装配件对应的服务仓储
	 * @param framework {@link Framework}
	 * @return {@link ServiceRepository}
	 */
	protected ServiceRepository getServiceRepository(Framework framework) {
		Assembly assembly = framework.getAssemblyRepository().getAssembly(0);
		return ServiceRepository.retrieveFrom(assembly);
	}
	
	/**
	 * 获取<code>symbolicName</code>对应的服务仓储
	 * @param framework {@link Framework}
	 * @param symbolicName {@link String},装配件的符号名称，具有唯一性
	 * @return {@link ServiceRepository}
	 * @throws AssemblyNotFoundException 未找到对应装配件时抛出例外
	 */
	protected ServiceRepository getServiceRepository(Framework framework, String symbolicName) {
		Assembly assembly = framework.getAssemblyRepository().getAssembly(symbolicName);
		return ServiceRepository.retrieveFrom(assembly);
	}
	
	/**
	 * 内核装配件
	 * @param framework {@link Framework}
	 * @return {@link Assembly}
	 */
	protected Assembly getKernelAssembly(Framework framework) {
		return framework.getAssemblyRepository().getAssembly(0);
	}
}
