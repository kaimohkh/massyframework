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
 * @日   期:  2019年1月19日
 */
package com.massyframework.assembly.spi;

import com.massyframework.assembly.AssemblyConfig;

/**
 * 装配件工厂
 *
 */
public interface HandlerFactory {
	
	/**
	 * 根据<code>AssemblyConfig</code>创建装配件处理器
	 * @param config {@link AssemblyConfig},装配件配置
	 * @return {@link Handler}
	 * @throws ContainerTypeNotSupportException 不支持的容器类型
	 */
	Handler createHandler(AssemblyConfig config) throws ContainerTypeNotSupportException;
}
