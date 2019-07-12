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
package com.massyframework.assembly.runtime.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.monitor.AssemblyTaskMonitor;
import com.massyframework.assembly.service.Filter;
import com.massyframework.assembly.service.ServiceFactory;
import com.massyframework.assembly.service.ServiceReference;
import com.massyframework.assembly.service.ServiceRegistedEvent;
import com.massyframework.assembly.service.ServiceRegistration;
import com.massyframework.assembly.service.ServiceRepository;
import com.massyframework.assembly.service.ServiceUnregistingEvent;
import com.massyframework.lang.Registration;
import com.massyframework.logging.Logger;

/**
 * 服务管理器
 * @author huangkaihui
 */
public final class ServiceAdmin extends ServiceEventPublisher{

	private final Map<Class<?>, CopyOnWriteArrayList<RegistrationImpl<?>>> registrationMap = 
			new ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<RegistrationImpl<?>>>();
	private final Map<Long, RegistrationImpl<?>> idMap =
			new ConcurrentHashMap<Long, RegistrationImpl<?>>();
	private final ComparatorImpl comparator = new ComparatorImpl();

	private Map<String, ServiceRepository> repoMap =
			new ConcurrentHashMap<String, ServiceRepository>();
	
	/**
	 * 构造方法
	 * @param taskMonitor {@link AssemblyTaskMonitor}
	 */
	public ServiceAdmin(AssemblyTaskMonitor taskMonitor) {
		super(taskMonitor);
	}
	
	/**
	 * 根据<code>assembly</code>获取服务仓储
	 * @param assembly {@link Assembly},装配件
	 * @return {@link ServiceRepository}
	 */
	public ServiceRepository getServiceRepository(Assembly assembly) {
		assert assembly != null : "\"assembly\" cannot be null.";
		ServiceRepository result = this.repoMap.get(assembly.getSymbolicName());
		if (result == null) {
			ServiceRepository tmp = new ServiceRepositoryDelegate(this, assembly);
			result = this.repoMap.putIfAbsent(assembly.getSymbolicName(), tmp);
			if (result == null) {
				result = tmp;
			}
		}
		
		return result;
	}
	
	/**
     * 全局模式下查找首个符合筛选条件的服务引用
     * @param filter 筛选器
     * @return {@link ServiceReference}， 可能返回null.
     */
	@SuppressWarnings("unchecked")
	protected <S> ServiceReference<S> findServiceReference(Filter filter) {
        Objects.requireNonNull(filter, "filter cannot be null.");

        for (List<RegistrationImpl<?>> list: this.registrationMap.values()){
            for (RegistrationImpl<?> registration: list){
                ServiceReference<?> reference = registration.getReference();
                if (filter.match(reference)){
                    return (ServiceReference<S>) reference;
                }
            }
        }

        return null;
    }

    /**
     * 按服务类型和过滤条件，查找首个符合要求的服务引用
     * @param clazz 服务类型
     * @param filter 过滤器
     * @return {@link ServiceReference}输出服务引用
     */
	@SuppressWarnings("unchecked")
	protected <S> ServiceReference<S> findServiceReference(Class<S> clazz, Filter filter) {
		Objects.requireNonNull(clazz, "clazz cannot be null.");
		Objects.requireNonNull(filter, "filter cannot be null.");

        List<RegistrationImpl<?>> list =
                this.registrationMap.get(clazz);
        if (list != null){
            for (RegistrationImpl<?> registration: list) {
                ServiceReference<?> reference = registration.getReference();
                if (filter.match(reference)) {
                    return (ServiceReference<S>) reference;
                }
            }
        }
        return null;
    }

    /**
     * 全局模式下查找所有满足筛选条件的服务引用
     * @param filter
     * @return
     */
	@SuppressWarnings("unchecked")
	protected <S> List<ServiceReference<S>> getServiceReferences(Filter filter) {
		Objects.requireNonNull(filter, "filter cannot be null.");
        List<ServiceReference<S>> result =
                new ArrayList<ServiceReference<S>>();
        for (List<RegistrationImpl<?>> list: this.registrationMap.values()){
            for (RegistrationImpl<?> registration: list){
                ServiceReference<?> reference = registration.getReference();
                if (filter.match(reference)){
                    result.add((ServiceReference<S>)reference);
                }
            }
        }

        return result;
    }
	
	 /**
     * 按服务类型和过滤条件，查找所有符合要求的服务引用
     * @param clazz 服务类型
     * @param filter 过滤器
     * @return {@link List}输出服务引用集合
     */
    @SuppressWarnings({ "unchecked" })
	protected <S> List<ServiceReference<S>> getServiceReferences(
			Class<S> clazz, Filter filter) {
    	Objects.requireNonNull(clazz, "clazz cannot be null.");
    	Objects.requireNonNull(filter, "filter cannot be null.");

        List<ServiceReference<S>> result = new ArrayList<>();
        List<RegistrationImpl<?>> list =
                this.registrationMap.get(clazz);
        if (list != null){
            for (RegistrationImpl<?> registration: list) {
                ServiceReference<?> reference = registration.getReference();
                if (filter.match(reference)) {
                    result.add((ServiceReference<S>)reference);
                }
            }
        }
        return result;
    }
    
    /**
     * 根据<code>reference</code>和<code>assembly</code>查询服务实例
     * @param reference 服务引用
     * @param assembly 查询服务的装配件
     * @return {@link S}
     */
    @SuppressWarnings("unchecked")
	protected <S> S getService(ServiceReference<S> reference, Assembly assembly){
        if (reference == null) return null;
        if (assembly == null) return null;

        long id = reference.getServiceId();
        RegistrationImpl<?> registration = idMap.get(id);
        if (registration != null){
            return (S) registration.getSerivce(assembly);
        }
        return null;
    }
    
    /**
     * 所有服务类型的列表
     * @return {@link List},服务类型列表
     */
    protected List<Class<?>> getServiceTypes(){
		List<Class<?>> result =
				new ArrayList<Class<?>>(this.registrationMap.keySet());
		return result;
	}


	/**
	 * 按<code>reference</code>和<code>factory</code>注册服务
	 * 
	 * @param assembly 所属装配件
	 * @param reference 服务引用
	 * @param factory   服务工厂
	 * @return {@link ServiceRegistration}
	 */
	protected <S> ServiceRegistration<S> register(Assembly assembly, ServiceReference<S> reference, ServiceFactory<S> factory) {
		RegistrationImpl<S> result = new RegistrationImpl<S>(assembly, reference, factory);
		// 添加记录
		this.addRegistration(result);

		Logger logger = this.getLogger();
		if (logger != null) {
			// 日志处理
			if (logger.isInfoEnabled()) {
				StringBuilder builder = new StringBuilder();
				builder.append("registed service success:");
				builder.append("id=").append(reference.getServiceId())
					.append(", objectTypes=");
				Class<?>[] clazzes = (Class<?>[]) reference.getProperty(ServiceReference.OBJECT_CLASS);
				int size = clazzes.length;
				builder.append("[");
				for (int i = 0; i < size; i++) {
					builder.append(clazzes[i].getName());
					if (i != size - 1) {
						builder.append(",");
					}
				}
				builder.append("]");
	
				logger.info(builder.toString());
			}
		}

		// 发布服务注册事件
		ServiceRegistedEvent<S> event = new ServiceRegistedEvent<S>(reference);
		this.applyEvent(assembly, event);
		return result;
	}
	
	 /**
     * 注销服务
     * @param registration
     */
	protected final <S> void doUnregister(RegistrationImpl<S> registration){
		if (this.containsRegistration(registration)) {
			// 发布服务注销事件
			ServiceUnregistingEvent<S> event = new ServiceUnregistingEvent<S>(registration.getReference());
			this.applyEvent(registration.getAssembly(), event);
			this.removeRegistration(registration);
	        
			Logger logger = this.getLogger();
			if (logger != null) {
		        //日志处理
		        if (logger.isInfoEnabled()) {
		        	StringBuilder builder = new StringBuilder();
					builder.append("unregister service success:");
					builder.append("id=").append(registration.getServiceId())
						.append(", objectTypes=");
					Class<?>[] clazzes = (Class<?>[]) registration.getObjectClasses();
					int size = clazzes.length;
					builder.append("[");
					for (int i = 0; i < size; i++) {
						builder.append(clazzes[i].getName());
						if (i != size - 1) {
							builder.append(",");
						}
					}
					builder.append("]");
	
					logger.info(builder.toString());
		        }
			}
		}
    }

	/**
	 * 添加注册凭据
	 * 
	 * @param registration
	 */
	private <S> void addRegistration(RegistrationImpl<S> registration) {
		Class<?>[] types = registration.getObjectClasses();
		for (Class<?> type : types) {
			CopyOnWriteArrayList<RegistrationImpl<?>> list = this.registrationMap.get(type);
			if (list == null) {
				CopyOnWriteArrayList<RegistrationImpl<?>> tmp = new CopyOnWriteArrayList<>();
				list = this.registrationMap.putIfAbsent(type, tmp);
				if (list == null) {
					list = tmp;
				}
			}

			this.addInOrder(list, registration, comparator);
		}
		
		long id = registration.getServiceId();
		this.idMap.putIfAbsent(id, registration);
	}
	
	/**
	 * 将<code>item</code>按排序添加到<code>list</code>集合中
	 * 
	 * @param target {@link CopyOnWriteArrayList}实例
	 * @param item 待加入<code>list</code>的元素
	 * @param comparator 比较器，用于计算<code>item</code>在<code>list</code>中的排序
	 * @return {@link int},返回<code>item</code>加入<code>list</code>后的排序号.
	 */
	private <T> int addInOrder(final CopyOnWriteArrayList<T> list, final T item,
			final Comparator<T> comparator) {
		final int insertAt;
		// The index of the search key, if it is contained in the list; otherwise,
		// (-(insertion point) - 1)
		final int index = Collections.binarySearch(list, item, comparator);
		if (index < 0) {
			insertAt = -(index + 1);
		} else {
			insertAt = index + 1;
		}

		list.add(insertAt, item);
		return insertAt;
	}
	
	/**
     * 移除注册凭据
     * @param registration
     */
	private <S> void removeRegistration(RegistrationImpl<S> registration){
        Class<?>[] types = registration.getObjectClasses();
        for (Class<?> type: types){
            CopyOnWriteArrayList<RegistrationImpl<?>> list = this.registrationMap.get(type);
            if (list != null){
                list.remove(registration);
            }
        }
        
        this.idMap.remove(registration.getServiceId());
    }
	
	/**
	 * 是否包含<code>registration</code>
	 * @param registration {@link Registration}
	 * @return {@link boolean},返回<code>true</code>表示包含，否则返回<ode>false</code>
	 */
	protected boolean containsRegistration(RegistrationImpl<?> registration) {
		return this.idMap.containsKey(registration.getServiceId());
	}
	
	/**
	 * 服务注册凭据实现类
	 * 
	 * @author huangkaihui
	 *
	 * @param <S>
	 */
	private class RegistrationImpl<S> implements ServiceRegistration<S> {

		private Assembly assembly;
		private ServiceReference<S> reference;
		private ServiceFactory<S> factory;

		/**
		 * 构造方法
		 * 
		 * @param reference 服务引用
		 * @param factory   服务工厂
		 */
		public RegistrationImpl(Assembly assembly, ServiceReference<S> reference, ServiceFactory<S> factory) {
			this.assembly = Objects.requireNonNull(assembly, "\"assembly\" cannot be null.");
			this.reference = Objects.requireNonNull(reference, "\"reference\" cannot be null.");
			this.factory = Objects.requireNonNull(factory, "\"factory\" cannot be null.");
		}
		
		/**
		 * 所属装配件
		 * @return {@link Assembly}
		 */
		public Assembly getAssembly() {
			return this.assembly;
		}
		
		/**
		 * 服务编号
		 * @return {@link long}
		 */
		public long getServiceId() {
			return this.reference.getServiceId();
		}

		/**
		 * 注册的服务类型
		 * 
		 * @return {@link Class}数组
		 */
		public Class<?>[] getObjectClasses() {
			return this.reference.getObjectClasses();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.massyframework.assembly.service.ServiceRegistration#getReference()
		 */
		@Override
		public ServiceReference<S> getReference() {
			return this.reference;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.massyframework.assembly.service.ServiceFactory#getSerivce(com.
		 * massyframework.assembly.Assembly)
		 */
		@Override
		public S getSerivce(Assembly assembly) {
			return this.factory.getSerivce(assembly);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.massyframework.lang.Registration#unregister()
		 */
		@Override
		public void unregister() {
			doUnregister(this);
		}
	}

	private class ComparatorImpl implements Comparator<RegistrationImpl<?>> {

		/**
		 * Compares its two arguments for order. Returns a negative integer, zero, or a
		 * positive integer as the first argument is less than, equal to, or greater
		 * than the second.
		 * <p>
		 * <p>
		 * The implementor must ensure that {@code sgn(compare(x, y)) ==
		 * -sgn(compare(y, x))} for all {@code x} and {@code y}. (This implies that
		 * {@code compare(x, y)} must throw an exception if and only if
		 * {@code compare(y, x)} throws an exception.)
		 * <p>
		 * <p>
		 * The implementor must also ensure that the relation is transitive:
		 * {@code ((compare(x, y)>0) && (compare(y, z)>0))} implies
		 * {@code compare(x, z)>0}.
		 * <p>
		 * <p>
		 * Finally, the implementor must ensure that {@code compare(x, y)==0} implies
		 * that {@code sgn(compare(x, z))==sgn(compare(y, z))} for all {@code z}.
		 * <p>
		 * <p>
		 * It is generally the case, but <i>not</i> strictly required that
		 * {@code (compare(x, y)==0) == (x.equals(y))}. Generally speaking, any
		 * comparator that violates this condition should clearly indicate this fact.
		 * The recommended language is "Note: this comparator imposes orderings that are
		 * inconsistent with equals."
		 * <p>
		 * <p>
		 * In the foregoing description, the notation
		 * {@code sgn(}<i>expression</i>{@code )} designates the mathematical
		 * <i>signum</i> function, which is defined to return one of {@code -1},
		 * {@code 0}, or {@code 1} according to whether the value of <i>expression</i>
		 * is negative, zero, or positive, respectively.
		 *
		 * @param o1 the first object to be compared.
		 * @param o2 the second object to be compared.
		 * @return a negative integer, zero, or a positive integer as the first argument
		 *         is less than, equal to, or greater than the second.
		 * @throws NullPointerException if an argument is null and this comparator does
		 *                              not permit null arguments
		 * @throws ClassCastException   if the arguments' types prevent them from being
		 *                              compared by this comparator.
		 */
		@Override
		public int compare(RegistrationImpl<?> o1, RegistrationImpl<?> o2) {
			ServiceReference<?> reference1 = o1.getReference();
			ServiceReference<?> reference2 = o2.getReference();

			Integer o1_ranking = reference1.getProperty(ServiceReference.RANKING, Integer.class);
			if (o1_ranking == null) {
				o1_ranking = 0;
			}
			Integer o2_ranking = reference2.getProperty(ServiceReference.RANKING, Integer.class);
			if (o2_ranking == null) {
				o2_ranking = 0;
			}

			int result = Integer.compare(o2_ranking, o1_ranking);
			if (result == 0) {
				result = Long.compare(reference1.getServiceId(), reference2.getServiceId());
			}

			if (result == 0) {
				String o1_cName = reference1.getProperty(ServiceReference.CNAME, String.class);
				String o2_cName = reference2.getProperty(ServiceReference.CNAME, String.class);
				if (o1_cName == null) {
					result = -1;
				} else {
					if (o2_cName == null) {
						result = 1;
					} else {
						result = o1_cName.compareTo(o2_cName);
					}
				}
				;

			}
			return result;
		}
	}
}
