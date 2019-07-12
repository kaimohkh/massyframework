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
 * @日   期:  2019年1月28日
 */
package com.massyframework.assembly.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.massyframework.assembly.initparam.InitParameters;
import com.massyframework.assembly.initparam.VariableReplacer;

/**
 * 具有不变性的初始化参数
 */
final class ImmutableInitParams implements InitParameters {
	
	private final Map<String, String> params;

	/**
	 * 
	 */
	public ImmutableInitParams(Map<String, String> initParams) {
		this.params = Collections.EMPTY_MAP.equals(initParams) ?
			initParams :
				Collections.unmodifiableMap(initParams);
		
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.support.params.InitParameters#createVariableReplacer()
	 */
	@Override
	public VariableReplacer createVariableReplacer() {
		return VariableReplacer.createVariableReplacer(this.params);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.support.params.InitParameters#getInitParameter(java.lang.String)
	 */
	@Override
	public String getInitParameter(String name) {
		if (name == null) return null;
		return this.params.get(name);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.support.params.InitParameters#getInitParameterNames()
	 */
	@Override
	public List<String> getInitParameterNames() {
		return new ArrayList<String>(this.params.keySet());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ImmutableInitParams";
	}


}
