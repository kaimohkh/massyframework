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
 * @日   期:  2019年3月28日
 */
package com.massyframework.assembly.protocols.assembly;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyRepository;

/**
 * URL处理器
 */
public class Handler extends URLStreamHandler {

	private static AssemblyRepository REPOSITORY;
	
	/**
	 * 构造方法
	 */
	public Handler() {
	}

	/* (non-Javadoc)
	 * @see java.net.URLStreamHandler#openConnection(java.net.URL)
	 */
	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		String host = u.getHost();
		Assembly assembly = REPOSITORY.getAssembly(host);
		ClassLoader classLoader = assembly.getAssemblyClassLoader();
		String path = u.getPath();
		URL resource = classLoader.getResource(path);
		return resource == null ? null : resource.openConnection();
	}
	
	/**
	 * 装配件仓储
	 * @return {@link AssemblyRepository}
	 */
	public AssemblyRepository getAssemblyRepository() {
		return REPOSITORY;
	}
	
	/**
	 * 绑定装配件仓储
	 * @param repository {@link AssemblyRepository}
	 */
	public static void bindAssemblyRepository(AssemblyRepository repository) {
		REPOSITORY = repository;
	}
}
