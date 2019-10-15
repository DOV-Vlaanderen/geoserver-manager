/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2012 GeoSolutions S.A.S.
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
package it.geosolutions.geoserver.rest.manager;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher.Format;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSAbstractDatastoreEncoder;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Manage stores.
 *
 * To pass connection parameters, use the encoders derived from
 * {@link GSAbstractDatastoreEncoder}.
 *
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * @version $Id: $
 */
public class GeoServerRESTStoreManager extends GeoServerRESTAbstractManager {

    /**
     * Default constructor.
     *
     * @param restURL GeoServer REST API endpoint
     * @param username GeoServer REST API authorized username
     * @param password GeoServer REST API password for the former username
     * @throws java.lang.IllegalArgumentException if any.
     */
    public GeoServerRESTStoreManager(URL restURL, String username, String password)
        throws IllegalArgumentException {
        super(restURL, username, password);
    }

    /**
     * Create a store.
     *
     * @param workspace Name of the workspace to contain the store. This
     *            will also be the prefix of any layer names contained in the
     *            store.
     * @param store the set of parameters to be set to the store (including connection parameters).
     * @return true if the store has been successfully created,
     *         false otherwise
     */
    public boolean create(String workspace, GSAbstractStoreEncoder store) {
        String sUrl = HTTPUtils.append(gsBaseUrl, "/rest/workspaces/", HTTPUtils.enc(workspace), "/", 
                store.getStoreType().toString(),".",Format.XML.toString()).toString();
        String xml = store.toString();
        String result = HTTPUtils.postXml(sUrl, xml, gsuser, gspass);
        return result != null;
    }
    
    /**
     * Update a store.
     *
     * @param workspace Name of the workspace that contains the store.
     * @param store the set of parameters to be set to the store (including connection parameters).
     * @return true if the store has been successfully updated,
     *         false otherwise
     */
    public boolean update(String workspace, GSAbstractStoreEncoder store) {
        return update(workspace, store.getName(), store);
    }
    

    /**
     * Update a store.
     *
     * @param workspace Name of the workspace that contains the store.
     * @param store the set of parameters to be set to the store (including connection parameters).
     * @return true if the store has been successfully updated,
     *         false otherwise
     * @param storeName a {@link java.lang.String} object.
     */
    public boolean update(String workspace, String storeName, GSAbstractStoreEncoder store) {
        String sUrl = HTTPUtils.append(gsBaseUrl, "/rest/workspaces/", HTTPUtils.enc(workspace),"/", 
                store.getStoreType().toString(),"/",
                HTTPUtils.enc(storeName),".",Format.XML.toString()).toString();
        String xml = store.toString();
        String result = HTTPUtils.putXml(sUrl, xml, gsuser, gspass);
        return result != null;
    }
    
    /**
     * Remove a given CoverageStore in a given Workspace.
     *
     * @param workspace The name of the workspace
     * @param store the set of parameters of the store
     * @param recurse if remove should be performed recursively
     * @return true if the CoverageStore was successfully removed.
     * @throws java.lang.IllegalArgumentException if any.
     */
    public boolean remove(final String workspace, final GSAbstractStoreEncoder store,
            final boolean recurse) throws IllegalArgumentException {
//            if (workspace == null || storename == null)
//                throw new IllegalArgumentException("Arguments may not be null!");
//            if (workspace.isEmpty() || storename.isEmpty())
//                throw new IllegalArgumentException("Arguments may not be empty!");

            final StringBuilder url=HTTPUtils.append(gsBaseUrl,"/rest/workspaces/",HTTPUtils.enc(workspace),"/", 
                    store.getStoreType().toString(), "/",HTTPUtils.enc(store.getName()));
            if (recurse)
                url.append("?recurse=true");
            URL deleteStore;
            try {
                deleteStore = new URL(url.toString());
            } catch (MalformedURLException e) {
                throw new IllegalStateException(e);
            }

            boolean deleted = HTTPUtils.delete(deleteStore.toExternalForm(), gsuser, gspass);
//            if (!deleted) {
//                LOGGER.warn("Could not delete CoverageStore " + workspace + ":" + storename);
//            } else {
//                LOGGER.info("CoverageStore successfully deleted " + workspace + ":" + storename);
//            }
            return deleted;
    }
    
    
}
