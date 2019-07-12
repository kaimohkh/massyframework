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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 约定需要输出的服务注解
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface ExportService {

	/**
	 * 输出服务类型数组
	 * @return {@link Class}数组
	 */
	Class<?>[] exportTypes();
		
	/**
	 * 服务属性
	 * @return {@link ServiceProperty}数组
	 */
	ServiceProperty[] properties() default {};
	
}
