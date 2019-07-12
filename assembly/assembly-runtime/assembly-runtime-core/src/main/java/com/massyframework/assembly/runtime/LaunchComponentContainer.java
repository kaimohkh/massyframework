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
 * @日   期:  2019年1月15日
 */
package com.massyframework.assembly.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.massyframework.assembly.Framework;
import com.massyframework.assembly.container.ComponentNameExistsException;
import com.massyframework.assembly.container.ComponentsException;
import com.massyframework.assembly.runtime.service.ExportServiceDefinitionUtils;
import com.massyframework.assembly.service.ExportServiceDefinition;
import com.massyframework.assembly.service.ServiceProperties;
import com.massyframework.assembly.service.ServiceReference;
import com.massyframework.assembly.support.LogSupporter;

/**
 * 初始化的组件容器
 */
public class LaunchComponentContainer extends LogSupporter{
	
	private Map<String ,ComponentEntry<?>> componentMap;

	/**
	 * 
	 */
	public LaunchComponentContainer() {
		this.componentMap = new ConcurrentHashMap<String, ComponentEntry<?>>();
	}

	/**
	 * 添加组件, 组件将由编号为0的内核装配件进行管理
	 * @param cName {@link String},组件在容器中的名称,具有唯一性
	 * @param component {@link C},组件实例
	 * @throws ComponentsException 添加组件时发生的例外
	 */
	public <C> void addComponent(String cName, C component) throws ComponentsException{
		Objects.requireNonNull(cName, "\"cName\" cannot be null.");
		Objects.requireNonNull(component, "\"component\" cannot be null.");
		
		ComponentEntry<C> entry = new ComponentEntry<>(component, null);
		if (this.componentMap.putIfAbsent(cName, entry) != null) {
			throw new ComponentNameExistsException(cName);
		}
		this.logInfo("add component: cName=\"" + cName+ "\", component=" + component.getClass().getName() + ".");
	}
	
	/**
	 * 添加可注册为服务的组件, 组件将由编号为0的内核装配件进行管理,并在{@link Framework#start()}方法中被注册为服务.
	 * @param cName {@link String},组件在容器中的名称,具有唯一性
	 * @param component {@link C},组件实例
	 * @param props {@link ServiceProperties},服务属性
	 * @throws ComponentsException 添加组件时发生的例外
	 */
	public <C> void addComponent(String cName, C component, ServiceProperties props) throws ComponentsException{
		Objects.requireNonNull(cName, "\"cName\" cannot be null.");
		Objects.requireNonNull(component, "\"component\" cannot be null.");
		Objects.requireNonNull(props, "\"props\" cannot be null.");
	
		Class<?>[] classes = props.getObjectClass();
		if ((classes == null) || (classes.length == 0)) {
			throw new IllegalArgumentException("ServiceProperties has not \"" + ServiceReference.OBJECT_CLASS + "\" Property.");
		}
		
		ComponentEntry<C> entry = new ComponentEntry<>(component, props);
		if (this.componentMap.putIfAbsent(cName, entry) != null) {
			throw new ComponentNameExistsException(cName);
		}
		this.logInfo("add service: cName=\"" + cName+ "\", component=" + component.getClass().getName() + ".");
	}
	
	/**
	 * 使用<code>componentType</code>获取组件实例<br>
	 * 本方法实际调用{@link #getComponent(componentType.getName, componentType)}。
	 * @param componentType {@link Class},组件类型
	 * @return {@link C}
	 */
	public <C> C getComponent(Class<C> componentType) {
		if (componentType == null) return null;
		return this.getComponent(componentType.getName(), componentType);
	}
	
	/**
	 * 按<code>cName</code>获取组件实例
	 * @param cName {@link String},组件在容器中的名称
	 * @return {@link Object}, 组件不存在则返回null.
	 */
	public Object getComponent(String cName) {
		if (cName == null) return null;
		ComponentEntry<?> entry = this.componentMap.get(cName);
		
		return entry == null ? null : entry.getComponent();
	}
	
	/**
	 * 按<code>cName</code>获取组件实例，并按指定的<code>componentType</code>返回
	 * @param cName {@link String},组件在容器中的名称
	 * @param componentType {@link Class},组件类型
	 * @return {@link C}, 组件不存在则返回null.
	 */
	public <C> C getComponent(String cName, Class<C> componentType) {
		if (cName == null) return null;
		ComponentEntry<?> entry = this.componentMap.get(cName);
		return entry == null ? null :
			componentType.cast(entry.getComponent());
	}
	
	/**
	 * 组件Map
	 * @return {@link ConcurrentHashMap},键为组件名，值为组件实例
	 */
	Map<String, Object> getComponentMap(){
		ConcurrentHashMap<String, Object>  result = new ConcurrentHashMap<>();
		for (Map.Entry<String, ComponentEntry<?>> entry: this.componentMap.entrySet()) {
			result.put(entry.getKey(), entry.getValue().getComponent());
		}
		return result;
	}
	
	/**
	 * 填充组件Map
	 * @param componentMap
	 */
	void fullComponentMap(Map<String, Object> componentMap) {
		if (componentMap == null) return ;

		for (Map.Entry<String, ComponentEntry<?>> entry: this.componentMap.entrySet()) {
			componentMap.put(entry.getKey(), entry.getValue().getComponent());
		}
	}
	
	/**
	 * 服务属性Map
	 * @return {@link ConcurrentHashMap},键为组件名，值为服务实例
	 */
	List<ExportServiceDefinition> getExportServiceDefinitions(){
		List<ExportServiceDefinition>  result = new ArrayList<>();
		for (Map.Entry<String, ComponentEntry<?>> entry: this.componentMap.entrySet()) {
			
			ServiceProperties props = entry.getValue().getServiceProperties();
			if (props != null) {
				ExportServiceDefinition  definition =
						ExportServiceDefinitionUtils.createExportServiceDefinition(props);
				props.setProperty(ServiceReference.CNAME, entry.getKey());
				result.add(definition);
			}
		}
		return result;
	}
	
	void fullExportServiceDefinition(List<ExportServiceDefinition> definitions) {
		if (definitions == null) return;
		
		for (Map.Entry<String, ComponentEntry<?>> entry: this.componentMap.entrySet()) {
			
			ServiceProperties props = entry.getValue().getServiceProperties();
			if (props != null) {
				ExportServiceDefinition  definition =
						ExportServiceDefinitionUtils.createExportServiceDefinition(props);
				props.setProperty(ServiceReference.CNAME, entry.getKey());
				definitions.add(definition);
			}
		}
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FrameworkContainer []";
	}

	/**
	 * 组件实例记录，包括组件实例和需要注册的服务属性
	 * @param <T>
	 */
	private class ComponentEntry<T> {
		
		private final T component;
		private final ServiceProperties props;
		
		/**
		 * 构造方法
		 * @param component 组件实例
		 * @param props 服务属性，可以为null.
		 */
		public ComponentEntry(T component, ServiceProperties props){
			this.component = component;
			this.props = props;
		}

		/**
		 * @return the component
		 */
		public T getComponent() {
			return component;
		}


		/**
		 * @return the props
		 */
		public ServiceProperties getServiceProperties() {
			return props;
		}
				
	}
}
