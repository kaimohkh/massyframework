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
package com.massyframework.lang.support;

import java.util.Objects;

import com.massyframework.lang.util.ThreadLocalUtils;

/**
 * {@link Runnable}封装器，提供线程变量的绑定的支持
 */
public class RunableWrapper implements Runnable {
	
	private final Runnable runnable;
	private final ThreadLocalBinderGroup binder;
	
	/**
	 * 构造方法<br>
	 * 缺省使用{@link ThreadLocalUtils#createThreadLocalBinder()}创建线程变量绑定器组
	 * @param runnable {@link Runnable}.实现{@link Runnable}接口的实例
	 */
	public RunableWrapper(Runnable runnable) {
		this(runnable, ThreadLocalUtils.createThreadLocalBinder());
	}

	/**
	 * 构发方法
	 * @param runnalbe {@link Runnable},实现{@link Runnable}接口的实例
	 * @parma binder {@link ThreadLocalBinderGroup}, 线程变量绑定组 
	 */
	public RunableWrapper(Runnable runnable, ThreadLocalBinderGroup binder) {
		this.runnable = Objects.requireNonNull(runnable, "\"runnable\" cannot be null.");
		this.binder = Objects.requireNonNull(binder, "\"binder\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		//绑定线程变量
		this.binder.bind();
		this.runnable.run();
	}

}
