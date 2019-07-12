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
 * @日   期:  2019年2月23日
 */
package com.massyframework.assembly.web.runtime.pagemapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyConfig;
import com.massyframework.assembly.AssemblyEventAdapter;
import com.massyframework.assembly.AssemblyReadiedEvent;
import com.massyframework.assembly.AssemblyUnactivingEvent;
import com.massyframework.assembly.initparam.InitParameters;
import com.massyframework.assembly.util.InitParametersUtils;
import com.massyframework.assembly.web.pagemapping.PageMappingEntry;
import com.massyframework.assembly.web.pagemapping.PageMappingRegistration;
import com.massyframework.assembly.web.pagemapping.PageMappingRepository;
import com.massyframework.logging.Logger;

/**
 * 页面映射的发现
 */
public class PageMappingFounder extends AssemblyEventAdapter {
	
	private static final String DOT   = ".";
	private static final String SPRIT = "/";
	private static final String COMMA = ",";
	private static final String SIGN  = "$";
	
	private final PageMappingRepository repository;
	private Map<String, List<PageMappingRegistration>> registrationsMap =
			new ConcurrentHashMap<>();

	/**
	 * 构造方法
	 * @param repository 页面映射仓储
	 */
	public PageMappingFounder(PageMappingRepository repository) {
		this.repository = Objects.requireNonNull(
				repository, "\"repository\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyEventAdapter#onEvent(com.massyframework.assembly.AssemblyReadiedEvent)
	 */
	@Override
	protected void onEvent(AssemblyReadiedEvent event) {
		super.onEvent(event);
		
		Assembly assembly = event.getAssembly();
		AssemblyConfig config = assembly.getAssemblyConfig();
		InitParameters initParams = 
				InitParametersUtils.subset(config, PageMappingRepository.PAGEMAPPING_PREFIX);
		List<String> names = initParams.getInitParameterNames();
		if (!names.isEmpty()) {			
			List<PageMappingRegistration> registrations =
					new ArrayList<PageMappingRegistration>();
			Logger logger = assembly.getLogger();
			for (String name: names) {
				String value = initParams.getInitParameter(name);
				
				//替换.为/
				String tmp = StringUtils.replace(name, DOT, SPRIT);
				//替换$为.，用于类似于： .jsp等具有扩展名的路径
				tmp = StringUtils.replace(tmp, SIGN, DOT);
				String uri = SPRIT.concat(StringUtils.replace(name, DOT, SPRIT));
				
				PageMappingEntry  entry = this.parse(value);
				try {
					PageMappingRegistration registraiton =
							repository.register(uri, entry);
					registrations.add(registraiton);
					if (logger.isDebugEnabled()) {
						logger.debug("add PageMapping: uri=" + uri +", path=" + entry.getPagePath() + ", forward=" + Boolean.toString(entry.isForward()));
					}
				}catch(Exception e) {
					
					if (logger.isErrorEnabled()) {
						logger.error("add PageMapping failed.", e);
					}
				}
			}
			
			this.registrationsMap.putIfAbsent(assembly.getSymbolicName(), registrations);
		}
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.AssemblyEventAdapter#onEvent(com.massyframework.assembly.AssemblyUnactivingEvent)
	 */
	@Override
	protected void onEvent(AssemblyUnactivingEvent event) {		
		List<PageMappingRegistration> registrations = 
				this.registrationsMap.remove(event.getAssembly().getSymbolicName());
		if (registrations != null) {
			for (PageMappingRegistration registration: registrations) {
				registration.unregister();
			}
		}
		super.onEvent(event);
	}
	
	public void clean() {
		List<String> symbolicNames = new ArrayList<>(this.registrationsMap.keySet());
		for (String symbolicName: symbolicNames) {
			List<PageMappingRegistration> registrations = 
					this.registrationsMap.remove(symbolicName);
			if (registrations != null) {
				for (PageMappingRegistration registration: registrations) {
					registration.unregister();
				}
			}
		}
	}

	/**
	 * 解析路径匹配记录.<br>
	 * 路径可以由两部分组成，中间用","分隔。前部分是path，后部分为DispatcherType，支持设置为"forward"或者"include"。<br>
	 * 默认不设置dispatcherType时，采用"forward".
	 * @param value {@link String}
	 * @return {@link PageMappingEntry}
	 */
	protected PageMappingEntry parse(String value) {
		int pos = StringUtils.indexOf(value, COMMA);
		String path = value;
		if (pos != -1) {
			path = StringUtils.substring(value, 0, pos-1);
			String dispatcherType = StringUtils.substring(value, pos);
			return dispatcherType.equalsIgnoreCase("include") ?
					new PageMappingEntry(path, false) :
						new PageMappingEntry(path, true);
		}else {
			return new PageMappingEntry(path);
		}
	}
}
