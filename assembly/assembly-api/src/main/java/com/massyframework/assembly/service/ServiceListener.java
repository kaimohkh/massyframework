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
 * @日   期:  2019年1月13日
 */
package com.massyframework.assembly.service;

/**
 * 服务事件监听器,，当服务发生注册、注销行为时，将触发监听器的{@link #onChanged(ServiceEvent)}方法
 */
public interface ServiceListener {

	/**
     * 触发服务事件
     * @param event {@link ServiceEvent}
     */
    void onChanged(ServiceEvent<?> event);
}
