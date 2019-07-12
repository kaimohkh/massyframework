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
package com.massyframework.assembly.web.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * MassyHttpServletResponse
 *
 */
public class MassyHttpServletResponse extends HttpServletResponseWrapper {

	private int sc;
	private String message;
	
	/**
	 * 构造方法
	 * @param response
	 */
	public MassyHttpServletResponse(HttpServletResponse response) {
		super(response);
		this.sc = response.getStatus();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponseWrapper#sendError(int, java.lang.String)
	 */
	@Override
	public void sendError(int sc, String msg) throws IOException {
		this.sc = sc;
		this.message = msg;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponseWrapper#sendError(int)
	 */
	@Override
	public void sendError(int sc) throws IOException {
		this.sc = sc;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponseWrapper#setStatus(int)
	 */
	@Override
	public void setStatus(int sc) {
		this.sc = sc;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponseWrapper#setStatus(int, java.lang.String)
	 */
	@Override
	public void setStatus(int sc, String sm) {
		this.sc = sc;
		this.message = sm;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponseWrapper#getStatus()
	 */
	@Override
	public int getStatus() {
		return this.sc;
	}

	/**
	 * 提交
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	void commit() throws IOException{
		if (this.sc == HttpServletResponse.SC_NOT_FOUND ){
			if (this.message != null){
				super.sendError(this.sc, this.message);
			}
		}else {
			HttpServletResponse response = (HttpServletResponse)this.getResponse();
			if (this.sc != response.getStatus()) {
				if (this.message == null) {
					response.setStatus(this.sc);
				}else {
					response.setStatus(this.sc, this.message);
				}
			}
		}
	}
}
