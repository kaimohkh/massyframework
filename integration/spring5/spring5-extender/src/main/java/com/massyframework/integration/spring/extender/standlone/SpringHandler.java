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
 * @日   期:  2019年1月24日
 */
package com.massyframework.integration.spring.extender.standlone;

import org.springframework.core.io.Resource;
import org.springframework.web.context.ContextLoader;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.ParameterNotFoundException;
import com.massyframework.assembly.monitor.AssemblyTask;
import com.massyframework.assembly.monitor.AssemblyTaskMonitor;
import com.massyframework.assembly.monitor.AssemblyTaskPhases;
import com.massyframework.assembly.spi.ActivationHandler;
import com.massyframework.assembly.spi.HandlerRegistration;
import com.massyframework.assembly.util.ContainerUtils;
import com.massyframework.integration.spring.extender.ConfigLocationsUtils;
import com.massyframework.integration.spring.extender.DependencyServiceBeanDefiner;
import com.massyframework.integration.spring.extender.ProfileUtils;
import com.massyframework.integration.spring.extender.SpringAssemblyContext;
import com.massyframework.integration.spring.extender.SpringContainer;
import com.massyframework.integration.spring.extender.context.SpringAnnotationApplicationContext;
import com.massyframework.integration.spring.extender.context.SpringXmlApplicationContext;
import com.massyframework.lang.util.ClassLoaderUtils;


/**
 * 提供Spring容器的处理器
 */
public class SpringHandler extends DependencyServiceBeanDefiner 
	implements ActivationHandler{

	private volatile HandlerRegistration<SpringAssemblyContext> registration;

	/**
	 * 构造方法
	 */
	public SpringHandler() {
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.ActivationHandler#doStarting()
	 */
	@Override
	public synchronized void doStarting() throws Exception {
		if (this.registration == null) {
			Assembly assembly = this.getAssembly();
			AssemblyTaskMonitor taskMonitor =
					getHandler(AssemblyTaskMonitor.class);
			AssemblyTask task = taskMonitor.startTask(assembly, AssemblyTaskPhases.CONTAINER_INITIALIZE);
			
			try {
				SpringContainer container = this.createContainer(
						ContainerUtils.hasContainerConfigClasses(assembly));
				this.bindSpringContainer(container);
				
				ClassLoader contextLoader =
						ClassLoaderUtils.setThreadContextClassLoader(assembly.getAssemblyClassLoader());
				try {	
					container.getApplicationContext().refresh();
				}finally {
					ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
				}
				
				//注册
				this.registration = this.getLifecycleManager().register(this.getAssemblyContext());
				while (container.getApplicationContext().isActive()) {
					try {
						Thread.sleep(100);
					}catch(Exception e) {
						
					}
					return;
				}
				
				task.complete();
			}catch(Exception e) {
				task.complete(e);
				throw e;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.ActivationHandler#doStopped()
	 */
	@Override
	public synchronized void doStopped() throws Exception {
		if (this.registration != null) {			
			SpringAssemblyContext context =
					this.registration.getHandler();
			this.registration.unregister();
			this.registration = null;
			
			ClassLoader contextLoader =
					ClassLoaderUtils.setThreadContextClassLoader(getAssembly().getAssemblyClassLoader());
			try {
				context.getContainer().getApplicationContext().close();
			}finally {
				ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
			}
			this.unbindSpringContainer();
		}
	}

	/**
	 * 创建Spring容器
	 * @param isAnnotation 是否采用注解配置
	 * @return {@link SpringContainer}
	 * @throws Exception {@link Exception},创建时发生的例外
	 */
	protected SpringContainer createContainer(boolean isAnnotation) throws Exception{
		Assembly assembly = this.getAssembly();
		
		if (isAnnotation) {
			Class<?>[] classes = ContainerUtils.getContainerConfigClasses(assembly);
					
			SpringAnnotationApplicationContext applicationContext = new SpringAnnotationApplicationContext(this);
			applicationContext.setClassLoader(assembly.getAssemblyClassLoader());
			
			applicationContext.register(classes);
			ProfileUtils.setProfiles(applicationContext, assembly);
			
			SpringContainer result = new SpringContainer(applicationContext);
			this.bindSpringContainer(result);
			return result;
		}else {
			Resource[] configResources = 
			ConfigLocationsUtils.parseConfigLocationsToArray(this.getAssembly());	
			if (configResources.length == 0l) {
				throw new ParameterNotFoundException("container." + ContextLoader.CONFIG_LOCATION_PARAM);
			}
					
			SpringXmlApplicationContext applicationContext = new SpringXmlApplicationContext(this);
			applicationContext.setClassLoader(this.getAssembly().getAssemblyClassLoader());
			
			applicationContext.setConfigResources(configResources);
			SpringContainer result = new SpringContainer(applicationContext);
			return result;
		}
		
	}
}
