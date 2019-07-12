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
 * @日   期:  2019年1月7日
 */
package com.massyframework.assembly;

import java.util.List;

import com.massyframework.lang.Adaptable;
import com.massyframework.logging.Logger;

/**
 * 运行在Massy Framework中的装配件，为程序代码提供一个边界，管理一组服务，并为这组服务注入其所依赖的资源。<br>
 * 
 * <P>
 * 装配件必须有符号名称，且必须唯一，用于在Framework中标识装配件，另外按装配件安装次序，<br>
 * Framework会提供一个长整形数作为其编号， 同样该编号在Framework的一次运行周期中保持唯一。<br>
 * 
 * <P>
 * 装配件生命周期包括以下阶段：
 * <ul>
 * <li>准备状态: 等待运行所需的资源就绪，对Framework输出的资源进行匹配</li>
 * <li>就绪状态: 所有运行所需的依赖资源匹配成功，随时可进入工作状态</li>
 * <li>工作状态: 构建{@link AssemblyContext}实例, 并对外输出资源； 当所依赖的资源被回收时，装配件将退回准备阶段</li>
 * </ul>
 * 
 * 装配件上下文{@link AssemblyContext}在进入工作状态后被创建，通过{@link AssemblyContext}可以获取装配件的服务。<br>
 */
public interface Assembly extends Adaptable, Comparable<Assembly>{
	
	/**
	 * 添加装配件监听器
	 * @param listener 监听器
	 */
	void addListener(AssemblyListener listener);
	
	/**
	 * 根据<code>name</code>获取属性
	 * @param name {@link String},属性名称
	 * @return {@link T},如果对应属性不存在，则返回null.
	 */
	Object getAttribute(String name);
	
	/**
	 * 根据<code>name</code>获取属性,并转换为<code>attrType</code>
	 * @param name {@link String},属性名称
	 * @param attrType {@link Class}, 属性类型
	 * @return {@link T},如果对应属性不存在，则返回null.
	 */
	default <T> T getAttribute(String name, Class<T> attrType) {
		Object result = this.getAttribute(name);
		return result == null ? null : attrType.cast(result);
	}
	
	/**
	 * 属性名称集合
	 * @return {@link List}
	 */
	List<String> getAttributeNames();
		
	/**
	 * 编号，当装配件被创建时，由Framework统一分配。<br>
	 * 编号在Framework的一次生命周期中保持恒定和唯一，在下次启动前被清零
	 * @return {@link long}
	 */
	long getAssemblyId();
	
	/**
	 * 装配件配置
	 * @return {@link AssemblyConfig}
	 */
	AssemblyConfig getAssemblyConfig();
	
	/**
	 * 装配件上下文, 提供聚合服务的查询。<br>
	 * 装配件上下文仅在装配件的进入工作状态后存在，在退出工作状态时被销毁。
	 * @return {@link AssemblyContext},可能返回null.
	 * @see AssemblyContext
	 */
	AssemblyContext getAssemblyContext();
					
	/**
	 * 装配件的类加载器
	 * @return {@link ClassLoader}
	 */
	ClassLoader getAssemblyClassLoader();
					
	/**
	 * 日志记录器
	 * @return {@link Logger}
	 */
	Logger getLogger();
	
	/**
	 * 符号名称，具有唯一性。<br>
	 * 本方法直接返回{@link #getAssemblyConfig()#getSymbolicName()}
	 * @return {@link String}
	 */
	String getSymbolicName();
						
	/**
	 * 是否进入工作状态，{@link #getAssemblyStatus}等于{@link AssemblyStatus#WORKING}
	 * @return <code>true</code>已进入工作状态, <code>false</code>未进入工作状态
	 */
	boolean isWorking();
		
	/**
	 * 移除装配件监听器<br>
	 * @param listener 监听器
	 */
	void removeListener(AssemblyListener listener);
	
	/**
	 * 设置属性
	 * @param name {@link String},属性名
	 * @param value {@link Object}, 属性值
	 * @return {@link Object}, 属性原值，如果之前属性不存在则返回null.
	 */
	Object setAttribute(String name, Object value);
}
