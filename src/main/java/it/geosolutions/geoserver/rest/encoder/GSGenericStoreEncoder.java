/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *
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

package it.geosolutions.geoserver.rest.encoder;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher.StoreType;
import it.geosolutions.geoserver.rest.encoder.utils.NestedElementEncoder;

public class GSGenericStoreEncoder extends GSAbstractStoreEncoder {

    private String type;

    public GSGenericStoreEncoder(StoreType storeType, String workspace, String type, String storeName, 
            String url, Boolean enabled) {
        super(storeType, storeName);
        this.type = type;
        if (workspace != null) {
            set("workspace", workspace);
        }
        if (storeName != null) {
            set("name", storeName);
        }
        if (enabled != null) {
            set("enabled", enabled.toString());
        }
        if (type != null) {
            set("type", type);
        }
        if (url != null) {
            if (storeType == StoreType.COVERAGESTORES) {
                set("url", url);
            } else {
                NestedElementEncoder connectionParameters = new NestedElementEncoder("connectionParameters");
                connectionParameters.set("url", url.toString());
                addContent(connectionParameters.getRoot());
            }
        }
    }
    
    @Override
    protected String getValidType() {
        return type;
    }
}