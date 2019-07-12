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
 * @日   期:  2019年2月13日
 */
package com.massyframework.assembly.initparam;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 基于Map的变量替换器
 */
final class MapVariableReplacer extends VariableReplacerBase {
	
    private final Map<String, String> variableMap;
    private final Pattern pattern;

	/**
	 * 构造方法
	 * @param variableMap {@link Map},变量映射器
	 */
	public MapVariableReplacer(Map<String, String> variableMap) {
		this.variableMap = variableMap;
        if (variableMap == null || variableMap.isEmpty()) {
            this.pattern = null;
        }else{
            this.pattern = this.buildPattern();
        }
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.support.VariableReplacerBase#getVariable(java.lang.String)
	 */
	@Override
	protected String getVariable(String name) {
		return this.variableMap.get(name);
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.support.VariableReplacerBase#getPattern()
	 */
	@Override
	protected Pattern getPattern() {
		return this.pattern;
	}
	
	/**
     * 构建变量替换的模式
     */
    protected Pattern buildPattern(){
    	String patternString = "\\$\\{(" + 
    			join(this.variableMap.keySet().iterator(), "|") + ")\\}"; 
		return Pattern.compile(patternString);
    }
    
    private String join(final Iterator<?> iterator, final String separator) {

        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, "");
        }

        // two or more elements
        final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }


}
