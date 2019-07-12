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
 * @日   期:  2019年1月15日
 */
package com.massyframework.assembly.runtime;

import java.util.Objects;

import com.massyframework.assembly.AssemblyRepository;
import com.massyframework.assembly.Framework;
import com.massyframework.assembly.FrameworkStatus;
import com.massyframework.logging.Logger;

/**
 * 代理的运行框架
 */
public class DelegateFramework implements Framework {
	
	private final Framework framework;

	/**
	 * @param framework {@link Framework},运行框架
	 */
	DelegateFramework(Framework framework) {
		this.framework = Objects.requireNonNull(framework, "\"framework\" cannot be null.");
	}

	@Override
	public AssemblyRepository getAssemblyRepository() {
		return this.framework.getAssemblyRepository();
	}

	@Override
	public Logger getLogger() {
		return this.framework.getLogger();
	}

	@Override
	public FrameworkStatus getStatus() {
		return this.framework.getStatus();
	}

	@Override
	public void start() throws Exception {
		this.framework.start();
	}

	@Override
	public void stop() throws Exception {
		this.framework.stop();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DelegateFramework";
	}
	
}
