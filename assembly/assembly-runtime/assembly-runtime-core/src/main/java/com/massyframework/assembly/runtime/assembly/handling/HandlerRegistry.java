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
package com.massyframework.assembly.runtime.assembly.handling;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.massyframework.assembly.spi.Handler;
import com.massyframework.assembly.spi.HandlerNotFoundException;
import com.massyframework.assembly.spi.HandlerRegistration;

/**
 * 提供装配件处理器的注册、查找和注销方法
 */
abstract class HandlerRegistry extends LifecylceEventPublisher {

	private List<RegistrationImpl<?>> registrations;
	
	/**
	 * 
	 */
	public HandlerRegistry() {
		this.registrations = new CopyOnWriteArrayList<>();
	}

	/**
	 * 按<code>handlerType</code>查找首个类型匹配的处理器
	 * @param handlerType 处理器类型
	 * @return {@link T}, 无匹配的可返回null.
	 */
	public <T> T findOne(Class<T> handlerType) {
		if (handlerType == null) return null;
		
		Optional<RegistrationImpl<?>> optional =
				this.registrations.stream()
					.filter(value -> handlerType.isAssignableFrom(value.getHandler().getClass()))
					.findFirst();
		
		return optional.isPresent() ? 
				handlerType.cast(optional.get().getHandler()):
					null;
	}

	/**
	 * 按<code>handlerType</code>查找所有类型匹配的处理器
	 * @param handlerType 处理器类型
	 * @return {@link List}
	 */
	public <T> List<T> findHandlers(Class<T> handlerType) {
		if (handlerType == null) return Collections.emptyList();
		
		return this.registrations.stream() 
				.filter(value ->  handlerType.isAssignableFrom(value.getHandler().getClass())) 
				.map(value ->  handlerType.cast(value.getHandler())) 
				.collect(Collectors.toList());
		
	}

	/**
	 * 按<code>handlerType</code>查找类型匹配的处理器
	 * @param handlerType 处理器类型
	 * @return {@link T}
	 * @throws HandlerNotFoundException 如果未找到匹配的处理器，则抛出例外
	 */
	public <T> T getHandler(Class<T> handlerType) throws HandlerNotFoundException {
		T result = this.findOne(handlerType);
		if (result == null) {
			throw new HandlerNotFoundException(handlerType);
		}
		return result;
	}

	/**
	 * 注册处理器
	 * @param handler {@link T},处理器实例
	 * @return {@link ReferenceRegistration}
	 */
	public <T> HandlerRegistration<T> register(T handler) throws Exception {
		RegistrationImpl<T> result = new RegistrationImpl<>(handler);
		
		if (handler instanceof Handler) {
			this.initHandler((Handler)handler);
		}
		this.registrations.add(result);
		logTrace("add handler " + handler.getClass().getName() +".");
		return result;
	}
	
	/**
	 * 注销处理器
	 * @param registration
	 */
	protected void doUnregister(RegistrationImpl<?> registration) {
		if (registration != null) {
			if (this.registrations.remove(registration)) {
				Object handler = registration.getHandler();
				if (handler instanceof Handler) {
					((Handler)handler).destroy();
				}
				
				logTrace("remove handler with assembly[" + this.getSymbolicName() + "]: " + handler.getClass().getName() +".");
			}
		}
	}
	
	/**
	 * 初始化处理器
	 * @param handler {@link Handler}
	 * @throws Exception 
	 */
	protected abstract <T> void initHandler(Handler handler) throws Exception;
	
	/**
	 * 获取装配件的符号名称
	 * @return {@link String}
	 */
	protected abstract String getSymbolicName();
	
	/**
	 * 处理器注册凭据
	 */
	private class RegistrationImpl<T> implements HandlerRegistration<T>{
		
		private final T handler;
		
		/**
		 * 构造方法
		 * @param handler {@link T}
		 */
		public RegistrationImpl(T handler) {
			this.handler = Objects.requireNonNull(handler, "\"handler\" cannot be null.");
		}

		@Override
		public void unregister() {
			doUnregister(this);
		}

		@Override
		public T getHandler() {
			return this.handler;
		}
		
	}
}
