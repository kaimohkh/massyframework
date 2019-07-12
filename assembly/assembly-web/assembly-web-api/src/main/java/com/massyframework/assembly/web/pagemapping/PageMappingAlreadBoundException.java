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
 * 页面映射已经绑定时抛出的例外
 */
public class PageMappingAlreadBoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7873807245641171409L;

	private final String uri;
	private final PageMappingEntry entry;
	
	/**
	 * @param message
	 */
	public PageMappingAlreadBoundException(String uri, PageMappingEntry entry) {
		super("PageMappingEntry alread bound: uri=" + uri + ", entry=" + entry + ".");
		this.uri = uri;
		this.entry = entry;
	}

	/**
	 * 映射的路径
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * 页面映射记录
	 * @return the entry
	 */
	public PageMappingEntry getPageMappingEntry() {
		return entry;
	}

	

}
