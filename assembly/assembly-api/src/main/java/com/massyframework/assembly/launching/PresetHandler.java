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

import com.massyframework.lang.Orderable;

/**
 * 预置处理器，在启动上下文创建后，第一时间对上下文执行初始化处理。
 * @author Huangkaihui
 *
 */
public interface PresetHandler extends Orderable{
	
	/**
	 * 执行启动上下文的初始化工作
	 * @param context {@link LaunchContext}
	 * @throws Exception 初始化时发生的非预期例外
	 */
	void init(LaunchContext context) throws Exception;
}
