package mg.tool;

import jakarta.servlet.http.HttpSession;

public class MySession {

    private HttpSession session;

    public MySession(HttpSession session) {
        this.session = session;
    }

      public HttpSession getSession(){
       return this.session;
    }

    public void setSession(HttpSession session){
      this.session = session;
    }


    public void add(String key, Object value) {
        if (session.getAttribute(key) != null) {
            throw new IllegalArgumentException("Key '" + key + "' already exists.");
        }
        session.setAttribute(key, value);
        System.out.println("Added: " + key + " = " + value);
    }

    public Object get(String key) {
        Object value = session.getAttribute(key);
        if (value == null) {
            throw new IllegalArgumentException("Key '" + key + "' not found.");
        }
        System.out.println("Votre valeur est : " + value);
        return value;
    }

    public void update(String key, Object value) {
        if (session.getAttribute(key) == null) {
            throw new IllegalArgumentException("Key '" + key + "' not found.");
        }
        session.setAttribute(key, value);
        System.out.println("Updated: " + key + " = " + value);
    }

    public void delete(String key) {
        if (session.getAttribute(key) == null) {
            System.out.println("Key '" + key + "' not found.");
            throw new IllegalArgumentException("Key '" + key + "' not found.");
        }
        session.removeAttribute(key);
        System.out.println("Deleted: " + key);
    }
}
