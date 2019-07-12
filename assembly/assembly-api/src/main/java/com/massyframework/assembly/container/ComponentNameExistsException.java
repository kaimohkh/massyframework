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
 * @日   期:  2019年1月8日
 */
package com.massyframework.assembly.container;

/**
 * 添加组件时，组件名称已经存在抛出的例外
 *
 */
public class ComponentNameExistsException extends ComponentsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7167539948702531631L;

	private final String cName;
	
	/**
	 * 
	 */
	public ComponentNameExistsException(String cName) {
		super("Component name is exits, cName=\"" + cName + "\".");
		this.cName = cName;
	}

	/**
	 * 组件的名称
	 * @return {@link String}
	 */
	public String getCName() {
		return this.cName;
	}
}
