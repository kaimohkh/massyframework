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
 * @日   期:  2019年1月7日
 */
package com.massyframework.assembly;

import com.massyframework.assembly.container.Container;

/**
 * 装配件上下文<br>
 * 当装配件进入运行状态后，有且只有一个与之关联的上下文,当装配件钝化时，其关联的上下文也同时被回收。<br>
 * <p>
 * 装配件上下文提供与装配件关联的聚合服务
 *
 */
public interface AssemblyContext extends Container {
				
	/**
	 * 获取关联的装配件
	 * @return {@link Assembly}
	 */
	Assembly getAssociatedAssembly();
	
}
