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
package com.massyframework.assembly.web.runtime;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import com.massyframework.assembly.AssemblyRepository;
import com.massyframework.assembly.Framework;
import com.massyframework.assembly.launching.Initializer;
import com.massyframework.assembly.launching.LaunchContext;
import com.massyframework.assembly.web.runtime.pagemapping.DefaultPageMappingRepository;
import com.massyframework.assembly.web.runtime.pagemapping.PageMappingProxyServlet;
import com.massyframework.assembly.web.runtime.pagemapping.PageMappingFounderRegister;
import com.massyframework.assembly.service.ServiceProperties;
import com.massyframework.assembly.web.ModularServletContextRepository;
import com.massyframework.assembly.web.WebResourceRepository;
import com.massyframework.assembly.web.pagemapping.PageMappingRepository;
import com.massyframework.assembly.web.processing.JspResourceProcessorRepository;

/**
 * Web资源初始化器
 */
public final class WebResourceInitializer implements Initializer {
	
	public static final String FILTERCHAINDEFINITIONS = "shiro.web.filter.filterChainDefinitions";
	
	/**
	 * 构造方法
	 */
	public WebResourceInitializer() {
		
	}
	
	
	@Override
	public void init(LaunchContext context) {
		if (context.runOnJ2EE()) {
			ServletContext servletContext = context.getComponent(ServletContext.class);
			this.registerServletComponents(servletContext, context);
			PageMappingRepository repository =
					this.registerPageMappingRepository(servletContext, context);
			context.addListener(new PageMappingFounderRegister(repository));
		}
	}


	/**
	 * 注册PageMappingRepository服务
	 * @param servletContext {@link ServletContext}, Servlet上下文
	 * @param context {@link LaunchContext},初始化上下文 
	 * @return {@link PageMappingRepository}
	 */
	protected PageMappingRepository registerPageMappingRepository(
			ServletContext servletContext, LaunchContext context) {
		//注册PageMappingRepository
		DefaultPageMappingRepository result = new DefaultPageMappingRepository();
		ServiceProperties props = new ServiceProperties();
		props.setObjectClass(PageMappingRepository.class);
		props.setDescription("页面映射仓储");
		props.setTopRanking();
		context.addComponent(PageMappingRepository.class.getName(), result, props);
		
		//注册Servlet
		PageMappingProxyServlet servlet = new PageMappingProxyServlet(result);
		ServletRegistration.Dynamic registration =
				servletContext.addServlet("commonsServlet", servlet);
		registration.setAsyncSupported(true);
		String urlPattern = this.getUrlPattern(context);
		registration.addMapping(urlPattern);
		
		//考虑到可能和shiro集成，添加shiro的web过滤定义
		StringBuilder builder = new StringBuilder();
		builder.append(urlPattern).append("=").append("anon").append("\r\n");
		builder.append(urlPattern).append("*").append("=").append("authc");
		context.setInitParameter(FILTERCHAINDEFINITIONS, builder.toString());
		return result;
	}
	
	protected String getUrlPattern(LaunchContext context) {
		return context.getInitParameter(
				PageMappingProxyServlet.COMMONS_URLPATTERN, PageMappingProxyServlet.DEFAULT_URLPATTREN);
	}
		
	/**
	 * 注册Servlet组件
	 * @param servletContext {@link ServletContext}, Servlet上下文
	 * @param context {@link LaunchContext},初始化上下文
	 */
	protected void registerServletComponents(ServletContext servletContext, LaunchContext context) {
		AssemblyRepository assemblyRepo = context.getComponent(AssemblyRepository.class);
		
		//注册模块化ServleContext仓储
		DefaultModularServletContextRepository repo = new DefaultModularServletContextRepository(servletContext, assemblyRepo);
		ServiceProperties props = new ServiceProperties();
		props.setDescription("装配件Servlet上下文");
		props.setObjectClass(ModularServletContextRepository.class);
		props.setTopRanking();
		context.addComponent(ModularServletContextRepository.class.getName(), repo, props);
		
		//注册Web资源仓储
		DefaultWebResourceRepository resourceRepo = new DefaultWebResourceRepository(repo);
		props = new ServiceProperties();
		props.setDescription("Web资源仓储");
		props.setObjectClass(WebResourceRepository.class);
		props.setTopRanking();
		context.addComponent(WebResourceRepository.class.getName(), resourceRepo, props);
		context.addListener(resourceRepo);
		
		//注册JspResource处理仓储
		DefaultJspResourceProcessorRepository processorRepo =
				new DefaultJspResourceProcessorRepository();
		props = new ServiceProperties();
		props.setDescription("提供对Jsp资源处理器的仓储");
		props.setObjectClass(JspResourceProcessorRepository.class);
		props.setTopRanking();
		context.addComponent(JspResourceProcessorRepository.class.getName(), processorRepo, props);
		
		//注册JspServlet
		ServletRegistration.Dynamic registration =
				servletContext.addServlet("jspServletSupport", new JspSupportServlet(repo, resourceRepo, processorRepo));
		registration.addMapping(new String[] {"*.jsp", "*.jspf", "*.jspx", "*.xsp", "*.JSP", "*.JSPF", "*.JSPX", "*.XSP"});
		registration.setAsyncSupported(true);
		
		if (!this.isRunAtStandlone(context)) {
			registration =
				servletContext.addServlet("staticResource", new StaticResourceServlet(resourceRepo));
			registration.addMapping("/");
			registration.setAsyncSupported(true);
		}
	}
	
	/**
	 * 是否运行在独立类加载器模式
	 * @param context {@link LaunchContext}
	 * @return {@link boolean},返回<code>true</code>表示是，否则返回<code>false</code>
	 */
	protected boolean isRunAtStandlone(LaunchContext context) {
		return context.getInitParameter(
				Framework.RUNMODE, Framework.RUNMODE_STANDALONE) 
					== Framework.RUNMODE_STANDALONE;
	}
}
