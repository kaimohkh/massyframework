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
 * @日   期:  2019年3月28日
 */
package com.massyframework.assembly.protocols.factory;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * 基于线程变量的动态URLStreamHandlerFactory
 *
 */
public class DynamicURLStreamHandlerFactory extends ParentAwareURLStreamHandlerFactory {

	protected static final ThreadLocal<URLStreamHandlerFactory> FACTORY = 
			new InheritableThreadLocal<>();
	
	/**
	 * 压入{@link URLStreamHandlerFactory}
	 * @param factory
	 */
	public static void push(URLStreamHandlerFactory factory) {
        // no need to synchronize as we use a thread local
        if ( !(factory instanceof ParentAwareURLStreamHandlerFactory) ) {
            factory = new URLStreamHandlerFactoryWrapper(factory);
        }
        URLStreamHandlerFactory old = (URLStreamHandlerFactory) FACTORY.get();
        ((ParentAwareURLStreamHandlerFactory)factory).setParentFactory(old);
        FACTORY.set(factory);
    }
	
	/**
	 * 弹出
	 */
	public static void pop() {
        ParentAwareURLStreamHandlerFactory factory = 
        		(ParentAwareURLStreamHandlerFactory)FACTORY.get();
        if ( factory != null ) {
            FACTORY.set(factory.getParent());
        }
    }

	
	/**
	 * 构造方法
	 */
	public DynamicURLStreamHandlerFactory() {
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.protocols.ParentAwareURLStreamHandlerFactory#create(java.lang.String)
	 */
	@Override
	protected URLStreamHandler create(String protocol) {
		ParentAwareURLStreamHandlerFactory factory = 
				(ParentAwareURLStreamHandlerFactory)FACTORY.get();
        if ( factory != null ) {
            return factory.createURLStreamHandler(protocol);
        }
        return null;
	}

}
