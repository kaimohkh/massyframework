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
 * @日   期:  2019年2月18日
 */
package com.massyframework.assembly.support;

/**
 * 实现本接口的类，能支持启动停止处理
 *
 */
public interface StartStopEnable {

	/**
	 * 启动
	 * @throws Exception 启动阶段发生非预期的例外
	 */
	void start() throws Exception;
	
	/**
	 * 停止
	 * @throws Exception 停止阶段发生非预期的例外
	 */
	void stop() throws Exception;
}
