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
 * @日   期:  2019年1月7日
 */
package com.massyframework.assembly;

import java.util.List;
import java.util.Set;

import com.massyframework.assembly.domain.Vendor;
import com.massyframework.assembly.initparam.InitParameters;
import com.massyframework.assembly.service.DependencyServiceDefinition;
import com.massyframework.assembly.service.ExportServiceDefinition;

/**
 * 装配件配置
 */
public interface AssemblyConfig extends InitParameters{
	
	/**
	 * 客户编号,由注册登记时由平台分配
	 */
	static final String CUSTOMER_ID   = "customer.id";
	
	/**
	 * 客户名称，在注册登记时资料
	 */
	static final String CUSTOMER_NAME = "customer.name";
	
	/**
	 * AssemblyInitializer类名
	 */
	static final String ASSEMBLY_INITIALIZER_CLASSNAME = "assembly.initializer";
	
	/**
	 * 是否存在指定<code>tag</code><br>
	 * 标签用来识别装配件所具有的某种特性。
	 * @param tag 标签
	 * @return <code>true</code>存在,<code>false</code>不存在
	 */
	boolean containsTag(String tag);
		
	/**
	 * 容器, 可能为null
	 * @return {@link String}
	 */
	String getContainerName();
	
	/**
	 * 附加处理器的类名.<br>
	 * 容器在进入工作状态，会自动创建附加处理器实例，并调用{@link AssemblyContextAware#setAssemblyContext(AssemblyContext)}方法；
	 * 在退出工作状态时，同样会调用{@link AssemblyContextAware#setAssemblyContext(AssemblyContext),同时释放附加处理器实例
	 * @return {@lnk List}
	 */
	List<String> getAttachHandlerClassNames();
	
	/**
	 * 所有依赖服务定义
	 * @return {@link DependencyServiceDefinition}列表
	 */
	List<DependencyServiceDefinition<?>> getDependencyServiceDefinitions();
		
	/**
	 * 所有的输出服务定义
	 * @return {@link ExportServiceDefinition}列表
	 */
	List<ExportServiceDefinition> getExportServiceDefinitions();
		
	/**
	 * 介绍说明
	 * @return {@link String}字符串，可能返回null.
	 */
	String getDescription();
			
	/**
	 * 装配件的名称
	 * @return {@link String}字符串，可以返回null.
	 */
	String getName();
	
	/**
	 * 所有的参数描述器
	 * @return {@link List}, 参数描述器列表
	 */
	List<ParameterDescriptor> getParameterDescriptors();
		
	/**
	 * 符号名称，由系统根据开发商组织和名称拼接而成，具有唯一性。<br>
	 * 
	 * <pre>
	 * 例如: {@link Vendor#getOrganization()} = "com.massy"
	 *      {@link #getName()} = "user.account";
	 *      则符号名称为: "com.massy.user.account";
	 * </pre>
	 * @return {@link String}
	 */
	String getSymbolicName();
		
	/**
	 * 标签集
	 * @return {@link Set}
	 */
	Set<String> getTags();
	
	/**
	 * 开发商
	 * @return {@link Vendor}， 可能返回null.
	 */
	Vendor getVendor();
}
