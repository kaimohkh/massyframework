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
 * @日   期:  2019年2月24日
 */
package com.massyframework.assembly.support;

import com.massyframework.assembly.AssemblyContext;
import com.massyframework.assembly.AssemblyContextAware;

/**
 * 装配件上下文感知对象
 */
public class AssemblyContextAwareObject implements AssemblyContextAware {
	
	private transient AssemblyContext assemblyContext;

	/**
	 * 构造方法 
	 */
	public AssemblyContextAwareObject() {

	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyContextAware#setAssemblyContext(com.massyframework.assembly.AssemblyContext)
	 */
	@Override
	public void setAssemblyContext(AssemblyContext context) {
		if (this.assemblyContext != context) {
			if (this.assemblyContext != null) {
				this.preprocessUnbind(this.assemblyContext);
			}
			
			this.assemblyContext = context;
			
			if (this.assemblyContext != null) {
				this.postProcessBind(this.assemblyContext);
			}
		}
	}
	
	/**
	 * 装配件上下文
	 * @return {@link AssemblyContext}, 可以返回null.
	 */
	public AssemblyContext getAssemblyContext() {
		return this.assemblyContext;
	}
	
	/**
	 * 取消和<code>context</code>的绑定前置处理
	 * @param context {@link AssemblyContext},要取消绑定的assemblyContext
	 */
	protected void preprocessUnbind(AssemblyContext context) {
		
	}
	
	/**
	 * 和<code>context</code>建立绑定后置处理
	 * @param context {@link AssemblyContext},已绑定的assemblyContext
	 */
	protected void postProcessBind(AssemblyContext context) {
		
	}

}
