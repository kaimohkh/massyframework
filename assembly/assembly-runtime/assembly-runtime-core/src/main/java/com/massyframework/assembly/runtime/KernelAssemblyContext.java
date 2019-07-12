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

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyAware;
import com.massyframework.assembly.AssemblyContext;
import com.massyframework.assembly.AssemblyContextAware;
import com.massyframework.assembly.AssemblyListener;
import com.massyframework.assembly.runtime.assembly.handling.AssemblyEventPublisherHandler;
import com.massyframework.assembly.spi.HandlerBase;
import com.massyframework.assembly.container.ComponentNamesProvider;
import com.massyframework.assembly.container.ComponentsException;
import com.massyframework.assembly.container.Container;
import com.massyframework.logging.LoggerAware;

/**
 * 内核装配件上下文
 *
 */
final class KernelAssemblyContext extends HandlerBase implements AssemblyContext,
	ComponentNamesProvider{

	private final KernelAssemblyContainer container;
	
	/**
	 * 构造方法
	 * @param container {@link Container},容器
	 */
	public KernelAssemblyContext(KernelAssemblyContainer container) {
		this.container = Objects.requireNonNull(
				container, "\"comonentMap\" cannot be null.");
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.HandlerBase#init()
	 */
	@Override
	protected void init() throws Exception {
		super.init();
		AssemblyEventPublisherHandler handler =
				this.getHandler(AssemblyEventPublisherHandler.class);
		List<String> names = this.container.getComponentNames();
		for (String name: names) {
			Object comp = this.container.getComponent(name);
			this.bindComponent(comp, handler);
		}
	}



	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.HandlerBase#destroy()
	 */
	@Override
	public void destroy() {
		AssemblyEventPublisherHandler handler =
				this.getHandler(AssemblyEventPublisherHandler.class);
		List<String> names = this.container.getComponentNames();
		for (String name: names) {
			Object comp = this.container.getComponent(name);
			this.unbindComponent(comp, handler);
		}
		super.destroy();
	}
	
	protected void bindComponent(Object object, AssemblyEventPublisherHandler handler) {
		AssemblyContextAware.maybeToBind(object, this);
		AssemblyAware.maybeToBind(object, this.getAssembly());
		LoggerAware.maybeToBind(object, this.getLogger());
		if (object instanceof AssemblyListener) {
			handler.addListener((AssemblyListener)object);
		}
	}
	
	protected void unbindComponent(Object object, AssemblyEventPublisherHandler handler) {
		if (object instanceof AssemblyListener) {
			handler.removeListener((AssemblyListener)object);
		}
		
		LoggerAware.maybeToBind(object, null);
		AssemblyAware.maybeToBind(object, null);
		AssemblyContextAware.maybeToBind(object, null);		
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.Container#containsComponent(java.lang.String)
	 */
	@Override
	public boolean containsComponent(String cName) {
		return this.container.containsComponent(cName);
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyContext#getAssociatedAssembly()
	 */
	@Override
	public Assembly getAssociatedAssembly() {
		return this.getAssembly();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.Container#getComponent(java.lang.String)
	 */
	@Override
	public Object getComponent(String cName) throws ComponentsException {
		return this.container.getComponent(cName);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.Container#getComponentsOfType(java.lang.Class)
	 */
	@Override
	public <T> Map<String, T> getComponentsOfType(Class<T> componentType) {
		return this.container.getComponentsOfType(componentType);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.Container#getComponentsWithAnnotation(java.lang.Class)
	 */
	@Override
	public Map<String, Object> getComponentsWithAnnotation(Class<? extends Annotation> annoType) {
		return this.container.getComponentsWithAnnotation(annoType);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.ComponentNamesProvider#getComponentNames()
	 */
	@Override
	public List<String> getComponentNames() {
		return this.container.getComponentNames();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "KernelAssemblyContext";
	}	
}

