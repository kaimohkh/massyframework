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
 * 装配域域文件签名验证失败时抛出的例外
 */
public class SignatureVerificationFailedException extends AssemblyDomainException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7197353072703348284L;

	private String name;
	
	/**
	 * 构造方法
	 * @param domain {@link String},域名
	 */
	public SignatureVerificationFailedException(String name) {
		super("domain file is unauthorized: META-INF/domains/" + name + ".");
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
