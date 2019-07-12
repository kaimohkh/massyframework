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
 * 开发商开发的装配件必须归属于某个装配域，装配域作为装配件符号名称的前缀。<br>
 * 运行平台在加载装配件前，先要验证装配域的合法有效性，包括开发商提供数字证书的公钥。<br>
 * 装配域及数字证书的目的，是对装配件的资源或对客户授权进行签名。
 */
public interface AssemblyDomain {
	
	/**
	 * 装配域名称，中间用"."分隔，最多只有两级。
	 */
	String getName();
	
	/**
	 * 开发商
	 * @return {@link Vendor}
	 */
	Vendor getVendor();
	
	/**
	 * 开发商的公钥(BASE64编码)
	 * @return {@link String}
	 */
	String getPublicKey();
		
}
