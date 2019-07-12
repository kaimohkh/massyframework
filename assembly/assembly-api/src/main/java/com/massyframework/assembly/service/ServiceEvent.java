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
 * @日   期:  2019年1月13日
 */
package com.massyframework.assembly.service;

import java.util.Objects;

/**
 * 服务事件
 *
 */
public abstract class ServiceEvent<S> {

	private final ServiceReference<S> reference;

	/**
	 * 构造方法
	 * @param reference 服务引用
	 */
	public ServiceEvent(ServiceReference<S> reference) {
		this.reference = Objects.requireNonNull(reference, "reference cannot be null.");
	}
		
	/**
	 * 服务引用
	 * @return
	 */
	public ServiceReference<S> getServiceReference(){
		return this.reference;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [reference=" + reference + "]";
	}

}
