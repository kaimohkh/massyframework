/**
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
 * Author: 黄开晖（117227773@qq.com）
 * Date:   2019年6月15日
 */
package com.massyframework.assembly.test;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.Framework;
import com.massyframework.assembly.launching.PresetHandler;
import com.massyframework.assembly.launching.launcher.DefaultLauncher;

/**
 * 运行框架工具
 * @author  Huangkaihui
 *
 */
public abstract class FrameworkUtils {
	
	private static Framework INSTANCE;
	
	/**
	 * 获取运行框架
	 * @return {@link Framework}
	 */
	@SuppressWarnings("exports")
	public static Framework getFramework() {
		if (INSTANCE == null) {
			createFramework(null);
		}
		
		return INSTANCE;
	}
	
	/**
	 * 根据<code>symoblicName</code>获取对应的装配件
	 * @param symbolicName {@link String},符号名称
	 * @return {@link Assembly}
	 */
	@SuppressWarnings("exports")
	public static Assembly getAssembly(String symbolicName) {
		return getFramework().getAssemblyRepository()
					.getAssembly(symbolicName);
	}
	
	/**
	 * 创建运行框架
	 * @param handler {@link PresetHandler}, 预置处理器
	 * @return {@link Framework}
	 */
	private synchronized static void createFramework(PresetHandler handler) {
		if (INSTANCE == null) {
			DefaultLauncher launcher = new DefaultLauncher();
			INSTANCE = handler == null ?
					launcher.launch() :
						launcher.launch(handler);
		}
	}

}
