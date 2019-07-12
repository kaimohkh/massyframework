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
 * @日   期:  2019年4月2日
 */
package com.massyframework.assembly.runtime.domain;

import com.massyframework.assembly.domain.AssemblyDomain;
import com.massyframework.assembly.domain.Vendor;

/**
 *
 *
 */
public class AssemblyDomainBuilder {
	
	private String name;
	private Vendor vendor;
	private String publicKey;
	private String signedData;

	/**
	 * 
	 */
	public AssemblyDomainBuilder() {
		
	}
	
	/**
	 * 装配域名
	 * @return {@link String}
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * 开发商
	 * @return {@link Vendor}
	 */
	public Vendor getVendor() {
		return this.vendor;
	}
	
	/**
	 * 开发商的公钥
	 * @return {@link String}
	 */
	public String getPublicKey() {
		return this.publicKey;
	}
	
	/**
	 * 签名的数据
	 * @return {@link String}
	 */
	public String getSignedData() {
		return this.signedData;
	}
	
	/**
	 * 设置装配域名
	 * @param name {@link String}
	 * @return {@link AssemblyDomainBuilder}
	 */
	public AssemblyDomainBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	/**
	 * 设置开发商
	 * @param vendor {@link Vendor}
	 * @return {@link AssemblyDomainBuilder}
	 */
	public AssemblyDomainBuilder setVendor(Vendor vendor) {
		this.vendor = vendor;
		return this;
	}
	
	/**
	 * 设置开发商公钥
	 * @param publicKey {@link String}
	 * @return {@link AssemblyDomainBuilder}
	 */
	public AssemblyDomainBuilder setPublicKey(String publicKey) {
		this.publicKey = publicKey;
		return this;
	}
	
	/**
	 * 设置签名数据
	 * @param signedData {@link String}
	 * @return {@link AssemblyDomainBuilder}
	 */
	public AssemblyDomainBuilder setSignedData(String signedData) {
		this.signedData = signedData;
		return this;
	}
	
	public AssemblyDomain build() {
		return new DefaultAssemblyDomain(this);
	}

}
