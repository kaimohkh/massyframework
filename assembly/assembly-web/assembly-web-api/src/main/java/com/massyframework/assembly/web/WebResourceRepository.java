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
 * @日   期:  2019年2月1日
 */
package com.massyframework.assembly.web;

/**
 * Web资源仓储, 管理存储在{@link #RESOURCES_PAHT}下的Web资源。
 */
public interface WebResourceRepository {

	static final String RESOURCES_PAHT = "/META-INF/resources";
	static final String PATH_SEPARATOR = "/";
	
	/**
	 * 按<code>resourceName</code>查找对应的{@link ClassLoader}
	 * @param resourceName {@link String},资源名称
	 * @return {@link ClassLoader},如未找到匹配的资源，可以返回null.
	 */
	ClassLoader findClassLoaderWithResource(String resourceName);
}
