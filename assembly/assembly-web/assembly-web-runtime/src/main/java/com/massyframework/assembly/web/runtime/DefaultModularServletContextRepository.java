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
package com.massyframework.assembly.web.runtime;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyRepository;
import com.massyframework.assembly.util.AssemblyUtils;
import com.massyframework.assembly.web.ModularServletContext;
import com.massyframework.assembly.web.ModularServletContextRepository;

/**
 * ModularServletContext服务工厂
 */
public class DefaultModularServletContextRepository implements ModularServletContextRepository {

	private ServletContext servletContext;
	private AssemblyRepository repo;
	private Map<ClassLoader, DefaultModularServletContext> contextMap =
			new ConcurrentHashMap<ClassLoader, DefaultModularServletContext>();
	
	/**
	 * 构造方法
	 * @param servletContext {@link ServletContext}, J2EE的Servlet上下文
	 * @param assemblyRepository {@link AssemblyRepository},装配件仓储
	 */
	public DefaultModularServletContextRepository(ServletContext servletContext, AssemblyRepository assemblyRepository) {
		this.servletContext = Objects.requireNonNull(servletContext, "\"servletContext\" cannot be null.");
		this.repo = Objects.requireNonNull(assemblyRepository, "\"assemblyRepository\" cannot be null.");
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ModularServletContextRepository#containsModularServletContext(com.massyframework.assembly.Assembly)
	 */
	@Override
	public boolean containsModularServletContext(Assembly assembly) {
		Optional<DefaultModularServletContext> optional=
				this.contextMap.values().stream()
					.filter( m -> m.getAssociatedAssemblies().contains(assembly))
					.findFirst();
		return optional.isPresent();
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ModularServletContextRepository#containsModularServletContext(java.lang.ClassLoader)
	 */
	@Override
	public boolean containsModularServletContext(ClassLoader classLoader) {
		if (classLoader == null) return false;
		return this.contextMap.containsKey(classLoader);
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ModularServletContextRepository#getModularServletContext(com.massyframework.assembly.Assembly)
	 */
	@Override
	public ModularServletContext getModularServletContext(Assembly assembly) {
		Objects.requireNonNull(assembly, "\"assembly\" cannot be null.");
		return this.getModularServletContext(assembly.getAssemblyClassLoader());
	}
		
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ModularServletContextRepository#getModularServletContext(java.lang.ClassLoader)
	 */
	@Override
	public ModularServletContext getModularServletContext(ClassLoader classLoader) {
		Objects.requireNonNull(classLoader, "\"classLoader\" cannot be null.");
		DefaultModularServletContext result = this.contextMap.get(classLoader);
		if (result == null) {
			List<Assembly> c = AssemblyUtils.getAssembliesWithClassLoader(classLoader, this.repo);
			
			//设置为内核装配件
			if (c.isEmpty()) {
				c.add(this.repo.getAssembly(0));
			}
			HashSet<Assembly> set = new HashSet<>(c);
			DefaultModularServletContext tmp =
					new DefaultModularServletContext(servletContext, classLoader, set);
			result = this.contextMap.put(classLoader, tmp);
			if (result == null) {
				result = tmp;
				result.init();
			}
		}
		return result;
	}
}
