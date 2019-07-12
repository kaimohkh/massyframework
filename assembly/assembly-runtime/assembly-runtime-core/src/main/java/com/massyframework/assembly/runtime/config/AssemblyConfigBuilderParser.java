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
 * @日   期:  2019年1月18日
 */
package com.massyframework.assembly.runtime.config;


import com.massyframework.assembly.AssemblyConfigBuilder;
import com.massyframework.assembly.AssemblyDefinition;
import com.massyframework.assembly.ParseException;
import com.massyframework.assembly.initparam.InitParameters;


/**
 * 装配件配置构建器解析
 */
public interface AssemblyConfigBuilderParser {

	/**
	 * 解析配置文件，并创建装配件配置
	 * @param definition {@link AssemblyDefinition},装配件定义
	 * @param frameworkInitParams {@link InitParameters}, 运行框架的初始化参数
	 * @return {@link AssemblyConfigBuilder},装配件配置构建器
	 * @throws ParseException 解析异常抛出的例外
	 */
	AssemblyConfigBuilder parse(AssemblyDefinition definition, InitParameters frameworkInitParams) throws ParseException;
	
}
