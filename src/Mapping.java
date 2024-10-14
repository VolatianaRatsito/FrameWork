package util;

public class Mapping {
    String className;
    MySet<VerbMethod> verbMethods;

    public Mapping(){
        verbMethods = new MySet<>();
    }

    public String getClassName(){return className;}
    public MySet<VerbMethod> getVerbMethods(){return verbMethods;}

    public void setClassName(String s){className=s;}
    // public void setVerbMethods(MySet<VerbMethod> vm){verbMethods = vm;}
    public void addVerbMethod(VerbMethod vm){
        this.verbMethods.add(vm);
    }

}
