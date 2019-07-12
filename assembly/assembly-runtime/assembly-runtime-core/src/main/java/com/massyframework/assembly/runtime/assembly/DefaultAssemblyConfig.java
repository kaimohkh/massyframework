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
 * @日   期:  2019年2月2日
 */
package com.massyframework.assembly.runtime.assembly;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.massyframework.assembly.AssemblyConfig;
import com.massyframework.assembly.AssemblyConfigBuilder;
import com.massyframework.assembly.ParameterDescriptor;
import com.massyframework.assembly.domain.Vendor;
import com.massyframework.assembly.initparam.VariableReplacer;
import com.massyframework.assembly.service.DependencyServiceDefinition;
import com.massyframework.assembly.service.ExportServiceDefinition;

/**
 * 提供基于原生代码封装的装配件配置<br>
 * 为确保装配件生产商的利益，对于初始化参数使用原生代码封装
 */
public final class DefaultAssemblyConfig implements AssemblyConfig {
	
	private static final String DOT = ".";

	private final String symbolicName;
	private final String name;
	private final String description;
	private final Vendor vendor;
	private final String containerName;
	private final Set<String> tags;
	private final List<String> attachClassNames;
	private final List<DependencyServiceDefinition<?>> dependencies;
	private final List<ExportServiceDefinition> exports;
	
	/**
	 * 构造方法
	 * @param builder {@link AssemblyConfigBuilder}
	 */
	protected DefaultAssemblyConfig(AssemblyConfigBuilder builder) {
		this.symbolicName         = Objects.requireNonNull(builder.getSymbolicName().toIdentifier(), "\"symbolciName\" cannot be null.");
		this.vendor               = Objects.requireNonNull(builder.getSymbolicName().getDomain().getVendor(), "\"vendor\" cannot be null.");
		
		this.name                 = Objects.requireNonNull(builder.getName() , "\"name\" cannot be null.");
		this.description          = builder.getDescription();
		
		this.containerName        = builder.getContainerName();
		this.dependencies         = builder.getDependencyServiceDefinitions();
		this.exports              = builder.getExportServiceDefinitions();
		this.tags                 = builder.getAssemblyTags();
		this.attachClassNames     = builder.getAttachClassNames();
	}
	
	public DefaultAssemblyConfig(String symbolicName, String name, String description, 
			Vendor vendor, String containerName, List<DependencyServiceDefinition<?>> dependencies, 
			List<ExportServiceDefinition> exports, Set<String> tags, List<String> attachClassNames) {
		this.symbolicName  = Objects.requireNonNull(symbolicName , "\"symbolicName\" cannot be null.");
		this.vendor        = Objects.requireNonNull(vendor, "\"vendor\" cannot be null.");
		this.name          = Objects.requireNonNull(name , "\"name\" cannot be null.");
		
		this.description = description;
		this.containerName = containerName;
		this.dependencies = dependencies == null ? Collections.emptyList() : Collections.unmodifiableList(dependencies);
		this.exports = exports == null ? Collections.emptyList() : Collections.unmodifiableList(exports);
		this.tags = tags == null ? Collections.emptySet() : Collections.unmodifiableSet(tags);
		this.attachClassNames     = attachClassNames == null ? Collections.emptyList(): attachClassNames;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfig#containsTag(java.lang.String)
	 */
	@Override
	public boolean containsTag(String tag) {
		return this.getTags().contains(tag);
	}
	
	@Override
	public List<String> getAttachHandlerClassNames() {
		return this.attachClassNames;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfig#getContainerName()
	 */
	@Override
	public String getContainerName() {
		return this.containerName;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfig#getDependencyServiceDefinitions()
	 */
	@Override
	public List<DependencyServiceDefinition<?>> getDependencyServiceDefinitions() {
		return this.dependencies == null ?
				Collections.emptyList() :
					this.dependencies;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfig#getExportServiceDefinitions()
	 */
	@Override
	public List<ExportServiceDefinition> getExportServiceDefinitions() {
		return this.exports == null ?
				Collections.emptyList() :
					this.exports;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfig#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfig#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.support.InitParameters#createVariableReplacer()
	 */
	@Override
	public VariableReplacer createVariableReplacer() {
		Map<String, String> map = this.getParameterMap();
		return VariableReplacer.createVariableReplacer(map);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.support.InitParameters#getInitParameter(java.lang.String)
	 */
	@Override
	public native String getInitParameter(String name);

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.support.InitParameters#getInitParameterNames()
	 */
	@Override
	public native List<String> getInitParameterNames();
	
	
	@Override
	public List<ParameterDescriptor> getParameterDescriptors() {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfig#getSymbolicName()
	 */
	@Override
	public String getSymbolicName() {
		return this.symbolicName;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfig#getTags()
	 */
	@Override
	public Set<String> getTags() {
		return this.tags == null ?
				Collections.emptySet() :
					this.tags;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfig#getVendor()
	 */
	@Override
	public Vendor getVendor() {
		return this.vendor;
	}
	
	/**
	 * 初始化参数Map
	 * @return {@link Map}
	 */
	protected native Map<String, String> getParameterMap();
}
