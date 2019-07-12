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
 * 再次注册具有相同符号名称的装配件时抛出的例外
 */
public class SymbolicNameExistsException extends AssemblyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4502609357166894309L;
	private final String symbolicName;
	
	/**
	 * 构造方法
	 * @param symbolicName 符号名称
	 */
	public SymbolicNameExistsException(String symbolicName) {
		super("symbolic name is used: symbolicName=" + symbolicName + ".");
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
