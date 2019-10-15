/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2014 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package it.geosolutions.geoserver.rest.decoder;

import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

/**
 * Parse DataStores returned as XML REST objects.
 * <P>
 * This is the XML document returned by GeoServer when requesting a DataStore:
 * <PRE>
 * {@code
 *<dataStore>
 *    <name>sf</name>
 *    <enabled>true</enabled>
 *    <workspace>
 *        <name>sf</name>
 *        <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate"
 *            href="http://localhost:8080/geoserver/rest/workspaces/sf.xml"
 *            type="application/xml"/>
 *    </workspace>
 *    <connectionParameters>
 *        <entry key="namespace">http://www.openplans.org/spearfish</entry>
 *        <entry key="url">file:data/sf</entry>
 *    </connectionParameters>
 *    <featureTypes>
 *        <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate"
 *            href="http://localhost:8080/geoserver/rest/workspaces/sf/datastores/sf/featuretypes.xml"
 *            type="application/xml"/>
 *    </featureTypes>
 *</dataStore>
 * }
 * </PRE>
 * Note: the whole XML fragment is stored in memory. At the moment, there are
 * methods to retrieve only the more useful data.
 *
 * @author etj
 * @version $Id: $
 */
public class RESTDataStore {

    private final Element dsElem;

    public enum DBType {

        POSTGIS("postgis"),
        ORACLE("oracle"),
        SHP("shp"),
        UNKNOWN(null);
        private final String restName;

        private DBType(String restName) {
            this.restName = restName;
        }

        public static DBType get(String restName) {
            for (DBType type : values()) {
                if (type == UNKNOWN) {
                    continue;
                }
                if (type.restName.equals(restName)) {
                    return type;
                }
            }
            return UNKNOWN;
        }
    };

    /**
     * <p>build</p>
     *
     * @param xml a {@link java.lang.String} object.
     * @return a {@link it.geosolutions.geoserver.rest.decoder.RESTDataStore} object.
     */
    public static RESTDataStore build(String xml) {
        if (xml == null) {
            return null;
        }

        Element e = JDOMBuilder.buildElement(xml);
        if (e != null) {
            return new RESTDataStore(e);
        } else {
            return null;
        }
    }

    /**
     * <p>Constructor for RESTDataStore.</p>
     *
     * @param dsElem a {@link org.jdom.Element} object.
     */
    protected RESTDataStore(Element dsElem) {
        this.dsElem = dsElem;
    }

    /**
     * <p>getName</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return dsElem.getChildText("name");
    }
    
    /**
     * <p>getStoreType</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStoreType() {
    	return dsElem.getChildText("type");
    }
    
    /**
     * <p>getDescription</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return dsElem.getChildText("description");
    }

    /**
     * <p>isEnabled</p>
     *
     * @return a boolean.
     */
    public boolean isEnabled() {
    	return Boolean.parseBoolean(dsElem.getChildText("enabled"));
    }
    
    /**
     * <p>getWorkspaceName</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getWorkspaceName() {
        return dsElem.getChild("workspace").getChildText("name");
    }
    
    /**
     * <p>getConnectionParameters</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getConnectionParameters() {
        Element elConnparm = dsElem.getChild("connectionParameters");
        if (elConnparm != null) {
        	@SuppressWarnings("unchecked")
			List<Element> elements = (List<Element>)elConnparm.getChildren("entry");
        	Map<String, String> params = new HashMap<String, String>(elements.size());
            for (Element element : elements) {
                String key = element.getAttributeValue("key");
                String value = element.getTextTrim();
                params.put(key, value);
            }
            return params;
        }
        return null;
    }

	/**
	 * <p>getConnectionParameter</p>
	 *
	 * @param paramName a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	@SuppressWarnings("unchecked")
	protected String getConnectionParameter(String paramName) {
        Element elConnparm = dsElem.getChild("connectionParameters");
        if (elConnparm != null) {
        	for (Element entry : (List<Element>) elConnparm.getChildren("entry")) {
                String key = entry.getAttributeValue("key");
                if (paramName.equals(key)) {
                    return entry.getTextTrim();
                }
            }
        }

        return null;
    }
	
    /**
     * <p>getType</p>
     *
     * @return a {@link it.geosolutions.geoserver.rest.decoder.RESTDataStore.DBType} object.
     */
    public DBType getType() {
        return DBType.get(getConnectionParameter("dbtype"));
    }
}
