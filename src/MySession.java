package util;

import jakarta.servlet.http.HttpSession;

public class MySession {
    public HttpSession session;

    public MySession(HttpSession session) {
        this.session = session;
    }
    public MySession() {
    }

    public void add(String key, Object value) {
        session.setAttribute(key, value);
    }

    public Object get(String key) {
        return session.getAttribute(key);
    }

    public void delete(String key) {
        session.removeAttribute(key);
    }
     
}
