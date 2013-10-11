package org.zanaafrica.surveys.model;

public enum Language 
{ 
    English(0), Kiswahili(1), Amharic(2);
    
    ///////////////////////////////////////////////////////////////

    private int index;
    private Language (int index) { this.index = index; }
    
    ///////////////////////////////////////////////////////////////
    
    public int getIndex () { return index; }
}
