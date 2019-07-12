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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.sun.tools.attach.VirtualMachine;


/**
 * 虚拟机工具类，提供对sun虚拟机
 * @author huangkaihui
 *
 */
final class VirtualMachinUtils {

	public static final String VM_CLASSNAME = "com.sun.tools.attach.VirtualMachine";
	private static Class<?> vmClass ;
	static{
		//加载 VirtualMachine class.
		try{
			vmClass = VirtualMachine.class;
		}catch(Throwable e){
		}
		
		try {
			if (vmClass== null) {
				vmClass =  Class.forName(VM_CLASSNAME);
			}
		}catch(Exception e) {
			
		}
		
		try{
			if (vmClass == null){
				vmClass = Thread.currentThread().getContextClassLoader().loadClass(VM_CLASSNAME);
			}
		}catch(Exception e){

		}
		
		try{
			if (vmClass == null){
				vmClass = ClassLoader.getSystemClassLoader().loadClass(VM_CLASSNAME);
			}
		}catch(Exception e){
			System.err.println("load " + VM_CLASSNAME + " failed.");
		}
		
	}
	
	/**
	 * 附加加载jar包
	 * @param agentPath
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected static void attach(String agentPath) 
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		if (vmClass == null){
			return;
		}
		
		// dynamic loading of the agent is only available in JDK 1.6+
		if (JavaVersions.VERSION < 6){
			System.out.println("cannot dynaimc attach to jvm.");
			return;
		}
							
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String pid = runtime.getName();
		if (pid.indexOf("@") != -1)
			pid = pid.substring(0, pid.indexOf("@"));
		
		Method method = vmClass.getMethod("attach", new Class[] { String.class });
		Object vm = method.invoke(null, new Object[]{pid});
		
		// now deploy the actual agent, which will wind up calling agentmain()
		Method loadAgentMethod = vm.getClass().getMethod("loadAgent", new Class[] { String.class });
		if (loadAgentMethod != null){
			loadAgentMethod.invoke(vm, new Object[] { agentPath });
		}
	}
	
	static Class<?> getVMClass(){
		return vmClass;
	}

}
