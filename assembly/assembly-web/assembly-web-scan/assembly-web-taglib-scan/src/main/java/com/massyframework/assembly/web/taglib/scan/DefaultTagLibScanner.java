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

import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletContext;

import org.apache.jasper.compiler.TldCache;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.web.spi.TagLibScanner;
import com.massyframework.logging.Logger;

/**
 *缺省的TagLibScanner
 *
 */
final class DefaultTagLibScanner implements TagLibScanner {
	
	private final Assembly assembly;

	/**
	 * 
	 */
	public DefaultTagLibScanner(Assembly assembly) {
		this.assembly = Objects.requireNonNull(assembly, "\"assembly\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.web.spi.TagLibScanner#scanTlds(javax.servlet.ServletContext)
	 */
	@Override
	public void scanTlds(ServletContext context) {
		ClassLoader classLoader = assembly.getAssemblyClassLoader();
		
		Logger logger = assembly.getLogger();
		SimpleInstanceManager instanceManager = new SimpleInstanceManager();
		boolean validate = Boolean.parseBoolean(
                context.getInitParameter(org.apache.jasper.Constants.XML_VALIDATION_TLD_INIT_PARAM));
        String blockExternalString = 
        		context.getInitParameter(org.apache.jasper.Constants.XML_BLOCK_EXTERNAL_INIT_PARAM);
        boolean blockExternal = false;
        if (blockExternalString == null) {
            blockExternal = true;
        } else {
            blockExternal = Boolean.parseBoolean(blockExternalString);
        }
        
        // scan the application for TLDs
        TldScannerEx scanner = new TldScannerEx(assembly, context, 
        		classLoader, true, validate, blockExternal);
        try {	        	
            scanner.scan();
        } catch (Exception e) {
        	if (logger.isErrorEnabled()) {
        		logger.error(e.getMessage(), e);
        	}
        }
        
        
    	//输出发现的标签
        Map<String, TldResourcePath>  uriMap =
        		scanner.getUriTldResourcePathMap();
        if (!uriMap.isEmpty()) {
        	StringBuilder builder = new StringBuilder();
        	builder.append("found Jasper TagLib:").append("\r\n");
	        		        
	        for (Map.Entry<String, TldResourcePath> entry: uriMap.entrySet()) {
	        	builder.append("  ").append("uri = ").append(entry.getKey()).append("\r\n");
	        }
	        logger.debug(builder.toString());
        }
                
        TldCache tldCache = 
	        	new TldCache(context, 
	        		uriMap,
                    scanner.getTldResourcePathTaglibXmlMap());
        
        context.setAttribute(InstanceManager.class.getName(), instanceManager);
        context.setAttribute(TldCache.SERVLET_CONTEXT_ATTRIBUTE_NAME, tldCache);
	}
}
