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

package com.consol.citrus.validation.xml;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.exceptions.UnknownElementException;
import com.consol.citrus.message.Message;
import com.consol.citrus.util.XMLUtils;
import com.consol.citrus.variable.VariableExtractor;
import com.consol.citrus.xml.xpath.XPathExpressionResult;
import com.consol.citrus.xml.xpath.XPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.namespace.NamespaceContext;
import java.util.*;
import java.util.Map.Entry;

/**
 * Class reads message elements via XPath expressions and saves the text values as new test variables.
 * Implementation parsed the message payload as DOM document, so XML message payload is needed here.
 *  
 * @author Christoph Deppisch
 */
public class XpathPayloadVariableExtractor implements VariableExtractor {

    /** Map defines xpath expressions and target variable names */
    private Map<String, String> xPathExpressions = new HashMap<String, String>();

    /** Namespace definitions used in xpath expressions */
    private Map<String, String> namespaces = new HashMap<String, String>();

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(XpathPayloadVariableExtractor.class);

    /** 
     * Determines desired behavior when XPath evaluation yields empty result.
     * When false, a CitrusRuntimeException will be thrown for slash-separated expressions,
     * and an UnknownElementException will be thrown for dot-separated expressions.
     * When true, the Citrus variable will be assigned the value of an empty string.
     * Default: false
     **/
    private boolean isLenient;

    /**
     * Will throw exceptions when elements not found.
     */
    public XpathPayloadVariableExtractor() {
        this(false);
    }

    /**
     * Configures desired behavior when elements not found.
     * @param isLenient Should VariableExtractor suppress exceptions when elements not found?
     */
    public XpathPayloadVariableExtractor(boolean isLenient) {
        this.isLenient = isLenient;
    }

    /**
     * Extract variables using Xpath expressions.
     */
    public void extractVariables(Message message, TestContext context) {
        if (CollectionUtils.isEmpty(xPathExpressions)) {return;}

        if (log.isDebugEnabled()) {
            log.debug("Reading XML elements with XPath");
        }

        NamespaceContext nsContext = context.getNamespaceContextBuilder().buildContext(message, namespaces);
        Document doc = XMLUtils.parseMessagePayload(message.getPayload(String.class));

        for (Entry<String, String> entry : xPathExpressions.entrySet()) {
            String pathExpression = context.replaceDynamicContentInString(entry.getKey());
            String variableName = entry.getValue();

            if (log.isDebugEnabled()) {
                log.debug("Evaluating XPath expression: " + pathExpression);
            }

            if (XPathUtils.isXPathExpression(pathExpression)) {
                evaluateSlashSeparatedExpression(pathExpression, variableName, doc, context, nsContext);
            } else {
                evaluateDotSeparatedExpression(pathExpression, variableName, doc, context);
            }
        }
    }

    /**
     * Set the xPath expressions to identify the message elements and variable names.
     * @param xPathExpressions the xPathExpressions to set
     */
    public void setXpathExpressions(Map<String, String> xPathExpressions) {
        this.xPathExpressions = xPathExpressions;
    }

    /**
     * List of expected namespaces.
     * @param namespaces the namespaces to set
     */
    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    /**
     * Gets the xPathExpressions.
     * @return the xPathExpressions
     */
    public Map<String, String> getXpathExpressions() {
        return xPathExpressions;
    }

    /**
     * Gets the namespaces.
     * @return the namespaces
     */
    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    public boolean isLenient() {
        return isLenient;
    }

    /**
     * Evaluate slash-separated XPath expression.
     * When the XPath evaluation yields a single result, that value is assigned to the named variable in the test context.
     * When the XPath evaluation yields a list of results, the list is converted to a comma-separated string and assigned to the named variable in the test context.
     * When the XPath evaluation yields no result, the behavior is governed by the isLenient flag.
     * If the flag is false (the default), a CitrusRuntimeException will be thrown.
     * If the flag is true, an empty string is assigned as the value of the named variable in the test context.
     * @param pathExpression XPath expression to be evaluated
     * @param variableName Name of variable in the test context
     * @param doc Document against which the XPath expression to be evaluated
     * @param context Test context that contains the variable whose value will be assigned
     * @param nsContext Namespace context in which the XPath expression will be evaluated
     */
    protected void evaluateSlashSeparatedExpression(String pathExpression, String variableName, Document doc, TestContext context, NamespaceContext nsContext) {
        XPathExpressionResult resultType = XPathExpressionResult.fromString(pathExpression, XPathExpressionResult.STRING);
        String expr = XPathExpressionResult.cutOffPrefix(pathExpression);

        try {
            Object value = XPathUtils.evaluate(doc, expr, nsContext, resultType);
            if (value instanceof List) {
                value = StringUtils.arrayToCommaDelimitedString(((List<String>)value).toArray(new String[((List)value).size()]));
            }
            context.setVariable(variableName, value);
        }
        catch (CitrusRuntimeException ex) {
            if (isLenient) {
                context.setVariable(variableName, "");
            }
            else {
                throw ex;
            }
        }
    }

    /**
     * Evaluate dot-separated XPath expression.
     * When the XPath expression yields an element node, the value of the first child is assigned to the named variable in the test context.
     * When the XPath expression yields an element node that has no first child, the empty string is assigned to the named variable in the test context.
     * When the XPath expression yields a non-element node, the value of the node is assigned to the named variable in the test context.
     * When the XPath evaluation yields no result, the behavior is governed by the isLenient flag.
     * If the flag is false (the default), an UnknownElementException will be thrown.
     * If the flag is true, an empty string is assigned as the value of the named variable in the test context.
     * @param pathExpression XPath expression to be evaluated
     * @param variableName Name of variable in the test context
     * @param doc Document against which the XPath expression to be evaluated
     * @param context Test context that contains the variable whose value will be assigned
     */
    protected void evaluateDotSeparatedExpression(String pathExpression, String variableName, Document doc, TestContext context) {
        Node node = XMLUtils.findNodeByName(doc, pathExpression);

        if (node == null) {
            if (isLenient) {
                context.setVariable(xPathExpressions.get(pathExpression), "");
            }
            else {
                throw new UnknownElementException("No element found for expression" + pathExpression);
            }
        }
        else if (node.getNodeType() == Node.ELEMENT_NODE) {
            if (node.getFirstChild() != null) {
                context.setVariable(xPathExpressions.get(pathExpression), node.getFirstChild().getNodeValue());
            } else {
                context.setVariable(xPathExpressions.get(pathExpression), "");
            }
        } else {
            context.setVariable(xPathExpressions.get(pathExpression), node.getNodeValue());
        }
    }

}
