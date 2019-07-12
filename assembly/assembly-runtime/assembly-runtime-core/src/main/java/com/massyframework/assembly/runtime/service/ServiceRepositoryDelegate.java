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
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.service.Filter;
import com.massyframework.assembly.service.ServiceListener;
import com.massyframework.assembly.service.ServiceNotFoundException;
import com.massyframework.assembly.service.ServiceReference;
import com.massyframework.assembly.service.ServiceRegistry;
import com.massyframework.assembly.service.ServiceRepository;

/**
 * 服务仓储代理
 * @author huangkaihui
 *
 */
class ServiceRepositoryDelegate extends ServiceRegistryDelegate 
	implements ServiceRepository {

	/**
	 * 构造方法
	 * @param serviceAdmin 服务管理器
	 * @param assembly {@link Assembly}，装配件
	 */
	public ServiceRepositoryDelegate(ServiceAdmin serviceAdmin, Assembly assembly) {
		super(serviceAdmin, assembly);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#addListener(com.massyframework.assembly.service.ServiceListener, com.massyframework.assembly.service.Filter)
	 */
	@Override
	public void addListener(ServiceListener listener, Filter filter) {
		this.getServiceAdmin().addListener(listener, filter);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#addListener(com.massyframework.assembly.service.ServiceListener, java.lang.String)
	 */
	@Override
	public void addListener(ServiceListener listener, String filterString) {
		Filter filter = this.createFilter(filterString);
		this.addListener(listener, filter);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#createFilter(java.lang.String)
	 */
	@Override
	public Filter createFilter(String filterString) {
		if (filterString == null){
            return EmptyFilter.INSTANCE;
        }
        return LDAPFilterFactory.newInstance(filterString);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#createFilter(java.lang.Class, java.lang.String)
	 */
	@Override
	public Filter createFilter(Class<?> serviceType, String filterString) {
		Objects.requireNonNull(serviceType, "serviceType cannot be null.");
		return new ServiceTypeFilter(serviceType, this.createFilter(filterString));
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#findServiceReference(java.lang.Class)
	 */
	@Override
	public <S> ServiceReference<S> findServiceReference(Class<S> serviceType) {
		return this.getServiceAdmin()
				.findServiceReference(serviceType, EmptyFilter.INSTANCE);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#findServiceReference(java.lang.Class, com.massyframework.assembly.service.Filter)
	 */
	@Override
	public <S> ServiceReference<S> findServiceReference(Class<S> serviceType, Filter filter) {
		return this.getServiceAdmin().findServiceReference(serviceType, filter);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#findServiceRefernece(java.lang.Class, java.lang.String)
	 */
	@Override
	public <S> ServiceReference<S> findServiceRefernece(Class<S> serviceType, String filterString) {
		return this.getServiceAdmin()
				.findServiceReference(serviceType, this.createFilter(filterString));
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#findServiceReference(com.massyframework.assembly.service.Filter)
	 */
	@Override
	public <S> ServiceReference<S> findServiceReference(Filter filter) {
		return this.getServiceAdmin().findServiceReference(filter);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#findServiceReference(java.lang.String)
	 */
	@Override
	public <S> ServiceReference<S> findServiceReference(String filterString) {
		return this.getServiceAdmin()
			.findServiceReference(this.createFilter(filterString));
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#getService(com.massyframework.assembly.service.ServiceReference)
	 */
	@Override
	public <S> S getService(ServiceReference<S> reference) throws ServiceNotFoundException {
		return this.getServiceAdmin().getService(reference, getAssembly());
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#getServiceRegistry()
	 */
	@Override
	public ServiceRegistry getServiceRegistry() {
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#getServiceTypes()
	 */
	@Override
	public List<Class<?>> getServiceTypes() {
		return this.getServiceAdmin().getServiceTypes();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#getServices(java.util.Collection)
	 */
	@Override
	public <S> List<S> getServices(Collection<ServiceReference<S>> references) throws ServiceNotFoundException {
		List<S> result = new ArrayList<S>();
		for (ServiceReference<S> reference:  references) {
			result.add(this.getServiceAdmin().getService(reference, getAssembly()));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#getServiceReferences(java.lang.Class)
	 */
	@Override
	public <S> List<ServiceReference<S>> getServiceReferences(Class<S> serviceType) {
		return this.getServiceAdmin()
				.getServiceReferences(serviceType, EmptyFilter.INSTANCE);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#getServiceReferences(java.lang.Class, com.massyframework.assembly.service.Filter)
	 */
	@Override
	public <S> List<ServiceReference<S>> getServiceReferences(Class<S> serviceType, Filter filter) {
		return this.getServiceAdmin()
				.getServiceReferences(serviceType, filter);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#getServiceReferences(java.lang.Class, java.lang.String)
	 */
	@Override
	public <S> List<ServiceReference<S>> getServiceReferences(Class<S> serviceType, String filterString) {
		return this.getServiceAdmin()
				.getServiceReferences(serviceType, this.createFilter(filterString));
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#getServiceReferences(com.massyframework.assembly.service.Filter)
	 */
	@Override
	public <S> List<ServiceReference<S>> getServiceReferences(Filter filter) {
		return this.getServiceAdmin().getServiceReferences(filter);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#getServiceReferences(java.lang.String)
	 */
	@Override
	public <S> List<ServiceReference<S>> getServiceReferences(String filterString) {
		return this.getServiceAdmin().getServiceReferences(this.createFilter(filterString));
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceRepository#removeListener(com.massyframework.assembly.service.ServiceListener)
	 */
	@Override
	public void removeListener(ServiceListener listener) {
		this.getServiceAdmin().removeListener(listener);
	}

}
