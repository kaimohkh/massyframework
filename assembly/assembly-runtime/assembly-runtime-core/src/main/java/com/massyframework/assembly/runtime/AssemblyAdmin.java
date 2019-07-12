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
 * @日   期:  2019年1月16日
 */
package com.massyframework.assembly.runtime;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyIdNotFoundException;
import com.massyframework.assembly.AssemblyNotFoundException;
import com.massyframework.assembly.AssemblyRegistration;
import com.massyframework.assembly.AssemblyRepository;
import com.massyframework.assembly.SymbolicNameExistsException;
import com.massyframework.assembly.runtime.assembly.AssemblyBase;

/**
 * 装配件管理器
 */
final class AssemblyAdmin extends AssemblyEventPublisher implements AssemblyRepository {

	private Map<String, RegistrationImpl> namedMap;
	private Map<Long, RegistrationImpl> idMap;
	
	/**
	 * 构造方法
	 */
	public AssemblyAdmin() {
		this.namedMap = new ConcurrentHashMap<>();
		this.idMap = new ConcurrentHashMap<>();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyRepository#getAssembly(long)
	 */
	@Override
	public Assembly getAssembly(long assemblyId) throws AssemblyIdNotFoundException{
		RegistrationImpl registration = this.idMap.get(assemblyId);
		if (registration == null) {
			throw new AssemblyIdNotFoundException(assemblyId);
		}
		return registration.getAssembly();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyRepository#getAssembly(java.lang.String)
	 */
	@Override
	public Assembly getAssembly(String symbolicName) throws AssemblyNotFoundException {
		Objects.requireNonNull(symbolicName, "\"symbolicName\" cannot be null.");
		RegistrationImpl registration = this.namedMap.get(symbolicName);
		if (registration == null) {
			throw new AssemblyNotFoundException(symbolicName);
		}
		return registration.getAssembly();
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyRepository#getAssemblies()
	 */
	@Override
	public List<Assembly> getAssemblies() {
		return this.idMap.values().stream()
				.map( registration -> registration.getAssembly())
				.collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyRepository#getAssemblies(java.util.function.Predicate)
	 */
	@Override
	public List<Assembly> getAssemblies(Predicate<Assembly> predicate) {
		return this.idMap.values().stream()
				.filter(registration -> predicate.test(registration.getAssembly()))
				.map(registration -> registration.getAssembly())
				.collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyRepository#size()
	 */
	@Override
	public int size() {
		return this.idMap.size();
	}
	
	/**
	 * 注册装配件
	 * @param assembly {@link Assembly},装配件
	 * @return {@link AssmemblyRegistration}, 注册凭据
	 * @throws SymbolicNameExistsException 符号名称已经被使用时抛出的例外
	 */
	public AssemblyRegistration register(AssemblyBase assembly) throws SymbolicNameExistsException{
		RegistrationImpl result = new RegistrationImpl(assembly);
		if (this.namedMap.putIfAbsent(assembly.getSymbolicName(), result) != null) {
			throw new SymbolicNameExistsException(assembly.getSymbolicName());
		}
		
		this.idMap.put(assembly.getAssemblyId(), result);
		return result;
	}

	private class RegistrationImpl implements AssemblyRegistration {
		
		private final AssemblyBase assembly;
		
		public RegistrationImpl(AssemblyBase assembly) {
			this.assembly = Objects.requireNonNull(assembly, "\"assembly\" cannot be null.");
		}

		/* (non-Javadoc)
		 * @see com.massyframework.lang.Registration#unregister()
		 */
		@Override
		public void unregister() {
			throw new RuntimeException("not implments.");
		}

		/* (non-Javadoc)
		 * @see com.massyframework.assembly.AssemblyRegistration#getAssembly()
		 */
		@Override
		public Assembly getAssembly() {
			return this.assembly;
		}
	
	}
}
