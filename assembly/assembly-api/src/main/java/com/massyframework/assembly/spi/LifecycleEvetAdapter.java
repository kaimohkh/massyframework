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
 * @日   期:  2019年1月19日
 */
package com.massyframework.assembly.spi;

/**
 * 生命周期事件适配器
 *
 */
public class LifecycleEvetAdapter implements LifecycleListener {

	/**
	 * 
	 */
	public LifecycleEvetAdapter() {
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.base.handle.LifecycleListener#onReadied()
	 */
	@Override
	public void onReadied() {
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.base.handle.LifecycleListener#onActivated()
	 */
	@Override
	public void onActivated() {
		
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.base.handle.LifecycleListener#onInactivating()
	 */
	@Override
	public void onInactivating() {
		
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.base.handle.LifecycleListener#onUnreadying()
	 */
	@Override
	public void onUnreadying() {
		
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.base.handle.LifecycleListener#onUninstalling()
	 */
	@Override
	public void onUninstalling() {
		
	}
}
