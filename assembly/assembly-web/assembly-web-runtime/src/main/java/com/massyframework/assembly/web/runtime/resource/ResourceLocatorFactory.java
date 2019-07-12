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
 * @日   期:  2019年1月27日
 */
package com.massyframework.assembly.web.runtime.resource;

/**
 * ResourceLocator工厂
 */
public final class ResourceLocatorFactory {

	/**
	 * 根据<code>assembly</code>创建{@link ResourceLocator}
	 * @param classLoader {@link ClassLoader},类加载器
	 * @return {@link ResourceLocator},资源定位器
	 */
	public static ResourceLocator createResoruceLocator(ClassLoader classLoader) {
		return new ClassLoaderResourceLocator(classLoader);
	}
}
