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
 * Date:   2019年6月5日
 */
package com.massyframework.io.util;

import java.io.Closeable;
import java.io.IOException;

/**
   * 输入输出工具类
 * @author  Huangkaihui
 *
 */
public abstract class IOUtils {

	/**
	   *  关闭可关闭对象
	 * @param closeable {@link Closeable}
	 */
	public static void close(Closeable closeable) {
		if (closeable == null) return;
		
		try {
			closeable.close();
		}catch(IOException e) {
			
		}
	}
}
