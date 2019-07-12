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
 * @日   期:  2019年4月6日
 */
package com.massyframework.assembly;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.massyframework.assembly.domain.AssemblyDomain;
import com.massyframework.assembly.domain.AssemblyDomainRepository;

/**
 * 符号名称，具有唯一性。<br>
 * 符号名称采用"."分隔多个字符串，例如： com.massyframework.assembly.core，其中：
 * <ul>
 * <li>装配域<br>
 * 由第1和第2小节构成， 如上所示的com.massyframework。
 * 装配域由开发商向平台提出申请取得，由平台保证不会将装配域分配给两个不同的开发商。
 * </li>
 * <li>域内值</li>
 * 从第3小节开始，属于域内的值，由装配域的持有者保证，其名称不重复
 * </ul>
 *
 */
public final class  SymbolicName {

	public static final String DOT = ".";
	
	private final AssemblyDomain domain;
	private final String name;
	
	/**
	 * 构造方法
	 * @param value {@link String},符号名称的值
	 */
	public SymbolicName(String value, AssemblyDomainRepository repository) {
		Objects.requireNonNull(repository, "\"repository\" cannot be null.");
		String text = StringUtils.deleteWhitespace(value);
		String arr[] = StringUtils.split(text, DOT);
		int size = arr.length;
		if (size < 3) {
			throw new IllegalArgumentException("invalid symbolicName.");
		}
		
		String domain = StringUtils.trim(arr[0]).concat(DOT).concat(StringUtils.trim(arr[1]));
		this.domain = repository.getDomain(domain);
		StringBuilder builder = new StringBuilder();
		for (int i=2; i<size; i++) {
			builder.append(StringUtils.trim(arr[i]));
			if (i != size -1) {
				builder.append(DOT);
			}
		}
		this.name = builder.toString();
	}
	
	/**
	 * 符号名称所属的装配域
	 * @param repository {@link AssemblyDomainRepository}
	 * @return {@link AssemblyDomain}
	 */
	public AssemblyDomain getDomain() {
		return this.domain;
	}
	
	/**
	 * 符号名称的名
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * 唯一值
	 * @return {@link String}
	 */
	public String toIdentifier() {
		return this.domain.getName().concat(DOT).concat(this.name);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + this.toIdentifier().hashCode();
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
		SymbolicName other = (SymbolicName) obj;
		return this.toIdentifier().equals(other.toIdentifier());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SymbolicName [identifier=" + toIdentifier()  + "]";
	}
	
}
