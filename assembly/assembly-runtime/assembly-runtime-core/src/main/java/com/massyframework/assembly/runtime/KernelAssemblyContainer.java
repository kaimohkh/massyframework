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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.massyframework.assembly.container.ComponentNamesProvider;
import com.massyframework.assembly.container.ComponentsException;
import com.massyframework.assembly.container.Container;

/**
 * 内核装配件的容器
 *
 */
final class KernelAssemblyContainer implements Container, ComponentNamesProvider{

	private Map<String, Object> componentMap;
	
	/**
	 * 构造方法
	 * @param componentMap {@link Map},键为名称，值为组件实例
	 */
	public KernelAssemblyContainer(Map<String, Object> componentMap) {
		this.componentMap = Collections.unmodifiableMap( 
				Objects.requireNonNull(componentMap, "\"comonentMap\" cannot be null."));
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.container.Container#containsComponent(java.lang.String)
	 */
	@Override
	public boolean containsComponent(String cName) {
		if (cName == null) return false;
		return this.componentMap.containsKey(cName);
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.container.Container#getComponent(java.lang.String)
	 */
	@Override
	public Object getComponent(String cName) throws ComponentsException {
		if (cName == null) return null;
		return this.componentMap.get(cName);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.Container#getComponentsOfType(java.lang.Class)
	 */
	@Override
	public <T> Map<String, T> getComponentsOfType(Class<T> componentType) {
		Map<String, T> result = new HashMap<String, T>();
		for (Map.Entry<String, Object> entry: this.componentMap.entrySet()) {
			if (componentType.isAssignableFrom(entry.getValue().getClass())) {
				result.put(entry.getKey(), componentType.cast(entry.getValue()));
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.Container#getComponentsWithAnnotation(java.lang.Class)
	 */
	@Override
	public Map<String, Object> getComponentsWithAnnotation(Class<? extends Annotation> annoType) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry: this.componentMap.entrySet()) {
			Class<?> beanClass = entry.getValue().getClass();
			if (beanClass.isAnnotationPresent(annoType)) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.ComponentNamesProvider#getComponentNames()
	 */
	@Override
	public List<String> getComponentNames() {
		return new ArrayList<>(this.componentMap.keySet());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "KernelAssemblyContainer";
	}
	
	
}

