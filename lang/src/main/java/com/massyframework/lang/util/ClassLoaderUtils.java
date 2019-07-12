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
 * @日   期:  2019年1月22日
 */
package com.massyframework.lang.util;

import java.util.Objects;

/**
   * 类加载器工具类
 *
 */
public abstract class ClassLoaderUtils {
	
	private static final char PACKAGE_SEPARATOR = '.';
	private static final char PATH_SEPARATOR = '/';
	
	/**
	 * 获取缺省的类加载器
	 * @return {@link ClassLoader}
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		}catch (Throwable ex) {
		}
		if (cl == null) {
			cl = ClassLoaderUtils.class.getClassLoader();
			if (cl == null) {
				try {
					cl = ClassLoader.getSystemClassLoader();
				}catch (Throwable ex) {
				}
			}
		}
		return cl;
	}
	
	/**
	 * 设置线程上下文类加载器，并返回线程上下文加载器前值
	 * @param classLoader {@link ClassLoader},类加载器
	 * @return {@link ClassLoader},线程上下文类加载器前值
	 */
	public static ClassLoader setThreadContextClassLoader(ClassLoader classLoader) {
		Objects.requireNonNull(classLoader, "\"classLoader\" cannot be null.");
		ClassLoader result = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		return result;
	}
	
	/**
	 * 将<code>clazz</code>转换为资源路径
	 * @param clazz {@link Class}，类名
	 * @return {@link String}
	 */
	public static String classPackageAsResourcePath(Class<?> clazz) {
		if (clazz == null) {
			return "";
		}
		String className = clazz.getName();
		int packageEndIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		if (packageEndIndex == -1) {
			return "";
		}
		String packageName = className.substring(0, packageEndIndex);
		return packageName.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
	}
	
}
