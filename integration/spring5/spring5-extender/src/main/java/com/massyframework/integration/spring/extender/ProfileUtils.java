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
 * @日   期:  2019年2月28日
 */
package com.massyframework.integration.spring.extender;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import com.massyframework.assembly.Assembly;

/**
 * Profile工具，将初始化参数中的profile注入到Environment中
 *
 */
public abstract class ProfileUtils {
	
	public static final String NAME = "spring.profiles.active";

	/**
	 * 设置profiles
	 * @param applicationContext {@link ConfigurableApplicationContext},可配置的应用程序上下文
	 * @param assembly {@link Assembly},装配件
	 */
	public static void setProfiles(
			ConfigurableApplicationContext applicationContext, Assembly assembly) {
		if (applicationContext == null) return;
		if (assembly == null) return;
		
		String profiles = assembly.getAssemblyConfig().getInitParameter(NAME);
		if (profiles != null) {
			ConfigurableEnvironment env = applicationContext.getEnvironment();
			env.setActiveProfiles(StringUtils.commaDelimitedListToStringArray(
					StringUtils.trimAllWhitespace(profiles)));
		}		
	}
}
