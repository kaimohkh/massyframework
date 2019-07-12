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
package com.massyframework.integration.spring.extender;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.context.ContextLoader;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyDefinition;

/**
 * 配置路径工具
 *
 */
public class ConfigLocationsUtils {
	
	private static String COLON = ":";
	
	/**
	 * 从<code>assembly</code>中解析配置文件的URL
	 * @param assembly {@link Assembly},装配件
	 * @return {@link Resource}数组
	 * @throws MalformedURLException 
	 */
	public static Resource[] parseConfigLocationsToArray(Assembly assembly) throws MalformedURLException{
		List<Resource> result = parseConfigLocations(assembly);
		return result.toArray(new Resource[result.size()]);
	}

	/**
	 * 从<code>assembly</code>中解析配置文件的URL
	 * @param assembly {@link Assembly},装配件
	 * @return {@link List},资源列表
	 * @throws MalformedURLException 
	 */
	public static List<Resource> parseConfigLocations(Assembly assembly) throws MalformedURLException {
		if (assembly == null) {
			return Collections.emptyList();
		}
		
		String text =
				assembly.getAssemblyConfig().getInitParameter(
						"container." + ContextLoader.CONFIG_LOCATION_PARAM);
		if (text == null) return Collections.emptyList();
		
		AssemblyDefinition definition = AssemblyDefinition.retrieveFrom(assembly);
		String[] arr = StringUtils.split(text, ",");
		List<Resource> result = new ArrayList<Resource>() ;
		for (String e: arr) {
			String location = StringUtils.trim(e);
			if (location.length()>0) {
				URL url = null;
				int index = StringUtils.indexOf(location, COLON);
				if (index == -1) {
					url = definition.getResource(location);
				}else {
					url = new URL(location);
				}
				result.add(new UrlResource(url));
			}
		}
		return result;
	}
}
