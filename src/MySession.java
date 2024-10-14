package session;

import jakarta.servlet.http.HttpSession;

public class MySession {
    HttpSession session;

    public MySession(){}
    public MySession(HttpSession s){this.session = s;}
    
    public void setSession(HttpSession s){ this.session = s;}
    public HttpSession getSession(){return this.session;}

    public Object getAttribute(String key){
        Object res = this.session.getAttribute(key);
        return res;
    }

    public void addAttribute(String key,Object attr){
        this.session.setAttribute(key,attr);
    }

    public void deleteAttribute(String key){
        this.session.removeAttribute(key);
    }
}
