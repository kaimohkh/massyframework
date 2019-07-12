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
package com.massyframework.assembly.util;

import java.lang.ref.SoftReference;
import java.net.URL;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import com.massyframework.assembly.Framework;

/**
 *
 *
 */
public class AssemblyDomainSchemaValidatorFactory {

	private static SoftReference<Validator> INSTANCE;

    /**
     * 获取装配件模式验证器
     * @return {@link Validator}
     * @throws SAXException
     */
    public static synchronized Validator getValidator() throws SAXException {
        Validator result = INSTANCE == null ? null : INSTANCE.get();
        if (result == null){
            URL url =  loadXmlSchema();
            SchemaFactory schemaFactory =
                    SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

            Schema schema = schemaFactory.newSchema(url);
            result = schema.newValidator();

            INSTANCE = new SoftReference<Validator>(result);
        }

        return result;
    }

    /**
             * 加载所有的Schema配置
     * @return {@link URL}
     */
    private static URL loadXmlSchema(){
        ClassLoader loader = Framework.class.getClassLoader();
        URL result =
        	loader.getResource("com/massyframework/assembly/xml/domain-2.3.xsd");
        
        return result;
    }

}
