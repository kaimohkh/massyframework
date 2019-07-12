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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLStreamHandlerFactory;

/**
 * URLStreamHandlerFactory工具类
 */
public abstract class URLStreamHandlerFactoryUtils {

	/**
	 * 设置URLStreamHandlerFactory
	 * @param factory {@link URLStreamHandlerFactory},工厂
	 * @throws Exception 
	 */
	public static void setURLStreamHandlerFactory(URLStreamHandlerFactory factory) throws Exception {
		try {
			// if we can set the factory, its the first!
			URL.setURLStreamHandlerFactory(factory);
		} catch (Error err) {
			// let's use reflection to get the field holding the factory
			final Field[] fields = URL.class.getDeclaredFields();
			int index = 0;
			Field factoryField = null;
			while (factoryField == null && index < fields.length) {
				final Field current = fields[index];
				if (Modifier.isStatic(current.getModifiers())
						&& current.getType().equals(URLStreamHandlerFactory.class)) {
					factoryField = current;
					factoryField.setAccessible(true);
				} else {
					index++;
				}
			}
			if (factoryField == null) {
				throw new Exception(
						"Unable to detect static field in the URL class for the URLStreamHandlerFactory. Please report this error together with your exact environment to the Apache Excalibur project.");
			}
			try {
				URLStreamHandlerFactory oldFactory = (URLStreamHandlerFactory) factoryField.get(null);
				if (factory instanceof ParentAwareURLStreamHandlerFactory) {
					((ParentAwareURLStreamHandlerFactory) factory).setParentFactory(oldFactory);
				}
				factoryField.set(null, factory);
			} catch (IllegalArgumentException e) {
				throw new Exception("Unable to set url stream handler factory " + factory);
			} catch (IllegalAccessException e) {
				throw new Exception("Unable to set url stream handler factory " + factory);
			}
		}
	}
	
	/**
	 * 获取静态的URLStreamHandlerFactory字段
	 * @return
	 */
	protected static Field getStaticURLStreamHandlerFactoryField() {
        Field[] fields = URL.class.getDeclaredFields();
        for ( int i = 0; i < fields.length; i++ ) {
            if ( Modifier.isStatic( fields[i].getModifiers() ) && fields[i].getType().equals( URLStreamHandlerFactory.class ) ) {
                fields[i].setAccessible( true );
                return fields[i];
            }
        }
        return null;
    }

}
