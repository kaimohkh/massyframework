/**
* @Copyright: 2017 smarabbit studio.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*  
* @作   者： 黄开晖<kaimohkh@gmail.com> 
* @日   期:  2017年10月10日
*
*/
package com.massyframework.assembly.service;

/**
 * 自定义服务追踪事件接口
 * @author huangkaihui
 */
public interface ServiceTrackerCustomizer<T> {

	/**
	 * 添加服务
	 * @param reference 服务引用
	 * @param repository 服务仓储
	 * @return {@link T},返回使用的服务实例,如果不使用服务，则返回null.
	 */
	T addService(ServiceReference<T> reference, ServiceRepository repository);
	
	/**
	 * 移除服务
	 * @param reference 服务引用
	 * @param service 服务实例
	 */
	void removeService(ServiceReference<T> reference, T service);
}
