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
 * @日   期:  2019年1月29日
 */
package com.massyframework.assembly.runtime.assembly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.massyframework.assembly.DependencyManager;
import com.massyframework.assembly.runtime.assembly.handling.DependencyServiceHandler;
import com.massyframework.assembly.service.DependencyServiceDefinition;
import com.massyframework.assembly.service.ServiceReference;

/**
 *
 *
 */
final class AssemblyDependencyManager implements DependencyManager {
	
	private final DependencyServiceHandler handler;

	/**
	 * 构造方法
	 * @param handler {@link DependencyServiceHandler},依赖服务处理器
	 */
	public AssemblyDependencyManager(DependencyServiceHandler handler) {
		this.handler = handler;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.DependencyManager#getDependencyServices()
	 */
	@Override
	public List<DependencyServiceDefinition<?>> getDependencyServices() {
		return this.handler == null ?
				Collections.emptyList() :
					this.handler.getDependencyServiceDefinitions();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.DependencyManager#getMatchedServiceReference(com.massyframework.assembly.service.DependencyServiceDefinition)
	 */
	@Override
	public <S> ServiceReference<S> getMatchedServiceReference(DependencyServiceDefinition<S> definition) {
		if (this.handler == null) return null;
		
		ServiceReference<S> result = this.handler.findMatchedServiceReference(definition);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.DependencyManager#getUnmatchDependencyServices()
	 */
	@Override
	public List<DependencyServiceDefinition<?>> getUnmatchDependencyServices() {
		return this.handler == null ?
				Collections.emptyList() :
					new ArrayList<>(this.handler.getUnmatchDependencyServiceDefinitions());
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.DependencyManager#allMatched()
	 */
	@Override
	public boolean allMatched() {
		return this.handler == null ? true : this.handler.isAllMatched();
	}

}
