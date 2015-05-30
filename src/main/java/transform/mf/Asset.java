package transform.mf;

import java.util.List;

import arc.mf.plugin.PluginService;
import arc.xml.XmlDoc;

/**
 * The interface for Mediaflux asset.
 * 
 * @author wilson
 * 
 */
public interface Asset {

    public static final int VERSION_LATEST = 0;

    /**
     * The asset identifier.
     * 
     * @return
     */
    String assetId();

    /**
     * The asset version.
     * 
     * @return
     */
    int version();

    /**
     * The asset namespace.
     * 
     * @return
     */
    String namespace();

    /**
     * The input content to the asset.
     * 
     * @return
     */
    PluginService.Input contentInput();

    /**
     * The output content from the asset.
     * 
     * @return
     * @throws Throwable
     */
    PluginService.Output contentOutput() throws Throwable;

    /**
     * The XML element that represents the meta data of the asset content.
     * 
     * @return
     */
    XmlDoc.Element contentElement();

    /**
     * Adds relationship from the given asset.
     * 
     * @param relationship
     *            the relationship type.
     * @param from
     *            the asset id.
     * @throws Throwable
     */
    void addRelationshipFrom(String relationship, String from) throws Throwable;

    /**
     * Removes relationship from the given asset.
     * 
     * @param relationship
     *            the relationship type.
     * @param from
     *            the asset id.
     * @throws Throwable
     */
    void removeRelationshipFrom(String relationship, String from) throws Throwable;

    /**
     * Adds relationship to the given asset.
     * 
     * @param relationship
     *            the relationship type.
     * @param to
     *            the asset id.
     * @throws Throwable
     */
    void addRelationshipTo(String relationship, String to) throws Throwable;

    /**
     * Removes relationship to the given asset.
     * 
     * @param relationship
     *            the relationship type.
     * @param to
     *            the asset id.
     * @throws Throwable
     */
    void removeRelationshipTo(String relationship, String to) throws Throwable;

    /**
     * Gets the access controls apply on the asset.
     * 
     * @return
     */
    List<ACL> acls();

    /**
     * Sets the access controls for the asset.
     * 
     * @param acls
     */
    void setAcls(List<ACL> acls);

    /**
     * The user who creates the asset.
     * 
     * @return
     */
    User creator();

}
