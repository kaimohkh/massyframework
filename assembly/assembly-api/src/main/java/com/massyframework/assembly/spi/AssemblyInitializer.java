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
 * Date:   2019年6月15日
 */
package com.massyframework.assembly.spi;

import com.massyframework.assembly.AssemblyConfigBuilder;

/**
 * 装配件初始化器<br>
 * 在assembly.xml配置文件中可以定义初始化器，用于自定义装配件的配置定义和授权检查。<br>
 * 正式发布时，实现该接口的类必须具备生产者的数字签名。否则该装配件将被标记为未授权。
 * 
 * @author  Huangkaihui
 *
 */
@FunctionalInterface
public interface AssemblyInitializer {

	/**
	 * 初始化
	 * @param builder {@link AssemblyConfigBuilder},装配件配置构建器
	 */
	void init(AssemblyConfigBuilder builder);
}
