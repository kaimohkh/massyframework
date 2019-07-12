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
 * @日   期:  2019年1月24日
 */
package com.massyframework.integration.spring.extender;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import com.massyframework.assembly.Assembly;
import com.massyframework.assembly.AssemblyAware;
import com.massyframework.assembly.AssemblyContextAware;
import com.massyframework.assembly.AssemblyException;
import com.massyframework.assembly.AssemblyListener;
import com.massyframework.assembly.service.DependencyServiceDefinition;
import com.massyframework.assembly.spi.DependencyServiceProvider;
import com.massyframework.assembly.spi.HandlerBase;
import com.massyframework.logging.Logger;
import com.massyframework.logging.LoggerAware;

/**
 * 提供对外部依赖Bean的定义
 *
 */
public class DependencyServiceBeanDefiner extends HandlerBase 
    implements BeanDefiner {

	private volatile SpringAssemblyContext context;
	
	/**
	 * 
	 */
	public DependencyServiceBeanDefiner() {

	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.spring.BeanDefiner#praperaBeanFactory(org.springframework.beans.factory.support.DefaultListableBeanFactory)
	 */
	@Override
	public void praperaBeanFactory(DefaultListableBeanFactory beanFactory) throws BeansException {		
		BeanLifecycleProcessor processor = new BeanLifecycleProcessor();
		beanFactory.addBeanPostProcessor(processor);
				
		//注入装配件
		this.injectBeanDefinition("assembly", Assembly.class, this.getAssembly(), beanFactory);
				
		Map<DependencyServiceDefinition<?>, Object> serviceMap =
				this.getDependencyServices();
		if (serviceMap != null){
			for (Map.Entry<DependencyServiceDefinition<?>,Object> entry: serviceMap.entrySet()){
				DependencyServiceDefinition<?> definition = entry.getKey();
				Object service = entry.getValue();
				
				//注入Bean
				this.injectBeanDefinition(
						definition.getCName(), definition.getRequiredType(), service, beanFactory);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.massyframework.container.spring.BeanDefiner#postBeanFactory(org.springframework.beans.factory.support.DefaultListableBeanFactory)
	 */
	@Override
	public void postBeanFactory(DefaultListableBeanFactory beanFactory) throws BeansException {

	}
	
	/**
	 * 注入Bean定义
	 * @param beanName bean名称，可以为null.
	 * @param beanType  bean类型
	 * @param bean bean实例 
	 * @param beanFactory bean工厂
	 */
	protected void injectBeanDefinition(String beanName, Class<?> beanType, Object bean, DefaultListableBeanFactory beanFactory){
		GenericBeanDefinition definition = new GenericBeanDefinition();
		
		definition.setBeanClass(DependencyServiceFactoryBean.class);
		MutablePropertyValues propertyValue = definition
				.getPropertyValues();
		propertyValue.add("objectType", beanType);
		propertyValue.add("object", bean);

		//使用组件编号注册Bean定义
		if (beanName == null){
			beanName = beanType.getName();
		}
		beanFactory.registerBeanDefinition(beanName, definition);
	}
	
	/**
	 * 装配件上下文
	 * @return {@link SpringAssemblyContext}
	 */
	public SpringAssemblyContext getAssemblyContext() {
		return this.context;
	}
	
	/**
	 * 绑定Spring容器
	 * @param container {@link SpringContainer}
	 */
	public void bindSpringContainer(SpringContainer container) {
		if (this.context == null) {
			this.context = new SpringAssemblyContext(
				this.getLifecycleManager(), container);
		}
	}
	
	/**
	 * 取消绑定
	 */
	public void unbindSpringContainer() {
		if (this.context != null) {
			this.context = null;
		}
	}
		
	/**
	 * 获取依赖服务实例
	 * @return {@link Map}, 可能返回null.
	 */
	protected Map<DependencyServiceDefinition<?>, Object> getDependencyServices(){
		DependencyServiceProvider handler =
				this.findOne(DependencyServiceProvider.class);
		if (handler != null) {
			return handler.getDependencyServices();
		}
		return Collections.emptyMap();
	}
	
	private class BeanLifecycleProcessor implements BeanPostProcessor, DestructionAwareBeanPostProcessor{
		
		private Logger logger;
		
		/* (non-Javadoc)
		 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
		 */
		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
			SpringAssemblyContext context = DependencyServiceBeanDefiner.this.context;
			if (context == null) {
				throw new AssemblyException("applicationContext hasnot contain AssemblyContext.");
			}
			
			//服务是否由外部注入
			boolean injected = this.isDependencyService(beanName);
			if (!injected) {
				AssemblyContextAware.maybeToBind(bean, context);
				AssemblyAware.maybeToBind(bean, getAssembly());
				if (bean  instanceof AssemblyListener) {
					getAssembly().addListener((AssemblyListener)bean);
				}
				
				if (logger == null) {
					this.logger = context.getAssociatedAssembly().getLogger();
				}
				LoggerAware.maybeToBind(bean, logger);
			}	
			
			return bean;
		}

		/* (non-Javadoc)
		 * @see org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor#postProcessBeforeDestruction(java.lang.Object, java.lang.String)
		 */
		@Override
		public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
			SpringAssemblyContext context = DependencyServiceBeanDefiner.this.context;
			if (context == null) {
				throw new AssemblyException("applicationContext hasnot contain AssemblyContext.");
			}
			
			if (bean instanceof AssemblyListener){
				if (!this.isDependencyService(beanName)) {
					getAssembly().removeListener((AssemblyListener)bean);
					AssemblyContextAware.maybeToBind(bean, null);
				}
			}						
		}
		
		protected boolean isDependencyService(String beanName) {
			if (beanName == null) return false;
			
			try {
				BeanDefinition definition = getAssemblyContext().getContainer()
						.getApplicationContext().getBeanFactory().getBeanDefinition(beanName);
				if (definition != null) {
					return DependencyServiceFactoryBean.class.getName().equals(definition.getBeanClassName());
				}
				return false;
			}catch(Exception e) {
				return false;
			}
		}
		
	}
}
