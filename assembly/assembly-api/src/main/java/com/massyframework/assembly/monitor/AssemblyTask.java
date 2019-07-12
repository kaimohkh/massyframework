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
 * @日   期:  2019年1月25日
 */
package com.massyframework.assembly.monitor;

import com.massyframework.assembly.Assembly;

/**
 * 装配件启停任务
 */
public interface AssemblyTask {
	
	/**
	 * 执行任务的装配件
	 * @return {@link Assembly}
	 */
	Assembly getAssembly();
	
	/**
	 * 启停任务阶段
	 * @return {@link AssemblyTaskPhases}
	 */
	AssemblyTaskPhases getAssemblyTaskPhases();
				
	/**
	 * 开始执行时间戳
	 * @return {@link long}
	 */
	long getTimestamp();
		
	/**
	 * 任务完成
	 */
	void complete();
	
	/**
	 * 任务完成，但有例外产生
	 * @param exception {@link Exception},例外
	 */
	void complete(Exception exception);
}
