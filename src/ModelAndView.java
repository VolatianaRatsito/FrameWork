package mg.tool;
import java.util.*;
public class ModelAndView {
     String url;
     Map<String, Object> data  = new HashMap<>();

    public ModelAndView(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    public <T> void add(String key, T value) {
        data.put(key, value);
    }
}
