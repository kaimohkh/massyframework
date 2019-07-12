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
 * @日   期:  2019年1月31日
 */
package com.massyframework.assembly.runtime.assembly.handling;

import com.massyframework.assembly.container.Container;
import com.massyframework.assembly.initparam.InitParameters;

/**
 * 生命周期初始化接口
 *
 */
public interface LifecycleInitializer {

	/**
	 * 初始化
	 * @param frameworkInitParams 运行框架启动的初始化参数
	 * @param kernelContainer 内核组件容器
	 * @throws Exception {@link Exception},发生非预期的例外
	 */
	void init(InitParameters frameworkInitParams, Container kernelContainer) throws Exception;
}
