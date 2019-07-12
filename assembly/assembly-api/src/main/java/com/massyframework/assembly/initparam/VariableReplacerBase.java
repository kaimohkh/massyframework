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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实现{@link VariableReplacer}的抽象类
 *
 */
abstract class VariableReplacerBase implements VariableReplacer {
	
	protected static final String EMPTY = "";
	
	/**
	 * 构造方法
	 */
	public VariableReplacerBase() {
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.support.VariableReplacer#findAndReplace(java.lang.String)
	 */
	@Override
	public String findAndReplace(String content) {
		if (content == null) return null;
        if (content.length()< 4) return content;
        
        Pattern pattern = this.getPattern();
        if (pattern == null) return content;

        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(sb,
            		this.getVariable(matcher.group(1)));
        }
        matcher.appendTail(sb);


        return sb.length() == 0 ? content : sb.toString();
    }
	
	/**
	 * 根据<code>name</code>获取对应的变量值
	 * @param name {@link String},变量名
	 * @return {@link String},变量值
	 */
	protected abstract String getVariable(String name);

	/**
	 * 字符匹配模式
	 * @return {@link Pattern}
	 */
	protected abstract Pattern getPattern();
}
