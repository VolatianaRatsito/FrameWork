package util;

import java.util.HashMap;

/**
 * ModelView
 */
public class ModelView {

    String url;

    HashMap<String,Object> data;

    public String getUrl(){return url;}
    public HashMap getData(){return data;}

    public void setUrl(String urll){url=urll;}
    public void setData(HashMap<String,Object> d){data=d;}

    public void add(String key, Object value){
        this.data.put(key, value);
    }
}