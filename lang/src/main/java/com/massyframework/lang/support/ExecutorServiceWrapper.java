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
package com.massyframework.lang.support;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 执行服务封装器
 */
public class ExecutorServiceWrapper implements ExecutorService {

	private volatile ExecutorService executor;
	
	/**
	 * 
	 */
	public ExecutorServiceWrapper() {
		
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
	 */
	@Override
	public void execute(Runnable command) {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			executor.execute(command);
		}else {
			new Thread(command).start();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.ExecutorService#shutdown()
	 */
	@Override
	public void shutdown() {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			executor.shutdown();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.ExecutorService#shutdownNow()
	 */
	@Override
	public List<Runnable> shutdownNow() {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			return executor.shutdownNow();
		}
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.ExecutorService#isShutdown()
	 */
	@Override
	public boolean isShutdown() {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			return this.executor.isShutdown();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.ExecutorService#isTerminated()
	 */
	@Override
	public boolean isTerminated() {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			return executor.isTerminated();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.ExecutorService#awaitTermination(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			return executor.awaitTermination(timeout, unit);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.ExecutorService#submit(java.util.concurrent.Callable)
	 */
	@Override
	public <T> Future<T> submit(Callable<T> task) {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			return executor.submit(task);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.ExecutorService#submit(java.lang.Runnable, java.lang.Object)
	 */
	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			return executor.submit(task, result);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.ExecutorService#submit(java.lang.Runnable)
	 */
	@Override
	public Future<?> submit(Runnable task) {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			return executor.submit(task);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.ExecutorService#invokeAll(java.util.Collection)
	 */
	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			return executor.invokeAll(tasks);
		}
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.ExecutorService#invokeAll(java.util.Collection, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			return executor.invokeAll(tasks);
		}
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.ExecutorService#invokeAny(java.util.Collection)
	 */
	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			return executor.invokeAny(tasks);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.ExecutorService#invokeAny(java.util.Collection, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		ExecutorService executor = this.getExecutorService();
		if (executor != null) {
			return executor.invokeAny(tasks,timeout, unit);
		}
		return null;
	}
	
	/**
	 * 获取执行服务
	 * @return {@link ExecutorService}
	 */
	public ExecutorService getExecutorService() {
		return this.executor;
	}
	
	/**
	 * 设置执行服务
	 * @param executorService {@link ExeutorService}
	 */
	public void setExecutorService(ExecutorService executorService) {
		this.executor = executorService;
	}

}
