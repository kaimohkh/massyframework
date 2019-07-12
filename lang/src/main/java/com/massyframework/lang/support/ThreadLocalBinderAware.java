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

import com.massyframework.lang.ThreadLocalBinder;

/**
 * 提供{@link #setThreadLocalBinder(ThreadLocalBinder)}设置{@link ThreadLocalBinder}
 */
public interface ThreadLocalBinderAware {

	/**
	 * 设置<code>binder</code>
	 * @param binder {@link ThreadLocalBinder}, 线程变量绑定器
	 */
	void setThreadLocalBinder(ThreadLocalBinder binder);
	
	/**
	 * 尝试绑定<code>binder</code>
	 * @param target 目标对象
	 * @param binder {@link ThreadLocalBinder}, 线程变量绑定器
	 * @return {@link boolean},返回true绑定成功，否则返回<code>False</code>
	 */
	static <T> boolean maybeToBind(T target, ThreadLocalBinder binder) {
		if (target == null) return false;
		
		if (target instanceof ThreadLocalBinderAware) {
			((ThreadLocalBinderAware)target).setThreadLocalBinder(binder);
			return true;
		}
		return false;
	}
}
