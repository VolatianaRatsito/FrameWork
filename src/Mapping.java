package mg.tool;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import mg.tool.VerbMethod;

public class Mapping {
    private String className;
    private Set<VerbMethod> verbMethods = new HashSet<>();

    // Getters and setters
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Set<VerbMethod> getVerbMethods() {
        return verbMethods;
    }

    public void setVerbMethods(Set<VerbMethod> verbMethods) {
        this.verbMethods = verbMethods;
    }

    public static void displayVerbMethods(Set<VerbMethod> verbMethods) {
        for (VerbMethod verbMethod : verbMethods) {
            System.out.println("Method Name: " + verbMethod.getMethodName() + ", Verb: " + verbMethod.getVerb());
        }
    }

    public void addVerbMethod(VerbMethod verbMethod) {
        this.verbMethods.add(verbMethod);
    }

    public VerbMethod getVerbMethodByUrl(String verb){
        Set<VerbMethod> verbMethods = this.verbMethods;
        for (VerbMethod verbMethod : verbMethods) {
            if (verbMethod.getVerb().equalsIgnoreCase(verb)) {
                return verbMethod;
            }
        }
        return null;
    }


    // Redefinition of equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mapping mapping = (Mapping) o;

        // Check if both have the same className and compare all VerbMethods
        return Objects.equals(className, mapping.className) &&
               verbMethods.equals(mapping.getVerbMethods());
    }

    // Redefinition of hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(className, verbMethods);
    }

    // Method to compare VerbMethods in the current Mapping with an object passed as argument
    public boolean containsVerbMethodFromObject(Object obj) {
        if (obj instanceof Mapping) {
            Mapping otherMapping = (Mapping) obj;
            // Loop through all VerbMethods in the current Mapping
            for (VerbMethod currentVerbMethod : this.verbMethods) {
                // Compare with all VerbMethods from the passed object's Mapping
                for (VerbMethod otherVerbMethod : otherMapping.getVerbMethods()) {
                    if (currentVerbMethod.getVerb().equalsIgnoreCase(otherVerbMethod.getVerb())) {
                        return true; // Return true if any VerbMethod's verb matches
                    }
                }
            }
        }
        return false; // Return false if no match is found
    }
}
