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
package com.massyframework.assembly.runtime.service;

import java.util.Objects;

import com.massyframework.assembly.service.DependencyServiceDefinition;

/**
 * 现{@link DependencyServiceDefinition}的缺省类
 *
 */
final class DefaultDependencyServiceDefinition<S> implements DependencyServiceDefinition<S>{

	private Class<S> requiredType;
    private String cName;
    private String filterString;

    /**
     * 构造方法
     * @param requiredType 依赖的类型
     * @param cName 依赖组件在容器中预置的名称
     * @param filterString 过滤字符串
     */
    public DefaultDependencyServiceDefinition(Class<S> requiredType, String cName, String filterString) {
        this.requiredType = Objects.requireNonNull(requiredType, "requiredType cannot be null.");
        this.cName = cName;
        this.filterString = filterString;
    }

    /**
     * 依赖服务被注入后，在装配件上下文中的名称
     *
     * @return {@link String}, 可能为null.
     */
    @Override
    public String getCName() {
        return this.cName;
    }

    /**
     * 依赖服务的类型
     *
     * @return {@link Class}
     */
    @Override
    public Class<S> getRequiredType() {
        return this.requiredType;
    }

    /**
     * 服务筛选条件
     *
     * @return {@link String}
     */
    @Override
    public String getFilterString() {
        return this.filterString;
    }
    
   
    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filterString == null) ? 0 : filterString.hashCode());
		result = prime * result + ((requiredType == null) ? 0 : requiredType.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultDependencyServiceDefinition<?> other = (DefaultDependencyServiceDefinition<?>) obj;
		if (filterString == null) {
			if (other.filterString != null)
				return false;
		} else if (!filterString.equals(other.filterString))
			return false;
		if (requiredType == null) {
			if (other.requiredType != null)
				return false;
		} else if (!requiredType.equals(other.requiredType))
			return false;
		return true;
	}

	@Override
    public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DefaultDependencyServiceDefinition")
			.append("(").append("requiredType").append("=").append(requiredType.getName());
		if (cName != null){
			builder.append(", cName='").append(cName).append("'");
		}
		if (filterString != null){
			builder.append(". filterString='").append(filterString).append("'");
		}
		builder.append("}");
		return builder.toString();	
    }

}
