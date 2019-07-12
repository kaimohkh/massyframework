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

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.massyframework.assembly.test.FrameworkUtils;

/**
 * Massy Framework初始化器
 * @author  Huangkaihui
 *
 */
public class FrameworkInitializer implements BeforeEachCallback {

	/**
	 * 
	 */
	public FrameworkInitializer() {
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		FrameworkUtils.getFramework();
	}

}
