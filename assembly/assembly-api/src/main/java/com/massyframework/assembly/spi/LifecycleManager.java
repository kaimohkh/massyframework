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

import java.util.List;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyConfig;
import com.massyframework.logging.Logger;

/**
 * 装配件的生命周期管理
 */
public interface LifecycleManager {
	
	/**
	 * 添加监听器
	 * @param listener {@link LifecycleListener}，监听器
	 */
	void addListener(LifecycleListener listener);
		
	/**
	 * 按<code>handlerType</code>查找首个类型匹配的处理器
	 * @param handlerType 处理器类型
	 * @return {@link T}, 无匹配的可返回null.
	 */
	<T> T findOne(Class<T> handlerType);
	
	/**
	 * 按<code>handlerType</code>查找所有类型匹配的处理器
	 * @param handlerType 处理器类型
	 * @return {@link List}
	 */
	<T> List<T> findHandlers(Class<T> handlerType);
		
	/**
	 * 获取关联的装配件
	 * @return {@link Assembly}
	 */
	Assembly getAssociatedAssembly();
	
	/**
	 * 装配件配置
	 * @return {@link AssemblyConfig}
	 */
	AssemblyConfig getAssemblyConfig();
	
	/**
	 * 装配件所处生命周期阶段
	 * @return {@link AssemblyStatus}
	 */
	AssemblyStatus getAssemblyStatus();
	
	/**
	 * 按<code>handlerType</code>查找类型匹配的处理器
	 * @param handlerType 处理器类型
	 * @return {@link T}
	 * @throws HandlerNotFoundException 如果未找到匹配的处理器，则抛出例外
	 */
	<T> T getHandler(Class<T> handlerType) throws HandlerNotFoundException;
	
	/**
	 * 日志记录器
	 * @return {@link Logger}
	 */
	Logger getLogger();
    
    /**
	 * 启动装配件
	 * @throws Exception 发生非预期的例外
	 */
	void start() throws Exception;
	
	/**
	 * 停止装配件运行
	 * @throws Exception 发生非预期的例外
	 */
	void stop() throws Exception;
	
	/**
	 * 注册处理器
	 * @param handler {@link T},处理器实例
	 * @return {@link ReferenceRegistration}
	 */
	<T> HandlerRegistration<T> register(T handler) throws Exception;
	
	/**
	 * 移除监听器
	 * @param listener {@link LifecycleListener},监听器
	 */
	void removeListener(LifecycleListener listener);
}
