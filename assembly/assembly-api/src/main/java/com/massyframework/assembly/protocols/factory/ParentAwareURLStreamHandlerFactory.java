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
 * 父URLStreamHandlerFactory感知
 *
 */
public abstract class ParentAwareURLStreamHandlerFactory implements URLStreamHandlerFactory {

	protected URLStreamHandlerFactory parentFactory;
	
	/**
	 * 构造方法
	 */
	public ParentAwareURLStreamHandlerFactory() {
    }
	
	/**
	 * 设置 ParentFactory
     * @param factory
     */
    public void setParentFactory(URLStreamHandlerFactory factory) {
        this.parentFactory = factory;
    }
	
	/**
	 * 获取父URLStreamHandlerFactory
	 * @return {@link URLStreamHandlerFactory}
	 */
	public URLStreamHandlerFactory getParent() {
        return this.parentFactory;
    }

	/* (non-Javadoc)
	 * @see java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
	 */
	@Override
	public URLStreamHandler createURLStreamHandler(String protocol) {
		URLStreamHandler handler = this.create(protocol);
        if ( handler == null && this.parentFactory != null ) {
            handler = this.parentFactory.createURLStreamHandler(protocol);
        }
        return handler;
	}

	/**
	 * 根据<code>protocal</code>创建URLStreamHandler
	 * @param protocol {@link String},协议
	 * @return {@link URLStreamHandler}
	 */
	protected abstract URLStreamHandler create(String protocol);
}
