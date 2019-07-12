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

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.service.ServiceFactory;
import com.massyframework.assembly.web.spi.TagLibScanner;

/**
 * DefaultTagLibScanner服务工厂
 */
final class DefaultTagLibScannerFactory implements ServiceFactory<TagLibScanner> {

	/**
	 * 构造方法
	 */
	public DefaultTagLibScannerFactory() {
		
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.service.ServiceFactory#getSerivce(com.massyframework.assembly.Assembly)
	 */
	@Override
	public TagLibScanner getSerivce(Assembly assembly) {
		return new DefaultTagLibScanner(assembly);
	}
}
