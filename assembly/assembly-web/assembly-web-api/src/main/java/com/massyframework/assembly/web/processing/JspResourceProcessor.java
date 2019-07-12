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
 * @日   期:  2019年2月1日
 */
package com.massyframework.assembly.web.processing;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.massyframework.assembly.web.ModularServletContext;
import com.massyframework.lang.util.ClassLoaderUtils;

/**
 * 构造方法
 */
public final class JspResourceProcessor implements HttpRequestProcessor {
	
	private static Set<String> EXTENSION_NAMES =
			new HashSet<String>(Arrays.asList(
					".jsp", ".jspf", ".jspx", ".xsp", ".JSP", ".JSPF", ".JSPX", ".XSP"));
	
	private Servlet jspServlet;
	private ClassLoader classLoader;

	/**
	 * 构造方法
	 */
	public JspResourceProcessor() {
	}

	/**
	 * 初始化
	 * @param context
	 * @throws ServletException
	 */
	public void init(ServletContext context) throws ServletException {
		try {
			Class<?> clazz =
					this.getClass().getClassLoader().loadClass("org.apache.jasper.servlet.JspServlet");
			
			this.jspServlet = Servlet.class.cast(clazz.getDeclaredConstructor().newInstance());
			
			ClassLoader contextLoader = null;
			if (context instanceof ModularServletContext) {
				contextLoader = context.getClassLoader();
			}else {
				contextLoader = Thread.currentThread().getContextClassLoader();
			}
			this.classLoader = new MixinClassLoader(JspResourceProcessor.class.getClassLoader(), contextLoader);
			contextLoader = 
					ClassLoaderUtils.setThreadContextClassLoader(this.classLoader);
			try {
				this.jspServlet.init(new ConfigImpl(context));
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
		}catch(Exception e) {
			throw new ServletException(e.getMessage(), e);
		}
	}
	

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.processing.HttpRequestProcessor#isSupport(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean isSupport(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		return this.isJspPage(pathInfo);
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.processing.HttpRequestProcessor#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ClassLoader contextLoader =
				ClassLoaderUtils.setThreadContextClassLoader(this.classLoader);
		try {
			this.jspServlet.service(request, response);
		}finally {
			ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
		}
	}

	/**
	 * 判断资源是否jsp页面
	 * @param resource 资源
	 * @return {@link boolean},返回<code>true</code>表示是需要jsp页面
	 */
	protected boolean isJspPage(String resource) {
		int pos = StringUtils.lastIndexOf(resource, ".");
		if (pos == -1) return false;
		
		if (resource.length() - pos > 5) {
			return false;
		}
		String ext = StringUtils.substring(resource, pos);
		return EXTENSION_NAMES.contains(ext);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.processing.HttpRequestProcessor#destroy()
	 */
	@Override
	public void destroy() {
		if (this.jspServlet != null) {
			this.jspServlet.destroy();
			this.jspServlet = null;
		}
	}


	private class ConfigImpl implements ServletConfig {
		
		private ServletContext context;
		private Map<String, String> initParams;
		
		public ConfigImpl(ServletContext context){
			this.context = context;
			Map<String, String> map= new HashMap<String, String>();
			map.put("logVerbosityLevel", "DEBUG");
			map.put("fork", "false");
			map.put("xpoweredBy", "false");
			map.put("compilerSourceVM", "1.7");
			map.put("compilerTargetVM", "1.7");
			map.put("logVerbosityLevel", "DEBUG");
			this.initParams = Collections.unmodifiableMap(map);
		}
		
		
		@Override
		public String getServletName() {
			return "jsp";
		}

		@Override
		public ServletContext getServletContext() {
			return this.context;
		}

		@Override
		public String getInitParameter(String name) {
			return this.initParams.get(name);
		}

		@Override
		public Enumeration<String> getInitParameterNames() {
			return Collections.enumeration(this.initParams.keySet());
		}
		
	}
	
	private class MixinClassLoader extends ClassLoader {
		
		private ClassLoader contextLoader;
		
		/**
		 * 构造方法
		 * @param parent {@link ClassLoader},父类加载器
		 * @param contextLoader {@link ClassLoader},上下文加载器
		 */
		public MixinClassLoader (ClassLoader parent, ClassLoader contextLoader) {
			super(parent);
			this.contextLoader = contextLoader;
		}

		/* (non-Javadoc)
		 * @see java.lang.ClassLoader#findClass(java.lang.String)
		 */
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			Class<?> result = null;
			try {
				result = this.contextLoader.loadClass(name);
			}catch(Exception e) {
				
			}
			
			if (result == null) {
				result = super.findClass(name);
			}
			return result;
		}		
	}

}
