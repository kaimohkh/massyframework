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
 * @日   期:  2019年2月23日
 */
package com.massyframework.assembly.web.runtime.pagemapping;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.massyframework.assembly.web.pagemapping.PageMappingAlreadBoundException;
import com.massyframework.assembly.web.pagemapping.PageMappingEntry;
import com.massyframework.assembly.web.pagemapping.PageMappingRegistration;
import com.massyframework.assembly.web.pagemapping.PageMappingRepository;

/**
 * 实现PageMappingRepository的缺省类
 */
public class DefaultPageMappingRepository implements PageMappingRepository {
	
	private Map<String, RegistrationImpl> registrationMap =
			new ConcurrentHashMap<String, RegistrationImpl>();
	
	/**
	 * 构造方法
	 */
	public DefaultPageMappingRepository() {
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.pagemapping.PageMappingRepository#findPageMappingEntry(java.lang.String)
	 */
	@Override
	public PageMappingEntry findPageMappingEntry(String requestURI) {
		RegistrationImpl registration = this.registrationMap.get(requestURI);
		return registration == null ? null : registration.getPageMappingEntry();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.pagemapping.PageMappingRepository#register(java.lang.String, com.massyframework.assembly.web.pagemapping.PageMappingEntry)
	 */
	@Override
	public PageMappingRegistration register(String uri, PageMappingEntry entry) throws PageMappingAlreadBoundException {
		RegistrationImpl result = new RegistrationImpl(uri, entry);
		if (this.registrationMap.putIfAbsent(uri, result) != null) {
			throw new PageMappingAlreadBoundException(uri, entry);
		};
		return result;
	}
	
	/**
	 * 撤销注册
	 * @param registration {@link RegistrationImpl}
	 */
	protected void doUnregister(RegistrationImpl registration) {
		if (registration != null) {
			this.registrationMap.remove(registration.getURI(), registration);
		}
	}

	/**
	 * 注册凭据实现类
	 */
	private class RegistrationImpl implements PageMappingRegistration {
		
		private String uri;
		private PageMappingEntry entry;
		
		public RegistrationImpl(String uri, PageMappingEntry entry) {
			this.uri = Objects.requireNonNull(uri, "\"uri\" cannot be null.");
			this.entry = Objects.requireNonNull(entry, "\"entry\" cannot be null.");
		}

		@Override
		public void unregister() {
			doUnregister(this);	
		}

		@Override
		public String getURI() {
			return this.uri;
		}

		@Override
		public PageMappingEntry getPageMappingEntry() {
			return this.entry;
		}
		
	}
}
