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
 * @日   期:  2019年1月13日
 */
package com.massyframework.assembly.service;

import java.util.Map;

/**
 * RFC 1960-based 筛选器<br>
 * Filter对象实例可以采用{@link ExportServiceRepository#createFilter(String)}方法创建。<br>
 * 筛选器可以用于通过对服务属性的筛选来选择服务。<br>
 * 一些LDAP筛选例子如下：
 * <pre>
 *  "(cn=Babs Jensen)"
 *  "(!(cn=Tim Howes))"
 *  "(&(" + Constants.OBJECT_CLASS + "=Person)(|(sn=Jensen)(cn=Babs J*)))"
 *  "(o=univ*of*mich*)"
 * </pre>
 *
 */
public interface Filter {

	/**
	 * 对map进行过滤，对map的key和value进行不区分大小写匹配。
	 * @param props {@link Map}属性
	 * @return {@code true}匹配，{@code false}不匹配
	 */
	boolean match(Map<String, Object> props);
	
	/**
	 * 对服务引用进行过滤，对reference的属性名和属性值进行不区分大小写进行匹配
	 * @param reference {@link ServiceReference}
	 * @return {@code true}匹配，{@code false}不匹配
	 */
	<S> boolean match(ServiceReference<S> reference);
}
