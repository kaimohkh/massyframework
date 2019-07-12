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

import com.massyframework.lang.Registration;

/**
 * 页面映射注册凭据
 */
public interface PageMappingRegistration extends Registration {
	
	/**
	 * 映射的URI
	 * @return {@link String}
	 */
	String getURI();

	/**
	 * 页面映射记录
	 * @return {@link PageMappingEntry}
	 */
	PageMappingEntry getPageMappingEntry();
}
