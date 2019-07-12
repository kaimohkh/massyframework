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

import java.util.Objects;

/**
 * 装配件的开发商
 *
 */
public final class Vendor {
	
	private long id;
	private String name;
	private String contact;
	
	/**
	 * 构造方法
	 * @param id {@link Long},编号
	 * @param name {@link String}, 名称
	 * @param contact {@link String},联系方式
	 */
	public Vendor(Long id, String name, String contact) {
		this.id = Objects.requireNonNull(id, "\"id\" cannot be null.").longValue();
		this.name = Objects.requireNonNull(name, "\"name\" cannot be null.");
		this.contact = contact;
	}

	/**
	 * 编号, 注册开发者后，由平台方提供具有唯一性的编号
	 * @return {@link long}
	 */
	public long getId() {
		return this.id;
	}
	
	/**
	 * 名称
	 * @return {@link String}
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * 联系方式
	 * @return {@link String}，可能为null.
	 */
	public String getContact() {
		return this.contact;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Vendor other = (Vendor) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Vender [id=" + id + ", name=" + name + "]";
	}
	
}
