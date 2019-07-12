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

import java.util.Objects;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.Framework;
import com.massyframework.assembly.FrameworkEventAdapter;
import com.massyframework.assembly.web.pagemapping.PageMappingRepository;

/**
 * 在Framework启停之间，注册和注销页面映射发现器
 */
public class PageMappingFounderRegister extends FrameworkEventAdapter {
	
	private final PageMappingRepository repository;
	private PageMappingFounder founder;

	/**
	 * 构造方法
	 * @param repository {@link PageMappingRepository}
	 */
	public PageMappingFounderRegister(PageMappingRepository repository) {
		this.repository = 
			Objects.requireNonNull(repository, "\"repository\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.FrameworkEventAdapter#doStarting(com.massyframework.assembly.Framework)
	 */
	@Override
	protected synchronized void doStarting(Framework framework) {
		super.doStarting(framework);
		if (this.founder == null) {			
			this.founder = new PageMappingFounder(repository);
			framework.getAssemblyRepository().getAssembly(0).addListener(this.founder);			
		}
	}
	
	

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.FrameworkEventAdapter#doStopped(com.massyframework.assembly.Framework)
	 */
	@Override
	protected synchronized void doStopped(Framework framework) {
		if (this.founder != null) {
			Assembly core= framework.getAssemblyRepository().getAssembly(0);
			core.removeListener(this.founder);
			this.founder = null;
		}
		super.doStopped(framework);
	}
	
}
