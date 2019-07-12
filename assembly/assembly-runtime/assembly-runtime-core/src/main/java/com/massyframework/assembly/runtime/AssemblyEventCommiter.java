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
 * @日   期:  2019年1月24日
 */
package com.massyframework.assembly.runtime;

import java.util.Objects;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyActivedEvent;
import com.massyframework.assembly.AssemblyListener;
import com.massyframework.assembly.AssemblyReadiedEvent;
import com.massyframework.assembly.AssemblyUnactivingEvent;
import com.massyframework.assembly.AssemblyUnreadingEvent;
import com.massyframework.assembly.runtime.assembly.handling.AssemblyEventPublisherHandler;
import com.massyframework.assembly.spi.HandlerBase;

/**
 * 装配件事件提交者
 *
 */
final class AssemblyEventCommiter extends HandlerBase implements AssemblyEventPublisherHandler {
	
	private final AssemblyAdmin assemblyAdmin;

	/**
	 * 构造方法
	 * @param assemblyAdmin {@link AssemblyAdmin}
	 */
	public AssemblyEventCommiter(AssemblyAdmin assemblyAdmin) {
		this.assemblyAdmin = Objects.requireNonNull(assemblyAdmin, "\"assemblyAdim\" cannot be null.");
	}
	
	

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.assembly.handling.AssemblyEventPublisherHandler#addListener(com.massyframework.assembly.AssemblyListener)
	 */
	@Override
	public void addListener(AssemblyListener listener) {
		this.assemblyAdmin.addListener(listener);		
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.assembly.handling.AssemblyEventPublisherHandler#postActivedEvent()
	 */
	@Override
	public void postActivedEvent() {
		Assembly assembly = this.getAssembly();
		this.assemblyAdmin.applyEvent(new AssemblyActivedEvent(assembly));		
	}
	 
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.assembly.handling.AssemblyEventPublisherHandler#sendUnactivingEvent()
	 */
	@Override
	public void sendUnactivingEvent() {
		Assembly assembly = this.getAssembly();
		this.assemblyAdmin.applyEvent(new AssemblyUnactivingEvent(assembly));		
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.assembly.handling.AssemblyEventPublisherHandler#postReadiedEvent()
	 */
	@Override
	public void postReadiedEvent() {
		Assembly assembly = this.getAssembly();
		this.assemblyAdmin.applyEvent(new AssemblyReadiedEvent(assembly));		
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.assembly.handling.AssemblyEventPublisherHandler#sendUnreadingEvent()
	 */
	@Override
	public void sendUnreadingEvent() {
		Assembly assembly = this.getAssembly();
		this.assemblyAdmin.applyEvent(new AssemblyUnreadingEvent(assembly));	
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.assembly.handling.AssemblyEventPublisherHandler#removeListener(com.massyframework.assembly.AssemblyListener)
	 */
	@Override
	public void removeListener(AssemblyListener listener) {
		this.assemblyAdmin.removeListener(listener);
	}
}
