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
 * @日   期:  2019年2月3日
 */
package com.massyframework.instrument;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarFile;

/**
 * @author huangkaihui
 *
 */
/**
 * 封装Instrumentation，用于确保注册的ClassFileTransformer能正确卸载
 * @author huangkaihui
 *
 */
class InstrumentationWrapper implements Instrumentation {
	
	private final Instrumentation inst;
	private Set<ClassFileTransformer> transformers =
			new CopyOnWriteArraySet<ClassFileTransformer>();

	/**
	 * 
	 */
	public InstrumentationWrapper(Instrumentation inst) {
		if (inst == null){
			throw new IllegalArgumentException("inst cannot be null.");
		}
		this.inst = inst;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#addTransformer(java.lang.instrument.ClassFileTransformer, boolean)
	 */
	public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {
		if (transformer != null){
			this.inst.addTransformer(transformer, canRetransform);
			this.transformers.add(transformer);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#addTransformer(java.lang.instrument.ClassFileTransformer)
	 */
	public void addTransformer(ClassFileTransformer transformer) {
		if (transformer != null){
			this.inst.addTransformer(transformer);
			this.transformers.add(transformer);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#removeTransformer(java.lang.instrument.ClassFileTransformer)
	 */
	public boolean removeTransformer(ClassFileTransformer transformer) {
		boolean result = this.inst.removeTransformer(transformer);
		if (result){
			this.transformers.remove(transformer);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#isRetransformClassesSupported()
	 */
	public boolean isRetransformClassesSupported() {
		return this.inst.isRedefineClassesSupported();
	}

	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#retransformClasses(java.lang.Class[])
	 */
	public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {
		this.inst.retransformClasses(classes);
	}

	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#isRedefineClassesSupported()
	 */
	public boolean isRedefineClassesSupported() {
		return this.inst.isRedefineClassesSupported();
	}

	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#redefineClasses(java.lang.instrument.ClassDefinition[])
	 */
	public void redefineClasses(ClassDefinition... definitions)
			throws ClassNotFoundException, UnmodifiableClassException {
		this.inst.redefineClasses(definitions);
	}

	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#isModifiableClass(java.lang.Class)
	 */
	public boolean isModifiableClass(Class<?> theClass) {
		return this.inst.isModifiableClass(theClass);
	}

	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#getAllLoadedClasses()
	 */
	@SuppressWarnings("rawtypes")
	public Class[] getAllLoadedClasses() {
		return this.inst.getAllLoadedClasses();
	}

	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#getInitiatedClasses(java.lang.ClassLoader)
	 */
	@SuppressWarnings("rawtypes")
	public Class[] getInitiatedClasses(ClassLoader loader) {
		return this.inst.getInitiatedClasses(loader);
	}

	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#getObjectSize(java.lang.Object)
	 */
	public long getObjectSize(Object objectToSize) {
		return this.inst.getObjectSize(objectToSize);
	}

	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#appendToBootstrapClassLoaderSearch(java.util.jar.JarFile)
	 */
	public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {
		this.inst.appendToBootstrapClassLoaderSearch(jarfile);
	}

	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#appendToSystemClassLoaderSearch(java.util.jar.JarFile)
	 */
	public void appendToSystemClassLoaderSearch(JarFile jarfile) {
		this.inst.appendToSystemClassLoaderSearch(jarfile);
	}

	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#isNativeMethodPrefixSupported()
	 */
	public boolean isNativeMethodPrefixSupported() {
		return this.inst.isNativeMethodPrefixSupported();
	}

	/* (non-Javadoc)
	 * @see java.lang.instrument.Instrumentation#setNativeMethodPrefix(java.lang.instrument.ClassFileTransformer, java.lang.String)
	 */
	public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {
		this.inst.setNativeMethodPrefix(transformer, prefix);
	}
	
	
	@Override
	public void redefineModule(Module module, Set<Module> extraReads, Map<String, Set<Module>> extraExports,
			Map<String, Set<Module>> extraOpens, Set<Class<?>> extraUses, Map<Class<?>, List<Class<?>>> extraProvides) {
		this.inst.redefineModule(module, extraReads, extraExports, extraOpens, extraUses, extraProvides);		
	}

	@Override
	public boolean isModifiableModule(Module module) {
		return this.inst.isModifiableModule(module);
	}

	/**
	 * 清除所有类文件转换器
	 */
	public void clean(){
		Set<ClassFileTransformer> tmp = this.transformers;
		this.transformers = null;
		for (ClassFileTransformer trans: tmp){
			this.inst.removeTransformer(trans);
		}
	}
	
	Instrumentation getInstation(){
		return this.inst;
	}

}

