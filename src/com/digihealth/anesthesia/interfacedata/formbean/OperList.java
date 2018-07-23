package com.digihealth.anesthesia.interfacedata.formbean;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

public class OperList
{
    private List<HisOperationFormBean> item;

    public List<HisOperationFormBean> getItem()
    {
        return item;
    }

    public void setItem(List<HisOperationFormBean> item)
    {
        this.item = item;
    }
}
