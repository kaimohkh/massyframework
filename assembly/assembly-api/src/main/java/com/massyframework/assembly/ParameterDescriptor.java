/**
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
 * Author: 黄开晖（117227773@qq.com）
 * Date:   2019年6月15日
 */
package com.massyframework.assembly;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * 参数描述器,用于说明参数的作用，使用方式
 * @author  Huangkaihui
 */
public final class ParameterDescriptor {
	
	private final String name;
	private final Set<String> keys;
	private final String description;
	private final String example;
	
	/**
	 * 构造方法
	 * @param name {@link String},参数名
	 * @param keys {@link Set},关键字
	 * @param description {@link String}, 说明
	 * @param example {@link String},样例
	 */
	public ParameterDescriptor(String name, Set<String> keys, String description, String example) {
		this.name = Objects.requireNonNull(name, "\"name\" cannot be null.");
		this.keys = keys == null ? Collections.emptySet(): keys;
		this.description = Objects.requireNonNull(description, "\"description\" cannot be nulll.");
		this.example = example;
	}

	/**
	 * 名称
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 关键字，用于搜索
	 * @return the keys
	 */
	public Set<String> getKeys() {
		return keys;
	}

	/**
	 * 说明
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 样例
	 * @return the example
	 */
	public String getExample() {
		return example;
	}

	@Override
	public String toString() {
		return "ParameterDescriptor [name=" + name + ", description=" + description + "]";
	}
}
