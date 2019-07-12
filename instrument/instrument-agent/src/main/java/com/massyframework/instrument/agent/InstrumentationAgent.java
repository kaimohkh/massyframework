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
 * @日   期:  2019年2月3日
 */
package com.massyframework.instrument.agent;

import java.lang.instrument.Instrumentation;

/**
 * 增强代理类，增强有两种方式：
 * <ul>
 * <li>启动时使用-javaagent进行增强, jvm通过{@link #premain(java.lang.String, Instrumentation)}方法注入Instrumentation接口</li>
 * <li>运行时附加增强, jvm通过{@link #agentmain(java.lang.String, Instrumentation)}方法注入Instrumentation接口</li>
 * </ul>
 */
public final class InstrumentationAgent {

	private static volatile Instrumentation instrumentation;
	
	/**
	 * 
	 * JDK5：设置Instrumentation
	 * @param agentArgs 参数
	 * @param inst {@link Instrumentation}接口
	 */
	public static void premain(String agentArgs, Instrumentation inst) {
		if (instrumentation == null){
			instrumentation = inst;
		}
	}
	
	/**
	 * JKD6：设置Instrumentation
	 * @param agentArgs 参数
	 * @param inst {@link Instrumentation}接口
	 */
	public static void agentmain(String agentArgs, Instrumentation inst) {	
		if (instrumentation == null){
			instrumentation = inst;
		}
	}
	
	/**
	 * 获取增强接口
	 * @return {@link Instrumentation}增强接口，可能返回null.
	 */
	public static Instrumentation getInstrumentation(){
		return instrumentation;
	}
}

