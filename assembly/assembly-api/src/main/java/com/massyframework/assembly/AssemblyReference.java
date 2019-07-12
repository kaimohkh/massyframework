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
 * @日   期:  2019年2月27日
 */
package com.massyframework.assembly;

/**
 * 通过{@link #getAssembly()}提供装配件
 *
 */
public interface AssemblyReference {

	/**
	 * 装配件
	 * @return {@link Assembly},可能返回null.
	 */
	Assembly getAssembly();
	
	/**
	 * 从<code>target</code>中取回装配件实例
	 * @param target {@link Object}
	 * @return {@link Assembly},可能返回null.
	 */
	static Assembly retrieveFrom(Object target) {
		if (target == null) return null;
		if (target instanceof AssemblyReference) {
			return ((AssemblyReference)target).getAssembly();
		}
		
		return null;
	}
}