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
 * @日   期:  2019年1月7日
 */
package com.massyframework.logging;

/**
 * 日志记录器
 */
public interface Logger {

	/**
     * 跟踪消息记录是否激活
     * @return {@link boolean},返回<code>true</code>表示已激活，<code>false</code>表示未激活
     */
    boolean isTraceEnabled();

    /**
     * 调试消息记录是否激活
     * @return {@link boolean},返回<code>true</code>表示已激活，<code>false</code>表示未激活
     */
    boolean isDebugEnabled();

    /**
     * 消息记录是否激活
     * @return {@link boolean},返回<code>true</code>表示已激活，<code>false</code>表示未激活
     */
    boolean isInfoEnabled();

    /**
     * 警告消息记录是否激活
     * @return {@link boolean},返回<code>true</code>表示已激活，<code>false</code>表示未激活
     */
    boolean isWarnEnabled();

    /**
     * 错误消息记录是否激活
     * @return {@link boolean},返回<code>true</code>表示已激活，<code>false</code>表示未激活
     */
    boolean isErrorEnabled();

    /**
     * 记录跟踪消息
     * @param message 消息
     */
    void trace(String message);

    /**
     * 记录跟踪消息
     * @param message 消息
     * @param cause 例外
     */
    void trace(String message, Throwable cause);

    /**
     * 记录调试消息
     * @param message 消息
     */
    void debug(String message);

    /**
     * 记录调试消息
     * @param message 消息
     * @param cause 例外
     */
    void debug(String message, Throwable cause);

    /**
     * 记录消息
     * @param message 消息
     */
    void info(String message);

    /**
     * 记录消息
     * @param message 消息
     * @param cause 例外
     */
    void info(String message, Throwable cause);

    /**
     * 记录警告消息
     * @param message 消息
     */
    void warn(String message);

    /**
     * 记录警告消息
     * @param message 消息
     * @param cause 例外
     */
    void warn(String message, Throwable cause);

    /**
     * 记录错误消息
     * @param message 消息
     */
    void error(String message);

    /**
     * 记录错误消息
     * @param message 消息
     * @param cause 例外
     */
    void error(String message, Throwable cause);
}
