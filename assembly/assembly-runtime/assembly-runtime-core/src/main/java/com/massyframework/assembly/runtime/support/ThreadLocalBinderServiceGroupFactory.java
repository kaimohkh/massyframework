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
 * @日   期:  2019年3月1日
 */
package com.massyframework.assembly.runtime.support;

import java.util.List;
import java.util.Objects;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.service.ServiceRepository;
import com.massyframework.lang.ThreadLocalBinder;
import com.massyframework.lang.ThreadLocalBinderFactory;
import com.massyframework.lang.support.ThreadLocalBinderGroup;


/**
 * 线程变量绑定服务组工厂
 */
public final class ThreadLocalBinderServiceGroupFactory implements ThreadLocalBinderFactory<ThreadLocalBinderGroup> {
	
	private ServiceRepository serviceRepo;

	/**
	 * 
	 */
	public ThreadLocalBinderServiceGroupFactory(Assembly assembly) {
		this.serviceRepo = ServiceRepository.retrieveFrom(
				Objects.requireNonNull(assembly, "\"assembly\" cannot be null."));	
	}

	/* (non-Javadoc)
	 * @see com.massyframework.lang.ThreadLocalBinderFactory#createThreadLocalBinder()
	 */
	@Override
	public ThreadLocalBinderGroup createThreadLocalBinder() {
		List<ThreadLocalBinder> binders =
			this.serviceRepo.getServices(ThreadLocalBinder.class);
		return new ThreadLocalBinderGroup(binders);
	}
	
	

}
