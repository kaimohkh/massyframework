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
 * Date:   2019年6月2日
 */
package com.massyframework.assembly.launching;

import java.net.URL;
import java.util.function.Consumer;

import com.massyframework.assembly.AssemblyRegistration;
import com.massyframework.assembly.Framework;
import com.massyframework.assembly.FrameworkListener;
import com.massyframework.assembly.ParseException;
import com.massyframework.assembly.SymbolicNameExistsException;
import com.massyframework.assembly.container.ComponentsException;
import com.massyframework.assembly.domain.AssemblyDomainFileNotFoundException;
import com.massyframework.assembly.domain.SignatureVerificationFailedException;
import com.massyframework.assembly.service.ServiceProperties;
import com.massyframework.logging.Logger;

/**
 * 启动上下文，提供设置启动的初始化参数、注册装配件，注册内核服务等方法
 * @author Huangkaihui
 *
 */
public interface LaunchContext {
	
	/**
	 * 添加组件, 组件将由编号为0的内核装配件进行管理
	 * @param cName {@link String},组件在容器中的名称,具有唯一性
	 * @param component {@link C},组件实例
	 * @throws ComponentsException 添加组件时发生的例外
	 */
	<C> void addComponent(String cName, C component) throws ComponentsException;
	
	/**
	 * 添加可注册为服务的组件, 组件将由编号为0的内核装配件进行管理,并在{@link Framework#start()}方法中被注册为服务.
	 * @param cName {@link String},组件在容器中的名称,具有唯一性
	 * @param component {@link C},组件实例
	 * @param props {@link ServiceProperties},服务属性
	 * @throws ComponentsException 添加组件时发生的例外
	 */
	<C> void addComponent(String cName, C component, ServiceProperties props) throws ComponentsException;
	
	/**
	 * 添加运行框架事件监听器
	 * @param listener {@link FrameworkListener},运行框架事件监听器
	 */
	void addListener(FrameworkListener listener);
		
	/**
	 * 初始化完成后执行
	 * @param consumer {@link Consumer}
	 */
	void afterInitialized(Consumer<LaunchContext> consumer);
	
	/**
	 * 使用<code>cName</code>获取指定名称的组件
	 * @param cName {@link String},组件在容器中的名称
	 * @return {@link Object},组件不存在则返回null.
	 */
	Object getComponent(String cName);
	
	/**
	 * 使用<code>componentType</code>获取组件实例<br>
	 * 本方法实际调用{@link #getComponent(componentType.getName, componentType)}。
	 * @param componentType {@link Class},组件类型
	 * @return {@link C}
	 */
	default <C> C getComponent(Class<C> componentType) {
		return this.getComponent(componentType.getName(), componentType);
	}
	
	/**
	 * 按<code>cName</code>获取组件实例，并按指定的<code>componentType</code>返回
	 * @param cName {@link String},组件在容器中的名称
	 * @param componentType {@link Class},组件类型
	 * @return {@link C}, 组件不存在则返回null.
	 */
	default <C> C getComponent(String cName, Class<C> componentType) {
		Object result = this.getComponent(cName);
		return result == null ? null : componentType.cast(result);
	}
	
	/**
	 * 获取<code>name</code>对应的初始化参数值
	 * @param name {@link String},参数名
	 * @return {@link String},参数值，参数不存在返回null.
	 */
	String getInitParameter(String name);
	
	/**
	 * 获取<code>name</code>对应的参数值，参数不存在返回<code>defaultValue</code>.
	 * @param name {@link String},参数名
	 * @param defaultValue {@link String},缺省值
	 * @return {@link String},参数值
	 */
	default String getInitParameter(String name, String defaultValue) {
		String result = this.getInitParameter(name);
		return result == null ? defaultValue : result;
	}
	
	/**
	   * 日志记录器
	 * @return {@link Logger}
	 */
	Logger getLogger();
	/**
	   *   设置初始化参数
	 * @param name {@link String},参数名
	 * @param value {@link String},参数值
	 * @return {@link boolean},返回<code>true</code>表示初始化参数设置成功，
	 * 	返回<code>false</code>表示初始化参数已经存在，设置失败。
	 */
	boolean setInitParameter(String name, String value);
	
	/**
	 * 注册装配件
	 * @param symbolicName {@link String},装配件名称
	 * @param assemblyClassLoader {@link ClassLoader},加载装配件内部组件的类加载器
	 * @return {@link AssemblyRegistration}
	 * @throws SymbolicNameExistsException 装配件符号名称已经存在时抛出的例外
	 * @throws AssemblyDomainFileNotFoundException 装配域名文件未找到时抛出的例外
	 * @throws SignatureVerificationFailedException 装配域名文件签名无效时抛出例外
	 * @throws ParseException 配置文件解析失败时抛出的例外
	 */
	default AssemblyRegistration registerAssembly(String symbolicName, 
			ClassLoader assemblyClassLoader) 
					throws SymbolicNameExistsException,
						AssemblyDomainFileNotFoundException,
						SignatureVerificationFailedException, 
						ParseException{
		return this.registerAssembly(symbolicName, null, assemblyClassLoader);
	}
	
	/**
	 * 注册装配件
	 * @param symbolicName {@link String},装配件名称
	 * @param assemblyXmlUrl {@link URL}, 装配件定义文件的路径
	 * @param assemblyClassLoader {@link ClassLoader},加载装配件内部组件的类加载器
	 * @return {@link AssemblyRegistration}
	 * @throws SymbolicNameExistsException 装配件符号名称已经存在时抛出的例外
	 * @throws AssemblyDomainFileNotFoundException 装配域名文件未找到时抛出的例外
	 * @throws SignatureVerificationFailedException 装配域名文件签名无效时抛出例外
	 * @throws ParseException 配置文件解析失败时抛出的例外
	 */
	AssemblyRegistration registerAssembly(String symbolicName, 
			URL assemblyXmlUrl, ClassLoader assemblyClassLoader) 
					throws SymbolicNameExistsException,
						AssemblyDomainFileNotFoundException,
						SignatureVerificationFailedException, 
						ParseException;
	
	/**
	   *   判断是否运行在J2EE环境下
	 * @return {@link boolean},返回<code>true</code>表示运行在J2EE下.
	 */
	default boolean runOnJ2EE() {
		return Framework.ENVIRONMENT_J2EE.equals(
				this.getInitParameter(Framework.ENVIRONMENT, Framework.ENVIRONMENT_J2SE));
	}
	
	/**
	 * 是否运行在开发模式下
	 * @return {@link boolean}, 返回<code>true</code>表示是，否则返回<code>false</code>
	 */
	boolean runOnDevelopMode();
}
