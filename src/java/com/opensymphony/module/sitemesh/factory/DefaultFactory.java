/*
 * Title:        DefaultFactory
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.factory;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.PageParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * DefaultFactory, reads configuration from <code>/WEB-INF/sitemesh.xml</code>, or uses the
 * default configuration if <code>sitemesh.xml</code> does not exist.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @author <a href="mailto:pathos@pandora.be">Mathias Bogaert</a>
 * @version $Revision: 1.1 $
 */
public class DefaultFactory extends BaseFactory {
    String configFileName = "/WEB-INF/sitemesh.xml";
    File configFile;
    long configLastModified;

    public DefaultFactory(Config config) {
        super(config);

        // configFilePath is null if loaded from war file
        String configFilePath = config.getServletContext().getRealPath(configFileName);

        if (configFilePath != null) { // disable config auto reloading for .war files
            configFile = new File(configFilePath);
        }

        loadConfig();
    }

    /** Refresh config before delegating to superclass. */
    public DecoratorMapper getDecoratorMapper() {
        refresh();
        return super.getDecoratorMapper();
    }

    /** Refresh config before delegating to superclass. */
    public PageParser getPageParser(String contentType) {
        refresh();
        return super.getPageParser(contentType);
    }

    /** Refresh config before delegating to superclass. */
    public boolean shouldParsePage(String contentType) {
        refresh();
        return super.shouldParsePage(contentType);
    }

    /** Load configuration from file. */
    private synchronized void loadConfig() {
        try {
            // Parse file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            InputStream is = null;

            if (configFile == null) {
                is = config.getServletContext().getResourceAsStream(configFileName);
            }
            else if (configFile.exists() && configFile.canRead()) {
                is = configFile.toURL().openStream();
            }

            if (is == null){ // load the default sitemesh configuration
                is = getClass().getClassLoader().getResourceAsStream("com/opensymphony/module/sitemesh/factory/sitemesh-default.xml");
            }

            if (is == null){ // load the default sitemesh configuration using another classloader
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/opensymphony/module/sitemesh/factory/sitemesh-default.xml");
            }

            if (is == null){
                throw new IllegalStateException("Cannot load default configuration from jar");
            }

            Document document = builder.parse(is);
            Element root = document.getDocumentElement();

            if (configFile != null) configLastModified = configFile.lastModified();

            // Verify root element
            if (!"sitemesh".equalsIgnoreCase(root.getTagName())) {
                report("Root element of sitemesh configuration file not <sitemesh>", null);
            }
            NodeList sections = root.getChildNodes();
            // Loop through child elements of root node
            for (int i = 0; i < sections.getLength(); i++) {
                if (sections.item(i) instanceof Element) {
                    Element curr = (Element)sections.item(i);
                    NodeList children = curr.getChildNodes();

                    if ("page-parsers".equalsIgnoreCase(curr.getTagName())) {
                        // handle <page-parsers>
                        loadPageParsers(children);
                    }
                    else if ("decorator-mappers".equalsIgnoreCase(curr.getTagName())) {
                        // handle <decorator-mappers>
                        loadDecoratorMappers(children);
                    }
                }
            }
        }
        catch (ParserConfigurationException e) {
            report("Could not get XML parser", e);
        }
        catch (IOException e) {
            report("Could not read config file : " + configFileName, e);
        }
        catch (SAXException e) {
            report("Could not parse config file : " + configFileName, e);
        }
    }

    /** Loop through children of 'page-parsers' element and add all 'parser' mappings. */
    private void loadPageParsers(NodeList nodes) {
        clearParserMappings();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element) {
                Element curr = (Element)nodes.item(i);

                if ("parser".equalsIgnoreCase(curr.getTagName())) {
                    String className = curr.getAttribute("class");
                    String contentType = curr.getAttribute("content-type");
                    mapParser(contentType, className);
                }
            }
        }
    }

    private void loadDecoratorMappers(NodeList nodes) {
        clearDecoratorMappers();
        Properties emptyProps = new Properties();

        pushDecoratorMapper("com.opensymphony.module.sitemesh.mapper.NullDecoratorMapper", emptyProps);

        // note, this works from the bottom node up.
        for (int i = nodes.getLength() - 1; i > 0; i--) {
            if (nodes.item(i) instanceof Element) {
                Element curr = (Element)nodes.item(i);
                if ("mapper".equalsIgnoreCase(curr.getTagName())) {
                    String className = curr.getAttribute("class");
                    Properties props = new Properties();
                    // build properties from <param> tags.
                    NodeList children = curr.getChildNodes();
                    for (int j = 0; j < children.getLength(); j++) {
                        if (children.item(j) instanceof Element) {
                            Element currC = (Element)children.item(j);
                            if ("param".equalsIgnoreCase(currC.getTagName())) {
                                props.put(currC.getAttribute("name"), currC.getAttribute("value"));
                            }
                        }
                    }
                    // add mapper
                    pushDecoratorMapper(className, props);
                }
            }
        }

        pushDecoratorMapper("com.opensymphony.module.sitemesh.mapper.InlineDecoratorMapper", emptyProps);
    }

    /** Check if configuration file has been modified, and if so reload it. */
    private void refresh() {
        if (configFile != null && configLastModified != configFile.lastModified()) loadConfig();
    }
}