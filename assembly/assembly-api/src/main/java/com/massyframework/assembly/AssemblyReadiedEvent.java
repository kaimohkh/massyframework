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
 * @日   期:  2019年2月21日
 */
package com.massyframework.assembly;

/**
 *装配件就绪后发出的事件
 *
 */
public final class AssemblyReadiedEvent extends AssemblyEvent {

	/**
	 * 构造方法
	 * @param assembly {@link Assembly},装配件
	 */
	public AssemblyReadiedEvent(Assembly assembly) {
		super(assembly);
	}

}
