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
 * @日   期:  2019年1月15日
 */
package com.massyframework.assembly;

/**
 * 按符号名称未找到装配件时抛出的例外
 */
public final class AssemblyNotFoundException extends AssemblyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 996706565462528695L;
	private String symbolicName;

	/**
	 * 构造方法
	 * @param symbolicName {@link String},装配件符号名称
	 */
	public AssemblyNotFoundException(String symbolicName) {
		super("assembly cannot be found: symbolicName=" + symbolicName + ".");
		this.symbolicName = symbolicName;
	}

	/**
	 * 装配件符号名称
	 * @return {@link String}
	 */
	public String getSymbolicName(){
		return this.symbolicName;
	}

}
