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

import com.massyframework.assembly.AssemblyException;

/**
 * 未找到匹配的处理器时抛出的例外
 *
 */
public class HandlerNotFoundException extends AssemblyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7849560311619157939L;
	private Class<?> handlerType;
	
	/**
	 * 
	 */
	public HandlerNotFoundException(Class<?> handlerType)  {
		super("cannot found handler: type=" + handlerType + ".");
		this.handlerType = handlerType;
	}

	/**
	 * 处理器类型
	 * @return {@link Class}
	 */
	public Class<?> getHandlerType(){
		return this.handlerType;
	}

}
