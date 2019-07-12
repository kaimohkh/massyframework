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
 * Date:   2019年6月8日
 */
package com.massyframework.assembly.launching.instrument;

import java.lang.instrument.Instrumentation;

import com.massyframework.assembly.launching.LaunchContext;
import com.massyframework.assembly.launching.PresetHandler;
import com.massyframework.instrument.InstrumentationFactory;


/**
 * 代码增强预置处理
 * @author  Huangkaihui
 *
 */
public class InstrumentPresetHandlder implements PresetHandler {

	/**
	 * 构造方法
	 */
	public InstrumentPresetHandlder() {
	}

	@Override
	public int getOrdered() {
		return 0;
	}

	@Override
	public void init(LaunchContext context) throws Exception{	
		            
		Instrumentation inst = InstrumentationFactory.getDefault();	
		if (inst != null) {
			context.addComponent(Instrumentation.class.getName(), inst);
		}
	}	
	
}
