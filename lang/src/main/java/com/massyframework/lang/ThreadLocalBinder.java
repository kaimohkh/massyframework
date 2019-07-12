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
 * 提供在两个线程之间同步特定线程变量的功能。<br>
 * 在被复制线程变量的线程中构建实现本接口的类实例，然后在另一个线程中调用{@link #bind()}方法，<br>
 * 从而将特定的线程变量从一个线程复制到另一个线程。<br>
 */
public interface ThreadLocalBinder {

	/**
	 * 绑定或者复制变量到当前线程.
	 */
	void bind();
}
