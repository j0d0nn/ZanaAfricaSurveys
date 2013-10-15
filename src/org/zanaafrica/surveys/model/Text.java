package org.zanaafrica.surveys.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.googlecode.objectify.annotation.Embed;

@Embed
public class Text implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public List<String> translations = new ArrayList<String>();
    
    ///////////////////////////////////////////////////////////////
    
    protected Text () { }    
    public Text (List<String> translations) { this.translations = translations; }
    public Text (String englishText) { this.translations.add(Language.English.getIndex(), englishText); }

    ///////////////////////////////////////////////////////////////
    
    public String getTranslation (Language language) { 
        return this.translations.get(language.getIndex()); 
    }
    
    public void setTranslation (Language language, String translation) {
        this.translations.add(language.getIndex(), translation);
    }
    
    public String getEnglish ()
    {
        return this.getTranslation(Language.English);
    }
    
    public String getKiswahili ()
    {
        return this.getTranslation(Language.Kiswahili);
    }
    
    public String getAmharic ()
    {
        return this.getTranslation(Language.Amharic);
    }
    
    public String toString ()
    {
        return this.getEnglish();
    }
}
