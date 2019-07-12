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
 * Date:   2019年6月8日
 */
package com.massyframework.io;

/**
 * {@link ResourceScanner}工厂，根据ClassLoader创建对应的扫描器
 * @author  Huangkaihui
 *
 */
public interface ResourceScannerFactory {

	/**
	 * 根据<code>classLoader</code>创建资源扫描器
	 * @param classLoader {@link ClassLoader},提供资源的类加载器
	 * @return {@link ResoruceScanner}
	 */
	ResourceScanner createResourceScanner(ClassLoader classLoader);
}
