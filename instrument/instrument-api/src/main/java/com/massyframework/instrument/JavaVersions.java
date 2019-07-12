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
 * @日   期:  2019年2月3日
 */
package com.massyframework.instrument;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author huangkaihui
 *
 */
final class JavaVersions {

	/**
     * Java version; one of 2, 3, 4, 5, 6, 7, 8 or 9.
     */
    public static int VERSION;
  
    private static final Class<?>[] EMPTY_CLASSES = new Class[0];

    static {
        String specVersion = System.getProperty("java.version"); 
        if ("1.2".equals(specVersion))
            VERSION = 2;
        else if ("1.3".equals(specVersion))
            VERSION = 3;
        else if ("1.4".equals(specVersion))
            VERSION = 4;
        else if ("1.5".equals(specVersion))
            VERSION = 5;
        else if ("1.6".equals(specVersion))
            VERSION = 6;
        else if ("1.7".equals(specVersion))
            VERSION = 7; 
        else if ("1.8".equals(specVersion))
            VERSION = 8;
        else if (specVersion.startsWith("9."))
        	VERSION =9;
        else if (specVersion.startsWith("10."))
        	VERSION =10;
        else if (specVersion.startsWith("11."))
        	VERSION =11;
        // maybe someday...
    }

    /**
     * Collects the parameterized type declarations for a given field.
     */
    public static Class<?>[] getParameterizedTypes(Field f) {
        try {
            return collectParameterizedTypes(f.getGenericType(), f.getType());
        } catch (Exception e) {
            return EMPTY_CLASSES;
        }
    }

    /**
     * Collects the parameterized return type declarations for a given method.
     */
    public static Class<?>[] getParameterizedTypes(Method meth) {
        try {
            return collectParameterizedTypes(meth.getGenericReturnType(), meth.getReturnType());
        } catch (Exception e) {
            return EMPTY_CLASSES;
        }
    }

    /**
     * Return all parameterized classes for the given type.
     */
    private static Class<?>[] collectParameterizedTypes(Type type, Class<?> cls) throws Exception {
        if (type instanceof ParameterizedType) {
            Type[] args = ((ParameterizedType)type).getActualTypeArguments();
            Class<?>[] clss = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                Class<?> c = extractClass(args[i]);
                if (c == null) {
                    return EMPTY_CLASSES;
                }
                clss[i] = c;
            }
            return clss;
        } else if (cls.getSuperclass() != Object.class) {
            return collectParameterizedTypes(cls.getGenericSuperclass(), cls.getSuperclass());
        } else {
            return EMPTY_CLASSES;
        }
    }
    
    /**
     * Extracts the class from the given argument, if possible. Null otherwise.
     */
    static Class<?> extractClass(Type type) throws Exception {
        if (type instanceof Class) {
            return (Class<?>)type;
        } else if (type instanceof ParameterizedType) {
            return extractClass(((ParameterizedType)type).getRawType());
        }
        return null;
    }


}
