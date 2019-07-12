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
 * @日   期:  2019年3月31日
 */
package com.massyframework.assembly.domain;

/**
 * 装配域文件不存在时抛出的例外
 *
 */
public class AssemblyDomainFileNotFoundException extends AssemblyDomainException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5237466240956674601L;

	private final String name;
	
	/**
	 * 构造方法
	 * @param domain {@link String}
	 */
	public AssemblyDomainFileNotFoundException(String name) {
		super("domain file not exists: META-INF/domain/" + name + ".");
		this.name = name;
	}
	
	/**
	 * 装配域名称
	 * @return {@link String}
	 */
	public String getName() {
		return this.name;
	}

	
}
