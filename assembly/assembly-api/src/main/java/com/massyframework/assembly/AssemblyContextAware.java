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
 * @日   期:  2019年1月24日
 */
package com.massyframework.assembly;

/**
 * 装配件上下文感知器
 *
 */
public interface AssemblyContextAware {

	/**
	 * 设置<code>assembly</code>
	 * @param context {@link AssemblyContext},装配件上下文
	 */
	void setAssemblyContext(AssemblyContext context);
	
	/**
	 * 尝试向<code>target</code>绑定<code>context</code>
	 * @param target {@link T},目标对象
	 * @param context {@link AssemblyContext},装配件上下文
	 * @return {@link boolean},返回<code>true</code>表示执行绑定，否则返回<code>false</code>
	 */
	public static <T> boolean maybeToBind(T target, AssemblyContext context) {
		if (target == null) return false;
		
		if (target instanceof AssemblyContextAware) {
			((AssemblyContextAware)target).setAssemblyContext(context);
			return true;
		}
		
		return false;
	}
}
