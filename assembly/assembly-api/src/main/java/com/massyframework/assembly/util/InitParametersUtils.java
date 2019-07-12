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
 * @日   期:  2019年1月28日
 */
package com.massyframework.assembly.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.massyframework.assembly.initparam.InitParameters;

/**
 * 初始化参数工具类
 *
 */
public final class InitParametersUtils {
	
	private static final String DOT = ".";

	/**
	 * 根据<code>prefix</code>获取<code>initParams</code>的子集。<br>
	 * prefix应该以"."作为结尾，如果没有".",方法执行时会自动补全。<br>
	 * 返回的子集的参数名，应该全部都截取了前缀部分，例子,初始化参数如下:
	 * <pre>
	 * container.type=servlet
	 * container.servlet.init.name=dispatcherServlet
	 * container.serviet.init.contextConfigLocation=classpath:META-INF/application.xml
	 * 
	 * 通过subset(initParams, "container.servlet.init"）获取的子集，其内容如下：
	 * name=dispatcherServlet
	 * contextConfigLocation=classpath:META-INF/application.xml
	 * </pre>
	 * 
	 * @param initParams {@link InitParameters},初始化化参数
	 * @param prefix {@link String},前缀，以满足前缀作为依据构建初始化参数子集。
	 * @return {@link InitParameters}
	 */
	public static InitParameters subset(InitParameters initParams, String prefix) {
		if (initParams == null) return new ImmutableInitParams(Collections.emptyMap());
		String path = StringUtils.endsWith(prefix, DOT) ?
				prefix : prefix.concat(DOT);
		
		Map<String, String> map = new HashMap<String, String>();
		
		try {
			List<String> names = initParams.getInitParameterNames();
			names = names.stream()
						.filter( name -> StringUtils.startsWith(name, path))
						.collect(Collectors.toList());
			if (names.isEmpty()) {
				return new ImmutableInitParams(Collections.emptyMap());
			}
			
			int length = path.length();	
			for (String name: names) {
				String trimName = StringUtils.substring(name, length);
				String value = initParams.getInitParameter(name);
				
				map.put(trimName, value);
			}
		}catch(Throwable e) {
			e.printStackTrace();
		}
		
		return new ImmutableInitParams(map);
	}
}
