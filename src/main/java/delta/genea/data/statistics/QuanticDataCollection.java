package delta.genea.data.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import delta.common.utils.misc.IntegerHolder;

/**
 * Stores data statistics for quantic values.
 * @author DAM
 * @param <T> Type of quantic keys.
 */
public class QuanticDataCollection<T extends Comparable<T>> implements DataCollection<T>
{
  private HashMap<T,IntegerHolder> _counters;
  
  /**
   * Constructor.
   */
  public QuanticDataCollection()
  {
    _counters=new HashMap<T,IntegerHolder>();
  }

  /**
   * Add a new sample.
   * @param t Data value.
   */
  public void addSample(T t)
  {
    IntegerHolder holder=_counters.get(t);
    if (holder==null)
    {
      holder=new IntegerHolder();
      _counters.put(t,holder);
    }
    holder.increment();
  }

  /**
   * Get an ordered list of keys. Order is prescribed be the
   * values of the counters.
   * @return A list of known quantic values.
   */
  public List<T> getOrderedKeys()
  {
    ArrayList<Map.Entry<T,IntegerHolder>> entries=new ArrayList<Map.Entry<T,IntegerHolder>>(_counters.entrySet());
    Collections.sort(entries,new DataComparator<T>());
    ArrayList<T> keys=new ArrayList<T>(entries.size());
    for(Map.Entry<T,IntegerHolder> entry : entries)
    {
      keys.add(entry.getKey());
    }
    return keys;
  }

  /**
   * Get the counter for a given key.
   * @param key Targeted key.
   * @return A counter value of zero if the given key does not exist in this data set.
   */
  public int getValue(T key)
  {
    IntegerHolder holder=_counters.get(key);
    if (holder!=null)
    {
      return holder.getInt();
    }
    return 0;
  }

  private static class DataComparator<T> implements Comparator<Entry<T,IntegerHolder>>
  {
    public int compare(Entry<T,IntegerHolder> e1, Entry<T,IntegerHolder> e2)
    {
      int n1=e1.getValue().getInt();
      int n2=e2.getValue().getInt();
      if (n1>n2)
      {
        return -1;
      }
      else if (n1<n2)
      {
        return 1;
      }
      return 0;
    }
  }
}
