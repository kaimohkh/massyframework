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
 * 提供注册服务的一组方法
 */
public interface ServiceRegistry {

	/**
     * 使用服务实例注册服务
     * @param service {@link S}, 服务实例
     * @param props {@link ServiceProperties}, 服务属性，可以为null.
     * @return {@link ServiceRegistration}
     */
    <S> ServiceRegistration<S> register(S service, ServiceProperties props);

    /**
     * 注册服务工厂注册服务
     * @param factory {@link ServiceFactory}, 服务工厂
     * @param props {@link ServiceProperties}, 服务属性， 可以为null.
     * @return {@link ServiceRegistration}
     */
    <S> ServiceRegistration<S> register(ServiceFactory<S> factory, ServiceProperties props);

}
