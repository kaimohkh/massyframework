/**
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
 * Author: 黄开晖（117227773@qq.com）
 * Date:   2019年6月6日
 */
package com.massyframework.lang;

/**
 * 支持适配的 能力，
 * @author  Huangkaihui
 *
 */
public interface Adaptable {

	/**
	 * 根据<code>adaptType</code>获取可适配的对象
	 * @param <T> {@link T},泛型
	 * @param adaptType {@link Class},适配类型
	 * @return {@link T},适配对象实例，如果不支持该适配可直接返回null.
	 */
	<T> T getAdapter(Class<T> adaptType);
}
