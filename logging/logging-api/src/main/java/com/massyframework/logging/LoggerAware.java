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
 * @日   期:  2019年1月19日
 */
package com.massyframework.logging;

/**
 * 日志记录器感知接口
 *
 */
public interface LoggerAware {

	/**
	 * 设置日志记录器
	 * @param logger {@link Logger},可以为null.
	 */
	void setLogger(Logger logger);
	
	/**
	 * 尝试向<code>target</code>设置<code>logger</code>
	 * @param target {@link T},目标对象
	 * @param logger {@link Logger},日志记录器
	 * @return {@link boolean},设置成功返回<code>true</code>.否则返回<code>false</code>
	 */
	static <T> boolean maybeToBind(T target, Logger logger) {
		if (target == null) return false;
		
		if (target instanceof LoggerAware) {
			((LoggerAware)target).setLogger(logger);
			return true;
		}
		
		return false;
	}
}
