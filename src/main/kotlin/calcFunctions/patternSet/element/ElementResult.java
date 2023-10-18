package calcFunctions.patternSet.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ElementResult {

    private int itemsToRemove;
    private HashMap<String, List<Object>> argumentMap;

    public ElementResult() {
        argumentMap = new HashMap<>();
    }

    public ElementResult clearArgumentMap() {
        argumentMap.clear();
        return this;
    }

    public ElementResult setArgumentMap(HashMap<String, List<Object>> argumentMap) {
        this.argumentMap = argumentMap;
        return this;
    }

    public ElementResult addToArgumentMap(String key, Object object) {
        ArrayList<Object> list = new ArrayList<>();
        list.add(object);

        argumentMap.put(key, list);
        return this;
    }

    public ElementResult addToArgumentMap(String key, List<Object> objects) {
        argumentMap.put(key, objects);
        return this;
    }

    public ElementResult setItemsToRemove(int itemsToRemove) {
        this.itemsToRemove = itemsToRemove;
        return this;
    }

    public int getItemsToRemove() {
        return itemsToRemove;
    }

    public HashMap<String, List<Object>> getArgumentMap() {
        return argumentMap;
    }

}