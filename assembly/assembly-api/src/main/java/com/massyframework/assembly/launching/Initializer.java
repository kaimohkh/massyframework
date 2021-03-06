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

/**
   *   用于在启动阶段，向Framework注册内核服务、装配件
 * @author Huangkaihui
 *
 */
public interface Initializer {

	/**
	 * 执行配置处理
	 * @param context {@link LaunchContext},启动上下文 
	 */
	void init(LaunchContext context);
}
