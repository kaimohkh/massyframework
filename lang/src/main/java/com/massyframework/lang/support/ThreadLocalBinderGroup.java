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
 * @日   期:  2019年1月6日
 */
package com.massyframework.lang.support;

import java.util.Collection;
import java.util.Objects;

import com.massyframework.lang.ThreadLocalBinder;

/**
 * 提供一组{@link ThreadLocalBinder}的封装.
 *
 */
public final class ThreadLocalBinderGroup implements ThreadLocalBinder {

	private final Collection<ThreadLocalBinder> binders;
	
	/**
	 * 构造方法
	 */
	public ThreadLocalBinderGroup(Collection<ThreadLocalBinder> binders) {
		this.binders = Objects.requireNonNull(binders, "\"binders\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see com.massyframework.lang.ThreadLocalBinder#bind()
	 */
	@Override
	public void bind() {
		this.binders.stream().forEach( value -> value.bind());
	}

}
