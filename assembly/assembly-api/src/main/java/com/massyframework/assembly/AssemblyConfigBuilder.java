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
 * @日   期:  2019年3月1日
 */
package com.massyframework.assembly;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.massyframework.assembly.domain.AssemblyDomain;
import com.massyframework.assembly.initparam.InitParameters;
import com.massyframework.assembly.service.DependencyServiceDefinition;
import com.massyframework.assembly.service.ExportServiceDefinition;

/**
   * 装配件配置构建器
 */
public interface AssemblyConfigBuilder extends InitParameters{
	
	/**
	 * 添加附加处理器类名,附加处理器必须实现{@link AssemblyContextAware}接口，并提供无参的构造方法。
	 * @param className {@link String},附加处理器类名
	 * @return {@link AssemblyConfigBuilder}
	 */
	AssemblyConfigBuilder addAttachHandlerClassName(String className);

	/**
	 * 添加<code>DependencyServiceDefinition</code>
	 * @param definition {@link DependencyServiceDefinition}, 装配件所依赖外部服务的定义
	 * @return {@link AssemblyConfigBuilder}
	 */
	AssemblyConfigBuilder addDependencyServiceDefinition(DependencyServiceDefinition<?> definition);
	
	/**
	 * 添加<code>definition</code>
	 * @param definition {@link ExportServiceDefinition},装配件进入工作状态后，可输出的服务定义
	 * @return {@link AssemblyConfigBuilder}
	 */
	AssemblyConfigBuilder addExportServiceDefinition(ExportServiceDefinition definition);
	
	/**
	 * 添加初始化参数
	 * @param name {@link String},参数名
	 * @param value {@link String},参数值
	 * @return {@link AssemblyConfigBuilder}
	 */
	AssemblyConfigBuilder addInitParameter(String name, String value);
	
	/**
	 * 添加初始化参数Map
	 * @param initParams {@link Map},初始化参数
	 * @return {@link AssemblyConfigBuilder}
	 */
	AssemblyConfigBuilder addinitParameters(Map<String, String> initParams);
	
	/**
	 * 添加参数描述器
	 * @param descriptor {@link ParameterDescriptor},参数描述器
	 * @return {@link AssemblyConfigBuilder}
	 */
	AssemblyConfigBuilder addParameterDescriptor(ParameterDescriptor descriptor);
	
	/**
	 * 添加标签
	 * @param tag {@link String},标签
	 * @return {@link AssemblyConfigBuilder}
	 */
	AssemblyConfigBuilder addTag(String tag);
	
	/**
	 * 添加标签
	 * @param tags {@link String}数组
	 * @return {@link AssemblyConfigBuilder}
	 */
	AssemblyConfigBuilder addTags(String... tags);
	
	/**
	 * 构建装配件配置
	 * @return {@link AssemblyConfig}
	 */
	AssemblyConfig build();
		
	/**
	 * 附加处理器类名列表
	 * @return {@link List},类名列表
	 */
	List<String> getAttachClassNames();
	
	/**
	 * 装配件类加载器
	 * @return {@link ClassLoader}
	 */
	ClassLoader getAssemblyClassLoader();
	
	/**
	 * 装配件定义
	 * @return {@link AssemblyDefinition}
	 */
	AssemblyDefinition getAssemblyDefinition();
	
	/**
	 * 装配件所属域
	 * @return {@link AssemblyDomain}
	 */
	default AssemblyDomain getAssemblyDomain() {
		return this.getAssemblyDefinition().getAssemblyDomain();
	}
	
	/**
	 * 装配件标签集合
	 * @return {@link Set}
	 */
	Set<String> getAssemblyTags();
	
	/**
	 * 容器名称
	 * @return {@link String}
	 */
	String getContainerName();
	
	/**
	 * 依赖服务定义集合
	 * @return {@link List}，依赖服务定义集合
	 */
	List<DependencyServiceDefinition<?>> getDependencyServiceDefinitions();
	
	/**
	 * 装配件功能说明
	 * @return {@link String}
	 */
	String getDescription();
	
	
	/**
	 * 输出服务定义集合
	 * @return {@link List},输出服务定义集合
	 */
	List<ExportServiceDefinition> getExportServiceDefinitions();
		
	/**
	 * 装配件名称
	 * @return {@link String}
	 */
	String getName();
	
	/**
	 * 获取参数描述器列表
	 * @return {@link List}
	 */
	List<ParameterDescriptor> getParameterDescritors();
	
	/**
	 * 通过供应商域名和装配件名称组成的符号名称
	 * @return {@link SymbolicName}
	 */
	SymbolicName getSymbolicName();
				
	/**
	 * 设置容器名称
	 * @param containerName {@link String},容器名称
	 * @return {@link AssemblyConfigBuilder}
	 */
	AssemblyConfigBuilder setContainerName(String containerName);
	
	/**
	 * 设置装配件功能说明
	 * @param description {@link String},装配件功能说明
	 * @return {@link AssemblyConfigBuilder}
	 */
	AssemblyConfigBuilder setDescription(String description);
		
	/**
	 * 设置友好名称
	 * @param name {@link String},友好名称
	 * @return {@link AssemblyConfigBuilder}
	 */
	AssemblyConfigBuilder setName(String name);
}
