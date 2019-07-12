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
 * @日   期:  2019年1月19日
 */
package com.massyframework.assembly.runtime.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.service.ServiceFactory;
import com.massyframework.assembly.service.ServiceProperties;
import com.massyframework.assembly.service.ServiceReference;
import com.massyframework.assembly.service.ServiceRegistration;
import com.massyframework.assembly.service.ServiceRegistry;

/**
 * 服务注册代理
 * @author huangkaihui
 *
 */
class ServiceRegistryDelegate implements ServiceRegistry {
	
	private final ServiceAdmin serviceAdmin;
	private final Assembly assembly;

	/**
	 * 构造方法
	 * @param serviceManager 服务管理
	 * @param assembly 装配件
	 */
	public ServiceRegistryDelegate(ServiceAdmin serviceAdmin, Assembly assembly) {
		this.serviceAdmin =
				Objects.requireNonNull(serviceAdmin, "\"serviceManagement\" cannot be null.");
		this.assembly = 
				Objects.requireNonNull(assembly, "\"assembly\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRegistry#register(java.lang.Object, com.massyframework.assembly.service.ServiceProperties)
	 */
	@Override
	public <S> ServiceRegistration<S> register(S service, ServiceProperties props) {
		Objects.requireNonNull(service, "\"service\" cannot be null.");
		Objects.requireNonNull(props, "\"props\" cannot be null.");
		Class<?>[] classes = props.getObjectClass();
		if (classes == null || classes.length == 0) {
			throw new IllegalArgumentException("service property cannot be empty: \"" + ServiceReference.OBJECT_CLASS + "\".");
		}
		
		ServiceReference<S> reference = this.createServiceReference(props, classes);
		ServiceFactory<S> factory = new SingleServiceFactory<S>(service);
		
		return this.doRegister(reference, factory);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRegistry#register(com.massyframework.assembly.service.ServiceFactory, com.massyframework.assembly.service.ServiceProperties)
	 */
	@Override
	public <S> ServiceRegistration<S> register(ServiceFactory<S> factory, ServiceProperties props) {
		Objects.requireNonNull(factory, "\"factory\" cannot be null.");
		Objects.requireNonNull(props, "\"props\" cannot be null.");
		Class<?>[] classes = props.getObjectClass();
		if (classes == null || classes.length == 0) {
			throw new IllegalArgumentException("service property cannot be empty: \"" + ServiceReference.OBJECT_CLASS + "\".");
		}
		
		ServiceReference<S> reference = this.createServiceReference(props, classes);
		return this.doRegister(reference, factory);
	}

		
	/**
	 * 创建服务引用
	 * @param props 服务属性
	 * @param exportTypes 输出的类型
	 * @return {@link ServiceReference}
	 */
	protected <S> ServiceReference<S> createServiceReference(
			ServiceProperties svcprops, Class<?>[] exportTypes){				
		Map<String, Object> map = new HashMap<String, Object>(svcprops.getProperties());
		map.put(ServiceReference.ASSEMBLY_SYMBOLICNAME, 
				this.getAssembly().getSymbolicName());
		map.put(ServiceReference.OBJECT_CLASS, exportTypes);
		map.put(ServiceReference.ID, ServiceIdGenerator.generate());
		return new ReferenceImpl<S>(map);
	}
		
	/**
	 * 执行服务注册
	 * @param reference 
	 * @param service
	 * @return
	 */
	protected <S> ServiceRegistration<S> doRegister(ServiceReference<S> reference, ServiceFactory<S> factory){
		//执行注册
		ServiceRegistration<S> result = 
				this.getServiceAdmin().register(this.getAssembly(), reference, factory);	
		return result;
	}
		
		
	/**
	 * 服务管理器
	 * @return {@link ServiceAdmin}
	 */
	protected ServiceAdmin getServiceAdmin() {
		return this.serviceAdmin;
	}
	
	/**
	 * 对应的装配件
	 * @return {@link Assembly}
	 */
	protected Assembly getAssembly() {
		return this.assembly;
	}
		
	/**
	 * 服务引用实现
	 * @author huangkaihui
	 *
	 * @param <S>
	 */
	private class ReferenceImpl<S> implements ServiceReference<S> {
		
		private Map<String, Object> props;
		
		public ReferenceImpl(Map<String, Object> props) {
			this.props = props;
		}

		/* (non-Javadoc)
		 * @see com.massyframework.assembly.service.ServiceReference#getProperty(java.lang.String)
		 */
		@Override
		public Object getProperty(String propName) {
			if (propName == null) {
				return null;
			}
			return this.props.get(propName);
		}

		/* (non-Javadoc)
		 * @see com.massyframework.assembly.service.ServiceReference#getProperty(java.lang.String, java.lang.Class)
		 */
		@Override
		public <P> P getProperty(String propName, Class<P> propType) {
			Object result = this.getProperty(propName);
			return result == null ? null : propType.cast(result);
		}

		/* (non-Javadoc)
		 * @see com.massyframework.assembly.service.ServiceReference#getPropertyNames()
		 */
		@Override
		public List<String> getPropertyNames() {
			return new ArrayList<String>(this.props.keySet());
		}
	}

		
	/**
	 * 单例服务工厂
	 * @author huangkaihui
	 *
	 * @param <S>
	 */
	private class SingleServiceFactory<S> implements ServiceFactory<S> {
		
		private S service;
		
		public SingleServiceFactory(S service) {
			this.service = Objects.requireNonNull(service, "\"service\" cannot be null.");
		}

		/* (non-Javadoc)
		 * @see com.massyframework.assembly.service.ServiceFactory#getSerivce(com.massyframework.assembly.Assembly)
		 */
		@Override
		public S getSerivce(Assembly assembly) {
			return this.service;
		}
		
	}
}
