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
package com.massyframework.assembly.spi;

import java.util.Collections;
import java.util.List;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyConfig;
import com.massyframework.assembly.support.LogSupporter;

/**
 * 处理器基类，提供初始化、其他处理器查找等方法的封装
 *
 */
public abstract class HandlerBase extends LogSupporter implements Handler {

	private LifecycleManager lifecycleManager;
	
	/**
	 * 
	 */
	public HandlerBase() {
		
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.Handler#init(com.massyframework.assembly.spi.LifecycleManager)
	 */
	@Override
	public final void init(LifecycleManager lifecycleManager) throws Exception {
		if (this.lifecycleManager == null) {
			this.lifecycleManager = lifecycleManager;
			this.setLogger(this.lifecycleManager.getLogger());
			this.init();
		}
	}
	
	/**
	 * 初始化
	 * @throws Exception
	 */
	protected void init() throws Exception{
		
	}
	
	/**
	 * 按<code>handlerType</code>查找首个类型匹配的处理器
	 * @param handlerType 处理器类型
	 * @return {@link T}
	 */
	public <T> T findOne(Class<T> handlerType) {
		return this.lifecycleManager == null ?
				null :
					this.lifecycleManager.findOne(handlerType);
	}
	
	/**
	 * 按<code>handlerType</code>查找所有类型匹配的处理器
	 * @param handlerType 处理器类型
	 * @return {@link List}
	 */
	public <T> List<T> findHandlers(Class<T> handlerType){
		return this.lifecycleManager == null ?
				Collections.emptyList() :
					this.lifecycleManager.findHandlers(handlerType);
	}
	
	/**
	 * 关联的装配件
	 * @return {@link Assembly}, 可能返回null.
	 */
	public Assembly getAssembly() {
		if (this.lifecycleManager != null) {
			return this.lifecycleManager.getAssociatedAssembly();
		}
		return null;
	}
	
	/**
	 * 装配件配置
	 * @return {@link AssemblyConfig},可能返回null.
	 */
	public AssemblyConfig getAssemblyConfig() {
		if (this.lifecycleManager != null) {
			return this.lifecycleManager.getAssemblyConfig();
		}
		return null;
	}
	
	/**
	 * 按<code>handlerType</code>查找首个类型匹配的处理器
	 * @param handlerType 处理器类型
	 * @return {@link T}
	 * @throws HandlerNotFoundException 未找到类型匹配的处理器时抛出的例外。
	 */
	public <T> T getHandler(Class<T> handlerType) throws HandlerNotFoundException{
		if (this.lifecycleManager == null) {
			throw new HandlerNotFoundException(handlerType);
		}
		
		return this.lifecycleManager.getHandler(handlerType);
	}
	
	/**
	 * 生命周期管理器
	 * @return {@link LifecycleManager}
	 */
	public LifecycleManager getLifecycleManager() {
		return this.lifecycleManager;
	}
	

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.spi.Handler#destroy()
	 */
	@Override
	public void destroy() {
		this.setLogger(null);
		this.lifecycleManager = null;
	}

}
