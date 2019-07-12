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
 * Date:   2019年6月9日
 */
package com.massyframework.assembly.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyConfig;
import com.massyframework.assembly.Framework;
import com.massyframework.assembly.FrameworkFactory;
import com.massyframework.assembly.container.Container;
import com.massyframework.assembly.domain.Vendor;
import com.massyframework.assembly.initparam.InitParameters;
import com.massyframework.assembly.launching.PresetHandler;
import com.massyframework.assembly.runtime.assembly.AssemblyBase;
import com.massyframework.assembly.runtime.assembly.AssemblyFactory;
import com.massyframework.assembly.runtime.assembly.DefaultAssemblyConfig;
import com.massyframework.assembly.runtime.assembly.handling.AssemblyHandlerFactory;
import com.massyframework.assembly.runtime.support.ThreadLocalBinderServiceGroupFactory;
import com.massyframework.assembly.service.ExportServiceDefinition;
import com.massyframework.lang.util.ThreadLocalUtils;
import com.massyframework.logging.Logger;
import com.massyframework.logging.LoggerFactory;

/**
 * 实现{@link FrameworkFactory}的默认类
 * @author  Huangkaihui
 *
 */
public class DefaultFrameworkFactory implements FrameworkFactory {

	/**
	 * 
	 */
	public DefaultFrameworkFactory() {
	}

	@Override
	public Framework createFramework(PresetHandler handler) {		
		try {
			NativeLibrary.loadLibrary();
			final DefaultLaunchContext context= new DefaultLaunchContext();
			AssemblyBase kernelAssembly = 
					this.createKernelAssembly(context);
			
			//线程变量绑定
			ThreadLocalBinderServiceGroupFactory factory =
					new ThreadLocalBinderServiceGroupFactory(kernelAssembly);
			ThreadLocalUtils.bind(factory);
			
			//初始化上下文
			handler.init(context);
			
			//初始化完成后的回调
			context.onInitCompleted();
			
			DefaultFramework framework = new DefaultFramework(
					context.getAssemblyAdmin(),
					context.getServiceAdmin(),
					context.getAssemblyTaskMonitor(),
					context.getFrameworkListeners());					
			return new DelegateFramework(framework);
		}catch(Throwable e) {
			Logger logger = LoggerFactory.getLogger("");
					
			if (logger.isErrorEnabled()) {
				logger.error("launching failed.", e);
			}
		}
		return null;
	}
	
	/**
	 * 初始化所有装配件
	 * @param assemblyAdmin {@link AssemblyAdmin}
	 * @param initParams {@link InitParameters},初始化参数
	 * @param container {@link Container},容器
	 * @param logger {@link Logger},日志记录器
	 */
	protected void initAssemblies(AssemblyAdmin assemblyAdmin,
			InitParameters initParams, Container container, Logger logger) {
		List<Assembly> assemblies = assemblyAdmin.getAssemblies();
		
		int size = assemblies.size();
		for (int i=0; i<size; i++) {
			AssemblyBase assembly = (AssemblyBase)assemblies.get(i);
			
			try {
				assembly.init(initParams, container);
			}catch(Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * 创建内核装配件
	 * @param context {@link DefaultLaunchContext},启动上下文
	 * @return {@link Assembly}
	 */
	protected AssemblyBase createKernelAssembly(DefaultLaunchContext context) {
		AssemblyConfig config = this.createKernelAssemblyConfig(context);
		KernelAssemblyContainer container =
				this.createKernelAssemblyContainer(context);
		List<Object> handlers = 
			AssemblyHandlerFactory.createDefaultHandler(config);
		handlers.add(new AssemblyEventCommiter(context.getAssemblyAdmin()));
		handlers.add(context.getAssemblyTaskMonitor());
		handlers.add(new KernelAssemblyContextHandler(container));
		
		AssemblyBase result= AssemblyFactory.createAssembly(
				config, 
				this.getClass().getClassLoader(), 
				context.getServiceAdmin(), 
				handlers);
		context.getAssemblyAdmin().register(result);
		return result;
	}
	
	/**
	 * 创建内核装配件配置
	 * @param context {@link DefaultLaunchContext}
	 * @return {@link AssemblyConfig}
	 */
	protected AssemblyConfig createKernelAssemblyConfig(
			DefaultLaunchContext context) {
		String symbolicName = "com.massyframework.assembly.core";
		String name ="基础服务-MassyFramework-内核";
		String description = "提供装配件运行的基础服务，包括：装配件和服务的管理";
		
		Vendor vendor = new Vendor(
				Long.valueOf(10000),
				"广州市众禾信息科技有限公司", 
				AssemblyHandlerFactory.KERNEL_NAME);
		Set<String> tags = new HashSet<String>();
		tags.add("模块化");
		tags.add("装配件");
		tags.add("服务管理");
		tags.add("依赖注入");
		tags.add("assembly");
		tags.add("ioc");
		tags.add("运行框架");
		tags.add("内核");
		
		final List<ExportServiceDefinition> exportDefintions =
				new ArrayList<>();
		context.afterInitialized(ctx ->
				context.getLaunchComponentContainer()
					.fullExportServiceDefinition(exportDefintions));
		
		return new DefaultAssemblyConfig(
					symbolicName,
					name,
					description,
					vendor,
					null,
					null,
					exportDefintions,
					tags,
					null
				);
	}
	
	/**
	 * 创建内核装配件的组件容器
	 * @return {@link KernelAssemblyContainer}
	 */
	protected KernelAssemblyContainer createKernelAssemblyContainer(
			DefaultLaunchContext context) {
		Map<String, Object> componentMap =
				new HashMap<String, Object>();
		context.afterInitialized(ctx ->
				context.getLaunchComponentContainer()
					.fullComponentMap(componentMap));
		
		return new KernelAssemblyContainer(componentMap);
	}
}
