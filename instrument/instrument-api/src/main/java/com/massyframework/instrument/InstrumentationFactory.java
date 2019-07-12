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
 * @日   期:  2019年2月3日
 */
package com.massyframework.instrument;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 增强工厂，提供系统注入的{@link Instrumentation}.
 * @author huangkaihui
 *
 */
public abstract class InstrumentationFactory {

	private static volatile Instrumentation INSTANCE = null;
	private static final String AGENT_CLASSNAME = "com.massyframework.instrument.agent.InstrumentationAgent";
	private static final String METHOD_NAME = "getInstrumentation";
	@SuppressWarnings("unused")
	private static final String ATTACHSELF = "jdk.attach.allowAttachSelf";

	/**
	 * 获取缺省的增强接口<br>
	 * 当首次调用本方法时，会使用{@link com.massyframework.instrument.agent.InstrumentationAgent#getInstrumentation()}的返回值，
	 * 当该值为null，且运行在jdk6以上环境时，会将Instrumentation-agent.jar包附加到systemLoader上，从而得到系统注入的增强接口。
	 * @return {@link Instrumentation}，可能返回null.
	 * @throws IOException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws ClassNotFoundException
	 */
	public static Instrumentation getDefault() 
			throws IOException, NoSuchMethodException, IllegalAccessException, 
					InvocationTargetException, ClassNotFoundException{
		
		//如果增强接口已经取得，则直接返回
		if (INSTANCE != null) return INSTANCE;
		
		
		try{
			//尝试看看是否已经加载agent
			INSTANCE = tryRetrieveInstrumentation();
		}catch(Exception e){
		
		}
		
		if (INSTANCE == null){		
			//附加agent jar.
			AgentJarLoader.attachAgentJar();
			//再次尝试
			INSTANCE = tryRetrieveInstrumentation();
		}
		
		return INSTANCE;		
	}
	
	/**
	 * 尝试获取{@link Instrumentation}接口
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected static Instrumentation tryRetrieveInstrumentation() throws ClassNotFoundException, 
		SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		ClassLoader loader = ClassLoader.getSystemClassLoader();			
		Class<?> clazz = loader.loadClass(AGENT_CLASSNAME);
	
		Method method = clazz.getMethod(METHOD_NAME, new Class<?>[0]);
		Instrumentation result = (Instrumentation)method.invoke(clazz, new Object[0]);
		if (result != null){
			return new InstrumentationWrapper(result);
		}
		return null;
	}
}
