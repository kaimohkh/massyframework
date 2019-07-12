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
 * @日   期:  2019年2月1日
 */
package com.massyframework.assembly.web.runtime;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.massyframework.assembly.web.ModularServletContext;
import com.massyframework.assembly.web.processing.JspResourceProcessor;
import com.massyframework.assembly.web.processing.JspResourceProcessorRepository;

/**
 *
 *
 */
class DefaultJspResourceProcessorRepository implements JspResourceProcessorRepository{
	
	private Map<ServletContext, JspResourceProcessor> processorMap =
			new ConcurrentHashMap<ServletContext, JspResourceProcessor>();

	/**
	 * 构造方法
	 */
	public DefaultJspResourceProcessorRepository() {
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.processing.JspResourceProcessorRepository#getJspResourceProcessor(com.massyframework.assembly.web.ModularServletContext)
	 */
	@Override
	public JspResourceProcessor getJspResourceProcessor(ModularServletContext servletContext) throws ServletException {
		JspResourceProcessor result = this.processorMap.get(servletContext);
		if (result == null) {
			JspResourceProcessor tmp = new JspResourceProcessor();
			result = this.processorMap.putIfAbsent(servletContext, tmp);
			if (result == null) {
				result = tmp;
				result.init(servletContext);
			}
		}
		
		return result;
	}


	/**
	 * 释放所有JspServletWrapper
	 */
	public void destroy() {
		Set<JspResourceProcessor> set = new HashSet<JspResourceProcessor>();
		for (Map.Entry<ServletContext, JspResourceProcessor> entry: this.processorMap.entrySet()) {
			set.add(entry.getValue());
		}
		this.processorMap.clear();
		
		for (JspResourceProcessor processor: set) {
			processor.destroy();
		}
	}
}
