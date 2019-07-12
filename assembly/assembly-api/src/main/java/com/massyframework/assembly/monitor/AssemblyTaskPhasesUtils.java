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
 * @日   期:  2019年1月27日
 */
package com.massyframework.assembly.monitor;

/**
 * 装配件启停任务阶段工具
 */
public final class AssemblyTaskPhasesUtils {

	/**
	 * 按<code>phases</code>输出阶段说明
	 * @param phases {@link AssemblyTaskPhases}
	 * @return {@link String}
	 */
	public static String toString(AssemblyTaskPhases phases) {
		String result = null;
		switch (phases) {
			case START: {
				result = "start";
				break;
			}
			case DEPENDENCY_MATCH:{
				result = "dependency service match";
				break;
			}
			case CONTAINER_INITIALIZE: {
				result = "container initalize";
				break;
			}
			
			case SERVICE_EXPORT: {
				result = "service export";
				break;
			}
			
			case SERVICE_UNEXPORT: {
				result = "service unexport";
				break;
			}
			
			case STOP: {
				result = "stop";
				break;
			}
			
			default:{
				result="";
			}
		}
		
		return result;
	}
}
