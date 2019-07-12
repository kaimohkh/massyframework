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
 * @日   期:  2019年1月13日
 */
package com.massyframework.assembly.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.massyframework.assembly.Assembly;

/**
 *
 *
 */
public interface ServiceRepository {

	/**
     * 添加服务事件监听器和对应的筛选器
     * @param listener 服务事件监听器
     * @param filter 服务事件筛选器
     */
    void addListener(ServiceListener listener, Filter filter);

    /**
     * 添加服务事件监听器
     * @param listener 服务事件监听器
     * @param filterString 服务事件筛选字符串，用于筛选关注的服务.<br>
     * 			可以为null，表示监听所有服务事件
     */
    void addListener(ServiceListener listener, String filterString);

    /**
     * 使用筛选字符串创建筛选器<br>
     * 筛选字符串用于筛选关心的服务
     * @param filterString 筛选字符串
     * @return {@link Filter}
     */
    Filter createFilter(String filterString);
    
    /**
     * 使用<code>serviceType</code>和筛选字符串创建筛选器
     * @param serviceType 服务类型
     * @param filterString 筛选字符串
     * @return {@link Filter}
     */
    Filter createFilter(Class<?> serviceType, String filterString);
    
    /**
     * 按<code>serviceType</code>查找服务实例<br>
     * 本方法组合{@link #findServiceReference(Class)}和{@link #getService(ServiceReference)}方法，
     * <pre>
     *   ServiceReference reference = findServiceReference(serviceType);
     *   if (reference == null) return null;
     *   return (S)getService(reference);
     * </pre>
     * @param serviceType 服务类型
     * @param <S>
     * @return {@link S}, 无匹配的服务实例可以返回null.
     */
	default <S> S findService(Class<S> serviceType){
        ServiceReference<S> reference = findServiceReference(serviceType);
        if (reference == null) return null;
        return getService(reference);
    }

    /**
     * 按<code>serviceType</code>查找服务实例<br>
     * 本方法组合{@link #findServiceReference(Class)}和{@link #getService(ServiceReference)}方法，
     * <pre>
     *   Filter filter = createFilter(filterString);
     *   ServiceReference reference = findServiceReference(serviceType, filter);
     *   if (reference == null) return null;
     *   return (S)getService(reference);
     * </pre>
     * @param serviceType 服务类型
     * @param filterString 服务过滤的字符串
     * @param <S>
     * @return {@link S}, 无匹配的服务实例可以返回null.
     */
    default <S> S findService(Class<S> serviceType, String filterString){
        Filter filter = createFilter(filterString);
        ServiceReference<S> reference = findServiceReference(serviceType, filter);
        if (reference == null) return null;
        return getService(reference);
    }

    /**
     * 按<code>serviceType</code>查找服务引用<br>
     * 存在多个同类型服务时，根据{@link Service#RANKING}（倒序)，和
     * {@link Service#ID}(正序) 排序，并返回最优的服务引用
     * @param serviceType 服务类型
     * @return {@link ServiceReference}，可能返回null.
     */
    <S> ServiceReference<S> findServiceReference(Class<S> serviceType);

    /**
     * 按<code>serviceType</code>和<code>filter</code>查找服务引用<br>
     * 存在多个同类型服务时，根据{@link Service#RANKING}（倒序)，和
     * {@link Service#ID}(正序) 排序，并返回最优的服务引用
     * @param serviceType 服务类型
     * @return {@link ServiceReference}，可能返回null.
     */
    <S> ServiceReference<S> findServiceReference(Class<S> serviceType, Filter filter);

    /**
     * 按<code>serviceType</code>和<code>filterString</code>查找服务引用<br>
     * 存在多个同类型服务时，根据{@link Service#RANKING}（倒序)，和
     * {@link Service#ID}(正序) 排序，并返回最优的服务引用
     * @param serviceType 服务类型
     * @param filterString 筛选字符串
     * @return {@link ServiceReference}
     */
    <S> ServiceReference<S> findServiceRefernece(Class<S> serviceType, String filterString);

    /**
     * 按<code>filter</code>查找服务引用<br>
     * 存在多个同类型服务时，根据{@link Service#RANKING}（倒序)，和
     * {@link Service#ID}(正序) 排序，并返回最优的服务引用
     *
     * <p>
     * 在已经返回服务类型的情况下，应尽可能使用效率更高的{@link #findServiceRefernece(Class, String)}方法进行查找。
     * @param filter 筛选器
     * @return {@link ServiceReference}，可能返回null.
     */
    <S> ServiceReference<S> findServiceReference(Filter filter);

    /**
     * 按<code>filterString</code>要求查找服务引用<br>
     * 存在多个同类型服务时，根据{@link Service#RANKING}（倒序)，和
     * {@link Service#ID}(正序) 排序，并返回最优的服务引用
     *
     * <p>
     * 在已经返回服务类型的情况下，应尽可能使用效率更高的{@link #findServiceRefernece(Class, String)}方法进行查找。
     * @param filterString 服务筛选字符串
     * @return {@link ServiceReference}，可能返回null.
     */
    <S> ServiceReference<S> findServiceReference(String filterString);

    /**
     * 按<code>serviceType</code>获取服务实例。<br>
     * 本方法调用{@link #findService(Class)}方法，返回结果为null时，抛出服务未找到例外。
     * @param serviceType 服务类型
     * @return {@link S}
     * @throws ServiceNotFoundException 服务未找到时，抛出的例外
     */
    default <S> S getService(Class<S> serviceType) throws ServiceNotFoundException{
        S result = findService(serviceType);
        if (result == null){
            throw new ServiceNotFoundException("service not found: serviceType=" + serviceType.getName() + ".");
        }
        return result;
    }

    /**
     * 按<code>serviceType</code>和<code>filterString</code>获取服务实例。<br>
     * 本方法调用{@link #findService(Class, java.lang.String)}方法，返回结果为null时，抛出服务未找到例外。
     * @param serviceType 服务类型
     * @return {@link S}
     * @throws ServiceNotFoundException 服务未找到时，抛出的例外
     */
    default <S> S getService(Class<S> serviceType, String filterString) throws ServiceNotFoundException{
        S result = findService(serviceType, filterString);
        if (result == null){
            throw new ServiceNotFoundException("service not found: serviceType=" + serviceType.getName() + "," +
                    "filterString=" + filterString + ".");
        }
        return result;
    }

    /**
     * 按<code>reference</code>获取服务实例
     * @param reference 服务引用
     * @return {@link Object},服务实例
     * @throws ServiceNotFoundException 服务未找到则抛出例外
     */
    <S> S getService(ServiceReference<S> reference) throws ServiceNotFoundException;

    /**
     * 服务注册器
     * @return {@link ServiceRegistry}
     */
    ServiceRegistry getServiceRegistry();

    /**
     * 按<code>references</code>获取服务实例集合
     * @param references 服务引用集合
     * @return {@link List}服务实例集合
     * @throws ServiceNotFoundException 服务未找到则抛出例外
     */
    <S> List<S> getServices(Collection<ServiceReference<S>> references) throws ServiceNotFoundException;
    
    /**
     * 按<code>serviceType</code>获取服务实例集合
     * @param serviceType 服务类型
     * @return {@link List}
     */
    default <S> List<S> getServices(Class<S> serviceType){
    	List<ServiceReference<S>> references = this.getServiceReferences(serviceType);
    	return references.isEmpty() ? Collections.emptyList() : this.getServices(references);
    }
    
    /**
     * 所有注册的服务类型
     * @return {@link List},服务类型列表
     */
    List<Class<?>> getServiceTypes();

    /**
     * 获取<code>serviceType</code>所有的服务引用
     * @param serviceType 服务类型
     * @return {@link List}
     */
    <S> List<ServiceReference<S>> getServiceReferences(Class<S> serviceType);

    /**
     * 获取<code>serviceType</code>和<code>filter</code>所有的服务引用
     * @param serviceType 服务类型
     * @return {@link List}
     */
   <S> List<ServiceReference<S>> getServiceReferences(Class<S> serviceType, Filter filter);

    /**
     * 获取<code>serviceType</code>和<code>filterString</code>所有的服务引用
     * @param serviceType 服务类型
     * @param filterString 筛选字符串
     * @return {@link List}
     */
    <S> List<ServiceReference<S>> getServiceReferences(Class<S> serviceType, String filterString);

    /**
     * 获取满足<code>filter</code>要求的所有服务引用<br>
     * 在已经返回服务类型的情况下，应尽可能使用效率更高的{@link #getServiceReferences(Class, String)}方法。
     * @param filter 筛选器
     * @return {@link List}
     */
    <S> List<ServiceReference<S>> getServiceReferences(Filter filter);

    /**
     * 获取满足<code>filterString</code>的所有服务引用<br>
     * 在已经返回服务类型的情况下，应尽可能使用效率更高的{@link #getServiceReferences(Class, String)}方法。
     * @param filterString 筛选字符串
     * @return {@link List}
     */
    <S> List<ServiceReference<S>> getServiceReferences(String filterString);

    /**
     * 移除服务事件监听器
     * @param listener 服务事件监听器
     */
    void removeListener(ServiceListener listener);
    
    /**
	 * 从<code>assembly</code>取回服务仓储
	 * @param assembly {@link Assembly},装配件
	 * @return {@link ServiceRepository}
	 */
	static ServiceRepository retrieveFrom(Assembly assembly) {
		Objects.requireNonNull(assembly, "\"assembly\" cannot be null.");
		return assembly.getAdapter(ServiceRepository.class);
	}
}
