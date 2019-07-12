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
 * 当装配件附载在Web组件上时，典型如同Spring的ContextLoaderListener,DispatcherServlet等组件，需要在系统启动阶段，向ServletContext动态注册Web组件。<br>
 * 同样，装配件必须等到这些注册的组件，执行了相应的Web init方法后，才能进入工作状态。
 * 实现接口的方法，可以在{@link #registWebComponent()}中注册Web组件，装配件可以根据{@link #inited()}方法判断Web组件的web init方法是否被执行。
 */
public interface WebComponentHandler {
	
	/**
	 * 向Web环境注册ServletContextListener，Filter, Servlet等组件。
	 * 仅当运行在j2ee环境下才调用本方法。
	 * @throws Exception {@link Exception},初始化发生的非预期例外
	 */
	void registWebComponent() throws Exception;

	/**
	 * 在Web场景下注册的组件是否已准备就绪？<br>
	 * Servlet的实例化要视注册时的选项，如果是首次请求时才实例化，那在发起首次请求前，本方法只能返回<code>false</code>
	 * @return {@link boolean},返回<code>true</code>表示已就绪，否则返回<code>false</code>
	 */
	boolean inited();
}
