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

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.web.util.WebUtils;

/**
 * 实现{@link Servlet}的抽象类
 */
public abstract class AbstractServlet implements Servlet {
	
	private AssemblyServletConfig config;

	/**
	 * 
	 */
	public AbstractServlet() {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public final void init(ServletConfig config) throws ServletException {
		try {
			this.config = this.createServletConfig(config);
			this.init();
		}catch(Exception e) {
			throw new ServletException(e.getMessage(), e);
		}
	}
	
	/**
	 * 初始化
	 * @throws ServletException
	 */
	protected void init() throws ServletException{
		
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#destroy()
	 */
	@Override
	public void destroy() {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#getServletConfig()
	 */
	@Override
	public ServletConfig getServletConfig() {
		return this.config;
	}
	
	/**
	 * Servlet上下文
	 * @return {@link ServletContext}
	 */
	public ServletContext getServletContext() {
		return this.config == null ? null: this.config.getServletContext();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#getServletInfo()
	 */
	@Override
	public String getServletInfo() {
		return null;
	}

	/**
	 * 根据<code>config</code>创建ServletConfig
	 * @param config {@link ServletConfig}
	 * @return {@link ServletConfig}
	 */
	protected AssemblyServletConfig createServletConfig(ServletConfig config) {
		Assembly assembly = WebUtils.getAssociatedAssembly(config);
		return new AssemblyServletConfig(assembly, config);
	}
	
	/**
	 * 关联的装配件
	 * @return {@link Assembly}
	 */
	public Assembly getAssociatedAssembly() {
		return this.config == null ? null : this.config.getAssociatedAssembly();
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
