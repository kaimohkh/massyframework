/**
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
 * Author: 黄开晖（117227773@qq.com）
 * Date:   2019年6月8日
 */
package com.massyframework.lang.support;

import java.util.Comparator;

import com.massyframework.lang.Orderable;

/**
 * Orderable排序器
 * @author  Huangkaihui
 *
 */
public final class OrderedComparator<T extends Orderable> implements Comparator<T> {
	
	/**
	 * 构造方法
	 */
	public OrderedComparator() {
	
	}

	@Override
	public int compare(T o1, T o2) {
		return Integer.compare(o1.getOrdered(), o2.getOrdered());
	}

	@Override
	public String toString() {
		return "OrderedComparator";
	}
}
