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
	
/**
 * 服务加载工具类
 * @author  Huangkaihui
 *
 */
public abstract class ServiceLoaderUtils {
	
	/**
	 * 加载所有预置的处理器
	 * @param serviceType {@link Class}, 服务类型
	 * @return {@link List},
	 */
	public static <T> List<T> loadServices(Class<T> serviceType){
		return loadServices(serviceType, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * 加载服务
	 * @param serviceType {@link Class}, 服务类型
	 * @param classLoader {@link ClassLoader},类加载器
	 * @return {@link List},
	 */
	public static <T> List<T> loadServices(Class<T> serviceType, ClassLoader classLoader){
		ServiceLoader<T> sl = ServiceLoader.load(serviceType, classLoader);
		Iterator<T> it = sl.iterator();
		List<T> result = new ArrayList<>();
		while (it.hasNext()) {
			result.add(it.next());
		}
		
		return result;
	}
	

}
