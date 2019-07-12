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
 * @日   期:  2019年1月29日
 */
package com.massyframework.assembly.support;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyConfig;
import com.massyframework.assembly.AssemblyContext;
import com.massyframework.assembly.AssemblyRepository;
import com.massyframework.assembly.DependencyManager;
import com.massyframework.assembly.service.DependencyServiceDefinition;
import com.massyframework.assembly.service.ServiceReference;
import com.massyframework.assembly.service.ServiceRepository;
import com.massyframework.assembly.container.ComponentNamesProvider;

/**
 * 控制台输出
 *
 */
public class ConsoleReporter {

	/**
	 * 构造方法
	 */
	public ConsoleReporter() {

	}

	/**
	 * 输出打印
	 */
	public void reportStarted(AssemblyRepository repo) {
		Objects.requireNonNull(repo, "\"repo\" cannot be null.");
		List<Assembly> assemblies = repo.getAssemblies();
		Collections.sort(assemblies);
		StringBuilder builder = new StringBuilder();
		this.reportTitle(builder);
		Assembly core = assemblies.get(0);
		this.reportInitParams(core, builder);
		this.reportComponents(core, builder);
		this.reportServices(core, builder);
		this.reportAssemblies(assemblies, builder);

		this.reportTail(builder);
		System.out.println(builder.toString());
	}

	protected void reportTitle(StringBuilder builder) {
		builder.append("[INFO]").append("\r\n")
				.append("[INFO] ------------------------------------------------------------").append("\r\n")
				.append("[INFO] ------------------------------------------------------------").append("\r\n")
				.append("[INFO] |||      |||     ||||     |||||||||  |||||||||  |||   |||  *").append("\r\n")
				.append("[INFO] || |    | ||    ||  ||    ||         ||          ||| |||   *").append("\r\n")
				.append("[INFO] ||  |  |  ||   ||||||||   |||||||||  |||||||||     |||     *").append("\r\n")
				.append("[INFO] ||   ||   ||   ||    ||           |          |     |||     *").append("\r\n")
				.append("[INFO] ||   ||   ||  ||      ||  |||||||||  |||||||||     |||     *").append("\r\n")
				.append("[INFO] ------------------------------------------------------------").append("\r\n")
				.append("[INFO] ------------------------------------------------------------").append("\r\n");
	}

	protected void reportTail(StringBuilder builder) {
		builder.append("[INFO] Framework Started.").append("\r\n")
				.append("[INFO] ------------------------------------------------------------").append("\r\n");
	}

	/**
	 * 报告装配件
	 * 
	 * @param builder
	 */
	protected void reportAssemblies(List<Assembly> assemblies, StringBuilder builder) {
		builder.append("[INFO] ").append("\r\n").append("[INFO] Found assemblies:").append("\r\n");
		for (Assembly assembly : assemblies) {
			AssemblyConfig config = assembly.getAssemblyConfig();
			int id = (int) assembly.getAssemblyId();
			String symbolicName = assembly.getSymbolicName();
			String name = config.getName();
			String message = String.format("%2d) %-84s %s", id, symbolicName, name);
			builder.append("[INFO] ").append(message).append("\r\n");

			DependencyManager dependencyManager = DependencyManager.retrieveFrom(assembly);
			List<DependencyServiceDefinition<?>> dependencies = dependencyManager.getUnmatchDependencyServices();
			for (DependencyServiceDefinition<?> definition : dependencies) {
				String msg = definition.getFilterString() != null
						? String.format("-- require: class= %s, filterString= %s",
								definition.getRequiredType().getName(), definition.getFilterString())
						: String.format("-- require: class= %s", definition.getRequiredType().getName());
				builder.append("[INFO]     ").append(msg).append("\r\n");
			}
		}
		builder.append("[INFO] ------------------------------------------------------------").append("\r\n");
	}

	/**
	 * 报告初始化参数
	 * 
	 * @param assembly
	 */
	protected void reportInitParams(Assembly assembly, StringBuilder builder) {
		builder.append("[INFO]").append("\r\n").append("[INFO] Init Parameters:").append("\r\n");
		AssemblyConfig config = assembly.getAssemblyConfig();
		List<String> names = config.getInitParameterNames();
		Collections.sort(names);
		int size = names.size();
		for (int i = 0; i < size; i++) {
			String name = names.get(i);
			String value = config.getInitParameter(name);
			value = StringUtils.replace(value, "\r\n", "  ");
			value = StringUtils.replace(value, "\n", "  ");
			String message = String.format("%2d) %s = %s", i, name, "\"" + value + "\"");
			builder.append("[INFO] ").append(message).append("\r\n");
		}
		builder.append("[INFO] ------------------------------------------------------------").append("\r\n");
	}

	/**
	 * 报告初始化的服务
	 * 
	 * @param assembly
	 */
	protected void reportComponents(Assembly assembly, StringBuilder builder) {
		AssemblyContext context = assembly.getAssemblyContext();

		if (context instanceof ComponentNamesProvider) {
			List<String> names = ((ComponentNamesProvider) context).getComponentNames();
			Collections.sort(names);

			builder.append("[INFO]  ").append("\r\n");
			builder.append("[INFO] Kernel Assembly Components:").append("\r\n");
			int size = names.size();

			for (int i = 0; i < size; i++) {
				String name = names.get(i);
				Object service = context.getComponent(name);
				String message = String.format("%2d) CName=%-78s classes =%s", i, name, service.getClass().getName());
				builder.append("[INFO] ").append(message).append("\r\n");
			}
			builder.append("[INFO] ------------------------------------------------------------").append("\r\n");
		}
	}

	/**
	 * 报告注册的服务
	 * 
	 * @param assembly {@link Assembly} 装配件
	 * @param builder  {@link StringBuilder},字符串构建器
	 */
	protected void reportServices(Assembly assembly, StringBuilder builder) {
		ServiceRepository repo = ServiceRepository.retrieveFrom(assembly);

		List<Class<?>> serviceTypes = repo.getServiceTypes();
		if (serviceTypes.isEmpty()) {
			return;
		}

		Collections.sort(serviceTypes, new ServiceTypeSore());
		builder.append("[INFO]  ").append("\r\n");
		builder.append("[INFO] registed ServiceTypes:").append("\r\n");

		int size = serviceTypes.size();
		for (int i = 0; i < size; i++) {
			Class<?> serviceType = serviceTypes.get(i);
			String name = serviceType.getName();

			String serviceNames = null;
			List<ServiceReference<?>> references = this.getServiceReferences(serviceType, repo);
			if (!references.isEmpty()) {
				Set<String> set = new HashSet<>();
				for (ServiceReference<?> reference : references) {
					String[] names = reference.getProperty(ServiceReference.NAME, String[].class);
					if (names != null) {
						for (String serviceName: names) {
							set.add(serviceName);
						}
					}
				}
				
				if (!set.isEmpty()) {
					serviceNames = StringUtils.join(set, ",");
				}
			}
			
			String message =  null;
			if (serviceNames == null) {
				message = String.format("%2d) type=%-78s", i, name, serviceNames);
			}else {
				message = String.format("%2d) type=%-78s names =%s", i, name, serviceNames);
			}

			builder.append("[INFO] ").append(message).append("\r\n");
		}
		builder.append("[INFO] ------------------------------------------------------------").append("\r\n");
	}

	/**
	 * 
	 * @param serviceType
	 * @param repository
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ServiceReference<?>> getServiceReferences(Class<?> serviceType, ServiceRepository repository) {
		List<?> result = repository.getServiceReferences(serviceType);
		return (List<ServiceReference<?>>) result;
	}

	private class ServiceTypeSore implements Comparator<Class<?>> {

		@Override
		public int compare(Class<?> o1, Class<?> o2) {
			String name1 = o1.getName();
			String name2 = o2.getName();
			return name1.compareTo(name2);
		}
		
	}
}
