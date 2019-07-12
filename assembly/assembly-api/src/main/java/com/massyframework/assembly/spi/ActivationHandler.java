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
 * @日   期:  2019年1月24日
 */
package com.massyframework.assembly.spi;

/**
 * 装配件激活处理器
 */
public interface ActivationHandler {
	
	/**
	 * 启动,使得进入激活状态
	 * @throws Exception
	 */
	void doStarting() throws Exception;
	
	/**
	 * 已停止，退出激活状态
	 * @throws Exception
	 */
	void doStopped() throws Exception;

}
