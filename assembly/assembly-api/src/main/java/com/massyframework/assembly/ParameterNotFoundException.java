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
 * @日   期:  2019年1月18日
 */
package com.massyframework.assembly;

/**
 * 没有找到特定的初始化参数时抛出的例外
 */
public class ParameterNotFoundException extends AssemblyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2490388832410674702L;
	private final String name;

	/**
	 * 构造方法
	 * @param name 参数名称
	 */
	public ParameterNotFoundException(String name) {
		super("cannot found init parameter: paramName=\"" + name + "\".");
		this.name = name;
	}

	/**
	 * 参数名称
	 * @return {@link String}
	 */
	public String getParameterrName() {
		return this.name;
	}
}
