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
 * Date:   2019年6月2日
 */
package com.massyframework.assembly.launching.launcher;

import java.util.Collections;
import java.util.List;

import com.massyframework.assembly.Framework;
import com.massyframework.assembly.FrameworkFactory;
import com.massyframework.assembly.launching.Initializer;
import com.massyframework.assembly.launching.Launcher;
import com.massyframework.assembly.launching.PresetHandler;
import com.massyframework.lang.support.OrderedComparator;

/**
   *   实现{@link Launcher}的缺省类
 * @author  Huangkaihui
 *
 */
public class DefaultLauncher implements Launcher {
	
	/**
	 * 构造方法
	 */
	public DefaultLauncher() {
	}

	@SuppressWarnings("exports")
	@Override
	public Framework launch() {
		return this.launch(null);
	}
	
	/**
	 * 启动运行框架
	 * @param previous {@link PreseHandler}
	 * @return {@link Framework}	
	 */
	@SuppressWarnings("exports")
	public Framework launch(PresetHandler previous) {			
		PresetHandlerCollection handler =
				this.createPresetHandlerCollection(previous);
		
		FrameworkFactory factory =
				this.createFrameworkFactory(handler);
		return factory.createFramework(handler);
	}
	
	/**
	   *   加载所有的预置处理器,并创建预置处理器集
	 * @return {@link PresetHandlerCollection}
	 */
	protected PresetHandlerCollection createPresetHandlerCollection(PresetHandler previous) {
		List<PresetHandler> handlers =
				ServiceLoaderUtils.loadServices(PresetHandler.class);
		Collections.sort(handlers, new OrderedComparator<PresetHandler>());
		if (previous != null) {
			handlers.add(0, previous);
		}
		handlers.add(
				new AssemblyInitializerProxy(
						Thread.currentThread().getContextClassLoader()));
		return new PresetHandlerCollection(handlers);
	}
	
	protected List<Initializer> loadInitializers(){
		List<Initializer> result = 
				ServiceLoaderUtils.loadServices(
						Initializer.class, this.getClass().getClassLoader());
		return result;
	}
	
	/**
	   *  创建FrameworkFactory
	 * @param classLoader {@link ClassLoader},提供FrameworkFactory的类加载器
	 * @return {@link FrameworkFactory},可能返回null.
	 */
	protected FrameworkFactory createFrameworkFactory(PresetHandlerCollection handler) {
		FrameworkFactory result = handler.getFrameworkFactory();
		if (result == null) {
			throw new RuntimeException("cannot found FrameworkFactory, please set it.");
		}
		return result;
	}
	
	
}
