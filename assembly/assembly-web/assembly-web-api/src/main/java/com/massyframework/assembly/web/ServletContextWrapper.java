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
package com.massyframework.assembly.web;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

/**
 * ServletContext封装器
 * @author huangkaihui
 */
public abstract class ServletContextWrapper implements ServletContext {
	
	private final ServletContext internal;
	
	/**
	 * 构造方法
	 * @param context 内部的ServletContext
	 */
	public ServletContextWrapper(ServletContext context){
		this.internal = Objects.requireNonNull(context, "\"context\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getContextPath()
	 */
	@Override
	public String getContextPath() {
		return this.getServletContext().getContextPath();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getContext(java.lang.String)
	 */
	@Override
	public ServletContext getContext(String uripath) {
		return this.getServletContext().getContext(uripath);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getMajorVersion()
	 */
	@Override
	public int getMajorVersion() {
		return this.getServletContext().getMajorVersion();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getMinorVersion()
	 */
	@Override
	public int getMinorVersion() {
		return this.getServletContext().getMinorVersion();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getEffectiveMajorVersion()
	 */
	@Override
	public int getEffectiveMajorVersion() {
		return this.getServletContext().getEffectiveMinorVersion();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getEffectiveMinorVersion()
	 */
	@Override
	public int getEffectiveMinorVersion() {
		return this.getServletContext().getEffectiveMinorVersion();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getMimeType(java.lang.String)
	 */
	@Override
	public String getMimeType(String file) {
		return this.getServletContext().getMimeType(file);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getResourcePaths(java.lang.String)
	 */
	@Override
	public Set<String> getResourcePaths(String path) {
		return this.getServletContext().getResourcePaths(path);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getResource(java.lang.String)
	 */
	@Override
	public URL getResource(String path) throws MalformedURLException {
		return this.getServletContext().getResource(path);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getResourceAsStream(java.lang.String)
	 */
	@Override
	public InputStream getResourceAsStream(String path) {
		return this.getServletContext().getResourceAsStream(path);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getRequestDispatcher(java.lang.String)
	 */
	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return this.getServletContext().getRequestDispatcher(path);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getNamedDispatcher(java.lang.String)
	 */
	@Override
	public RequestDispatcher getNamedDispatcher(String name) {
		return this.getServletContext().getNamedDispatcher(name);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServlet(java.lang.String)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Servlet getServlet(String name) throws ServletException {
		return this.getServletContext().getServlet(name);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServlets()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Enumeration<Servlet> getServlets() {
		return this.getServletContext().getServlets();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServletNames()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Enumeration<String> getServletNames() {
		return this.getServletContext().getServletNames();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#log(java.lang.String)
	 */
	@Override
	public void log(String msg) {
		this.getServletContext().log(msg);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#log(java.lang.Exception, java.lang.String)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void log(Exception exception, String msg) {
		this.getServletContext().log(exception, msg);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#log(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void log(String message, Throwable throwable) {
		this.getServletContext().log(message, throwable);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getRealPath(java.lang.String)
	 */
	@Override
	public String getRealPath(String path) {
		return this.getServletContext().getRealPath(path);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServerInfo()
	 */
	@Override
	public String getServerInfo() {
		return this.getServletContext().getServerInfo();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getInitParameter(java.lang.String)
	 */
	@Override
	public String getInitParameter(String name) {
		return this.getServletContext().getInitParameter(name);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getInitParameterNames()
	 */
	@Override
	public Enumeration<String> getInitParameterNames() {
		return this.getServletContext().getInitParameterNames();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#setInitParameter(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean setInitParameter(String name, String value) {
		return this.getServletContext().setInitParameter(name, value);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String name) {	
		return this.getServletContext().getAttribute(name);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getAttributeNames()
	 */
	@Override
	public Enumeration<String> getAttributeNames() {
		return this.internal.getAttributeNames();
	}
	
	/**
	 * 获取所有属性名
	 * @return {@link Set}
	 */
	public Set<String> getAttributeNamesToSet(){
		Set<String> result = new HashSet<String>();
		
		Enumeration<String> em = this.getServletContext().getAttributeNames();
		while (em.hasMoreElements()){
			result.add(em.nextElement());
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute(String name, Object object) {
		this.getServletContext().setAttribute(name, object);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#removeAttribute(java.lang.String)
	 */
	@Override
	public void removeAttribute(String name) {
		this.getServletContext().removeAttribute(name);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServletContextName()
	 */
	@Override
	public String getServletContextName() {
		return this.getServletContext().getServletContextName();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addServlet(java.lang.String, java.lang.String)
	 */
	@Override
	public Dynamic addServlet(String servletName, String className) {
		return this.getServletContext().addServlet(servletName, className);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addServlet(java.lang.String, javax.servlet.Servlet)
	 */
	@Override
	public Dynamic addServlet(String servletName, Servlet servlet) {
		return this.getServletContext().addServlet(servletName, servlet);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addServlet(java.lang.String, java.lang.Class)
	 */
	@Override
	public Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
		return this.getServletContext().addServlet(servletName, servletClass);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#createServlet(java.lang.Class)
	 */
	@Override
	public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
		return this.getServletContext().createServlet(clazz);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServletRegistration(java.lang.String)
	 */
	@Override
	public ServletRegistration getServletRegistration(String servletName) {
		return this.getServletContext().getServletRegistration(servletName);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServletRegistrations()
	 */
	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		return this.getServletContext().getServletRegistrations();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addFilter(java.lang.String, java.lang.String)
	 */
	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
		return this.getServletContext().addFilter(filterName, className);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addFilter(java.lang.String, javax.servlet.Filter)
	 */
	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
		return this.getServletContext().addFilter(filterName, filter);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addFilter(java.lang.String, java.lang.Class)
	 */
	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
		return this.getServletContext().addFilter(filterName, filterClass);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#createFilter(java.lang.Class)
	 */
	@Override
	public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
		return this.getServletContext().createFilter(clazz);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getFilterRegistration(java.lang.String)
	 */
	@Override
	public FilterRegistration getFilterRegistration(String filterName) {
		return this.getServletContext().getFilterRegistration(filterName);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getFilterRegistrations()
	 */
	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		return this.getServletContext().getFilterRegistrations();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getSessionCookieConfig()
	 */
	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		return this.getServletContext().getSessionCookieConfig();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#setSessionTrackingModes(java.util.Set)
	 */
	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
		this.getServletContext().setSessionTrackingModes(sessionTrackingModes);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getDefaultSessionTrackingModes()
	 */
	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		return this.getServletContext().getDefaultSessionTrackingModes();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getEffectiveSessionTrackingModes()
	 */
	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		return this.getServletContext().getEffectiveSessionTrackingModes();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addListener(java.lang.String)
	 */
	@Override
	public void addListener(String className) {
		this.getServletContext().addListener(className);

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addListener(java.util.EventListener)
	 */
	@Override
	public <T extends EventListener> void addListener(T t) {
		this.getServletContext().addListener(t);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addListener(java.lang.Class)
	 */
	@Override
	public void addListener(Class<? extends EventListener> listenerClass) {
		this.getServletContext().addListener(listenerClass);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#createListener(java.lang.Class)
	 */
	@Override
	public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
		return this.getServletContext().createListener(clazz);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getJspConfigDescriptor()
	 */
	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		return this.getServletContext().getJspConfigDescriptor();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getClassLoader()
	 */
	@Override
	public ClassLoader getClassLoader() {
		return this.getServletContext().getClassLoader();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#declareRoles(java.lang.String[])
	 */
	@Override
	public void declareRoles(String... roleNames) {
		this.getServletContext().declareRoles(roleNames);

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getVirtualServerName()
	 */
	@Override
	public String getVirtualServerName() {
		return this.getServletContext().getVirtualServerName();
	}

	/**
	 * 获取内部的{@link ServletContext}
	 * @return
	 */
	public ServletContext getServletContext(){
		return this.internal;
	}
	
}