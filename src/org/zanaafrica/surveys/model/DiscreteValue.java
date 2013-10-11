package org.zanaafrica.surveys.model;

import java.io.Serializable;

public class DiscreteValue implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private Integer value;
    private Text label;

    ///////////////////////////////////////////////////////////////    

    protected DiscreteValue () { }        
    public DiscreteValue (Integer value, Text label) {
        this.value = value;
        this.label = label;
    }

    ///////////////////////////////////////////////////////////////    

    public Integer getValue () { return value; }
    public Text getLabel () { return label; }
}
