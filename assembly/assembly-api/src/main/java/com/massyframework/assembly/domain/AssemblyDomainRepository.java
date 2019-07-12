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

import com.massyframework.assembly.ParseException;

/**
 * 装配域仓储
 */
public interface AssemblyDomainRepository {
	
	/**
	 * 路径
	 */
	static final String PATH = "META-INF/assembly/domain/";

	/**
	 * 从{@link #PATH}路径下，按<code>name</code>获取对应的装配域文件。
	 * 如果文件存在，则对文件进行解析，返回解析的{@link AssemblyDomain}
	 * @param name {@link String},域名
	 * @return {@link AssemblyDomain}
	 * @throws AssemblyDomainFileNotFoundException 没找到对应的域名文件时抛出的例外
	 * @throws SignatureVerificationFailedException 域名文件签名验证失败时抛出的例外
	 * @throws ParseException 配置文件解析失败时抛出的例外
	 */
	AssemblyDomain getDomain(String name) 
			throws AssemblyDomainFileNotFoundException, SignatureVerificationFailedException, ParseException;
	
}
