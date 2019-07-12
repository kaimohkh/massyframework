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
 * @日   期:  2019年2月28日
 */
package com.massyframework.assembly;

/**
 * 装配件感知器
 *
 */
public interface AssemblyAware {

	/**
	 * 设置<code>assembly</code>
	 * @param assembly {@link Assembly},为null表示清除绑定.
	 */
	void setAssembly(Assembly assembly);
	
	/**
	 * 尝试向<code>target</code>绑定装配件<br>
	 * 如果<code>target</code>支持{@link AssemblyAware}接口，则调用{@link #setAssembly(Assembly)}绑定装配件.
	 * @param target  {@link T},目标对象
	 * @param assembly {@link Assembly},为null表示清除绑定.
	 * @return {@link boolean},返回<code>true</code>表示执行调用，否则返回<code>false</code>
	 */
	static <T> boolean maybeToBind(T target, Assembly assembly) {
		if (target == null) return false;
		if (target instanceof AssemblyAware) {
			((AssemblyAware)target).setAssembly(assembly);
			return true;
		}
		
		return false;
	}
}
