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
 * @日   期:  2019年4月2日
 */
package com.massyframework.assembly.runtime.domain;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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

import com.massyframework.assembly.ParseException;
import com.massyframework.assembly.domain.Vendor;
import com.massyframework.assembly.util.AssemblyDomainSchemaValidatorFactory;
import com.massyframework.io.util.IOUtils;

/**
 *
 *
 */
public class AssemblyDomainXmlParser {

	private static final String DOMAIN      =  "domain";
	private static final String NAME        =  "name";
	private static final String VENDOR      =  "vendor";
	private static final String ID          =  "id";
	private static final String CONTACT     =  "contact";
	private static final String PUBLICKEY   =  "publicKey";
	private static final String SIGNEDDATA  =  "signedData";
	
	private final Validator validator;
	
	/**
	 * 
	 */
	public AssemblyDomainXmlParser() {
		try {
            this.validator = AssemblyDomainSchemaValidatorFactory.getValidator();
        }catch(Exception e){
            throw new ParseException("schema validator create failed.",e);
        }
	}
	
	public DefaultAssemblyDomain parseAssemblyDomain(URL url) throws ParseException, ParseException {
		try {
            this.validatingXmlConfiguration(url);
        }catch(Exception e){
            throw new ParseException("validate xml failed:" + url.getPath() + ".", e);
        }
		
		Document document = this.createDocument(url);
				
		NodeList nodeList =
                document.getElementsByTagName(DOMAIN).item(0).getChildNodes();
        int size = nodeList.getLength();
        
        AssemblyDomainBuilder builder =
        		new AssemblyDomainBuilder();
        
        for (int i=0; i<size; i++) {
        	Node node = nodeList.item(i);
        	switch (node.getNodeName()){
        		case NAME :{
        			String name = StringUtils.trim(node.getTextContent());
        			builder.setName(name);
        			break;
        		}
        		case PUBLICKEY: {
        			String publicKey = StringUtils.deleteWhitespace(StringUtils.trim(node.getTextContent()));
        			builder.setPublicKey(publicKey);
        			break;
        		}
        		case VENDOR: {
        			Vendor vendor = this.parseVendor(node);
        			builder.setVendor(vendor);
        			break;
        		}
        		case SIGNEDDATA: {
        			String signedData = 
        				StringUtils.deleteWhitespace(StringUtils.trim(node.getTextContent()));
        			builder.setSignedData(signedData);
        			break;
        		}
        	}
        	        	
        }
        
        return new DefaultAssemblyDomain(builder); 
	}

	/**
	 * 解析开发商
	 * @param document {@link Document}
	 * @return {@link Vendor}
	 */
	protected Vendor parseVendor(Node vendorNode) {
		NodeList nodeList = vendorNode.getChildNodes();
        int size = nodeList.getLength();
        
        Long id = null;
        String name = null;
        String contact = null;
        
        for (int i=0; i<size; i++){
        	Node node = nodeList.item(i);
            switch (node.getNodeName()){
	            case ID: {
	            	id = Long.parseLong(StringUtils.trim(node.getTextContent()));
	            	break;
	            }
	            case NAME : {
	            	name = StringUtils.trim(node.getTextContent());
	            	break;
	            }
	            case CONTACT : {
	            	contact =  StringUtils.trim(node.getTextContent());
	            	break;
	            }
            }
        }
        
        return new Vendor(id, name, contact);
	}

	/**
     * 创建Xml Document对象
     * @param inputStream 输入流
     * @return {@link Document}
     * @throws ParserConfigurationException 解析发生错误抛出的异常
     * @throws IOException IO读写发生异常抛出的异常
     * @throws SAXException SAX解析发生错误抛出的异常
     */
    protected final Document createDocument(URL url)
            throws ParseException,ParseException {
    	InputStream is = null;
        try{
        	is = url.openStream();
            DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
            DocumentBuilder builder=factory.newDocumentBuilder();
            return builder.parse(is);
        }catch(ParserConfigurationException e){
            throw new ParseException("load assembly domain file failed.", e);
        }catch(SAXException e){
            throw new ParseException("load assembly domain file failed.", e);
        }catch(IOException e){
            throw new ParseException("load assembly domain file failed.", e);
        }finally {
        	IOUtils.close(is);
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
}
