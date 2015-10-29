
package de.trispeedys.resourceplanning.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for hierarchicalEventItemDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="hierarchicalEventItemDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assignmentString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="entityId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="hierarchyLevel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="infoString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="itemKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="itemType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hierarchicalEventItemDTO", propOrder = {
    "assignmentString",
    "entityId",
    "hierarchyLevel",
    "infoString",
    "itemKey",
    "itemType"
})
public class HierarchicalEventItemDTO {

    protected String assignmentString;
    protected Long entityId;
    protected int hierarchyLevel;
    protected String infoString;
    protected String itemKey;
    protected String itemType;

    /**
     * Gets the value of the assignmentString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssignmentString() {
        return assignmentString;
    }

    /**
     * Sets the value of the assignmentString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssignmentString(String value) {
        this.assignmentString = value;
    }

    /**
     * Gets the value of the entityId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * Sets the value of the entityId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEntityId(Long value) {
        this.entityId = value;
    }

    /**
     * Gets the value of the hierarchyLevel property.
     * 
     */
    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    /**
     * Sets the value of the hierarchyLevel property.
     * 
     */
    public void setHierarchyLevel(int value) {
        this.hierarchyLevel = value;
    }

    /**
     * Gets the value of the infoString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfoString() {
        return infoString;
    }

    /**
     * Sets the value of the infoString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfoString(String value) {
        this.infoString = value;
    }

    /**
     * Gets the value of the itemKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemKey() {
        return itemKey;
    }

    /**
     * Sets the value of the itemKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemKey(String value) {
        this.itemKey = value;
    }

    /**
     * Gets the value of the itemType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * Sets the value of the itemType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemType(String value) {
        this.itemType = value;
    }

}
