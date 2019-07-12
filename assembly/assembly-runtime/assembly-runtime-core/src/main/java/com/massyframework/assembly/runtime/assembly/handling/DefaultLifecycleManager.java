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
 * @日   期:  2019年1月19日
 */
package com.massyframework.assembly.runtime.assembly.handling;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyConfig;
import com.massyframework.assembly.AssemblyContext;
import com.massyframework.assembly.Framework;
import com.massyframework.assembly.container.Container;
import com.massyframework.assembly.initparam.InitParameters;
import com.massyframework.assembly.monitor.AssemblyTask;
import com.massyframework.assembly.monitor.AssemblyTaskMonitor;
import com.massyframework.assembly.monitor.AssemblyTaskPhases;
import com.massyframework.assembly.runtime.assembly.AssemblyBase;
import com.massyframework.assembly.spi.ActivationHandler;
import com.massyframework.assembly.spi.AssemblyStatus;
import com.massyframework.assembly.spi.ContainerTypeNotSupportException;
import com.massyframework.assembly.spi.Handler;
import com.massyframework.assembly.spi.HandlerFactory;
import com.massyframework.assembly.spi.LifecycleManager;
import com.massyframework.assembly.spi.ReadyingHandler;
import com.massyframework.assembly.spi.WebComponentHandler;
import com.massyframework.assembly.util.ContainerUtils;
import com.massyframework.lang.util.ClassLoaderUtils;

/**
 * 缺省的装配件生命周期管理
 */
final class DefaultLifecycleManager extends HandlerRegistry implements LifecycleManager, LifecycleInitializer {
	
	private final AssemblyBase assembly;
	private final AssemblyConfig config;
	private final AtomicReference<AssemblyStatus> status;
	private volatile boolean runAtJ2ee = true;

	/**
	 * 构造方法
	 * @param config {@link AssemblyConfig},装配件配置
	 * @param classLoader {@link ClassLoader},装配件类加载器
	 */
	public DefaultLifecycleManager(AssemblyBase assembly, AssemblyConfig config) {
		this.assembly = Objects.requireNonNull(assembly, "\"assembly\" cannot be null.");
		this.config = Objects.requireNonNull(config, "\"config\" cannot be null.");
		this.status = new AtomicReference<AssemblyStatus>(AssemblyStatus.INIT);
	}
	

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.LifecycleManager#getAssociatedAssembly()
	 */
	@Override
	public Assembly getAssociatedAssembly() {
		return this.assembly;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.LifecycleManager#getAssemblyConfig()
	 */
	@Override
	public AssemblyConfig getAssemblyConfig() {
		return this.config;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.LifecycleManager#getAssemblyStatus()
	 */
	@Override
	public AssemblyStatus getAssemblyStatus() {
		return this.status.get();
	}
	

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.assembly.handling.LifecycleInitializer#init(com.massyframework.assembly.support.InitParameters, com.massyframework.container.Container)
	 */
	@Override
	public void init(InitParameters frameworkInitParams, Container kernelContainer) throws Exception {
        if (this.status.get() == AssemblyStatus.INIT) {
			
			String containerName = this.getAssemblyConfig().getContainerName();
			if (containerName != null) {
				this.logDebug("require conatiner :" + containerName + ".");
				//加载和注册容器处理器
				HandlerFactory factory = kernelContainer.getComponent(containerName, HandlerFactory.class);
				Handler handler = factory.createHandler(this.getAssemblyConfig());
				if (handler == null) {
					String type = ContainerUtils.getContainerType(this.getAssemblyConfig());
					throw new ContainerTypeNotSupportException(type);
				}
				this.register(handler);
			}
			
			this.runAtJ2ee = Framework.ENVIRONMENT_J2EE.equals(
					frameworkInitParams.getInitParameter(Framework.ENVIRONMENT));
			if (this.runAtJ2ee) {
				List<WebComponentHandler> handlers = 
						this.findHandlers(WebComponentHandler.class);
				for (WebComponentHandler handler: handlers) {
					handler.registWebComponent();
				}
			}
			
			this.status.set(AssemblyStatus.PREPARE);
		}
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.LifecycleManager#start()
	 */
	@Override
	public void start() throws Exception {
		ClassLoader contextLoader =	ClassLoaderUtils.setThreadContextClassLoader(
				this.getAssociatedAssembly().getAssemblyClassLoader());
		AssemblyTaskMonitor taskMonitor = this.getHandler(AssemblyTaskMonitor.class);
		AssemblyTask task = taskMonitor.startTask(this.getAssociatedAssembly(), AssemblyTaskPhases.START);
		
		try {
			if (this.isPrapered()){
				this.doStart();
			}
			task.complete();
		}catch(Exception e) {
			task.complete(e);
			throw e;
		}finally {
			ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
		}
	}
	
	/**
	 * 执行启动
	 * 
	 * @throws Exception
	 */
	protected synchronized void doStart() throws Exception {
		if (this.getAssemblyStatus() == AssemblyStatus.READY) {
			if (this.isConfigureCompleted()) {
				List<ActivationHandler> handlers = this.findHandlers(ActivationHandler.class);
				for (ActivationHandler actHandler : handlers) {
					actHandler.doStarting();
				}
				
				this.applyActivatedEvent();

				this.getHandler(AssemblyContext.class);
				this.status.set(AssemblyStatus.WORKING);
				
				AssemblyEventPublisherHandler publisherHandler =
						this.getHandler(AssemblyEventPublisherHandler.class);
				this.logInfo("assembly is working.");
				publisherHandler.postActivedEvent();
				
			}
		}
	}
	
	/**
	 * 配置是否完成
	 * 
	 * @return {@link boolean}, <code>true</code>完成，可以进入工作状态,
	 *         <code>false</code>未完成，不能进入工作状态
	 */
	protected boolean isConfigureCompleted() {
		List<WebComponentHandler> handlers = this.findHandlers(WebComponentHandler.class);
		if (!handlers.isEmpty()) {
			if (!this.runAtJ2ee) {
				return false;
			}else {
				for (WebComponentHandler handler: handlers) {
					if (!handler.inited()) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 检查是否完成准备工作
	 * @return
	 * @throws Exception
	 */
	protected synchronized boolean isPrapered() throws Exception {
		if (this.getAssemblyStatus() == AssemblyStatus.PREPARE) {
			DependencyServiceHandler dependencyHandler = this
					.findOne(DependencyServiceHandler.class);
			if (dependencyHandler != null) {
				if (!dependencyHandler.isAllMatched()) {
					return false;
				}
			} 
			
			// 就绪前处理
			List<ReadyingHandler> handlers = this.findHandlers(ReadyingHandler.class);
			for (ReadyingHandler handler : handlers) {
				handler.readying();
			}

			this.status.set(AssemblyStatus.READY);
			this.logDebug("assembly is readied.");
			this.applyReadiedEvent();
			AssemblyEventPublisherHandler publisher =
					this.getHandler(AssemblyEventPublisherHandler.class);
			publisher.postReadiedEvent();
			return true;
		}
		return this.getAssemblyStatus() == AssemblyStatus.READY;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.LifecycleManager#stop()
	 */
	@Override
	public void stop() throws Exception {
		ClassLoader contextLoader =	ClassLoaderUtils.setThreadContextClassLoader(
				this.getAssociatedAssembly().getAssemblyClassLoader());
		AssemblyTaskMonitor taskMonitor = this.getHandler(AssemblyTaskMonitor.class);
		AssemblyTask task = taskMonitor.startTask(this.getAssociatedAssembly(), AssemblyTaskPhases.STOP);
		
		try {
			this.doStop();
			this.doUnready();
			task.complete();
		}catch(Exception e){
			task.complete(e);
			throw e;
		}finally {
			ClassLoaderUtils.setThreadContextClassLoader(contextLoader);
		}
	}
	
	/**
	 * 执行启动
	 * 
	 * @throws Exception
	 */
	protected synchronized void doStop() throws Exception {
		if (this.getAssemblyStatus() == AssemblyStatus.WORKING) {

			this.applyInactivatingEvent();
			this.status.set(AssemblyStatus.READY);
			this.logDebug("assembly is readied.");

			List<ActivationHandler> handlers = this.findHandlers(ActivationHandler.class);
			for (ActivationHandler actHandler : handlers) {
				try {
					actHandler.doStopped();
				} catch (Exception e) {
					this.logWarn(actHandler + " doStopped() failed.", e);
				}
			}
		}
	}

	/**
	 * 执行退出就绪状态
	 * 
	 * @throws Exception
	 */
	protected void doUnready() throws Exception {
		if (this.getAssemblyStatus() == AssemblyStatus.READY) {
			DependencyServiceMatcher dependencyHandler = this
					.findOne(DependencyServiceMatcher.class);
			if (dependencyHandler != null) {
				if (!dependencyHandler.isAllMatched()) {
					AssemblyEventPublisherHandler publisher =
							this.getHandler(AssemblyEventPublisherHandler.class);
					publisher.sendUnreadingEvent();
					this.applyUnreadyingEvent();
					this.status.set(AssemblyStatus.PREPARE);
	
					List<ReadyingHandler> handlers = this.findHandlers(ReadyingHandler.class);
					for (ReadyingHandler handler : handlers) {
						handler.unreadyed();
					}
					this.logDebug("assembly is prepared.");
				}
			}
		}
	}
	
	

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.assembly.handling.HandlerRegistry#initHandler(com.massyframework.assembly.spi.Handler)
	 */
	@Override
	protected <T> void initHandler(Handler handler) throws Exception {
		handler.init(this);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.assembly.handling.HandlerRegistry#getSymbolicName()
	 */
	@Override
	protected String getSymbolicName() {
		return this.config.getSymbolicName();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DefaultLifecycleManager [symbolicName=" + config.getSymbolicName() + "]";
	}

	
}
