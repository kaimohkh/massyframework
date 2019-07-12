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
 * @日   期:  2019年1月14日
 */
package com.massyframework.assembly.runtime.assembly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyConfig;
import com.massyframework.assembly.AssemblyContext;
import com.massyframework.assembly.AssemblyListener;
import com.massyframework.assembly.DependencyManager;
import com.massyframework.assembly.container.Container;
import com.massyframework.assembly.initparam.InitParameters;
import com.massyframework.assembly.runtime.assembly.handling.AssemblyEventPublisherHandler;
import com.massyframework.assembly.runtime.assembly.handling.DependencyServiceHandler;
import com.massyframework.assembly.runtime.assembly.handling.LifecycleInitializer;
import com.massyframework.assembly.runtime.assembly.handling.LifecycleManagerFactory;
import com.massyframework.assembly.runtime.service.ServiceAdmin;
import com.massyframework.assembly.service.ServiceRepository;
import com.massyframework.assembly.spi.AssemblyStatus;
import com.massyframework.assembly.spi.LifecycleManager;
import com.massyframework.logging.Logger;
import com.massyframework.logging.LoggerAware;
import com.massyframework.logging.LoggerFactory;

/**
 * 提供装配件语义的基类
 */
public class AssemblyBase implements Assembly {
	
	private final long id;	
	private final LifecycleManager lifecycleManager;
	private final Logger logger;
	private final ClassLoader classLoader;
	private final ServiceRepository serviceRepository;
	private final Map<String, Object> attributeMap;
	

	/**
	 * 构造方法
	 * @param id 装配件编号
	 * @param lifecycleManager {@link LifecycleManager},生命周期管理器
	 * @param serviceAdmin {@link ServiceAdmin}, 服务管理器
	 * @param handlers {@link List},装配件处理器列表
	 */
	AssemblyBase(long id, AssemblyConfig config, ClassLoader classLoader, ServiceAdmin serviceAdmin, List<Object> handlers) {
		this.id = id;
		this.classLoader = Objects.requireNonNull(classLoader, "\"classLoader\" cannot be null.");
		this.lifecycleManager =
				LifecycleManagerFactory.createLifecycleManager(this, config);
		Objects.requireNonNull(serviceAdmin, "\"serviceAdmin\" cannot be null.");
		this.logger = LoggerFactory.getLogger("[Assembly:id=" + id + ",symbolicName=" + config.getSymbolicName()  +  "]");
		this.serviceRepository = serviceAdmin.getServiceRepository(this);
		this.attributeMap = new HashMap<String, Object>();
		
		LoggerAware.maybeToBind(this.lifecycleManager, this.logger);
		
		if (handlers != null) {
			for (Object handler: handlers) {
				try {
					this.lifecycleManager.register(handler);
				}catch(Exception e) {
					logger.error("register handler falied:" + handler.getClass().getName() + ".");
				}
			}
		}
		
	}
	
	@Override
	public <T> T getAdapter(Class<T> adaptType) {
		if (adaptType == ServiceRepository.class) {
			return adaptType.cast(this.serviceRepository);
		}
		
		if (adaptType == DependencyManager.class) {
			return adaptType.cast(
					new AssemblyDependencyManager(
							this.lifecycleManager.findOne(DependencyServiceHandler.class)));
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Assembly#addListener(com.massyframework.assembly.AssemblyListener)
	 */
	@Override
	public void addListener(AssemblyListener listener) {
		AssemblyEventPublisherHandler handler =
				this.lifecycleManager.getHandler(AssemblyEventPublisherHandler.class);
		handler.addListener(listener);
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Assembly#getAttribute(java.lang.String)
	 */
	@Override
	public synchronized Object getAttribute(String name) {
		if (name == null) return null;
		return this.attributeMap.get(name);
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Assembly#getAttributeNames()
	 */
	@Override
	public synchronized List<String> getAttributeNames() {
		return new ArrayList<>(this.attributeMap.keySet());
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Assembly#getAssemblyId()
	 */
	@Override
	public final long getAssemblyId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Assembly#getAssemblyConfig()
	 */
	@Override
	public final AssemblyConfig getAssemblyConfig() {
		return this.lifecycleManager.getAssemblyConfig();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Assembly#getAssmeblyContext()
	 */
	@Override
	public final AssemblyContext getAssemblyContext() {
		return this.lifecycleManager.findOne(AssemblyContext.class);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Assembly#getAssemblyClassLoader()
	 */
	@Override
	public final ClassLoader getAssemblyClassLoader() {
		return this.classLoader;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Assembly#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return this.lifecycleManager.getLogger();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Assembly#getSymbolicName()
	 */
	@Override
	public final String getSymbolicName() {
		return this.getAssemblyConfig().getSymbolicName();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Assembly#isWorking()
	 */
	@Override
	public final boolean isWorking() {
		return this.lifecycleManager.getAssemblyStatus() == AssemblyStatus.WORKING;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Assembly#removeListener(com.massyframework.assembly.AssemblyListener)
	 */
	@Override
	public void removeListener(AssemblyListener listener) {
		AssemblyEventPublisherHandler handler =
				this.lifecycleManager.getHandler(AssemblyEventPublisherHandler.class);
		handler.removeListener(listener);		
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.Assembly#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public synchronized Object setAttribute(String name, Object value) {
		if (name == null) return null;
		return this.attributeMap.put(name, value);
	}

	/**
	 * 初始化
	 * @param initParams (@link InitParameters}, 内核的初始化参数
	 * @param kernel {@link Container},内核的容器
	 * @throws Exception
	 */
	public final void init(InitParameters initParams, Container kernel) throws Exception {
		if (this.lifecycleManager instanceof LifecycleInitializer) {
			((LifecycleInitializer)this.lifecycleManager).init(initParams, kernel);
		}
	}
	
	/**
	 * 启动
	 * @throws Exception
	 */
	public final void start() throws Exception{
		this.lifecycleManager.start();
	}
	
	/**
	 * 停止
	 * @throws Exception
	 */
	public final void stop() throws Exception{
		this.lifecycleManager.stop();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public final int compareTo(Assembly o) {
		return Long.compare(this.getAssemblyId(), o.getAssemblyId());
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssemblyBase other = (AssemblyBase) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		return "AssemblyBase [id=" + id + ", symbolicName=" + getSymbolicName() + "]";
	}
	
}
