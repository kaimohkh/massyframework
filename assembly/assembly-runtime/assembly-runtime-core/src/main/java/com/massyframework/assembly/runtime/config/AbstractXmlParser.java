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
 * @日   期:  2019年3月1日
 */
package com.massyframework.assembly.runtime.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.massyframework.assembly.AssemblyConfigBuilder;
import com.massyframework.assembly.AssemblyDefinition;
import com.massyframework.assembly.ParseException;
import com.massyframework.assembly.initparam.InitParameters;
import com.massyframework.assembly.initparam.VariableReplacer;
import com.massyframework.assembly.runtime.assembly.DefaultAssemblyConfigBuilder;
import com.massyframework.assembly.runtime.service.DependencyServiceDefinitionUtils;
import com.massyframework.assembly.runtime.service.ExportServiceDefinitionUtils;
import com.massyframework.assembly.service.DependencyService;
import com.massyframework.assembly.service.DependencyServiceDefinition;
import com.massyframework.assembly.service.ExportService;
import com.massyframework.assembly.service.ExportServiceDefinition;
import com.massyframework.assembly.service.ServiceProperties;
import com.massyframework.assembly.service.ServiceProperty;
import com.massyframework.assembly.service.ServiceReference;


/**
 * 抽象xml装配件配置解析器
 *
 */
abstract class AbstractXmlParser implements AssemblyConfigBuilderParser {
	
	public static final String ASSEMBLY                = "assembly";
    public static final String SYMBOLICNAME            = "symbolicName";
    public static final String NAME                    = "name";
    public static final String DESCRIPTION             = "description";
    public static final String TAGS                    = "tags";
    
    public static final String CONTAINER               = "container";
    public static final String CONTAINER_CLASSES       = "container.classes";
    
    public static final String CONFIGLOCATION          = "config-location";
    public static final String CLASSES                 = "classes";
    public static final String SERVLETCONTEXTLISTENER  = "servletcontext-listener";
    public static final String SERVLET                 = "servlet";
    public static final String URL_PATTERN             = "url-pattern";
    public static final String LOAD_ON_STARTUP         = "load-on-startup";
    public static final String ASYNC_SUPPORTED         = "async-supported";
    
    public static final String INIT_PARAMS             = "init-params";
    public static final String PARAMETER               = "parameter";
    public static final String PARAMETER_KEY           = "key";
    public static final String PARAMETER_VALUE         = "value";

    public static final String PAGE_MAPPINGS           = "page-mappings";
    public static final String PAGE_MAPPING            = "mapping";
    public static final String ALIAS                   = "alias";
    public static final String PAGE                    = "page";

    public static final String HTTP_RESOURCES          = "http-resources";
    public static final String HTTP_RESOURCE           = "resource";

    public static final String DEPENDENCY_SERVICE      = "dependency-service";
    public static final String CNAME                   = "cName";
    public static final String CLASS                   = "class";
    public static final String FILTERSTRING            = "filterString";

    public static final String EXPORT_SERVICE          = "export-service";
    public static final String PROPERTIES              = "properties";
    public static final String PROPERTY                = "property";
    public static final String PROPERTY_NAME           = "name";
    public static final String PROPERTY_TYPE           = "type";
    public static final String PROPERTY_VALUE          = "value";
    public static final String IS_SERVICEFACTORY       = "is-servicefactory";
    
    public static final String DOT                     = ".";

    private static final String EMPTY_STRING = "";
    private final Validator validator;

	/**
	 * 构造方法
	 */
	public AbstractXmlParser() {
		try {
            this.validator = AssemblySchemaValidatorFactory.getValidator();
        }catch(Exception e){
            throw new ParseException("schema validator create failed.",e);
        }
	}

	/* (non-Javadoc)
	 * @see com.massyframework.assembly.runtime.config.AssemblyConfigBuilderParser#parse(com.massyframework.assembly.AssemblyDefinition, com.massyframework.assembly.support.InitParameters)
	 */
	@Override
	public AssemblyConfigBuilder parse(AssemblyDefinition definition, InitParameters frameworkInitParams)
			throws ParseException {
		Objects.requireNonNull(definition, "\"definition\" cannot be null.");
		Objects.requireNonNull(frameworkInitParams, "\"frameworkInitParams\" cannot be null.");
		
		AssemblyConfigBuilder result = new DefaultAssemblyConfigBuilder(definition, frameworkInitParams);		
		URL xmlURL = definition.getAssemblyURL();
        this.doParserAssemblyXml(result, xmlURL);
		
		return result;
	}
	
	protected void doParserAssemblyXml(AssemblyConfigBuilder builder, URL xmlURL) {
		try {
            this.validatingXmlConfiguration(xmlURL);
        }catch(Exception e){
            throw new ParseException("validate xml failed:" + xmlURL.getPath() + ".", e);
        }
                
        InputStream inputStream = null;
        try {
            inputStream = xmlURL.openStream();
            Document document = this.createDocument(inputStream);
            this.doParseAssemblyInformation(document, builder);
            this.doParseInitParameters(document, builder);
                        
            VariableReplacer replacer = builder.createVariableReplacer();
            this.doParserDependencyDefinitions(document, builder, replacer);
            this.doParserExportDefinitions(document, builder, replacer);
            this.doParserClassesConfiguration(builder, replacer);
        }catch(IOException e) {
            throw new ParseException("load assembly configuration file failed.", e);
        }catch(ClassNotFoundException e){
            throw new ParseException("load assembly configuration file failed.", e);
        }catch(Exception e) {
        	 throw new ParseException("load assembly configuration file failed.", e);
		}finally{
        	if (inputStream != null) {
        		try {
        			inputStream.close();
        		}catch(IOException e) {}
        	}
        }
	}
		
	/**
	 * 解析输出服务定义
	 * @param document {@link Document}, xml doc
	 * @param builder {@link AssemblyConfigBuilder},装配件配置构建器
	 * @param replacer 变量替换器
	 * @throws ClassNotFoundException 类未找到时抛出的例外
	 */
	protected void doParserExportDefinitions(
            Document document, AssemblyConfigBuilder builder, VariableReplacer replacer)
            throws ClassNotFoundException{
        NodeList list = document.getElementsByTagName(EXPORT_SERVICE);
        if (list != null){
            int size = list.getLength();
            for (int i=0; i<size; i++){
                Node node = list.item(i);

                String cName = this.getTextContent(node.getAttributes().getNamedItem(CNAME));
                if (cName != null){
                    cName = replacer.findAndReplace(cName);
                }
                List<String> classNames = new ArrayList<String>();
                Map<String, Object> props = new HashMap<String,Object>();

                NodeList childs = node.getChildNodes();
                int count = childs.getLength();
                for (int j=0; j<count; j++){
                    Node child = childs.item(j);
                    switch (child.getNodeName()){
                        case CLASS :{
                            classNames.add(this.getTextContent(child));
                            break;
                        }
                        case PROPERTIES: {
                            props = this.doParserProperties(child.getChildNodes(), replacer);
                            break;
                        }
                    }
                }

                Class<?>[] classes = new Class<?>[classNames.size()];
                for (int k=0; k<classNames.size(); k++){
                    classes[k] = builder.getAssemblyClassLoader().loadClass(classNames.get(k));
                }

                props.put(ServiceReference.CNAME, cName);
                props.put(ServiceReference.OBJECT_CLASS, classes);
                ExportServiceDefinition definition =
                		ExportServiceDefinitionUtils.createExportServiceDefinition(
                				new ServiceProperties(props));
                builder.addExportServiceDefinition(definition);
            }
        }
    }
	
	/**
	 * 解析依赖服务定义
	 * @param document {@link Document}, xml doc
	 * @param builder {@link AssemblyConfigBuilder}, 装配件配置构建器
	 * @param replacer 变量替换器
	 * @throws ClassNotFoundException 类未找到时抛出的例外
	 */
	protected void doParserDependencyDefinitions(
            Document document, AssemblyConfigBuilder builder, VariableReplacer replacer)
            throws ClassNotFoundException{
		NodeList list = document.getElementsByTagName(DEPENDENCY_SERVICE);
        if (list != null){
            int size = list.getLength();
            for (int i=0; i<size; i++){
                Node node = list.item(i);
                String cName = this.getTextContent(node.getAttributes().getNamedItem(CNAME));
                if (cName != null){
                    cName = replacer.findAndReplace(cName);
                }
                String className = null;
                String filterString = null;
                NodeList childs = node.getChildNodes();
                int count = childs.getLength();
                for (int j=0; j<count; j++){
                    Node child = childs.item(j);
                    switch (child.getNodeName()){
                        case CLASS:{
                            className = this.getTextContent(child);
                            break;
                        }
                        case FILTERSTRING: {
                            filterString = this.getTextContent(child);
                            if (filterString != null) {
                            	filterString = replacer.findAndReplace(filterString);
                            	if (filterString.isEmpty()) {
                            		filterString = null;
                            	}
                            }
                            break;
                        }
                    }
                }

                Class<?> clazz = builder.getAssemblyClassLoader().loadClass(className);
                DependencyServiceDefinition<?> definition =
                		DependencyServiceDefinitionUtils.createDependencyServcieDefinition(clazz, cName, filterString);
                builder.addDependencyServiceDefinition(definition);
            }
        }
	}
	
	/**
	 * 解析初始化参数
	 * @param document {@link Document}, xml doc
	 * @param builder {@link AssemblyConfigBuilder}, 装配件配置构建器
	 * @throws ParseException 解析异常抛出的例外
	 */
	protected void doParseInitParameters(Document document, AssemblyConfigBuilder builder) throws ParseException{               		
		//解析节点
        NodeList list = document.getElementsByTagName(INIT_PARAMS);
        if (list != null){
            if (list.getLength() > 0){
                list = list.item(0).getChildNodes();
                int size = list.getLength();
                for (int i=0; i<size; i++){
                    Node node = list.item(i);
                    if (node.getNodeName().equals(PARAMETER)){
                        Node keyNode = node.getAttributes().getNamedItem(PARAMETER_KEY);
                        Node valueNode = node.getAttributes().getNamedItem(PARAMETER_VALUE);

                        String paramName = this.getTextContent(keyNode);                        
                        String paramValue = this.getTextContent(valueNode);                    
                        builder.addInitParameter(paramName, paramValue);
                    }
                }
            }
        }
	}
	
	/**
	 * 创建装配件注册凭据
	 * @param document {@link Document}， xml doc
	 * @param builder {@link AssemblyConfigBuilder},装配件配置构建器
	 * @return {@link AssemblyConfigBuilder}
	 * @throws ParseException 解析异常抛出的例外
	 */
	protected void doParseAssemblyInformation(Document document, 
			AssemblyConfigBuilder builder) throws ParseException{
		NodeList nodeList =
                document.getElementsByTagName(ASSEMBLY).item(0).getChildNodes();
        int size = nodeList.getLength();

        String symbolicName = null;
        String name = null;
        String description = null;
        String tags = null;
        String containerName = null;
        
        for (int i=0; i<size; i++){
            Node node = nodeList.item(i);
            switch (node.getNodeName()){
                case SYMBOLICNAME :{
                    symbolicName = this.getTextContent(node);
                    break;
                }
                case NAME : {
                    name = this.getTextContent(node);
                    break;
                }
                case DESCRIPTION : {
                    description = this.getTextContent(node);
                    break;
                }
                case TAGS : {
                    tags = this.getTextContent(node);
                    break;
                }
                case CONTAINER : {
                	containerName = this.getTextContent(node);
                    break;
                }
            }
        }
                
        if (!builder.getSymbolicName().toIdentifier().equals(symbolicName)) {
        	throw new ParseException("symbolicName is not match:" + symbolicName + ".");
        }
                
        builder.setName(name)
        	.setDescription(description)
        	.setContainerName(containerName);
          
        //标签
        if (tags != null) {
        	String[] arr = StringUtils.split(tags);
        	for (String tag : arr) {
        		tag = StringUtils.trim(tag);
        		if (!StringUtils.isEmpty(tag)) {
        			builder.addTag(tag);
        		}
        	}
        }
	}
	
	 /**
     * 解析容器类的注解
     * @param configuration 类配置项
     * @param builder 装配件构建器
     * @param replacer 变量替换器
     * @param initializeContext 初始化上下文
     */
    protected void doParserClassesConfiguration(AssemblyConfigBuilder builder, VariableReplacer replacer) throws Exception{
    	String classNames = builder.getInitParameter(CONTAINER_CLASSES);
    	if (classNames != null) {
    		ClassLoader classLoader = builder.getAssemblyClassLoader();
    		String[] arr = StringUtils.split(classNames);
    		for (String className: arr) {
    			className = StringUtils.trim(className);
    			if (className.length()>0) {
    				Class<?> clazz = classLoader.loadClass(className);
    				this.doParserAnnotation(clazz, builder, replacer);
    			}
    		}
    	}
    }
	
	/**
	 * 解析容器类上的注解依赖
	 * @param configuration 类配置
	 * @param builder {@link AssemblyConfigBuilder}, 装配件配置构建器
	 * @param replacer 变量替换器
	 * @param kernelContainer 内核容器
	 */
	protected void doParserAnnotation(Class<?> clazz, 
			AssemblyConfigBuilder builder, VariableReplacer replacer){	
		if (clazz == Object.class) return;
		if (clazz == null) return;

		DependencyService[] dependencySerivces = clazz.getDeclaredAnnotationsByType(DependencyService.class);
		for (DependencyService ds : dependencySerivces){
			DependencyServiceDefinition<?> definition =
					DependencyServiceDefinitionUtils.createDependencyServcieDefinition(
							ds.requiredType(), 
							this.getStringWithAnnotationValue(replacer.findAndReplace(ds.cName())), 
							this.getStringWithAnnotationValue(replacer.findAndReplace(ds.filterString())));
			builder.addDependencyServiceDefinition(definition);
		}
				
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method: methods) {
			ExportService anno = method.getDeclaredAnnotation(ExportService.class);
			if (anno != null) {
				Map<String, Object> props = new HashMap<String, Object>();
				for (ServiceProperty property: anno.properties()){
					String value = replacer.findAndReplace(property.value());
					switch (property.propertyType()){
						case STRING : {
							props.put(property.key(), value);
							break;
						}
						case INT : {
							props.put(property.key(), Integer.parseInt(value));
							break;
						}
						case BOOL : {
							props.put(property.key(), Boolean.parseBoolean(value));
							break;
						}
						case LONG : {
							props.put(property.key(), Long.parseLong(value));
							break;
						}
						case SHORT : {
							props.put(property.key(), Short.parseShort(value));
							break;
						}
						case FLOAT : {
							props.put(property.key(), Float.parseFloat(value));
							break;
						}
						case DOUBLE : {
							props.put(property.key(), Double.parseDouble(value));
							break;
						}
						case CHAR: {
							props.put(property.key(), replacer.findAndReplace(value).toCharArray());
							break;
						}
						case BYTE : {
							props.put(property.key(), Byte.parseByte(value));
							break;
						}
					}
				}
				
				String methodName = method.getName();
				if (methodName.startsWith("get")) {
					methodName = StringUtils.substring(methodName, 3, 4).toLowerCase() 
							+ StringUtils.substring(methodName, 4);
				}
				String cName = methodName;
				
				props.put(ServiceReference.CNAME, cName);
				props.put(ServiceReference.OBJECT_CLASS, anno.exportTypes());
				ExportServiceDefinition definition =
						ExportServiceDefinitionUtils.createExportServiceDefinition(
								new ServiceProperties(props));
				builder.addExportServiceDefinition(definition); 
			}
				
		}	
				
		Class<?> parent = clazz.getSuperclass();
		this.doParserAnnotation(parent, builder, replacer);
	}
		
	/**
     * 解析属性
     * @param list {@link List}
     * @return {@link Map}
     */
    protected Map<String, Object> doParserProperties(NodeList list, VariableReplacer replacer){
        Map<String, Object> result = new HashMap<String, Object>();
        int size = list.getLength();
        for (int i=0; i<size; i++){
            Node node = list.item(i);
            if (node.getNodeName().equals(PROPERTY)){
                Node attr = node.getAttributes().getNamedItem(PROPERTY_NAME);
                String name = replacer.findAndReplace(this.getTextContent(attr));

                attr = node.getAttributes().getNamedItem(PROPERTY_TYPE);
                String type = attr == null? "string": attr.getTextContent().toLowerCase();
                attr = node.getAttributes().getNamedItem(PROPERTY_VALUE);
                String value = replacer.findAndReplace(this.getTextContent(attr));

                switch(type){
                    case "string":{
                        if (ServiceReference.NAME.equals(name)){
                            String text[] = StringUtils.split(StringUtils.trim(value), ",");
                            result.put(name, text);
                        }else{
                            result.put(name, value);
                        }
                        break;
                    }
                    case "int" : {
                        result.put(name, Integer.parseInt(value));
                        break;
                    }
                    case "short" : {
                    	result.put(name, Short.parseShort(value));
                    	break;
                    }
                    case "boolean" : {
                        result.put(name, Boolean.parseBoolean(value));
                        break;
                    }
                    case "long" : {
                        result.put(name, Long.parseLong(value));
                        break;
                    }
                    case "float" : {
                        result.put(name, Float.parseFloat(value));
                        break;
                    }
                    case "double": {
                        result.put(name, Double.parseDouble(value));
                        break;
                    }
                    case "char": {
                        result.put(name, value.toCharArray());
                        break;
                    }
                    case "byte": {
                        result.put(name, Byte.parseByte(value));
                        break;
                    }
                }

                //服务排名
                if (name.equals(ServiceReference.RANKING)){
                    result.put(name, Integer.parseInt(value));
                }
            }
        }

        return result;
    }
	
	/**
     * 获取节点的TextContent数据
     * @param node 节点
     * @return {@link String}, 可以返回null.
     */
    protected String getTextContent(Node node){
        if (node == null) return null;

        String result = node.getTextContent();
        if (result == null){
            return null;
        }
        
        result = StringUtils.trim(result);
        if (result.isEmpty()){
            return null;
        }

        return result;
    }
	
	/**
     * 创建Xml Document对象
     * @param inputStream 输入流
     * @return {@link Document}
     * @throws ParserConfigurationException 解析发生错误抛出的异常
     * @throws IOException IO读写发生异常抛出的异常
     * @throws SAXException SAX解析发生错误抛出的异常
     */
    protected final Document createDocument(InputStream inputStream)
            throws ParseException,IOException {
        try{
            DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
            DocumentBuilder builder=factory.newDocumentBuilder();
            return builder.parse(inputStream);
        }catch(ParserConfigurationException e){
            throw new ParseException("load assmebly configuration file failed.", e);
        }catch(SAXException e){
            throw new ParseException("load assmebly configuration file failed.", e);
        }catch(IOException e){
            throw e;
        }
    }
    
	/**
     * 验证Xml配置文件的格式
     * @param url 资源定位
     * @throws SAXException SAX验证不通过抛出异常
     * @throws IOException 发生IO读写错误时抛出异常
     */
    protected final void validatingXmlConfiguration(URL url) throws SAXException, IOException{
        InputStream inputStream = null;
        try{
            inputStream = url.openStream();
            StreamSource source = new StreamSource(inputStream);
            validator.validate(source);
        }finally{
        	if (inputStream != null) {
        		try {
        			inputStream.close();
        		}catch(IOException e) {}
        	}
        }
    }
    
    /**
     * 从注解值中取得对应的字符串<br>
     * 如果注解值为"".则返回null.
     * @param value 注解值
     * @return {@link String}, 可以返回null.
     */
    protected String getStringWithAnnotationValue(String value){
    	if (EMPTY_STRING.equals(value)){
    		return null;
    	}
    	
    	return value;
    }
}
