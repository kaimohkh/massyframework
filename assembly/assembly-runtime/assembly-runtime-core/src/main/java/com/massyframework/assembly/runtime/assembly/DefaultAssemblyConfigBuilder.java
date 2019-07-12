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
package com.massyframework.assembly.runtime.assembly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.massyframework.assembly.AssemblyConfig;
import com.massyframework.assembly.AssemblyConfigBuilder;
import com.massyframework.assembly.AssemblyDefinition;
import com.massyframework.assembly.ParameterDescriptor;
import com.massyframework.assembly.SymbolicName;
import com.massyframework.assembly.initparam.InitParameters;
import com.massyframework.assembly.initparam.VariableReplacer;
import com.massyframework.assembly.service.DependencyServiceDefinition;
import com.massyframework.assembly.service.ExportServiceDefinition;
import com.massyframework.assembly.util.InitParametersUtils;

/**
 * 装配件配置构建器基类，实现配置构建器的基本语义
 *
 */
public final class DefaultAssemblyConfigBuilder implements AssemblyConfigBuilder {
	
	private static final String DOT = ".";
		
	private List<DependencyServiceDefinition<?>> dependencies;
	private List<ExportServiceDefinition> exports;
	private Set<String> tags;
	private Set<String> existNames;
	private List<String> attachClassNames;
	private List<ParameterDescriptor> parameterDescriptors;

	private final AssemblyDefinition definiton;
	private final InitParameters subsetParams;
	private final VariableReplacer replacer;
	
	private String name;
	private String containerName;
	private String description;	
	
	/**
	 * 构造方法
	 * @param definition {@link AssemblyDefintion},装配件定义
	 * @param frameworkParams {@link InitParameters}, 运行框架的初始化参数
	 */
	public DefaultAssemblyConfigBuilder(AssemblyDefinition definition, InitParameters frameworkParams) {
		this.definiton = Objects.requireNonNull(definition, "\"definition\" cannot be null.");
		this.subsetParams =InitParametersUtils.subset(
				Objects.requireNonNull(frameworkParams, "\"frameworkParams\" cannot be null."),
						this.getSymbolicName().toIdentifier());
		this.replacer = frameworkParams.createVariableReplacer();
	}
	
	@Override
	public AssemblyConfigBuilder addAttachHandlerClassName(String className) {
		if (className == null) return this;
		if (this.attachClassNames == null) {
			this.attachClassNames = new ArrayList<>();
		}
		
		this.attachClassNames.add(className);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#addDependencyServiceDefinition(com.massyframework.assembly.service.DependencyServiceDefinition)
	 */
	@Override
	public AssemblyConfigBuilder addDependencyServiceDefinition(DependencyServiceDefinition<?> definition) {
		if (definition != null) {
			this.getOrCreateDependencies().add(definition);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#addExportServiceDefinition(com.massyframework.assembly.service.ExportServiceDefinition)
	 */
	@Override
	public AssemblyConfigBuilder addExportServiceDefinition(ExportServiceDefinition definition) {
		if (definition != null) {
			this.getOrCreateExports().add(definition);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#addInitParameter(java.lang.String, java.lang.String)
	 */
	@Override
	public AssemblyConfigBuilder addInitParameter(String name, String value) {
		if (name == null) return this;
		
		String newValue = this.findAndReplaceValue(name, value);
		if (this.setInitParameter(name, newValue)) {
			if (this.existNames == null) {
				this.existNames = new HashSet<String>();
			}
			this.existNames.add(name);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#addinitParameters(java.util.Map)
	 */
	@Override
	public AssemblyConfigBuilder addinitParameters(Map<String, String> initParams) {
		if (initParams == null) return this;
		
		for (Map.Entry<String, String> entry: initParams.entrySet()) {
			String newValue = this.findAndReplaceValue(entry.getKey(), entry.getValue());
			
			String name = entry.getKey();
			if (this.setInitParameter(name, newValue)) {
				if (this.existNames == null) {
					this.existNames = new HashSet<String>();
				}
				this.existNames.add(name);
			}
		}
		return this;
	}
	
	
	@Override
	public AssemblyConfigBuilder addParameterDescriptor(ParameterDescriptor descriptor) {
		if (descriptor == null) return this;
		
		if (this.parameterDescriptors == null) {
			this.parameterDescriptors = new ArrayList<>();
		}
		
		this.parameterDescriptors.add(descriptor);
		return this;
	}

	/**
	 * 尝试查找或者替换变量值
	 * 
	 * @param name 变量名
	 * @param value 变量值
	 * @return  {@link String},新值
	 */
	protected String findAndReplaceValue(String name, String value) {
		String newValue = this.subsetParams.getInitParameter(name);
		if (newValue == null) {
			newValue = value;
		}
		
		newValue = this.replacer.findAndReplace(newValue);
		return newValue;
	}
	
	/**
	 * 设置初始化参数
	 * @param name {@link String},参数名
	 * @param value {@link String},参数值
	 * @return {@link boolean}, 
	 */
	protected native boolean setInitParameter(String name, String value);
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#addTag(java.lang.String)
	 */
	@Override
	public AssemblyConfigBuilder addTag(String tag) {
		if (tag != null) {
			this.getOrCreateTags().add(tag);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#addTags(java.lang.String[])
	 */
	@Override
	public AssemblyConfigBuilder addTags(String... tags) {
		for (String tag: tags) {
			if (!StringUtils.isEmpty(tag)) {
				this.getOrCreateTags().add(tag);
			}
		}
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#build()
	 */
	@Override
	public AssemblyConfig build() {
		Set<String> exists = this.existNames == null ? Collections.emptySet() : this.existNames;
		List<String> names = this.subsetParams.getInitParameterNames();
		for (String name: names) {
			if (!exists.contains(name)) {
				this.setInitParameter(name, this.subsetParams.getInitParameter(name));
			}
		}
		return new DefaultAssemblyConfig(this);
	}
	

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#getAssemblyClassLoader()
	 */
	@Override
	public ClassLoader getAssemblyClassLoader() {
		return this.definiton.getAssemblyClassLoader();
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#getAssemblyDefinition()
	 */
	@Override
	public AssemblyDefinition getAssemblyDefinition() {
		return this.definiton;
	}
	
	@Override
	public List<String> getAttachClassNames() {
		return this.attachClassNames == null ?
				Collections.emptyList() :
					this.attachClassNames;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#getContainerName()
	 */
	@Override
	public String getContainerName() {
		return this.containerName;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#getDependencyServiceDefinitions()
	 */
	@Override
	public List<DependencyServiceDefinition<?>> getDependencyServiceDefinitions() {
		return this.dependencies == null ? Collections.emptyList() : this.dependencies;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#getExportServiceDefinitions()
	 */
	@Override
	public List<ExportServiceDefinition> getExportServiceDefinitions() {
		return this.exports == null ? Collections.emptyList() : this.exports;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#getAssemblyTags()
	 */
	@Override
	public Set<String> getAssemblyTags() {
		return this.tags == null ? Collections.emptySet() : this.tags;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public List<ParameterDescriptor> getParameterDescritors() {
		return this.parameterDescriptors == null ?
					Collections.emptyList() :
						this.parameterDescriptors;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#setContainerName(java.lang.String)
	 */
	@Override
	public AssemblyConfigBuilder setContainerName(String containerName) {
		this.containerName = containerName;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#setDescription(java.lang.String)
	 */
	@Override
	public AssemblyConfigBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#setName(java.lang.String)
	 */
	@Override
	public AssemblyConfigBuilder setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * 根据<code>name</code>获取参数值
	 * @param name {@link String}, 参数名
	 * @return {@link String},参数值，参数不存在，可以返回null.
	 */
	public native String getInitParameter(String name);
	
			
	/**
	 * 所有初始化参数名称
	 * @return {@link List}
	 */
	public native List<String>  getInitParameterNames();	
	
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#getSymbolicName()
	 */
	@Override
	public SymbolicName getSymbolicName() {
		return this.definiton.getSymbolicName();
	}
	
	

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyConfigBuilder#createVariableReplacer()
	 */
	@Override
	public VariableReplacer createVariableReplacer() {
		return VariableReplacer.createVariableReplacer(this);
	}

	/**
	 * 获取依赖服务列表，如果依赖服务列表为null，则创建一个新的依赖服务列表返回
	 * @return {@link List}
	 */
	protected List<DependencyServiceDefinition<?>> getOrCreateDependencies(){
		if (this.dependencies == null) {
			this.dependencies = new ArrayList<>();
		}
		return this.dependencies;
	}
	
	/**
	 * 获取输出服务列表，如果输出服务列表为null，则创建一个新的输出服务列表返回
	 * @return {@link List}
	 */
	protected List<ExportServiceDefinition> getOrCreateExports(){
		if (this.exports == null) {
			this.exports = new ArrayList<>();
		}
		return this.exports;
	}
		
	/**
	 * 获取标签集，如果标签集为null,则创建一个新的标签集
	 * @return {@link Set}
	 */
	protected Set<String> getOrCreateTags(){
		if (this.tags == null) {
			this.tags = new HashSet<String>();
		}
		return this.tags;
	}
	
	
}
