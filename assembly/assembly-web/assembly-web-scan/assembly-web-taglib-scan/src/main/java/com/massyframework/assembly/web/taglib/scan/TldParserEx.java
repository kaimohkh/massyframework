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
package com.massyframework.assembly.web.taglib.scan;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.descriptor.XmlErrorHandler;
import org.apache.tomcat.util.descriptor.tld.TaglibXml;
import org.apache.tomcat.util.descriptor.tld.TldParser;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 扩展TldParser类，避免发生Xml解析时找不到Jboss Module中的类
 * @author huangkaihui
 *
 */
final class TldParserEx extends TldParser {

	private static final Log log = LogFactory.getLog(TldParserEx.class);
	/**
	 * @param namespaceAware
	 * @param validation
	 * @param blockExternal
	 */
	public TldParserEx(boolean namespaceAware, boolean validation, boolean blockExternal) {
		super(namespaceAware, validation, blockExternal);
	}

	/**
	 * @param namespaceAware
	 * @param validation
	 * @param ruleSet
	 * @param blockExternal
	 */
	public TldParserEx(boolean namespaceAware, boolean validation, RuleSet ruleSet, boolean blockExternal) {
		super(namespaceAware, validation, ruleSet, blockExternal);
	}

	@Override
	public TaglibXml parse(TldResourcePath path) throws IOException, SAXException {
		Digester digester = this.getDigester();
		if (digester != null){
			try (InputStream is = path.openStream()) {
	            XmlErrorHandler handler = new XmlErrorHandler();
	            digester.setErrorHandler(handler);

	            TaglibXml taglibXml = new TaglibXml();
	            digester.push(taglibXml);

	            InputSource source = new InputSource(path.toExternalForm());
	            source.setByteStream(is);
	            digester.parse(source);
	            if (!handler.getWarnings().isEmpty() || !handler.getErrors().isEmpty()) {
	                handler.logFindings(log, source.getSystemId());
	                if (!handler.getErrors().isEmpty()) {
	                    // throw the first to indicate there was a error during processing
	                    throw handler.getErrors().iterator().next();
	                }
	            }
	            return taglibXml;
	        } finally {
	            digester.reset();
	        }
		}
		return new TaglibXml();
	}

	private Digester getDigester(){
		try{
			Field field = TldParser.class.getDeclaredField("digester");
			if (field != null){
				field.setAccessible(true);
				return (Digester)field.get(this);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
