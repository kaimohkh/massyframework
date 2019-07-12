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
 * 适配对象工厂，提供{@link #getAdapter(Adaptable)}获取适配对象实例
 * @author  Huangkaihui
 *
 */
public interface AdapterFactory<H extends Adaptable> {

	
	/**
	 * 根据<code>host</code>创建
	 * @param host {@link H},宿主
	 * @param adaptType {@link Class},适配类型 
	 * @return {@link A},不支持<code>adaptTyp</code>直接返回null.
	 */
	<A> A getAdapter(H host, Class<A> adaptType);
}
