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

import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import com.massyframework.assembly.AssemblyConfigBuilder;
import com.massyframework.assembly.AssemblyDefinition;
import com.massyframework.assembly.AssemblyRegistration;
import com.massyframework.assembly.FrameworkListener;
import com.massyframework.assembly.ParseException;
import com.massyframework.assembly.SymbolicName;
import com.massyframework.assembly.SymbolicNameExistsException;
import com.massyframework.assembly.container.ComponentsException;
import com.massyframework.assembly.domain.AssemblyDomainFileNotFoundException;
import com.massyframework.assembly.domain.AssemblyDomainRepository;
import com.massyframework.assembly.domain.SignatureVerificationFailedException;
import com.massyframework.assembly.initparam.InitParameters;
import com.massyframework.assembly.initparam.VariableReplacer;
import com.massyframework.assembly.launching.LaunchContext;
import com.massyframework.assembly.monitor.AssemblyTaskMonitor;
import com.massyframework.assembly.protocols.assembly.Handler;
import com.massyframework.assembly.protocols.assembly.HandlerFactory;
import com.massyframework.assembly.protocols.factory.URLStreamHandlerFactoryUtils;
import com.massyframework.assembly.runtime.config.AssemblyXmlConfigBuilderParser;
import com.massyframework.assembly.runtime.domain.DefaultAssemblyDomainRepository;
import com.massyframework.assembly.runtime.service.ServiceAdmin;
import com.massyframework.assembly.service.ServiceProperties;
import com.massyframework.logging.Logger;
import com.massyframework.logging.LoggerAware;
import com.massyframework.logging.LoggerFactory;

/**
 * 实现{@link LaunchContext}的基本语义
 * @author  Huangkaihui
 *
 */
final class DefaultLaunchContext implements LaunchContext {
	
	private static final String DOT = ".";
	
	private final Logger logger;
	private final List<FrameworkListener> listeners;
	private final List<Consumer<LaunchContext>> consumers; 
	
	private final LaunchComponentContainer container;
	private final AssemblyDomainRepository domainRepository;
	private final FrameworkInitParameters initParams;
	
	private final AssemblyAdmin assemblyAdmin;
	private final DefaultAssemblyTaskMonitor assemblyTaskMonitor;
	private final ServiceAdmin serviceAdmin;

	/**
	 * 构造方法
	 */
	public DefaultLaunchContext() {
		this.logger = LoggerFactory.getLogger("");
		this.listeners = new CopyOnWriteArrayList<>();
		this.consumers = new CopyOnWriteArrayList<>();
		
		this.container = new LaunchComponentContainer();
		this.container.setLogger(this.logger);
		
		this.domainRepository = new DefaultAssemblyDomainRepository();
		this.initParams = new FrameworkInitParameters();
		
		this.assemblyTaskMonitor = new DefaultAssemblyTaskMonitor();
		this.serviceAdmin = new ServiceAdmin(this.assemblyTaskMonitor);
		this.assemblyAdmin = new AssemblyAdmin();
		
		LoggerAware.maybeToBind(this.container, logger);
		LoggerAware.maybeToBind(this.assemblyAdmin, logger);
		LoggerAware.maybeToBind(this.serviceAdmin, logger);
		
		Handler.bindAssemblyRepository(this.assemblyAdmin);
		try {
			URLStreamHandlerFactoryUtils.setURLStreamHandlerFactory(new HandlerFactory());
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			}
		}
		
		this.container.addComponent(
				AssemblyTaskMonitor.class.getName(), this.assemblyTaskMonitor);
		ServiceProperties props = new ServiceProperties();
		props.setObjectClass(AssemblyDomainRepository.class);
		props.setDescription("装配域的管理仓储");
		this.container.addComponent(
				AssemblyDomainRepository.class.getName(), this.domainRepository, props);
	
	}
	
	
	@Override
	public <C> void addComponent(String cName, C component) throws ComponentsException {
		this.container.addComponent(cName, component);
	}

	@Override
	public <C> void addComponent(String cName, C component, ServiceProperties props) throws ComponentsException {
		this.container.addComponent(cName, component, props);
	}

	@Override
	public void addListener(FrameworkListener listener) {
		if (listener == null) return ;
		this.listeners.add(listener);
	}

	@Override
	public void afterInitialized(Consumer<LaunchContext> consumer) {
		if (consumer == null) return;
		this.consumers.add(consumer);
	}
	
	@Override
	public Object getComponent(String cName) {
		return this.container.getComponent(cName);
	}

	@Override
	public native String getInitParameter(String name);
	
	/**
	 * 所有初始化参数名称
	 * @return {@link List}
	 */
	public native List<String> getInitParameterNames();

	@Override
	public Logger getLogger() {
		return this.logger;
	}
	
	@Override
	public native boolean setInitParameter(String name, String value);

	@Override
	public AssemblyRegistration registerAssembly(String symbolicName, URL assemblyXmlUrl,
			ClassLoader assemblyClassLoader) throws SymbolicNameExistsException, AssemblyDomainFileNotFoundException,
			SignatureVerificationFailedException, ParseException {
		
		SymbolicName sName = new SymbolicName(symbolicName, this.domainRepository);
		AssemblyDefinition definition =
				new AssemblyDefinition(sName, assemblyXmlUrl, assemblyClassLoader);
		AssemblyXmlConfigBuilderParser parser =
				new AssemblyXmlConfigBuilderParser();
		AssemblyConfigBuilder builder =
				parser.parse(definition, this.initParams);		
		return this.doRegisterAssembly(builder);
	}
	
	/**
	 * 是否运行在开发模式下
	 */
	public native boolean runOnDevelopMode();
	
	/**
	 *  注册装配件
	 * @param builder {@link AssemblyConfigBuilder}
	 * @return {@link AssemblyRegistration}
	 */
	protected native AssemblyRegistration doRegisterAssembly(AssemblyConfigBuilder builder);

	void onInitCompleted() {
		for (Consumer<LaunchContext> consumer: this.consumers) {
			try {
				consumer.accept(this);
			}catch(Throwable e) {
				if (this.logger.isErrorEnabled()) {
					this.logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * 装配件域仓储
	 * @return {@link AssemblyDomainRepository}
	 */
	public AssemblyDomainRepository getAssemblyDomainRepository() {
		return this.domainRepository;
	}
	
	/**
	 * 装配件管理器
	 * @return {@link AssemblyAdmin}
	 */
	public AssemblyAdmin getAssemblyAdmin() {
		return this.assemblyAdmin;
	}
	
	/**
	 * 装配件任务监视器 
	 * @return {@link AssemblyTaskMonitor}
	 */
	public AssemblyTaskMonitor getAssemblyTaskMonitor() {
		return this.assemblyTaskMonitor;
	}
	
	/**
	 * 服务管理器
	 * @return {@link ServiceAdmin}
	 */
	public ServiceAdmin getServiceAdmin() {
		return this.serviceAdmin;
	}
	
	/**
	 * 运行框架事件监听器列表
	 * @return {@link List},运行框架事件监听器列表
	 */
	List<FrameworkListener> getFrameworkListeners(){
		return this.listeners;
	}
	
	/**
	 * 组件容器
	 * @return {@link LaunchComponentContainer}
	 */
	public LaunchComponentContainer getLaunchComponentContainer() {
		return this.container;
	}
	
	private class FrameworkInitParameters implements InitParameters {

		@Override
		public VariableReplacer createVariableReplacer() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getInitParameter(String name) {
			return DefaultLaunchContext.this.getInitParameter(name);
		}

		@Override
		public List<String> getInitParameterNames() {
			return DefaultLaunchContext.this.getInitParameterNames();
		}
		
	}
}
