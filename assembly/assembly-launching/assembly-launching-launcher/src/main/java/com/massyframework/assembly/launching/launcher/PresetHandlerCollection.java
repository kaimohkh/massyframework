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
 * Date:   2019年6月8日
 */
package com.massyframework.assembly.launching.launcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import com.massyframework.assembly.FrameworkFactory;
import com.massyframework.assembly.launching.LaunchContext;
import com.massyframework.assembly.launching.PresetHandler;
import com.massyframework.logging.Logger;

/**
   *   配置参数预处理集合
 * @author  Huangkaihui
 *
 */
public final class PresetHandlerCollection implements PresetHandler {
	
	private List<PresetHandler> handlers;

	/**
	 * @param previous
	 */
	@SuppressWarnings("exports")
	public PresetHandlerCollection(List<PresetHandler> handlers) {
		this.handlers = new ArrayList<PresetHandler>();
		this.handlers.add(new ConfigurationHandler());
		this.handlers.addAll(handlers);
	}

	@SuppressWarnings("exports")
	@Override
	public void init(LaunchContext context) throws Exception {
		Logger logger = context.getLogger();
		StringBuilder builder = new StringBuilder();
		
		for (PresetHandler handler: this.handlers) {
			try {
				handler.init(context);
				
				if (logger.isTraceEnabled()) {
					if (builder.length() != 0) {
						builder.setLength(0);
					}
					builder.append("PresetHandler[")
						.append(handler.getClass().getName())
						.append("]")
						.append(" inited.");
					logger.trace(builder.toString());
				}
			}catch(Throwable e) {
				if (logger.isErrorEnabled()) {
					if (builder.length() != 0) {
						builder.setLength(0);
					}
					builder.append("PresetHandler[")
						.append(handler.getClass().getName())
						.append("]")
						.append(" init failed.");
					
					logger.error(builder.toString(), e);
				}
			}
		}
	}

	/**
	   *   运行框架
	 * @return {@link FrameworkFactory},可能返回null.
	 */
	@SuppressWarnings("exports")
	public FrameworkFactory getFrameworkFactory() {
		int size = this.handlers.size();
		for (int i=size-1; i>0; i--) {
			PresetHandler handler = this.handlers.get(i);
			if (handler instanceof FrameworkFactory) {
				return FrameworkFactory.class.cast(handler);
			}
		}
		
		//如果没有，则使用本地ClassLoader查找FrameworkFactory
		ServiceLoader<FrameworkFactory> sl =
				ServiceLoader.load(FrameworkFactory.class, this.getClass().getClassLoader());
		Iterator<FrameworkFactory> it = sl.iterator();
		return it.hasNext()? it.next() : null;
	}

}
