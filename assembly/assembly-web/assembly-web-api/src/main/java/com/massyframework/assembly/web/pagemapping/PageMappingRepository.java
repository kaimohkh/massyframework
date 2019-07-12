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
 * @日   期:  2019年2月23日
 */
package com.massyframework.assembly.web.pagemapping;

/**
 * 页面映射仓储
 */
public interface PageMappingRepository {
	
	/**
	 * 页面映射的前缀
	 */
	static final String PAGEMAPPING_PREFIX = "pageMapping";

	/**
	 * 按<code>requestURI</code>查找页面映射记录 
	 * @param requestURI {@link String}, 请求的URI
	 * @return {@link PageMappingEntry},页面映射记录，未找到匹配记录则返回null.
	 */
	PageMappingEntry findPageMappingEntry(String requestURI);
	
	/**
	 * 注册页面映射
	 * @param uri {@link String},映射路径
	 * @param entry {@link PageMappingEntry}, 页面映射记录
	 * @return {@link PageMappingRegistration}
	 * @throws PageMappingAlreadBoundException 如果映射路径已经被绑定，则抛出例外
	 */
	PageMappingRegistration register(String uri, PageMappingEntry entry) throws PageMappingAlreadBoundException;
}
