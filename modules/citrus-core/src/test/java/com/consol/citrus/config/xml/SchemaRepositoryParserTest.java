/*
 * Copyright 2006-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.config.xml;

import com.consol.citrus.json.JsonSchemaRepository;
import com.consol.citrus.json.schema.SimpleJsonSchema;
import com.consol.citrus.testng.AbstractBeanDefinitionParserTest;
import com.consol.citrus.xml.XsdSchemaRepository;
import com.consol.citrus.xml.schema.RootQNameSchemaMappingStrategy;
import com.consol.citrus.xml.schema.TargetNamespaceSchemaMappingStrategy;
import com.consol.citrus.xml.schema.WsdlXsdSchema;
import com.consol.citrus.xml.schema.XsdSchemaCollection;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * @author Christoph Deppisch
 */
public class SchemaRepositoryParserTest extends AbstractBeanDefinitionParserTest {

    @Test
    public void testSchemaRepositoryParser() {
        Map<String, XsdSchemaRepository> schemaRepositories = beanDefinitionContext.getBeansOfType(XsdSchemaRepository.class);

        Assert.assertEquals(schemaRepositories.size(), 4);

        // 1st schema repository
        XsdSchemaRepository schemaRepository = schemaRepositories.get("schemaRepository1");
        Assert.assertEquals(schemaRepository.getSchemaMappingStrategy().getClass(), TargetNamespaceSchemaMappingStrategy.class);
        Assert.assertNotNull(schemaRepository.getSchemas());
        Assert.assertEquals(schemaRepository.getSchemas().size(), 5);
        Assert.assertEquals(schemaRepository.getSchemas().get(0).getClass(), SimpleXsdSchema.class);
        Assert.assertEquals(schemaRepository.getSchemas().get(1).getClass(), WsdlXsdSchema.class);
        Assert.assertEquals(schemaRepository.getSchemas().get(2).getClass(), SimpleXsdSchema.class);
        Assert.assertEquals(schemaRepository.getSchemas().get(3).getClass(), WsdlXsdSchema.class);
        Assert.assertEquals(schemaRepository.getSchemas().get(4).getClass(), XsdSchemaCollection.class);
        Assert.assertNotNull(schemaRepository.getLocations());
        Assert.assertEquals(schemaRepository.getLocations().size(), 0);

        // 2nd schema repository
        schemaRepository = schemaRepositories.get("schemaRepository2");
        Assert.assertNotNull(schemaRepository.getSchemas());
        Assert.assertEquals(schemaRepository.getSchemas().size(), 15);
        Assert.assertNotNull(schemaRepository.getLocations());
        Assert.assertEquals(schemaRepository.getLocations().size(), 1);
        Assert.assertEquals(schemaRepository.getLocations().get(0), "classpath:com/consol/citrus/validation/*");

        // 3rd schema repository
        schemaRepository = schemaRepositories.get("schemaRepository3");
        Assert.assertEquals(schemaRepository.getSchemaMappingStrategy().getClass(), RootQNameSchemaMappingStrategy.class);

        Assert.assertTrue(beanDefinitionContext.containsBean("schema1"));
        Assert.assertTrue(beanDefinitionContext.containsBean("schema2"));
        Assert.assertTrue(beanDefinitionContext.containsBean("wsdl1"));
        Assert.assertTrue(beanDefinitionContext.containsBean("wsdl2"));
        Assert.assertTrue(beanDefinitionContext.containsBean("schemaCollection1"));
    }

    @Test
    public void testXmlSchemaRepositoryDeclaration() {

        //GIVEN

        //WHEN
        Map<String, XsdSchemaRepository> schemaRepositories = beanDefinitionContext.getBeansOfType(XsdSchemaRepository.class);

        //THEN
        XsdSchemaRepository xmlSchemaRepository = schemaRepositories.get("xmlSchemaRepository");
        Assert.assertEquals(1, xmlSchemaRepository.getSchemas().size());
    }

    @Test
    public void testJsonSchemaRepositoryParser() {

        //GIVEN

        //WHEN
        Map<String, JsonSchemaRepository> schemaRepositories = beanDefinitionContext.getBeansOfType(JsonSchemaRepository.class);

        //THEN
        Assert.assertEquals(schemaRepositories.size(), 2);

        // 1st schema repository
        JsonSchemaRepository schemaRepository = schemaRepositories.get("jsonSchemaRepository1");
        Assert.assertNotNull(schemaRepository.getSchemas());
        Assert.assertEquals(schemaRepository.getSchemas().size(), 2);
        Assert.assertEquals(schemaRepository.getSchemas().get(0).getClass(), SimpleJsonSchema.class);
        Assert.assertEquals(schemaRepository.getSchemas().get(1).getClass(), SimpleJsonSchema.class);
        Assert.assertNotNull(schemaRepository.getLocations());
        Assert.assertEquals(schemaRepository.getLocations().size(), 0);

        // 2nd schema repository
        schemaRepository = schemaRepositories.get("jsonSchemaRepository2");
        Assert.assertNotNull(schemaRepository.getSchemas());
        Assert.assertEquals(schemaRepository.getSchemas().size(), 2);
        Assert.assertNotNull(schemaRepository.getLocations());
        Assert.assertEquals(schemaRepository.getLocations().size(), 1);
        Assert.assertEquals(schemaRepository.getLocations().get(0), "classpath:com/consol/citrus/validation/*");

        Assert.assertTrue(beanDefinitionContext.containsBean("jsonSchema1"));
        Assert.assertTrue(beanDefinitionContext.containsBean("jsonSchema2"));
    }
}
