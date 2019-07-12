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
 * @日   期:  2019年1月16日
 */
package com.massyframework.assembly;

/**
 * 装配件事件监听器,可监听激活(进入工作状态)/取消激活(退出工作状态)事件。
 *
 */
public interface AssemblyListener {

	/**
	 * 装配件状态发生改变时，触发事件处理
	 * @param event {@link AssemblyEvent}
	 */
	void onChanged(AssemblyEvent event);
}
