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
 * @日   期:  2019年1月24日
 */
package com.massyframework.assembly.support;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyContext;
import com.massyframework.assembly.spi.HandlerBase;
import com.massyframework.assembly.container.ComponentNotFoundException;
import com.massyframework.assembly.container.ComponentsException;
import com.massyframework.assembly.container.Container;

/**
 * 简单的AssemblyContext,内部嵌入Container
 */
public class AssemblyContextHandler<C extends Container> extends HandlerBase 
	implements AssemblyContext {
	
	private final C container;

	/**
	 * 构造方法
	 * @param container {@link Container},容器
	 */
	public AssemblyContextHandler(C container) {
		this.container = Objects.requireNonNull(container, "\"caontiner\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.Container#containsComponent(java.lang.String)
	 */
	@Override
	public boolean containsComponent(String cName) {
		return this.container.containsComponent(cName);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.Container#getComponent(java.lang.String)
	 */
	@Override
	public Object getComponent(String cName) throws ComponentsException {
		try {
			return this.container.getComponent(cName);
		}catch(Exception e) {
			throw new ComponentNotFoundException(e.getMessage(), e);
		}
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
	 * @see com.massyframework.assembly.AssemblyContext#getAssociatedAssembly()
	 */
	@Override
	public Assembly getAssociatedAssembly() {
		return this.getAssociatedAssembly();
	}

	/**
	 * 容器
	 * @return {@link Container}
	 */
	public C getContainer() {
		return this.container;
	}
}
