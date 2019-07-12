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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.massyframework.io.ResourceScanner;

/**
 * 基于Spring的资源扫描器
 * @author  Huangkaihui
 *
 */
public class SpringResourceScanner implements ResourceScanner {
	
	private final PathMatchingResourcePatternResolver resolver;

	/**
	   *  构造方法
	 * @param resolver 解析器
	 */
	protected SpringResourceScanner(PathMatchingResourcePatternResolver resolver) {
		this.resolver =
			Objects.requireNonNull(resolver, "\"resolver\" cannot be null.");
	}

	@Override
	public URL getResource(String location) throws IOException {
		Resource resource =
				this.resolver.getResource(location);
		if (resource == null) return null;
		return resource.exists() ? resource.getURL() : null;
	}

	@Override
	public List<URL> getResources(String locationPattern) throws IOException {
		Resource[] resources =
				this.resolver.getResources(locationPattern);
		
		List<URL> result = new ArrayList<>();
		for (Resource resource: resources) {
			if (resource.exists()) {
				result.add(resource.getURL());
			}
		}
		return result;
	}

}
