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

import java.net.URL;
import java.util.Objects;

import com.massyframework.assembly.ParseException;
import com.massyframework.assembly.domain.AssemblyDomain;
import com.massyframework.assembly.domain.AssemblyDomainFileNotFoundException;
import com.massyframework.assembly.domain.AssemblyDomainRepository;
import com.massyframework.assembly.domain.SignatureVerificationFailedException;

/**
 * 缺省的装配域仓储
 */
public final class DefaultAssemblyDomainRepository 
	extends AssemblyDomainXmlParser
	implements AssemblyDomainRepository {
	
	private static final String PATH = "META-INF/domain/";
	private static final String SUFFIX = ".xml";
	
	private ClassLoader classLoader;
	
	/**
	 * 
	 */
	public DefaultAssemblyDomainRepository() {
		this.classLoader = AssemblyDomainRepository.class.getClassLoader();
	}
	
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.domain.AssemblyDomainRepository#getDomain(java.lang.String)
	 */
	@Override
	public AssemblyDomain getDomain(String name)
			throws AssemblyDomainFileNotFoundException, SignatureVerificationFailedException, ParseException{
		Objects.requireNonNull(name, "\"name\" cannot be null.");
		URL url = classLoader.getResource(PATH.concat(name).concat(SUFFIX));
		if (url == null) {
			throw new AssemblyDomainFileNotFoundException(name);
		}
		
		AssemblyDomain result =
			this.parseAssemblyDomain(url);
		return result;
	}
	
}
