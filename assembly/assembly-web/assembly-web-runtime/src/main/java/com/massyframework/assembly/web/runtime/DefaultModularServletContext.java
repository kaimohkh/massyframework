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
 * @日   期:  2019年1月27日
 */
package com.massyframework.assembly.web.runtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.web.runtime.resource.ResourceLocator;
import com.massyframework.assembly.web.runtime.resource.ResourceLocatorFactory;
import com.massyframework.assembly.service.ServiceRepository;
import com.massyframework.assembly.util.AssemblyUtils;
import com.massyframework.assembly.web.ModularServletContext;
import com.massyframework.assembly.web.ServletContextWrapper;
import com.massyframework.assembly.web.spi.TagLibScanner;
import com.massyframework.logging.Logger;

/**
 * 缺省的装配件
 */
class DefaultModularServletContext extends ServletContextWrapper implements ModularServletContext {
	
	private static final String ROOT_PREFIX = "$.";
	private static final String TEMP_DIR = "javax.servlet.context.tempdir";
	
	private final Set<Assembly> assemblies;
	private final ClassLoader classLoader;
	private final ResourceLocator resourceLocator;
	private final Logger logger;
	
	private final Map<String, Object> attributeMap =
			new ConcurrentHashMap<String, Object>();
	private final Map<String, String> initParams =
			new ConcurrentHashMap<String, String>();

	/**
	 * 构造方法
	 * @param context {@link ServletContext},J2EE容器的ServletContext
	 * @param assembly  {@link Assembly},装配件
	 */
	public DefaultModularServletContext(ServletContext context, ClassLoader classLoader,  Set<Assembly> assemblies) {
		super(context);
		this.classLoader = Objects.requireNonNull(classLoader, "\"classLoader\" cannot be null.");
		if (assemblies == null || assemblies.isEmpty()) {
			throw new IllegalArgumentException("\"assemblies\" canot be empty.");
		}
		this.assemblies = Collections.unmodifiableSet(assemblies);
		
		this.resourceLocator = ResourceLocatorFactory.createResoruceLocator(this.classLoader);
		this.logger = this.assemblies.iterator().next().getLogger();
	}
	
	/**
	 * 执行初始化
	 */
	void init() {
		this.initTempDir();
		this.scanTlds();
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ServletContextWrapper#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String name) {
		if (StringUtils.startsWith(name, ROOT_PREFIX)) {
			String realName = StringUtils.substring(name, 2);
			return super.getAttribute(realName);
		}
		
		Object result = this.attributeMap.get(name);
		if (result == null) {
			result = super.getAttribute(name);
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ServletContextWrapper#getAttributeNames()
	 */
	@Override
	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(this.getAttributeNamesToSet());
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ServletContextWrapper#getAttributeNamesToSet()
	 */
	@Override
	public Set<String> getAttributeNamesToSet() {
		Set<String> result = super.getAttributeNamesToSet();
		this.attributeMap.keySet()
			.forEach(name -> result.add(name));
		return result;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ServletContextWrapper#getInitParameter(java.lang.String)
	 */
	@Override
	public String getInitParameter(String name) {
		String result = this.initParams.get(name);
		if (result == null) {
			result = super.getInitParameter(name);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ServletContextWrapper#getInitParameterNames()
	 */
	@Override
	public Enumeration<String> getInitParameterNames() {
		Set<String> result = new HashSet<String>();
		Enumeration<String> em = super.getInitParameterNames();
		while (em.hasMoreElements()) {
			result.add(em.nextElement());
		}
		result.addAll(this.initParams.keySet());
		
		return Collections.enumeration(result);
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ServletContextWrapper#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute(String name, Object object) {
		if (StringUtils.startsWith(name, ROOT_PREFIX)) {
			String realName = StringUtils.substring(name, 2);
			super.setAttribute(realName, object);
			return;
		}
		
		this.attributeMap.put(name, object);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ServletContextWrapper#setInitParameter(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean setInitParameter(String name, String value) {
		if (StringUtils.startsWith(name, ROOT_PREFIX)) {
			String realName = StringUtils.substring(name, 2);
			return this.setInitParameter(realName, value);
		}
		
		return this.initParams.putIfAbsent(name, value) == null;
	}
	

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ServletContextWrapper#getResourcePaths(java.lang.String)
	 */
	@Override
	public Set<String> getResourcePaths(String path) {
		return this.resourceLocator.getResourcePaths(path, this.getServletContext());
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ServletContextWrapper#getResource(java.lang.String)
	 */
	@Override
	public URL getResource(String path) throws MalformedURLException {
		return this.resourceLocator.getResource(path, this.getServletContext(), this.logger);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ServletContextWrapper#getResourceAsStream(java.lang.String)
	 */
	@Override
	public InputStream getResourceAsStream(String path) {
		try {
			URL url = this.getResource(path);
			if (url != null) {
				return url.openStream();
			}
		} catch (IOException e) {
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.ServletContextWrapper#getClassLoader()
	 */
	@Override
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}
	
	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.AssemblyServletContext#getAssociatedAssemblies()
	 */
	@Override
	public Set<Assembly> getAssociatedAssemblies() {
		return this.assemblies;
	}
	
	/**
	 * 初始化临时目录
	 */
	private void initTempDir() {
		List<Assembly> c = new ArrayList<>(this.assemblies);
		String symbolicNames = AssemblyUtils.getSymbolicNames(c);
		String name = Integer.toString(symbolicNames.hashCode());
		File tempDir= new File((File)this.getServletContext()
				.getAttribute(TEMP_DIR), name);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		this.setAttribute(TEMP_DIR, tempDir);
	}
	
	/**
	 * 扫描tld标签
	 */
	private void scanTlds() {
		ServiceRepository repo = 
				ServiceRepository.retrieveFrom(this.assemblies.iterator().next());
		TagLibScanner scanner = repo.findService(TagLibScanner.class);
		if (scanner != null) {
			scanner.scanTlds(this);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		List<Assembly> c = new ArrayList<>(this.assemblies);
		String symbolicNames = AssemblyUtils.getSymbolicNames(c);
		return this.getClass().getSimpleName() + " [" + symbolicNames + "]";
	}

}
