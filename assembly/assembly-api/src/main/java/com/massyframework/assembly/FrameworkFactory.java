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
 * @日   期:  2019年1月9日
 */
package com.massyframework.assembly;

import com.massyframework.assembly.launching.LaunchContext;
import com.massyframework.assembly.launching.PresetHandler;

/**
 * 运行框架工厂,提供创建运行框架的方法
 */
public interface FrameworkFactory {

	/**
	 * 使用<code>configuration</code>和<code>listener</code>创建运行框架<br>
	 * 
	 * @param handler {@link PresetHandler},预置处理器，在{@link LaunchContext}创建时，第一时间对其进行初始化。
	 * @return {@link Framework}
	 */
	Framework createFramework(PresetHandler handler);
}
