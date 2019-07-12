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
 * @日   期:  2019年1月8日
 */
package com.massyframework.assembly.initparam;

import java.util.Map;

/**
 * 变量替换器，支持替换字符串中的${var}变量
 *
 */
public interface VariableReplacer {

	/**
     * 发现并替换变量<br>
     * 如果未发现变量，则直接返回<code>content</code>
     * @param content 文本内容
     * @return {@link String}
     */
    String findAndReplace(String content);

    /**
     * 创建变量替换器
     * @param variables {@link Map},变量
     * @return {@link VariableReplacer}, 变量替换器
     */
    public static VariableReplacer createVariableReplacer(Map<String, String> variables){
        return new MapVariableReplacer(variables);
    }
    
    /**
     * 创建变量替换器
     * @param initParameters {@link InitParameters},初始化参数
     * @return {@link VariableReplacer}, 变量替换器
     */
    public static VariableReplacer createVariableReplacer(InitParameters initParameters){
    	return new InitParametersVariableReplacer(initParameters);
    }

}
