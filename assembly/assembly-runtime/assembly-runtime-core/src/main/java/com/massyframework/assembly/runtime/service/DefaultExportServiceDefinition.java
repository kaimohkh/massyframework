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

import java.util.Objects;

import com.massyframework.assembly.service.ExportServiceDefinition;
import com.massyframework.assembly.service.ServiceProperties;
import com.massyframework.assembly.service.ServiceReference;

/**
 * 缺省的输出服务定义
 */
final class DefaultExportServiceDefinition implements ExportServiceDefinition {

	private ServiceProperties serviceProps;
		
	/**
	 * 构造方法
	 * @param serviceProperties {@link ServiceProperties},服务属性
	 */
	public DefaultExportServiceDefinition(ServiceProperties serviceProperties) {
		this.serviceProps = Objects.requireNonNull(
				serviceProperties, "\"serviceProperties\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ExportServiceDefinition#getCName()
	 */
	@Override
	public String getCName() {
		return this.serviceProps.getProperty(ServiceReference.CNAME, String.class);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ExportServiceDefinition#getServiceProperties()
	 */
	@Override
	public ServiceProperties getServiceProperties() {
		return this.serviceProps;
	}
}
