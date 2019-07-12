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
 * @日   期:  2019年1月25日
 */
package com.massyframework.assembly.runtime;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.monitor.AssemblyTask;
import com.massyframework.assembly.monitor.AssemblyTaskComplatedEvent;
import com.massyframework.assembly.monitor.AssemblyTaskEvent;
import com.massyframework.assembly.monitor.AssemblyTaskListener;
import com.massyframework.assembly.monitor.AssemblyTaskMonitor;
import com.massyframework.assembly.monitor.AssemblyTaskPhases;
import com.massyframework.assembly.monitor.AssemblyTaskPhasesUtils;
import com.massyframework.assembly.monitor.AssemblyTaskStartedEvent;
import com.massyframework.logging.Logger;
import com.massyframework.logging.LoggerAware;

/**
 * 实现{@link AssemblyTaskMonitor}接口的任务监视器
 *
 */
final class DefaultAssemblyTaskMonitor implements AssemblyTaskMonitor, LoggerAware {

	private final AtomicLong number = new AtomicLong();
	private final Map<Long, DefaultAssemblyTask> taskMap =
			new ConcurrentHashMap<Long, DefaultAssemblyTask>();
	private final List<AssemblyTaskListener> listeners = 
			new CopyOnWriteArrayList<>();
	private Logger logger;
	
	/**
	 * 
	 */
	public DefaultAssemblyTaskMonitor() {
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.monitor.AssemblyTaskMonitor#addListener(com.massyframework.assembly.monitor.AssemblyTaskListener)
	 */
	@Override
	public void addListener(AssemblyTaskListener listener) {
		if (listener != null) {
			this.listeners.add(listener);
		}
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.monitor.AssemblyTaskMonitor#startTask(com.massyframework.assembly.Assembly, com.massyframework.assembly.monitor.AssemblyTaskPhases)
	 */
	@Override
	public <T> AssemblyTask startTask(Assembly assembly, AssemblyTaskPhases phases) {
		long id = this.number.getAndIncrement();
		DefaultAssemblyTask result = new DefaultAssemblyTask(id, assembly, phases);
		this.taskMap.put(id, result);
		
		AssemblyTaskStartedEvent event = new AssemblyTaskStartedEvent(result);
		this.applyEvent(event);
		return result;
	}
	
	/**
	 * 执行完成事件
	 * @param task {@link DefaultAssemblyTask}
	 * @param exception {@link Exception},可以为null.
	 */
	private void doComplete(DefaultAssemblyTask task, Exception exception) {
		if (task == null) return;
		
		if (this.taskMap.remove(task.id, task)) {
			AssemblyTaskComplatedEvent event = new AssemblyTaskComplatedEvent(task, exception);
			this.applyEvent(event);
		}
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.monitor.AssemblyTaskMonitor#removeListener(com.massyframework.assembly.monitor.AssemblyTaskListener)
	 */
	@Override
	public void removeListener(AssemblyTaskListener listener) {
		if (listener != null) {
			this.listeners.remove(listener);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.logging.LoggerAware#setLogger(com.massyframework.logging.Logger)
	 */
	@Override
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	/**
	 * 日志记录器
	 * @return {@link Logger}
	 */
	protected Logger getLogger() {
		return this.logger;
	}
	
	/**
	 * 发布事件
	 * @param event
	 */
	protected void applyEvent(AssemblyTaskEvent event) {
		for (AssemblyTaskListener listener: this.listeners) {
			try {
				listener.onEvent(event);
			}catch(Exception e) {
				Logger logger = this.getLogger();
				if (logger != null) {
					if (logger.isErrorEnabled()) {
						logger.error(listener.getClass().getName() + " onEvent() failed.", e);
					}
				}
			}
		}
	}


	private final class DefaultAssemblyTask implements AssemblyTask{
		
		private final long id;
		private final Assembly assembly;
		private final AssemblyTaskPhases phases;
		private final long timestamp;

		/**
		 * 构造方法
		 * @param id {@link long},任务编号
		 * @param content
		 */
		public DefaultAssemblyTask(long id, Assembly assembly, AssemblyTaskPhases phases) {
			this.id = id;
			this.assembly = Objects.requireNonNull(assembly, "\"assembly\" cannot be null.");
			this.phases = phases;
			this.timestamp = System.currentTimeMillis();
		}

	
		/* (non-Javadoc)
		 * @see com.massyframework.assembly.monitor.AssemblyTask#getAssembly()
		 */
		@Override
		public Assembly getAssembly() {
			return this.assembly;
		}

		/* (non-Javadoc)
		 * @see com.massyframework.assembly.monitor.AssemblyTask#getAssemblyTaskPhases()
		 */
		@Override
		public AssemblyTaskPhases getAssemblyTaskPhases() {
			return this.phases;
		}

		/* (non-Javadoc)
		 * @see com.massyframework.assembly.monitor.AssemblyTask#getTimestamp()
		 */
		@Override
		public long getTimestamp() {
			return this.timestamp;
		}

		/* (non-Javadoc)
		 * @see com.massyframework.assembly.monitor.AssemblyTask#complete()
		 */
		@Override
		public void complete() {
			doComplete(this, null);	
		}
		
		/* (non-Javadoc)
		 * @see com.massyframework.assembly.monitor.AssemblyTask#complete(java.lang.Exception)
		 */
		@Override
		public void complete(Exception exception) {
			doComplete(this, exception);	
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DefaultAssemblyTask [");
			builder.append("assembly").append("=").append("\"").append(assembly.getSymbolicName()).append("\"").append(",");
			builder.append("phases=").append("=").append(AssemblyTaskPhasesUtils.toString(this.phases));
			builder.append("]");
			return builder.toString();
		}
	}
}
