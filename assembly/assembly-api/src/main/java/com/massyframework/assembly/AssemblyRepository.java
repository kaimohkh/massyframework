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
 * @日   期:  2019年1月15日
 */
package com.massyframework.assembly;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import com.massyframework.assembly.service.ServiceRepository;

/**
 * 装配件仓储，提供所有注册的装配件
 *
 */
public interface AssemblyRepository {

	/**
     * 使用<code>assemblyId</code>查找装配件
     * @param assemblyId 装配件编号
     * @return {@link Assembly},装配件,未找到对应装配件可返回null.
     * @throws AssemblyIdNotFoundException 无法找到对应编号的装配件时抛出例外
     */
    Assembly getAssembly(long assemblyId) throws AssemblyIdNotFoundException;

    /**
     * 按<code>symbolicName</code>查找装配件
     * @param symbolicName 符号名称
     * @return {@link Assembly},装配件，未找到对应装配件可返回null.
     * @throws AssemblyNotFoundException 按符号名称未找到装配件时抛出的例外
     */
    Assembly getAssembly(String symbolicName) throws AssemblyNotFoundException;

    /**
     * 显示所有安装的装配件
     * @return {@link Assembly}
     */
    List<Assembly> getAssemblies();

    /**
     * 显示所有满足<code>predicate</code>条件的装配件<br>
     * 使用规则检查器对所有装配件进行遍历，当{@link predicate#test(t)}返回true时，将装配件作为返回列表集合元素之一。
     * @param spec {@link predicate}规则检查器
     * @return {@link List}
     */
    List<Assembly> getAssemblies(Predicate<Assembly> predicate);
    
    /**
     * 装配件总数
     * @return {@link int}
     */
    int size();
    
    /**
     * 从<code>assembly</code>取回装配件仓储
     * @param assembly {@link Assembly},装配件
     * @return {@link AssemblyRepository}
     */
    static AssemblyRepository retrieveFrom(Assembly assembly) {
    	Objects.requireNonNull(assembly, "\"assembly\" cannot be null.");
    	ServiceRepository serviceRepository = ServiceRepository.retrieveFrom(assembly);
    	return serviceRepository.getService(AssemblyRepository.class);
    }
}
