/**
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
 * Author: 黄开晖（117227773@qq.com）
 * Date:   2019年6月15日
 */
package com.massyframework.assembly.test.junit5;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.Framework;
import com.massyframework.assembly.test.FrameworkUtils;
import com.massyframework.assembly.test.annotation.SymbolicName;

import java.lang.reflect.Parameter;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * 装配件参数解析器
 * @author  Huangkaihui
 *
 */
public class AssemblyParameterResolver implements ParameterResolver {

	/**
	 * 构造方法
	 */
	public AssemblyParameterResolver() {
		
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		if (parameterContext.getParameter().getType() == Assembly.class) {
			return true;
		}
		
		if (parameterContext.getParameter().getType() == Framework.class) {
			return true;
		}
		return false;
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		Parameter parameter = parameterContext.getParameter();
		if (Framework.class == parameter.getType()) {
			return FrameworkUtils.getFramework();
		}
		
		if (Assembly.class == Assembly.class) {
			SymbolicName symbolicName = parameter.getAnnotation(SymbolicName.class);
			return FrameworkUtils.getAssembly(symbolicName.value());
		}
		
		return null;
	}

}
