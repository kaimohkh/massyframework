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
 * @日   期:  2019年1月28日
 */
package com.massyframework.assembly.spi;

import com.massyframework.assembly.AssemblyException;

/**
 * 不支持的容器类型
 */
public class ContainerTypeNotSupportException extends AssemblyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2489218844791715872L;
	
	private final String type;

	/**
	 * 构造方法
	 * @param type {@link String},容器类型
	 */
	public ContainerTypeNotSupportException(String type) {
		super("not support container type:" + type + ".");
		this.type = type;
	}

	/**
	 * 容器类型
	 * @return {@link String}
	 */
	public String getType() {
		return this.type;
	}
}
