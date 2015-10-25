
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
 *         &lt;element name="hierarchyLevel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="infoString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "hierarchyLevel",
    "infoString",
    "itemType"
})
public class HierarchicalEventItemDTO {

    protected int hierarchyLevel;
    protected String infoString;
    protected String itemType;

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
