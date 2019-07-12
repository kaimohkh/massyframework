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
package com.massyframework.assembly.web.taglib.scan;

import com.massyframework.assembly.Framework;
import com.massyframework.assembly.launching.Initializer;
import com.massyframework.assembly.launching.LaunchContext;
import com.massyframework.assembly.service.ServiceProperties;
import com.massyframework.assembly.web.spi.TagLibScanner;

/**
 *初始化方法
 */
public class TagLigScannerInitializer implements Initializer {
	
	/**
	 * 构造方法
	 */
	public TagLigScannerInitializer() {
		
	}

	@Override
	public void init(LaunchContext context) {
		if (context.runOnJ2EE()) {
			if (Framework.RUNMODE_ISOLATION.equals(context.getInitParameter(Framework.RUNMODE))) {
				ServiceProperties props = new ServiceProperties();
				props.setObjectClass(TagLibScanner.class);
				props.setDescription("扫描ServletContext中META-INF目录下的标签库");
				props.setTopRanking();
				
				context.addComponent(TagLibScanner.class.getName(), new DefaultTagLibScannerFactory(), props);
			}
		}	
	}
	
}
