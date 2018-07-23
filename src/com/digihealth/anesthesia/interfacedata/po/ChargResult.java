package com.digihealth.anesthesia.interfacedata.po;

import javax.xml.bind.annotation.XmlElement;

public class ChargResult
{
    private String resultCode;
    private String resultMessage;
    private String itemCode;
    private String itemName;
    private String recipeNo;
    private String itemId;
    
    @XmlElement(name = "pk_interfaceID")
    public String getItemId()
    {
        return itemId;
    }
    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }
    public String getResultCode()
    {
        return resultCode;
    }
    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }
    public String getResultMessage()
    {
        return resultMessage;
    }
    public void setResultMessage(String resultMessage)
    {
        this.resultMessage = resultMessage;
    }
    public String getItemCode()
    {
        return itemCode;
    }
    public void setItemCode(String itemCode)
    {
        this.itemCode = itemCode;
    }
    public String getItemName()
    {
        return itemName;
    }
    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }
    @XmlElement(name = "RecipeNo")
    public String getRecipeNo()
    {
        return recipeNo;
    }
    public void setRecipeNo(String recipeNo)
    {
        this.recipeNo = recipeNo;
    }
}
