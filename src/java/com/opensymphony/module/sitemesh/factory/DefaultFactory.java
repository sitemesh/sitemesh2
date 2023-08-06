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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * DefaultFactory, reads configuration from the <code>sitemesh.configfile</code> init param,
 * or <code>/WEB-INF/sitemesh.xml</code> if not specified, or uses the
 * default configuration if <code>sitemesh.xml</code> does not exist.
 *
 * <p>To use the <code>sitemesh.configfile</code> parameter, add the following to your web.xml:
 * <pre>
 * &lt;context-param&gt;
 *      &lt;param-name&gt;sitemesh.configfile&lt;/param-name&gt;
 *      &lt;param-value&gt;/WEB-INF/etc/sitemesh.xml&lt;/param-value&gt;
 *  &lt;/context-param&gt;
 * </pre>
 * </p>
 * 
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @author <a href="mailto:pathos@pandora.be">Mathias Bogaert</a>
 * @version $Revision: 1.8 $
 */
public class DefaultFactory extends BaseFactory {

    private static final String DEFAULT_CONFIG_FILENAME = "/WEB-INF/sitemesh.xml";
    String configFileName;
    File configFile;
    long configLastModified;
    private long configLastCheck = 0L;
    public static long configCheckMillis = 3000L;
    Map configProps = new HashMap();

    String excludesFileName;
    File excludesFile;

    public DefaultFactory(Config config) {
        super(config);

        configFileName = config.getServletContext().getInitParameter("sitemesh.configfile");
        if (configFileName == null) {
            configFileName = DEFAULT_CONFIG_FILENAME;
        }

        // configFilePath is null if loaded from war file
        String initParamConfigFile = config.getConfigFile();
        if (initParamConfigFile != null) {
            configFileName = initParamConfigFile;
        }

        configFile = loadFile(configFileName);
        loadConfig();
    }

    private File loadFile(String fileName) {
        if (fileName.startsWith("classpath:")) {
            return null;
        }
        String filePath = config.getServletContext().getRealPath(fileName);
        return filePath != null? new File(filePath) : null;
    }

    private InputStream loadStream(File file, String fileName) throws IOException {
        if (file != null && file.exists() && file.canRead()) {
            return file.toURI().toURL().openStream();
        }
        if (fileName.startsWith("classpath:")) {
            return loadClasspathStream(fileName.substring("classpath:".length()));
        }
        return config.getServletContext().getResourceAsStream(fileName);
    }

    private InputStream loadClasspathStream(String classPathResource) {
        // load the configuration
        InputStream is = getClass().getClassLoader().getResourceAsStream(classPathResource);
        if (is == null){ // load the configuration using another classloader
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(classPathResource);
        }
        return is;
    }

    private Element loadRootElement(InputStream is) throws ParserConfigurationException, IOException, SAXException {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        return document.getDocumentElement();
    }

    /** Load configuration from file. */
    private synchronized void loadConfig() {
        try {
            // Load and parse the sitemesh.xml file
            Element root = loadSitemeshXML();

            NodeList sections = root.getChildNodes();
            // Loop through child elements of root node
            for (int i = 0; i < sections.getLength(); i++) {
                if (sections.item(i) instanceof Element) {
                    Element curr = (Element)sections.item(i);
                    NodeList children = curr.getChildNodes();

                    if ("config-refresh".equalsIgnoreCase(curr.getTagName())) {
                        String seconds = curr.getAttribute("seconds");
                        configCheckMillis = Long.parseLong(seconds) * 1000L;
                    } else if ("property".equalsIgnoreCase(curr.getTagName())) {
                        String name = curr.getAttribute("name");
                        String value = curr.getAttribute("value");
                        if (!"".equals(name) && !"".equals(value)) {
                            configProps.put("${" + name + "}", value);
                        }
                    } else if ("page-parsers".equalsIgnoreCase(curr.getTagName())) {
                        // handle <page-parsers>
                        loadPageParsers(children);
                    } else if ("decorator-mappers".equalsIgnoreCase(curr.getTagName())) {
                        // handle <decorator-mappers>
                        loadDecoratorMappers(children);
                    } else if ("excludes".equalsIgnoreCase(curr.getTagName())) {
                        // handle <excludes>
                        String fileName = replaceProperties(curr.getAttribute("file"));
                        if (!"".equals(fileName)) {
                            excludesFileName = fileName;
                            loadExcludes();
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            throw new FactoryException("Could not get XML parser", e);
        } catch (IOException e) {
            throw new FactoryException("Could not read config file : " + configFileName, e);
        } catch (SAXException e) {
            throw new FactoryException("Could not parse config file : " + configFileName, e);
        }
    }

    private Element loadSitemeshXML()
            throws ParserConfigurationException, IOException, SAXException {
        InputStream is = loadStream(configFile, configFileName);
        if (is == null) {
            is = loadClasspathStream("com/opensymphony/module/sitemesh/factory/sitemesh-default.xml");
            if (is == null) {
                throw new IllegalStateException("Cannot load default configuration from jar");
            }
        }

        if (configFile != null) configLastModified = configFile.lastModified();

        Element root = loadRootElement(is);
        // Verify root element
        if (!"sitemesh".equalsIgnoreCase(root.getTagName())) {
            throw new FactoryException("Root element of sitemesh configuration file not <sitemesh>", null);
        }
        return root;
    }

    private void loadExcludes()
            throws ParserConfigurationException, IOException, SAXException {
        excludesFile = loadFile(excludesFileName);
        InputStream is = loadStream(excludesFile, excludesFileName);
        if (is == null) {
            throw new IllegalStateException("Cannot load excludes configuration file \"" + excludesFileName + "\" as specified in \"sitemesh.xml\" or \"sitemesh-default.xml\"");
        }

        Element root = loadRootElement(is);
        NodeList sections = root.getChildNodes();

        // Loop through child elements of root node looking for the <excludes> block
        for (int i = 0; i < sections.getLength(); i++) {
            if (sections.item(i) instanceof Element) {
                Element curr = (Element)sections.item(i);
                if ("excludes".equalsIgnoreCase(curr.getTagName())) {
                    loadExcludeUrls(curr.getChildNodes());
                }
            }
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
                                String value = currC.getAttribute("value");
                                props.put(currC.getAttribute("name"), replaceProperties(value));
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

    /**
     * Reads in all the url patterns to exclude from decoration.
     */
    private void loadExcludeUrls(NodeList nodes) {
        clearExcludeUrls();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element) {
                Element p = (Element) nodes.item(i);
                if ("pattern".equalsIgnoreCase(p.getTagName()) || "url-pattern".equalsIgnoreCase(p.getTagName())) {
                    Text patternText = (Text) p.getFirstChild();
                    if (patternText != null) {
                        String pattern = patternText.getData().trim();
                        if (pattern != null) {
                            addExcludeUrl(pattern);
                        }
                    }
                }
            }
        }
    }

    /** Check if configuration file has been modified, and if so reload it. */
    public void refresh() {
        long time = System.currentTimeMillis();
        if (time - configLastCheck < configCheckMillis) {
            return;
        }
        configLastCheck = time;

        if (configFile != null && configLastModified != configFile.lastModified()) {
            loadConfig();
        }
    }

    /**
     * Replaces any properties that appear in the supplied string
     * with their actual values
     *
     * @param str the string to replace the properties in
     * @return the same string but with any properties expanded out to their
     * actual values
     */
    private String replaceProperties(String str) {
        Set props = configProps.entrySet();
        for (Iterator it = props.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            int idx;
            while ((idx = str.indexOf(key)) >= 0) {
                StringBuilder buf = new StringBuilder(100);
                buf.append(str.substring(0, idx));
                buf.append(entry.getValue());
                buf.append(str.substring(idx + key.length()));
                str = buf.toString();
            }
        }
        return str;
    }
}
