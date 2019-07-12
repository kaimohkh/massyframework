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
 * @日   期:  2019年1月13日
 */
package com.massyframework.assembly.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyRepository;
import com.massyframework.assembly.Framework;
import com.massyframework.assembly.FrameworkEvent;
import com.massyframework.assembly.FrameworkListener;
import com.massyframework.assembly.FrameworkStatus;
import com.massyframework.assembly.monitor.AssemblyTask;
import com.massyframework.assembly.monitor.AssemblyTaskComplatedEvent;
import com.massyframework.assembly.monitor.AssemblyTaskEventAdapter;
import com.massyframework.assembly.monitor.AssemblyTaskMonitor;
import com.massyframework.assembly.monitor.AssemblyTaskPhasesUtils;
import com.massyframework.assembly.monitor.AssemblyTaskStartedEvent;
import com.massyframework.assembly.runtime.assembly.AssemblyBase;
import com.massyframework.assembly.runtime.service.ServiceAdmin;
import com.massyframework.assembly.support.ConsoleReporter;
import com.massyframework.logging.Logger;
import com.massyframework.logging.LoggerAware;
import com.massyframework.logging.LoggerFactory;

/**
 * 提供Framework基础语义的抽象类
 */
abstract class FrameworkBase implements Framework {
	
	private final Logger logger = LoggerFactory.getLogger("[MassyFramework]");
	
	private final AssemblyRepository assemblyRepo;
	private final ServiceAdmin serviceAdmin;
	private final AssemblyTaskMonitor assemblyTaskMonitor;
	private final List<FrameworkListener> listeners ;
	private volatile FrameworkStatus status;
	
	private FrameworkExecutorService executor;
		
	/**
	 * 构造方法
	 * @param assemblyRepo {@link AssemblyRepository},装配件仓储
	 * @param serviceAdmin {@link ServiceAdmin},服务管理器 
	 * @param taskMonitor {@link DefaultAssemblyTaskMonitor},装配件启停任务监视器
	 * @param listeners {@link List},FrameworkListener列表
	 */
	public FrameworkBase(AssemblyRepository assemblyRepo, ServiceAdmin serviceAdmin, AssemblyTaskMonitor taskMonitor, List<FrameworkListener> listeners) {
		this.assemblyRepo = Objects.requireNonNull(assemblyRepo, "\"assemblyRepo\" cannot be null.");
		this.assemblyTaskMonitor = Objects.requireNonNull(taskMonitor, "\"taskMonitor\" cannot be null.");
		this.serviceAdmin = Objects.requireNonNull(serviceAdmin, "\"serviceAdmin\" cannot be null.");
		this.listeners = listeners == null ?
				new CopyOnWriteArrayList<>() : 
					new CopyOnWriteArrayList<>(listeners);
		this.status = FrameworkStatus.INITED;
		
		LoggerAware.maybeToBind(this.assemblyRepo, logger);
		LoggerAware.maybeToBind(this.serviceAdmin, logger);
		LoggerAware.maybeToBind(this.assemblyTaskMonitor, logger);
	}
		
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Framework#getAssemblyRepository()
	 */
	@Override
	public AssemblyRepository getAssemblyRepository() {
		return this.assemblyRepo;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Framework#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return this.logger;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Framework#getStatus()
	 */
	@Override
	public FrameworkStatus getStatus() {
		return this.status;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Framework#start()
	 */
	@Override
	public void start() throws Exception {		
		//创建装配件启停任务监视器
		EventAdapter eventAdapter = new EventAdapter();
		this.assemblyTaskMonitor.addListener(eventAdapter);
		FrameworkEvent event = new FrameworkEvent(this);
		List<FrameworkListener> listeners = new ArrayList<>(this.listeners);
		List<Assembly> assemblies = this.assemblyRepo.getAssemblies();
		Collections.sort(assemblies);
		
		try {
			//先判断内核装配件是否启动
			Assembly kernelAssembly = this.assemblyRepo.getAssembly(0);
			if (!kernelAssembly.isWorking()) {
				((AssemblyBase)kernelAssembly).start();
			}
			
			this.status = FrameworkStatus.STARTING;			
			for (FrameworkListener listener: listeners) {
				try {
					listener.onEvent(event);
				}catch(Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error(listener.getClass().getName() + " onEvent() failed.", e);
					}
				}
			}
			
			
			for (Assembly assembly: assemblies) {
				AssemblyBase base = (AssemblyBase)assembly;
				if (base.getAssemblyId() != 0) {
					new Thread(new StartTask(base)).start();
				}
			}			
			Thread.sleep(1000);
		}finally {		
			eventAdapter.await();
			this.assemblyTaskMonitor.removeListener(eventAdapter);			
		}
		
		this.status = FrameworkStatus.STARTED;
		//反转列表次序
		Collections.reverse(listeners);
		for (FrameworkListener listener: listeners) {
			try {
				listener.onEvent(event);
			}catch(Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error(listener.getClass().getName() + " onEvent() failed.", e);
				}
			}
		}
		this.status = FrameworkStatus.RUNNING;
		
		this.executor = new FrameworkExecutorService();
		ExecutorServiceAware.maybeToBind(this.assemblyRepo, this.executor);
		ExecutorServiceAware.maybeToBind(this.serviceAdmin, this.executor);
		
		ConsoleReporter reporter = new ConsoleReporter();
		reporter.reportStarted(this.getAssemblyRepository());
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Framework#stop()
	 */
	@Override
	public void stop() throws Exception {
		//创建装配件启停任务监视器
		EventAdapter eventAdapter = new EventAdapter();
		this.assemblyTaskMonitor.addListener(eventAdapter);
		FrameworkEvent event = new FrameworkEvent(this);
		List<FrameworkListener> listeners = new ArrayList<>(this.listeners);
		
		try {
			this.status = FrameworkStatus.STOPPING;	
			for (FrameworkListener listener: listeners) {
				try {
					listener.onEvent(event);
				}catch(Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error(listener.getClass().getName() + " onEvent() failed.", e);
					}
				}
			}
			
			List<Assembly> assemblies = this.assemblyRepo.getAssemblies();
			Collections.sort(assemblies);
			for (Assembly assembly: assemblies) {
				AssemblyBase base = (AssemblyBase)assembly;
				if (base.getAssemblyId() != 0) {
					new Thread(new StopTask(base)).start();
				}
			}
			
			Thread.sleep(1000);
		}finally {
			//移除装配件启停任务监视器
			eventAdapter.await();
			this.assemblyTaskMonitor.removeListener(eventAdapter);
		}
		
		this.status = FrameworkStatus.STOPPED;
		//反转列表次序
		Collections.reverse(listeners);
		for (FrameworkListener listener: listeners) {
			try {
				listener.onEvent(event);
			}catch(Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error(listener.getClass().getName() + " onEvent() failed.", e);
				}
			}
		}
		
		try {
			Assembly kernelAssembly = this.assemblyRepo.getAssembly(0);
			((AssemblyBase)kernelAssembly).stop();
		}catch(Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			}
		}
		
		this.status = FrameworkStatus.INITED;
		
		ExecutorServiceAware.maybeToBind(this.assemblyRepo, null);
		ExecutorServiceAware.maybeToBind(this.serviceAdmin, null);
		this.executor.shutdown();
		while (!this.executor.isShutdown()) {
			try {
				Thread.sleep(100);
			}catch(Exception e) {
				
			}
		}
		this.executor = null;
	}
	
	private class StartTask implements Runnable {
		
		private AssemblyBase assembly;
		
		/**
		 * 构造方法
		 * @param assembly
		 */
		public StartTask(AssemblyBase assembly) {
			this.assembly = assembly;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				this.assembly.start();
			}catch(Exception e) {
				Logger logger = getLogger();
				if (logger.isErrorEnabled()) {
					logger.error("start " + assembly + " failed.",e);
				}
			}
		}
		
	}
	
	private class StopTask implements Runnable {
		
		private AssemblyBase assembly;
		
		/**
		 * 构造方法
		 * @param assembly
		 */
		public StopTask(AssemblyBase assembly) {
			this.assembly = assembly;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				this.assembly.stop();
			}catch(Exception e) {
				Logger logger = getLogger();
				if (logger.isErrorEnabled()) {
					logger.error("stop " + assembly + " failed.",e);
				}
			}
		}
		
	}
	
	
	/**
	 * 装配件任务事件监听器
	 */
	private class EventAdapter extends AssemblyTaskEventAdapter {
		
		private Set<AssemblyTask> taskSet = new CopyOnWriteArraySet<>();
		private CountDownLatch latch = new CountDownLatch(1);
		
		/**
		 * 构造方法
		 */
		public EventAdapter() {
		}

		/* (non-Javadoc)
		 * @see com.massyframework.assembly.monitor.AssemblyTaskEventAdapter#doEvent(com.massyframework.assembly.monitor.AssemblyTaskStartedEvent)
		 */
		@Override
		protected void doEvent(AssemblyTaskStartedEvent event) {
			AssemblyTask task = event.getAssemblyTask();
			Logger logger = task.getAssembly().getLogger();
			if (logger.isDebugEnabled()) {
				logger.debug("start task: phases=" + 
						AssemblyTaskPhasesUtils.toString(task.getAssemblyTaskPhases()));
			}
			this.taskSet.add(event.getAssemblyTask());
		}

		/* (non-Javadoc)
		 * @see com.massyframework.assembly.monitor.AssemblyTaskEventAdapter#doEvent(com.massyframework.assembly.monitor.AssemblyTaskComplatedEvent)
		 */
		@Override
		protected void doEvent(AssemblyTaskComplatedEvent event) {
			AssemblyTask task = event.getAssemblyTask();
			Logger logger = task.getAssembly().getLogger();
			if (event.getException() != null) {
				logger.error("task do completed with error: phases= " +
						AssemblyTaskPhasesUtils.toString(task.getAssemblyTaskPhases()), event.getException());
			}else {
				logger.debug("task do completed: phases=" + 
						AssemblyTaskPhasesUtils.toString(task.getAssemblyTaskPhases()));
			}
			this.taskSet.remove(event.getAssemblyTask());
			
			if (taskSet.isEmpty()) {
				if (this.latch.getCount() > 0) {
					this.latch.countDown();
				}
			}
		}
		
		/**
		 * 等候，直到任务都执行完毕
		 */
		public void await() {
			try {
				this.latch.countDown();
			}catch(Exception e) {
				
			}
		}
	}
}
