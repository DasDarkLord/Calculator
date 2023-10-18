package calcFunctions.argumentSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArgumentSet {

    private HashMap<String, List<Object>> varArgObjects;
    public boolean readSuccess;

    public ArgumentSet(HashMap<String, List<Object>> varArgObjects) {
        this.varArgObjects = varArgObjects;
    }

    public boolean hasValue(String name) {
        if (varArgObjects == null) return false;
        return varArgObjects.containsKey(name) && getValue(name) != null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String name) {
        if (varArgObjects == null) return null;
        return varArgObjects.get(name) != null ? (T) varArgObjects.get(name).get(0) : null;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getVarargValue(String name) {
        if (varArgObjects == null) return null;
        return varArgObjects.get(name) != null ? new ArrayList<>(varArgObjects.get(name).stream().map(obj -> (T) obj).toList()) : null;
    }

    public ArgumentSet withReadSuccess(boolean readSuccess) {
        this.readSuccess = readSuccess;
        return this;
    }

    @Override
    public String toString() {
        return "ArgumentSet{" + varArgObjects + "}";
    }

}
