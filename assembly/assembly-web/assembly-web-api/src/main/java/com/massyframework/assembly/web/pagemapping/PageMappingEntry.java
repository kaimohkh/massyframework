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

import java.util.Objects;

/**
 * 页面映射记录
 */
public final class PageMappingEntry {
	
	private final String pagePath;
	private final boolean forward;
	
	/**
	 * 构造方法
	 * @param pagePath 页面的路径
	 */
	public PageMappingEntry(String pagePath) {
		this(pagePath, true);
	}

	/**
	 * 构造方法
	 * @param pagePath 页面的路径
	 * @param forward 是否前置导向
	 */
	public PageMappingEntry(String pagePath, boolean forward) {
		this.pagePath = Objects.requireNonNull(pagePath, "\"pagePath\" cannot be null.");
		this.forward = forward;
	}

	/**
	 * @return the pagePath
	 */
	public String getPagePath() {
		return pagePath;
	}

	/**
	 * @return the forward
	 */
	public boolean isForward() {
		return forward;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (forward ? 1231 : 1237);
		result = prime * result + ((pagePath == null) ? 0 : pagePath.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PageMappingEntry [pagePath=" + pagePath + ", forward=" + forward + "]";
	}	
}
