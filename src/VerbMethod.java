package util;

public class VerbMethod {
    String verb;
    String methodName;
    Class[] parameterTypes;

    public String getVerb(){
        return this.verb;
    }
    public String getMethodName(){
        return this.methodName;
    }
    public Class[] getParameterTypes(){
        return this.parameterTypes;
    }

    public void setVerb(String v){
        this.verb = v;
    }
    public void setMethodName(String m){
        this.methodName = m;
    }
    public void setParameterTypes(Class[] pm){
        this.parameterTypes = pm;
    }
}
