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
 * @日   期:  2019年1月26日
 */
package com.massyframework.assembly.runtime.assembly.handling;

import java.util.ArrayList;
import java.util.List;

import com.massyframework.assembly.AssemblyConfig;

/**
 * 装配件处理器工厂
 *
 */
public final class AssemblyHandlerFactory {

	public static final String KERNEL_NAME = "www.massyframework.com";
	/**
	 * 创建缺省的处理器
	 * @param config {@link AssemblyConfig},装配件配置
	 * @param handlers {@link List},处理器
	 */
	public static List<Object> createDefaultHandler(AssemblyConfig config) {
		List<Object> result = new ArrayList<>();	
		if (!config.getDependencyServiceDefinitions().isEmpty()) {
			result.add(new DependencyServiceMatcher());
		}
		
		if (KERNEL_NAME.equals(config.getSymbolicName())) {
			result.add(new ExportServiceExporter());
		}else {
			if (!config.getExportServiceDefinitions().isEmpty()) {
				result.add(new ExportServiceExporter());
			}
		}
		return result;
	}
}
