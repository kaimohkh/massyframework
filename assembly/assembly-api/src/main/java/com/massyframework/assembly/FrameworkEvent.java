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
 * @日   期:  2019年1月8日
 */
package com.massyframework.assembly;

import java.util.Objects;

/**
 * 运行框架事件，提供Framework上下文
 *
 */
public final class FrameworkEvent {

	private final Framework framework;
	
	/**
	 * 构造方法
	 * @param framework {@link framework},运行框架
	 */
	public FrameworkEvent(Framework framework) {
		this.framework = 
			Objects.requireNonNull(framework, "\"framework\" cannot be null.");
	}
	
	/**
	 * 运行框架
	 * @return {@link framework}
	 */
	public Framework getFramework() {
		return this.framework;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FrameworkEvent";
	}
}
