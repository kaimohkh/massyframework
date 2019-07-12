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
package com.massyframework.assembly.runtime;

import java.util.Objects;

import com.massyframework.assembly.AssemblyContext;
import com.massyframework.assembly.spi.ActivationHandler;
import com.massyframework.assembly.spi.HandlerBase;
import com.massyframework.assembly.spi.HandlerRegistration;
import com.massyframework.assembly.container.Container;

/**
 * 内核装配件上下文处理器，提供注册和注销装配件上下文的方法
 *
 */
final class KernelAssemblyContextHandler extends HandlerBase
	implements ActivationHandler{
	
	private final KernelAssemblyContainer container;
	private HandlerRegistration<AssemblyContext> registration;

	/**
	 * 构造方法
	 * @param container {@link Container},服务容器
	 */
	public KernelAssemblyContextHandler(KernelAssemblyContainer container) {
		this.container = Objects.requireNonNull(container, "\"comonentMap\" cannot be null.");
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.ActivationHandler#doStarting()
	 */
	@Override
	public synchronized void doStarting() throws Exception {
		if (this.registration == null) {
			this.registration = this.getLifecycleManager().register(
					new KernelAssemblyContext(this.container));
		}
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.ActivationHandler#doStopped()
	 */
	@Override
	public synchronized void doStopped() throws Exception {
		if (this.registration != null) {
			this.registration .unregister();
			this.registration = null;
		}
	}
	
}
