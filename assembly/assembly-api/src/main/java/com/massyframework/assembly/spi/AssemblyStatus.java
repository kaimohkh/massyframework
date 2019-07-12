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

/**
 * 装配件的生命周期状态
 * <ul>
 * <li>准备状态: 等待运行所需的资源就绪，对Framework输出的资源进行匹配</li>
 * <li>就绪状态: 所有运行所需的依赖资源匹配成功，随时可进入工作状态</li>
 * <li>工作状态: 构建{@link AssemblyContext}实例, 并对外输出资源； 当所依赖的资源被回收时，装配件将退回准备阶段</li>
 * </ul>
 */
public enum AssemblyStatus {
	
	/**
	 * 初始化状态，正在执行初始化工作
	 */
	INIT,

	/**
	 * 准备状态
	 * <br>还存有未满足的依赖资源，等待资源准备就绪
	 */
	PREPARE,
	
	/**
	 * 就绪状态
	 * <br>所有依赖资源都已经满足，随时可进入工作状态。
	 * <br>提供Servlet的装配件，因Servlet的延迟加载，可暂时保留在就绪状态。一旦http请求到达，可立刻进入工作状态。
	 */
	READY,
	
	/**
	 * 工作状态
	 * <br>进入工作状态，同时向外部输出资源。
	 * <br>当依赖资源不可用时，会退出工作状态到准备状态
	 */
	WORKING
}
