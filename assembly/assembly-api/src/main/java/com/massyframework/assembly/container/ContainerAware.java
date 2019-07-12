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
package com.massyframework.assembly.container;

/**
 * 提供{@link #setContainer(Container)}用于设置容器实例
 */
public interface ContainerAware {

	/**
	 * 设置<code>container</code>
	 * @param container {@link Container},容器
	 */
	void setContainer(Container container);
	
	/**
	 * 尝试绑定<code>container</code>
	 * @param target 目标对象
	 * @param container {@link Container},容器
	 * @return {@link boolean},返回true绑定成功，否则返回<code>False</code>
	 */
	static <T> boolean maybeToBind(T target, Container container) {
		if (target == null) return false;
		
		if (target instanceof ContainerAware) {
			((ContainerAware)target).setContainer(container);
			return true;
		}
		return false;
	}
}
