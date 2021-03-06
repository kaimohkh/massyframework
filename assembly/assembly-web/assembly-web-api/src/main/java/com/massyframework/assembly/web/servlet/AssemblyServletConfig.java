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
 * @日   期:  2019年1月27日
 */
package com.massyframework.assembly.web.servlet;

import java.util.Enumeration;
import java.util.Objects;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.service.ServiceRepository;
import com.massyframework.assembly.web.ModularServletContext;
import com.massyframework.assembly.web.ModularServletContextRepository;

/**
 * 关联装配件的{@link ServletConfig}
 */
public class AssemblyServletConfig implements ServletConfig {
	
	private final Assembly assembly;
	private final ServletConfig config;
	private final ModularServletContext servletContext;

	/**
	 * 构造方法
	 * @param assembly {@link Assembly},装配件
	 * @param servletConfig {@link ServletConfig}，Servlet配置
	 */
	public AssemblyServletConfig(Assembly assembly, ServletConfig servletConfig) {
		this.assembly = Objects.requireNonNull(assembly, "\"assembly\" cannot be null.");
		this.config = Objects.requireNonNull(servletConfig, "\"servletConfig\" cannot be null.");
		this.servletContext = this.createAssemblyServletContext();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getServletName()
	 */
	@Override
	public String getServletName() {
		return this.config.getServletName();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getServletContext()
	 */
	@Override
	public final ServletContext getServletContext() {
		return this.servletContext;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
	 */
	@Override
	public String getInitParameter(String name) {
		return this.config.getInitParameter(name);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameterNames()
	 */
	@Override
	public Enumeration<String> getInitParameterNames() {
		return this.config.getInitParameterNames();
	}

	/**
	 * 关联的装配件
	 * @return {@link Assembly}
	 */
	public final Assembly getAssociatedAssembly() {
		return this.assembly;
	}
	
	/**
	 * 关联装配件的Servlet上下文
	 * @return {@link ModularServletContext}
	 */
	public final ModularServletContext getAssemblyServletContext() {
		return this.servletContext;
	}
	
	/**
	 * 创建{@link ModularServletContext}
	 * @return {@link ModularServletContext}
	 */
	protected ModularServletContext createAssemblyServletContext() {
		ServiceRepository repo = ServiceRepository.retrieveFrom(this.getAssociatedAssembly());
		
		ModularServletContextRepository mscRepo =
				repo.getService(ModularServletContextRepository.class);
		return mscRepo.getModularServletContext(this.assembly);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [assembly=" + assembly + "]";
	}

}
