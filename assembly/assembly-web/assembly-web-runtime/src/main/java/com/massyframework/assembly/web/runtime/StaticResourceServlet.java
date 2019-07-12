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
 * @日   期:  2019年2月1日
 */
package com.massyframework.assembly.web.runtime;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.massyframework.assembly.web.WebResourceRepository;

/**
 * 静态资源Servlet处理器
 *
 */
public class StaticResourceServlet implements Servlet {
	
	private static final String GET = "GET";
	private static final String LAST_MODIFIED = "Last-Modified"; 
	private static final String IF_MODIFIED_SINCE = "If-Modified-Since"; 
	private static final String IF_NONE_MATCH = "If-None-Match";
	private static final String ETAG = "ETag"; 

	private final WebResourceRepository webResRepo;
	private ServletConfig config; 
	
	/**
	 * 
	 */
	public StaticResourceServlet(WebResourceRepository webResRepo) {
		this.webResRepo = Objects.requireNonNull(webResRepo, "\"webResRepo\" cannot be null.");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		this.config = config;	
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#getServletConfig()
	 */
	@Override
	public ServletConfig getServletConfig() {
		return this.config;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse) res;
		if (request.getMethod().equals(GET)){
			String path = request.getRequestURI();
			if (path != null && path.startsWith("/WEB-INF/")) { //$NON-NLS-1$
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			URL url = this.findResource(path);
			if (url != null){
				this.writeResource(request, response, path, url);
			}else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		}		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#getServletInfo()
	 */
	@Override
	public String getServletInfo() {
		return null;
	}


	/* (non-Javadoc)
	 * @see javax.servlet.Servlet#destroy()
	 */
	@Override
	public void destroy() {
	}
		
	/**
	 * 按<code>path</code>查找对应的资源
	 * @param path {@link String}，路径
	 * @return {@link URL},未找到对应的资源，可以返回null.
	 */
	protected URL findResource(String path) {
		ClassLoader classLoader = this.webResRepo.findClassLoaderWithResource(path);
		return classLoader == null ? null :
			classLoader.getResource(WebResourceRepository.RESOURCES_PAHT.concat(path));
	}
	
	/**
	 * 加载静态资源并写入Http响应
	 * @param req http请求
	 * @param resp http响应
	 * @param resourcePath 资源路径
	 * @param resourceURL 资源URL
	 * @throws IOException 发生IO异常
	 */
	private void writeResource(final HttpServletRequest req, final HttpServletResponse resp, 
			final String pathInfo, final URL resourceURL) throws IOException {
		URLConnection connection = resourceURL.openConnection();
		long lastModified = connection.getLastModified();
		int contentLength = connection.getContentLength();

		String etag = null;
		if (lastModified != -1 && contentLength != -1)
			etag = "W/\"" + contentLength + "-" + lastModified + "\""; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		// Check for cache revalidation.
		// We should prefer ETag validation as the guarantees are stronger and all HTTP 1.1 clients should be using it
		String ifNoneMatch = req.getHeader(IF_NONE_MATCH);
		if (ifNoneMatch != null && etag != null && ifNoneMatch.indexOf(etag) != -1) {
			resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		}

		long ifModifiedSince = req.getDateHeader(IF_MODIFIED_SINCE);
		// for purposes of comparison we add 999 to ifModifiedSince since the fidelity
		// of the IMS header generally doesn't include milli-seconds
		if (ifModifiedSince > -1 && lastModified > 0 && lastModified <= (ifModifiedSince + 999)) {
			resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		}

		// return the full contents regularly
		if (contentLength != -1)
			resp.setContentLength(contentLength);

		String contentType = req.getServletContext().getMimeType(pathInfo);
		if (contentType != null)
			resp.setContentType(contentType);

		if (lastModified > 0)
			resp.setDateHeader(LAST_MODIFIED, lastModified);

		if (etag != null)
			resp.setHeader(ETAG, etag);

		if (contentLength != 0) {
			// open the input stream
			InputStream is = null;
			try {
				is = connection.getInputStream();
				// write the resource
				try {
					OutputStream os = resp.getOutputStream();
					int writtenContentLength = writeResourceToOutputStream(is, os);
					if (contentLength == -1 || contentLength != writtenContentLength)
						resp.setContentLength(writtenContentLength);
				} catch (IllegalStateException e) { // can occur if the response output is already open as a Writer
					Writer writer = resp.getWriter();
					writeResourceToWriter(is, writer);
					// Since ContentLength is a measure of the number of bytes contained in the body
					// of a message when we use a Writer we lose control of the exact byte count and
					// defer the problem to the Servlet Engine's Writer implementation.
				}
			} catch (FileNotFoundException e) {
				// FileNotFoundException may indicate the following scenarios
				// - url is a directory
				// - url is not accessible
				sendError(resp, HttpServletResponse.SC_FORBIDDEN);
			} catch (SecurityException e) {
				// SecurityException may indicate the following scenarios
				// - url is not accessible
				sendError(resp, HttpServletResponse.SC_FORBIDDEN);
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
						// ignore
					}
			}
		}
					
	}
	
	/**
	 * 发生错误消息
	 * @param resp
	 * @param sc
	 * @throws IOException
	 */
	private void sendError(final HttpServletResponse resp, int sc) throws IOException {
		try {
			resp.reset();
			resp.sendError(sc);
		} catch (IllegalStateException e) {
		}
	}
	
	/**
	 * 写入资源到输出流
	 * @param is
	 * @param os
	 * @return
	 * @throws IOException
	 */
	private int writeResourceToOutputStream(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[8192];
		int bytesRead = is.read(buffer);
		int writtenContentLength = 0;
		while (bytesRead != -1) {
			os.write(buffer, 0, bytesRead);
			writtenContentLength += bytesRead;
			bytesRead = is.read(buffer);
		}
		return writtenContentLength;
	}
	
	/**
	 * 写入Writer中
	 * @param is
	 * @param writer
	 * @throws IOException
	 */
	private void writeResourceToWriter(InputStream is, Writer writer) throws IOException {
		Reader reader = new InputStreamReader(is);
		try {
			char[] buffer = new char[8192];
			int charsRead = reader.read(buffer);
			while (charsRead != -1) {
				writer.write(buffer, 0, charsRead);
				charsRead = reader.read(buffer);
			}
		} finally {
			if (reader != null) {
				reader.close(); // will also close input stream
			}
		}
	}
}
