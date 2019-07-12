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
import java.util.Objects;

/**
 *
 *
 */
public class URLStreamHandlerFactoryWrapper extends ParentAwareURLStreamHandlerFactory {
	
	protected final URLStreamHandlerFactory factory;

	/**
	 * @param factory
	 */
	public URLStreamHandlerFactoryWrapper(URLStreamHandlerFactory factory) {
		this.factory = Objects.requireNonNull(factory, "\"factory\" cannot be null.");
	}
	
	

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.protocols.ParentAwareURLStreamHandlerFactory#create(java.lang.String)
	 */
	@Override
	protected URLStreamHandler create(String protocol) {
		return this.factory.createURLStreamHandler(protocol);
	}
}
