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
 * @日   期:  2019年1月8日
 */
package com.massyframework.assembly.initparam;

import java.util.List;

/**
 * 初始化参数集
 */
public interface InitParameters {
	
	/**
	 * 根据目前的参数，创建变量替换器
	 * @return {@link VariableReplacer}
	 */
	VariableReplacer createVariableReplacer();

	/**
	 * 按<code>name</code>获取对应的参数值
	 * @param name {@link String},参数名
	 * @return {@link String},如果参数不存在，可以返回null.
	 */
	String getInitParameter(String name);
	
	/**
	 * 按<code>name</code>获取对应的参数值
	 * @param name {@link String},参数名
	 * @param defaultValue {@link String},缺省值
	 * @return {@link String},如果参数不存在则返回缺省值
	 */
	default String getInitParameter(String name, String defaultValue) {
		String result = this.getInitParameter(name);
		return result == null ? defaultValue : result;
	}
		
	/**
	 * 所有初始化参数名称
	 * @return {@link List}
	 */
	List<String>  getInitParameterNames();	
}
