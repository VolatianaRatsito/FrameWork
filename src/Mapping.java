package util;

public class Mapping {
    private String className;
    private MySet<VerbMethod> verbMethods;

    public Mapping() {
        verbMethods = new MySet<>();
    }

    public String getClassName() {
        return className;
    }

    public MySet<VerbMethod> getVerbMethods() {
        return verbMethods;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    // Vérifie si un verbe existe déjà dans la collection
    public boolean containsVerb(String verb) {
        for (VerbMethod vb : verbMethods) {
            if (vb.getVerb().equalsIgnoreCase(verb)) {
                return true;
            }
        }
        return false;
    }

    // Ajoute un VerbMethod après vérification des doublons
    public void addVerbMethod(VerbMethod vm) throws IllegalArgumentException {
        if (containsVerb(vm.getVerb())) {
            throw new IllegalArgumentException("Le verbe HTTP " + vm.getVerb() + " existe déjà pour cette URL");
        }
        this.verbMethods.add(vm);
    }
}
