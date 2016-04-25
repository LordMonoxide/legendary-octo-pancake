package gfx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class Destroyable {
  private static final Map<String, List<Integer>> ids = new HashMap<>();
  
  private Destroyable() { }
  
  @SuppressWarnings("null")
  public static void registerDestroyable(Object obj, int id) {
    String type = obj.getClass().getName();
    
    if(!ids.containsKey(type)) {
      ids.put(type, new ArrayList<>());
    }
    
    ids.get(type).add(Integer.valueOf(id));
  }
  
  @SuppressWarnings("null")
  public static void unregisterDestroyable(Object obj, int id) {
    String type = obj.getClass().getName();
    
    if(!ids.containsKey(type)) {
      System.err.println("Attempted to unregister " + type + ", but none have been registered"); //$NON-NLS-1$ //$NON-NLS-2$
      return;
    }
    
    if(!ids.get(type).remove(Integer.valueOf(id))) {
      System.err.println("Attempted to unregister " + type + " " + id + ", which has not been registered"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
  }
  
  @SuppressWarnings("null")
  public static void checkForMemoryLeaks() {
    boolean destroyed = true;
    
    for(Entry<String, List<Integer>> id_list : ids.entrySet()) {
      for(Integer id : id_list.getValue()) {
        System.err.println(id_list.getKey() + " " + id + " was not destroyed"); //$NON-NLS-1$ //$NON-NLS-2$
        destroyed = false;
      }
    }
    
    if(destroyed) {
      System.out.println("All resources destroyed"); //$NON-NLS-1$
    }
  }
}
