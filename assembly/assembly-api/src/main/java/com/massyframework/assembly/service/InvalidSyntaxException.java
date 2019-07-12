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
package com.massyframework.assembly.service;

import com.massyframework.assembly.AssemblyException;

/**
 * 无效的语法，当筛选字符串不满足语法要求时，抛出例外
 * @author huangkaihui
 *
 */
public class InvalidSyntaxException extends AssemblyException {

	/**
    *
    */
   private static final long serialVersionUID = 7728016049464733575L;

   private final String message;
   private final String filter;

   /**
    * 构造方法
    * @param msg 错误消息
    * @param filter 过滤串
    */
   public InvalidSyntaxException(String msg, String filter) {
       super(msg);
       this.message = msg;
       this.filter = filter;
   }

   /**
    * 信息
    * @return the message
    */
   public String getMessage() {
       return message;
   }

   /**
    * 过滤字符串
    * @return the filter
    */
   public String getFilter() {
       return filter;
   }
}
