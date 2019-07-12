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
package com.massyframework.assembly.runtime;

import java.util.concurrent.ExecutorService;

/**
 * ExecutorService服务感知接口
 */
public interface ExecutorServiceAware {

	/**
	 * 设置执行服务线程池
	 * @param executor {@link ExecutorService},可能为null.
	 */
	void setExecutorService(ExecutorService executor);
	
	/**
	 * 尝试向<code>target</code>绑定<code>executor</code>
	 * @param target {@link T},目标对象
	 * @param executor {@link ExecutorService},执行服务线程池
	 * @return {@link boolean},如果<code>target</code>支持{@link ExecutorServiceAware}并调用{@link #setExecutorService()}方法后,返回<code>true</code>。
	 * 	否则返回<code>false</code>
	 */
	static <T> boolean maybeToBind(T target, ExecutorService executor) {
		if (target == null) return false;
		
		if (target instanceof ExecutorServiceAware) {
			((ExecutorServiceAware)target).setExecutorService(executor);
			return true;
		}
		
		return false;
	}
}
