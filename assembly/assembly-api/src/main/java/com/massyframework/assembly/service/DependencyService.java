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
package com.massyframework.assembly.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 约定需要引入哪些外部服务注解
 * @author huangkaihui
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(value=DependencyServices.class)
public @interface DependencyService {

	/**
	 * 要求的类型
	 * @return {@link Class}
	 */
	Class<?> requiredType();
	
	/**
	 * 引入服务在装配件容器中的名称
	 * @return {@link String}
	 */
	String cName() default "";
	
	/**
	 * 过滤条件, 满足LDAP的格式要求，具体可参见{@link Filter}
	 * @return {@link String}
	 */
	String filterString() default "";
}
