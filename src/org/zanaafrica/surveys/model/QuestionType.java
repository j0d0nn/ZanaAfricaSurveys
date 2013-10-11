package org.zanaafrica.surveys.model;

public enum QuestionType
{
    MultipleChoice(0b100), NumericRange(0b010), FreeText(0b001), MCAndText(0b101), NumAndText(0b011);
    
    ///////////////////////////////////////////////////////////////

    private int components;
    
    private QuestionType (int components) { this.components = components; }
    
    ///////////////////////////////////////////////////////////////

    public boolean hasMultipleChoice () { return (this.components & 0b100) > 0; }
    public boolean hasNumberValue () { return (this.components & 0b010) > 0; }
    public boolean hasFreeText () { return (this.components & 0b001) > 0; }
    public boolean isFreeTextOnly () { return FreeText.equals(this); }    
}
