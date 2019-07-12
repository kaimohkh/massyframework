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
package com.massyframework.lang;

/**
 * {@link ThreadLocalBinder}工厂，提供创建{@link ThreadLocalBinder}的方法。
 *
 */
public interface ThreadLocalBinderFactory<T extends ThreadLocalBinder> {

	/**
	 * 创建{@link ThreadLocalBinder}实例，在创建实例时，复制拟传递到其他线程的线程变量
	 * @return {@link T}
	 */
	T createThreadLocalBinder();
}
