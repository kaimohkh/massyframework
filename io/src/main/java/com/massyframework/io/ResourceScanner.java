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
package com.massyframework.io;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
   *   资源扫描器
 * @author  Huangkaihui
 *
 */
public interface ResourceScanner {
	
	/**
	 * 返回任意满足<code>location</code>的资源
	 * @param location {@link String},资源定位。
	 * @return {@link URL},无对应资源可返回null.
	 * @throws IOException io读发生的例外
	 */
	URL getResource(String location) throws IOException ;

	/**
	 * 返回所有满足<code>locationPattern</code>的资源.
	 * @param locationPattern {@link String},资源定位模式，和Spring的ResourcePatternResolver保持兼容。
	 * @return {@link List}
	 * @throws IOException io读发生的例外
	 */
	List<URL> getResources(String locationPattern) throws IOException ;
}
