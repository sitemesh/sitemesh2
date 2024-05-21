/*
 * Title:        ConfigLoader
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.factory.DefaultFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import jakarta.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The ConfigLoader reads a configuration XML file that contains Decorator definitions
 * (name, url, init-params) and path-mappings (pattern, name).
 *
 * <p>These can then be accessed by the getDecoratorByName() methods and getMappedName()
 * methods respectively.</p>
 *
 * <p>The DTD for the configuration file in old (deprecated) format is located at
 * <a href="http://www.opensymphony.com/dtds/sitemesh_1_0_decorators.dtd">
 *  http://www.opensymphony.com/dtds/sitemesh_1_0_decorators.dtd
 * </a>.</p>
 *
 * <p>The DTD for the configuration file in new format is located at
 * <a href="http://www.opensymphony.com/dtds/sitemesh_1_5_decorators.dtd">
 *  http://www.opensymphony.com/dtds/sitemesh_1_5_decorators.dtd
 * </a>.</p>
 *
 * <p>Editing the config file will cause it to be auto-reloaded.</p>
 *
 * <p>This class is used by ConfigDecoratorMapper, and uses PathMapper for pattern matching.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @author <a href="mailto:pathos@pandora.be">Mathias Bogaert</a>
 * @version $Revision: 1.8 $
 *
 * @see com.opensymphony.module.sitemesh.mapper.ConfigDecoratorMapper
 * @see com.opensymphony.module.sitemesh.mapper.PathMapper
 */
public class ConfigLoader {

    /**
     * State visibile across threads stored in a single container so that we
     * can efficiently atomically access it with the guarantee that we wont see
     * a partially loaded configuration in the face of one thread reloading the
     * configuration while others are trying to read it.
     */
    private static class State {
        /**
         * Timestamp of the last time we checked for an update to the
         * configuration file used to rate limit the frequency at which we check
         * for efficiency.
         */
        long lastModificationCheck = System.currentTimeMillis();

        /**
         * Timestamp of the modification time of the configuration file when we
         * generated the state.
         */
        long lastModified;

        /**
         * Whether a thread is currently checking if the configuration file has
         * been modified and potentially reloading it and therefore others
         * shouldn't attempt the same till it's done.
         */
        boolean checking = false;

        Map decorators = new HashMap();
        PathMapper pathMapper = new PathMapper();
    }

    /**
     * Mark volatile so that the installation of new versions is guaranteed to
     * be visible across threads.
     */
    private volatile State state;

    private File configFile = null;
    private String configFileName = null;

    private Config config = null;

    /**
     * Create new ConfigLoader using supplied File.
     */
    public ConfigLoader(File configFile) throws ServletException {
        this.configFile = configFile;
        this.configFileName = configFile.getName();
        state = loadConfig();
    }

    /**
     * Create new ConfigLoader using supplied filename and config.
     */
    public ConfigLoader(String configFileName, Config config) throws ServletException {
        this.config = config;
        this.configFileName = configFileName;
        if (config.getServletContext().getRealPath(configFileName) != null) {
            this.configFile = new File(config.getServletContext().getRealPath(configFileName));
        }
        state = loadConfig();
    }

    /**
     * Retrieve Decorator based on name specified in configuration file.
     */
    public Decorator getDecoratorByName(String name) throws ServletException {
        return (Decorator) refresh().decorators.get(name);
    }

    /** Get name of Decorator mapped to given path. */
    public String getMappedName(String path) throws ServletException {
        return refresh().pathMapper.get(path);
    }

    /**
     * Load configuration from file.
     */
    private State loadConfig() throws ServletException {
        // The new state which we build up and atomically replace the old state
        // with atomically to avoid other threads seeing partial modifications.
        State newState = new State();
        try {
            // Build a document from the file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document;
            if (configFile != null && configFile.canRead()) {
                // Keep time we read the file to check if the file was modified
                newState.lastModified = configFile.lastModified();
                document = builder.parse(configFile);
            } else {
                document = builder.parse(config.getServletContext().getResourceAsStream(configFileName));
            }

            // Parse the configuration document
            parseConfig(newState, document);

            return newState;
        }
        catch (ParserConfigurationException e) {
            throw new ServletException("Could not get XML parser", e);
        }
        catch (IOException e) {
            throw new ServletException("Could not read the config file: " + configFileName, e);
        }
        catch (SAXException e) {
            throw new ServletException("Could not parse the config file: " + configFileName, e);
        }
        catch (IllegalArgumentException e) {
            throw new ServletException("Could not find the config file: " + configFileName, e);
        }
    }

    private void parseConfig(State newState, Document document) {
        Element root = document.getDocumentElement();

        // get the default directory for the decorators
        String defaultDir = getAttribute(root, "defaultdir");
        if (defaultDir == null) defaultDir = getAttribute(root, "defaultDir");

        // Get decorators
        NodeList decoratorNodes = root.getElementsByTagName("decorator");
        Element decoratorElement;

        for (int i = 0; i < decoratorNodes.getLength(); i++) {
            String name, page, uriPath = null, role = null;

            // get the current decorator element
            decoratorElement = (Element) decoratorNodes.item(i);

            if (getAttribute(decoratorElement, "name") != null) {
                // The new format is used
                name = getAttribute(decoratorElement, "name");
                page = getAttribute(decoratorElement, "page");
                uriPath = getAttribute(decoratorElement, "webapp");
                role = getAttribute(decoratorElement, "role");

                // Append the defaultDir
                if (defaultDir != null && page != null && page.length() > 0 && !page.startsWith("/")) {
                    if (page.charAt(0) == '/') page = defaultDir + page;
                    else page = defaultDir + '/' + page;
                }

                // The uriPath must begin with a slash
                if (uriPath != null && uriPath.length() > 0) {
                    if (uriPath.charAt(0) != '/') uriPath = '/' + uriPath;
                }

                // Get all <pattern>...</pattern> and <url-pattern>...</url-pattern> nodes and add a mapping
                populatePathMapper(newState, decoratorElement.getElementsByTagName("pattern"), role, name);
                populatePathMapper(newState, decoratorElement.getElementsByTagName("url-pattern"), role, name);
            } else {
                // NOTE: Deprecated format
                name = getContainedText(decoratorNodes.item(i), "decorator-name");
                page = getContainedText(decoratorNodes.item(i), "resource");
                // We have this here because the use of jsp-file is deprecated, but we still want
                // it to work.
                if (page == null) page = getContainedText(decoratorNodes.item(i), "jsp-file");
            }

            Map params = new HashMap();

            NodeList paramNodes = decoratorElement.getElementsByTagName("init-param");
            for (int ii = 0; ii < paramNodes.getLength(); ii++) {
                String paramName = getContainedText(paramNodes.item(ii), "param-name");
                String paramValue = getContainedText(paramNodes.item(ii), "param-value");
                params.put(paramName, paramValue);
            }
            storeDecorator(newState, new DefaultDecorator(name, page, uriPath, role, params));
        }

        // Get (deprecated format) decorator-mappings
        NodeList mappingNodes = root.getElementsByTagName("decorator-mapping");
        for (int i = 0; i < mappingNodes.getLength(); i++) {
            Element n = (Element) mappingNodes.item(i);
            String name = getContainedText(mappingNodes.item(i), "decorator-name");

            // Get all <url-pattern>...</url-pattern> nodes and add a mapping
            populatePathMapper(newState, n.getElementsByTagName("url-pattern"), null, name);
        }
    }

    private void populatePathMapper(State newState, NodeList patternNodes, String role, String name) {
        for (int j = 0; j < patternNodes.getLength(); j++) {
            Element p = (Element) patternNodes.item(j);
            Text patternText = (Text) p.getFirstChild();
            if (patternText != null) {
                String pattern = patternText.getData().trim();
                if (pattern != null) {
                    if (role != null) {
                        // concatenate name and role to allow more
                        // than one decorator per role
                        newState.pathMapper.put(name + role, pattern);
                    } else {
                        newState.pathMapper.put(name, pattern);
                    }
                }
            }
        }
    }

    private static String getAttribute(Element element, String name) {
        if (element != null && element.getAttribute(name) != null && element.getAttribute(name).trim() != "") {
            return element.getAttribute(name).trim();
        } else {
            return null;
        }
    }

    private static String getContainedText(Node parent, String childTagName) {
        try {
            Node tag = ((Element) parent).getElementsByTagName(childTagName).item(0);
            return ((Text) tag.getFirstChild()).getData();
        }
        catch (Exception e) {
            return null;
        }
    }

    private void storeDecorator(State newState, Decorator d) {
        if (d.getRole() != null) {
            newState.decorators.put(d.getName() + d.getRole(), d);
        } else {
            newState.decorators.put(d.getName(), d);
        }
    }

    /**
     * Check if configuration file has been updated, and if so, reload.
     */
    private State refresh() throws ServletException {
        // Read the current state just once since another thread can swap
        // another version in at any time.
        State currentState = state;
        if (configFile == null) {
            return currentState;
        }

        // Rate limit the stat'ing of the config file to find its
        // modification time to once every five seconds to reduce the
        // number of system calls made. We grab the monitor of currentState
        // so that we can safely read the values shared across threads and
        // so that only one thread is performing the modification check at
        // a time.
        long current = System.currentTimeMillis();
        long oldLastModified;

        boolean check = false;
        synchronized (currentState) {
            oldLastModified = currentState.lastModified;
            if (!currentState.checking && current >= currentState.lastModificationCheck + DefaultFactory.configCheckMillis) {
                currentState.lastModificationCheck = current;
                currentState.checking = true;
                check = true;
            }
        }

        if (check) {
            // Perform the file stat'ing system call without holding a lock
            // on the current state.
            State newState = null;
            try {
                long currentLastModified = configFile.lastModified();
                if (currentLastModified != oldLastModified) {
                    // The configuration file has been modified since we last
                    // read it so reload the configuration without holding a
                    // lock on the current state and then slam down the new
                    // state for other threads to see. The State.checking flag
                    // being set on currentState will prevent other threads
                    // from attempting to reload the state while we are and
                    // the new state will have the flag cleared so that we can
                    // continue checking if the configuration file is modified.
                    newState = loadConfig();
                    state = newState;
                    return newState;
                }
            } finally {
                // In the event of a failure of the modification time check,
                // or while reloading the configuration file, mark that we're no
                // longer checking if the modification time or reloading so that
                // we'll retry.
                if (newState == null) {
                    synchronized (currentState) {
                        currentState.checking = false;
                    }
                }
            }

        }
        return currentState;
    }
}