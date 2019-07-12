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
package com.massyframework.assembly.web.runtime.resource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.servlet.ServletContext;

import com.massyframework.logging.Logger;

/**
 * 资源定位器
 *
 */
public interface ResourceLocator {

	/**
	 * 根据<code>path</code>定位资源的URL。
	 * @param path {@link String},路径
	 * @param servletContext {@link ServletContext},J2EE容器的ServletContext
	 * @param logger {@link Logger},日志记录器
	 * @return {@link URL}
	 * @throws MalformedURLException 如果<code>path</code>不符合规则，则抛出例外
	 */
	URL getResource(String path, ServletContext servletContext, Logger logger) throws MalformedURLException;
	
	/**
	 * 返回Web应用程序中类似目录资源的下级资源列表,类似目录资源的路径以"/"结尾。<br>
	 * 返回的路径都是相对于根目录，或者相对于Web应用程序的"/WEB-INF/lib"目录中的JAR文件的"/META-INF/resources"目录，并且具有前导"/"。
	 * 
	 * <p>
	 * <pre>
	 * 例如，对于包含以下内容的Web应用程序：
	 * /welcome.html
	 * /catalog/index.html
	 * /catalog/products.html
	 * /catalog/offers/books.html
	 * /catalog/offers/music.html
	 * /customer/login.jsp
	 * /WEB-INF/web.xml
	 * /WEB-INF/classes/com.acme.OrderServlet.class
	 * /WEB-INF/lib/catalog.jar!/META-INF/resources/catalog/moreOffers/books.html
	 * 
	 * getResourcePaths("/")将返回{"/welcome.html","/catalog/","/customer/","/WEB-INF/"};
	 * getResourcePaths("/catalog/")将返回{"/catalog/index.html","/catalog/products.html","/catalog/offers/","/catalog/moreOffers/"}。
	 * </pre>
	 * @param path {@link String},路径
	 * @param servletContext {@link ServletContext},J2EE容器的Servlet上下文
	 * @return {@link Set}
	 */
	Set<String> getResourcePaths(String path, ServletContext servletContext);
}
