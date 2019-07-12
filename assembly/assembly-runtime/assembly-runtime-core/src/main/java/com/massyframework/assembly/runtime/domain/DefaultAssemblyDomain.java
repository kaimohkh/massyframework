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
package com.massyframework.assembly.runtime.domain;

import java.util.Objects;

import com.massyframework.assembly.crypto.SignatureInformation;
import com.massyframework.assembly.domain.AssemblyDomain;
import com.massyframework.assembly.domain.Vendor;
import com.massyframework.assembly.util.AssemblyUtils;

/**
 * 装配域
 */
public final class DefaultAssemblyDomain implements AssemblyDomain, SignatureInformation {
	
	private final String name;
	private final Vendor vendor;
	private final String publicKey;
	private final String signedData;
	
	/**
	 * 构造方法
	 */
	protected DefaultAssemblyDomain(AssemblyDomainBuilder builder) {
		this.name = Objects.requireNonNull(builder.getName(), "\"name\" cannot be null.");
		this.vendor = Objects.requireNonNull(builder.getVendor(), "\"vendor\" cannot be null.");
		this.publicKey = Objects.requireNonNull(builder.getPublicKey(), "\"publicKey\" cannot be null.");
		this.signedData = Objects.requireNonNull(builder.getSignedData(), "\"signedData\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.domain.AssemblyDomain#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.domain.AssemblyDomain#getVendor()
	 */
	@Override
	public Vendor getVendor() {
		return this.vendor;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.domain.AssemblyDomain#getPublishKey()
	 */
	@Override
	public String getPublicKey() {
		return this.publicKey;
	}
	
	

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.crypto.SignatureInformation#getContent()
	 */
	@Override
	public String getContent() {
		return AssemblyUtils.getSignatureContext(
				this.getName(), 
				this.getVendor().getId(), 
				this.getPublicKey());
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.crypto.SignatureInformation#getSignedData()
	 */
	@Override
	public String getSignedData() {
		return this.signedData;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + getName().hashCode();
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultAssemblyDomain other = (DefaultAssemblyDomain) obj;
		if (getName() == other.getName()) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DefaultAssemblyDomain [name=" + getName() + "]";
	}
}
