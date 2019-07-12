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
package com.massyframework.integration.spring.extender;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.massyframework.io.ResourceScanner;
import com.massyframework.io.ResourceScannerFactory;

/**
   * 基于Spring的资源扫描工厂
 * @author  Huangkaihui
 *
 */
public class SpringResourceScannerFactory implements ResourceScannerFactory {

	/**
	 * 构造方法
	 */
	public SpringResourceScannerFactory() {
		
	}

	@Override
	public ResourceScanner createResourceScanner(ClassLoader classLoader) {
		PathMatchingResourcePatternResolver resolver =
				new PathMatchingResourcePatternResolver(classLoader);
		
		return new SpringResourceScanner(resolver);
	}

	
}
