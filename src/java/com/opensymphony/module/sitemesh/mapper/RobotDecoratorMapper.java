/*
 * Title:        RobotDecoratorMapper
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.RequestConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Properties;

/**
 * The RobotDecoratorMapper will use the specified decorator when the requester
 * is identified as a robot (also known as spider, crawler, ferret) of a search engine.
 *
 * <p>The name of this decorator should be supplied in the <code>decorator</code>
 * property.</p>
 *
 * @author <a href="mailto:pathos@pandora.be">Mathias Bogaert</a>
 * @version $Revision: 1.3 $
 *
 * @see com.opensymphony.module.sitemesh.DecoratorMapper
 */
public class RobotDecoratorMapper extends AbstractDecoratorMapper {
    private String decoratorName = null;

    /** All known robot hosts (list can be found <a href="http://www.spiderhunter.com">here</a>). */
    private static final String[] botHosts = {"alltheweb.com", "alta-vista.net", "altavista.com",
                                              "atext.com", "euroseek.net", "excite.com",
                                              "fast-search.net", "google.com", "googlebot.com",
                                              "infoseek.co.jp", "infoseek.com", "inktomi.com",
                                              "inktomisearch.com", "linuxtoday.com.au", "lycos.com",
                                              "lycos.com", "northernlight.com", "pa-x.dec.com"};

    /**
     * All known robot user-agent headers (list can be found
     * <a href="http://www.robotstxt.org/wc/active.html">here</a>).
     *
     * <p>NOTE: To avoid bad detection:</p>
     *
     * <ul>
     *  <li>Robots with ID of 2 letters only were removed</li>
     *  <li>Robot called "webs" were removed</li>
     *  <li>directhit was changed in direct_hit (its real id)</li>
     * </ul>
     */
    private static final String[] botAgents = {
        "acme.spider", "ahoythehomepagefinder", "alkaline", "appie", "arachnophilia",
        "architext", "aretha", "ariadne", "aspider", "atn.txt", "atomz", "auresys",
        "backrub", "bigbrother", "bjaaland", "blackwidow", "blindekuh", "bloodhound",
        "brightnet", "bspider", "cactvschemistryspider", "calif", "cassandra",
        "cgireader", "checkbot", "churl", "cmc", "collective", "combine", "conceptbot",
        "core", "cshkust", "cusco", "cyberspyder", "deweb", "dienstspider", "diibot",
        "direct_hit", "dnabot", "download_express", "dragonbot", "dwcp", "ebiness",
        "eit", "emacs", "emcspider", "esther", "evliyacelebi", "fdse", "felix",
        "ferret", "fetchrover", "fido", "finnish", "fireball", "fish", "fouineur",
        "francoroute", "freecrawl", "funnelweb", "gazz", "gcreep", "getbot", "geturl",
        "golem", "googlebot", "grapnel", "griffon", "gromit", "gulliver", "hambot",
        "harvest", "havindex", "hometown", "wired-digital", "htdig", "htmlgobble",
        "hyperdecontextualizer", "ibm", "iconoclast", "ilse", "imagelock", "incywincy",
        "informant", "infoseek", "infoseeksidewinder", "infospider", "inspectorwww",
        "intelliagent", "iron33", "israelisearch", "javabee", "jcrawler", "jeeves",
        "jobot", "joebot", "jubii", "jumpstation", "katipo", "kdd", "kilroy",
        "ko_yappo_robot", "labelgrabber.txt", "larbin", "legs", "linkscan",
        "linkwalker", "lockon", "logo_gif", "lycos", "macworm", "magpie", "mediafox",
        "merzscope", "meshexplorer", "mindcrawler", "moget", "momspider", "monster",
        "motor", "muscatferret", "mwdsearch", "myweb", "netcarta", "netmechanic",
        "netscoop", "newscan-online", "nhse", "nomad", "northstar", "nzexplorer",
        "occam", "octopus", "orb_search", "packrat", "pageboy", "parasite", "patric",
        "perignator", "perlcrawler", "phantom", "piltdownman", "pioneer", "pitkow",
        "pjspider", "pka", "plumtreewebaccessor", "poppi", "portalb", "puu", "python",
        "raven", "rbse", "resumerobot", "rhcs", "roadrunner", "robbie", "robi",
        "roverbot", "safetynetrobot", "scooter", "search_au", "searchprocess",
        "senrigan", "sgscout", "shaggy", "shaihulud", "sift", "simbot", "site-valet",
        "sitegrabber", "sitetech", "slurp", "smartspider", "snooper", "solbot",
        "spanner", "speedy", "spider_monkey", "spiderbot", "spiderman", "spry",
        "ssearcher", "suke", "sven", "tach_bw", "tarantula", "tarspider", "tcl",
        "techbot", "templeton", "titin", "titan", "tkwww", "tlspider", "ucsd",
        "udmsearch", "urlck", "valkyrie", "victoria", "visionsearch", "voyager",
        "vwbot", "w3index", "w3m2", "wanderer", "webbandit", "webcatcher", "webcopy",
        "webfetcher", "webfoot", "weblayers", "weblinker", "webmirror", "webmoose",
        "webquest", "webreader", "webreaper", "websnarf", "webspider", "webvac",
        "webwalk", "webwalker", "webwatch", "wget", "whowhere", "wmir", "wolp",
        "wombat", "worm", "wwwc", "wz101", "xget", "nederland.zoek"
    };

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        decoratorName = properties.getProperty("decorator");
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        Decorator result = null;

        if (decoratorName != null && isBot(request)) {
            result = getNamedDecorator(request, decoratorName);
        }

        return result == null ? super.getDecorator(request, page) : result;
    }

    /** Check if the current request came from  a robot (also known as spider, crawler, ferret) */
    private static boolean isBot(HttpServletRequest request) {
        if (request == null) return false;

        // force creation of a session
        HttpSession session = request.getSession(true);

        if (Boolean.FALSE.equals(session.getAttribute(RequestConstants.ROBOT))) {
            return false;
        }
        else if (Boolean.TRUE.equals(session.getAttribute(RequestConstants.ROBOT))) {
            // a key was found in the session indicating it is a robot
            return true;
        }
        else {
            if ("robots.txt".indexOf(request.getRequestURI()) != -1) {
                // there is a specific request for the robots.txt file, so we assume
                // it must be a robot (only robots request robots.txt)

                // set a key in the session, so the next time we don't have to manually
                // detect the robot again
                session.setAttribute(RequestConstants.ROBOT, Boolean.TRUE);
                return true;
            }
            else {
                String userAgent = request.getHeader("User-Agent");

                if (userAgent != null && userAgent.trim().length() > 2) {
                    // first check for common user-agent headers, so that we can speed
                    // this thing up, hopefully clever spiders will not send a fake header
                    if (userAgent.indexOf("MSIE")      != -1 || userAgent.indexOf("Gecko")   != -1    // MSIE and Mozilla
                     || userAgent.indexOf("Opera")     != -1 || userAgent.indexOf("iCab")    != -1    // Opera and iCab (mac browser)
                     || userAgent.indexOf("Konqueror") != -1 || userAgent.indexOf("KMeleon") != -1    // Konqueror and KMeleon
                     || userAgent.indexOf("4.7")       != -1 || userAgent.indexOf("Lynx")    != -1) { // NS 4.78 and Lynx
                        // indicate this session is not a robot
                        session.setAttribute(RequestConstants.ROBOT, Boolean.FALSE);
                        return false;
                    }

                    for (int i = 0; i < botAgents.length; i++) {
                        if (userAgent.indexOf(botAgents[i]) != -1) {
                            // set a key in the session, so the next time we don't have to manually
                            // detect the robot again
                            session.setAttribute(RequestConstants.ROBOT, Boolean.TRUE);
                            return true;
                        }
                    }
                }

                // detect the robot from the host or user-agent
                String remoteHost = request.getRemoteHost(); // requires one DNS lookup

                // if the DNS server didn't return a hostname, getRemoteHost returns the
                // IP address, which is ignored here (the last char is checked, because some
                // remote hosts begin with the IP)
                if (remoteHost != null && remoteHost.length() > 0 && remoteHost.charAt(remoteHost.length() - 1) > 64) {
                    for (int i = 0; i < botHosts.length; i++) {
                        if (remoteHost.indexOf(botHosts[i]) != -1) {
                            // set a key in the session, so the next time we don't have to manually
                            // detect the robot again
                            session.setAttribute(RequestConstants.ROBOT, Boolean.TRUE);
                            return true;
                        }
                    }
                }

                // remote host and user agent are not in the predefined list,
                // so it must be an unknown robot or not a robot

                // indicate this session is not a robot
                session.setAttribute(RequestConstants.ROBOT, Boolean.FALSE);
                return false;
            }
        }
    }
}