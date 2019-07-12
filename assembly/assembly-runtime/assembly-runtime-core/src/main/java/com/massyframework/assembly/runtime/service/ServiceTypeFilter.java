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

import java.util.Map;

import com.massyframework.assembly.service.Filter;
import com.massyframework.assembly.service.ServiceReference;

/**
 * 服务类型筛选器
 * @author huangkaihui
 *
 */
class ServiceTypeFilter implements Filter {
	
	private final Class<?> serviceType;
	private final Filter filter;

	/**
	 * 
	 */
	protected ServiceTypeFilter(Class<?> serviceType, Filter filter) {
		this.serviceType = serviceType;
		this.filter = filter;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.Filter#match(java.util.Map)
	 */
	@Override
	public boolean match(Map<String, Object> props) {
		if (this.isObjectClassMatch(this.getObjectClasses(props))){
			return this.filter.match(props);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.Filter#match(com.massyframework.assembly.service.ServiceReference)
	 */
	@Override
	public <S> boolean match(ServiceReference<S> reference) {
		if (this.isObjectClassMatch(this.getObjectClasses(reference))){
			return this.filter.match(reference);
		}
		return false;
	}
	
	protected boolean isObjectClassMatch(Class<?>[] objectClasses){
		for (Class<?> objectClass: objectClasses){
			if (this.serviceType == objectClass){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 对象类型
	 * @param props 属性
	 * @return {@link Class}数组
	 */
	protected Class<?>[] getObjectClasses(Map<String, Object> props){
		return (Class<?>[])props.get(ServiceReference.OBJECT_CLASS);
	}
	
	/**
	 * 对象类型
	 * @param reference
	 * @return
	 */
	protected Class<?>[] getObjectClasses(ServiceReference<?> reference){
		return (Class<?>[])reference.getProperty(ServiceReference.OBJECT_CLASS);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServiceTypeFilter [serviceType=" + serviceType + ", filter=" + filter + "]";
	}

	
}

