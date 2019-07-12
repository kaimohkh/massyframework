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
 * @日   期:  2019年1月6日
 */
package com.massyframework.lang.util;

import java.util.Objects;

import com.massyframework.lang.ThreadLocalBinderFactory;
import com.massyframework.lang.support.RunableWrapper;
import com.massyframework.lang.support.ThreadLocalBinderGroup;

/**
 * 线程变量工具类，提供线程变量绑定组的创建。
 */
public abstract class ThreadLocalUtils {

	private static volatile ThreadLocalBinderFactory<ThreadLocalBinderGroup> INSTANCE;
	
	/**
	 * 创建线程变量绑定器组
	 * @return {@link ThreadLocalBinderGroup}
	 */
	public static ThreadLocalBinderGroup createThreadLocalBinder() {
		Objects.requireNonNull(INSTANCE, "INSTANCE cannot be null, please bind it.");
		return INSTANCE.createThreadLocalBinder();
	}
	
	/**
	 * 根据<code>runnable</code>创建支持线程变量绑定的{@link RunableWrapper}
	 * @param runnable {@link Runnable}, 线程执行体
	 * @return {@link RunableWrapper}
	 */
	public static RunableWrapper createRunableWrapper(Runnable runnable) {
		return new RunableWrapper(runnable);
	}
	
	/**
	 * 绑定线程变量绑定器组工厂
	 * @param factory {@link ThreadLocalBinderFactory}，线程变量绑定器组工厂
	 */
	public static void bind(ThreadLocalBinderFactory<ThreadLocalBinderGroup> factory) {
		INSTANCE = factory;
	}
}
