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
package com.massyframework.assembly.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.web.util.WebUtils;

/**
 * 实现{@link Filter}的抽象类
 *
 */
public abstract class AbstractFilter implements Filter {

	private AssemblyFilterConfig config;
	
	/**
	 * 
	 */
	public AbstractFilter() {
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public final void init(FilterConfig filterConfig) throws ServletException {
		try {
			this.config = this.createFilterConfig(config);
			this.init();
		}catch(Exception e){
			throw new ServletException(e.getMessage(), e);
		}
	}
	
	/**
	 * 初始化
	 */
	protected void init() throws ServletException{
		
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}
	
	/**
	 * 过滤器配置
	 * @return {@link FilterConfig}
	 */
	public FilterConfig getFilterConfig() {
		return this.config;
	}
	
	/**
	 * 关联的装配件
	 * @return {@link Assembly}
	 */
	public Assembly getAssociatedAssembly() {
		return this.config == null ? null: this.config.getAssociatedAssembly();
	}
		
	/**
	 * Servlet上下文
	 * @return {@link ServletContext}
	 */
	protected ServletContext getServletContext() {
		return this.config == null ? null : this.config.getServletContext();
	}

	/**
	 * 创建{@link FilterConfig}
	 * @param config {@link FilterConfig},外部传入的FilterConfig
	 * @return {@link FilterConfig}
	 */
	protected AssemblyFilterConfig createFilterConfig(FilterConfig config) {
		Assembly assembly = WebUtils.getAssociatedAssembly(config);
		AssemblyFilterConfig result =
				new AssemblyFilterConfig(assembly, config);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getAssociatedAssembly() != null ?
				this.getClass().getSimpleName() + " [assembly=" + this.getAssociatedAssembly() + "]" :
					this.getClass().getSimpleName();
	}
	
}
