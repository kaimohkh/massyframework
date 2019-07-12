/**
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
 * Author: 黄开晖（117227773@qq.com）
 * Date:   2019年6月8日
 */
package com.massyframework.assembly.launching.servlet;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyContext;
import com.massyframework.assembly.Framework;
import com.massyframework.assembly.launching.launcher.DefaultLauncher;

/**
 * 基于Servlet3的Web启动器
 * @author  Huangkaihui
 *
 */
public final class FrameworkWebLauncher implements ServletContainerInitializer {

	/**
	 * 
	 */
	public FrameworkWebLauncher() {
		
	}

	@SuppressWarnings("exports")
	@Override
	public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
		ServletContextRegister register =
				new ServletContextRegister(ctx);
		DefaultLauncher launcher = new DefaultLauncher();
		Framework framework = launcher.launch(register);
		ctx.setAttribute(Framework.class.getName(), framework);
		ctx.addListener(new FrameworkRunner());
	}

	private class FrameworkRunner implements ServletContextListener {

		@Override
		public void contextInitialized(ServletContextEvent sce) {
			ServletContext context = sce.getServletContext();
			Framework framework = this.retrieveFramework(context);
			try {
				if (framework != null) {
					framework.start();
				}
			}catch(Exception e) {
				context.log("Massy Framework start failed.", e);
			}
		}

		@Override
		public void contextDestroyed(ServletContextEvent sce) {
			ServletContext context = sce.getServletContext();
			Framework framework = this.retrieveFramework(context);
			try {
				if (framework != null) {
					context.setAttribute(Framework.class.getName(), null);
					this.clearClassFileTransformer(framework);
					framework.stop();
					framework = null;
				}
			}catch(Exception e) {
				context.log("Massy Framework stop failed.", e);
			}			
		}
		
		/**
		   *    清理注册的类文件转换器
		 * @param framework {@link Framework}
		 */
		protected void clearClassFileTransformer(Framework framework) {
			Assembly assembly = framework.getAssemblyRepository().getAssembly(0);
			AssemblyContext context = assembly.getAssemblyContext();
			if (context.containsComponent(
					Instrumentation.class.getName())) {
				Instrumentation inst = context.getComponent(
						Instrumentation.class.getName(), Instrumentation.class);
				try {
					
					Method method = inst.getClass().getDeclaredMethod("clear", new Class<?>[0]);
					method.invoke(inst, new Object[0]);
				}catch(Exception e) {
					
				}
				
			}
		}
		
		/**
		 * 从<code>context</code>中取回Framework
		 * @param context {@link ServletContext},Servlet上下文
		 * @return {@link Framework}
		 */
		protected Framework retrieveFramework(ServletContext context) {
			Object value = context.getAttribute(Framework.class.getName());
			if (value != null) {
				if (Framework.class.isAssignableFrom(value.getClass())) {
					return Framework.class.cast(value);
				}
			}
			
			return null;
		}
		
	}
}
