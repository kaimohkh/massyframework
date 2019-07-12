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
 * @日   期:  2019年1月27日
 */
package com.massyframework.assembly.runtime.service;

import java.util.Map;

import com.massyframework.assembly.service.Filter;
import com.massyframework.assembly.service.ServiceReference;

/**
 * 不执行过滤的空过滤器，对所有匹配请求均返回<code>true</code>
 * @author huangkaihui
 *
 */
class EmptyFilter implements Filter {

	public static final EmptyFilter INSTANCE = new EmptyFilter();

    protected EmptyFilter() {
    }

    /**
     * 对map进行过滤，对map的key和value进行不区分大小写匹配。
     *
     * @param props {@link Map}属性
     * @return {@code true}匹配，{@code false}不匹配
     */
    @Override
    public boolean match(Map<String, Object> props) {
        return true;
    }

    /**
     * 对服务引用进行过滤，对reference的属性名和属性值进行不区分大小写进行匹配
     *
     * @param reference {@link ServiceReference}
     * @return {@code true}匹配，{@code false}不匹配
     */
    @Override
    public <S> boolean match(ServiceReference<S> reference) {
        return true;
    }
}
