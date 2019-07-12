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

import com.massyframework.assembly.support.StartStopEnable;
import com.massyframework.logging.Logger;

/**
 * 提供装配件运行基础设施的框架
 */
public interface Framework extends StartStopEnable {
	
	/**
	 * 运行模式，属性名
	 */
	static final String RUNMODE = "run.mode";
	
	/**
	 * 在同一个ClassLoader加载装配件 
	 */
	static final String RUNMODE_STANDALONE = "standalone";
	
	/**
	 * 使用不同的ClassLoader加载装配件
	 */
	static final String RUNMODE_ISOLATION  = "isolation";
	
	/**
	 * 环境属性，取值仅为{@link #ENVIRONMENT_J2SE}和{@link #ENVIRONMENT_J2EE}
	 */
	static final String ENVIRONMENT = "environment";
	
	/**
	 * J2SE运行环境
	 */
	static final String ENVIRONMENT_J2SE = "j2se";
	
	/**
	 * J2EE运行环境
	 */
	static final String ENVIRONMENT_J2EE = "j2ee";
		
	/**
	 * 使用本系统用户的编号，需要向平台申请<br>
	 * 如果该值为0,表明时在开发模式下运行
	 */
	static final String USER_ID    = "user.id";
	
	/**
	 * 使用本系统用户的名称
	 */
	static final String USER_NAME  = "user.name";
	
	
	/**
	 * 装配件工厂
	 * @return {@link AssemblyRepository}
	 */
	AssemblyRepository getAssemblyRepository();
	
	/**
	 * 日志记录器
	 * @return {@link Logger}
	 */
	Logger getLogger();
	
	/**
	 * 所处阶段
	 * @return {@link FrameworkStatus}
	 */
	FrameworkStatus getStatus();
}
