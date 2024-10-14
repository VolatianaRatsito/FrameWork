package util;

import java.util.HashSet;
import java.util.Set;

import java.util.HashSet;

public class MySet<E> extends HashSet<E> {

    @Override
    public boolean equals(Object o) {
        if (o instanceof VerbMethod) {
            VerbMethod obj = (VerbMethod) o;
            for (E element : this) {
                VerbMethod vb = (VerbMethod) element;
                if (vb.getVerb().equalsIgnoreCase(obj.getVerb()) || 
                    vb.getMethodName().equalsIgnoreCase(obj.getMethodName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public VerbMethod getVerbMethodCorresp(String verb) {
        for (E element : this) {
            VerbMethod vb = (VerbMethod) element;
            if (vb.getVerb().equalsIgnoreCase(verb)) {
                return vb;
            }
        }
        return null;
    }
}
