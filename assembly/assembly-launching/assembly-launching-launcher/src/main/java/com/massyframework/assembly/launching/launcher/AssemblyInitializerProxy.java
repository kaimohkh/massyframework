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
 * Date:   2019年6月22日
 */
package com.massyframework.assembly.launching.launcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.massyframework.assembly.launching.LaunchContext;
import com.massyframework.assembly.launching.PresetHandler;
import com.massyframework.logging.Logger;

/**
 * 装配件初始化代理
 * @author  Huangkaihui
 *
 */
public class AssemblyInitializerProxy implements PresetHandler {
	
	private final List<ClassLoader> classLoaders;
	
	/**
	 * 构造方法
	 */
	public AssemblyInitializerProxy(ClassLoader classLoader) {
		this.classLoaders = new ArrayList<>();
		this.classLoaders.add(classLoader);
	}

	/**
	 * 构造方法
	 */
	public AssemblyInitializerProxy(List<ClassLoader> classLoaders) {
		this.classLoaders = classLoaders == null ?
				Collections.emptyList() :
					new ArrayList<>(classLoaders);
	}

	@SuppressWarnings("exports")
	@Override
	public void init(LaunchContext context) throws Exception {
		if (this.classLoaders.isEmpty()) return;		
		Logger logger = context.getLogger();
		
		AssemblyScanner scanner = new AssemblyScanner();
		for (ClassLoader classLoader: classLoaders) {
			try {
				List<String> symbolicNames =
						scanner.scanAssembly(classLoader);
				
				for (String symbolicName: symbolicNames) {
					try {
						context.registerAssembly(symbolicName, classLoader);
					}catch(Throwable e) {
						if (logger.isErrorEnabled()) {
							logger.error("register assembly failed: symbolicName= " + symbolicName+ ".");
						}
					}
				}
			}catch(Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("scanner assembly failed: classLoader= " + classLoader + ".");
				}
			}
		}
	}

}
