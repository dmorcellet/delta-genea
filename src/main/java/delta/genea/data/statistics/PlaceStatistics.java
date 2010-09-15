package delta.genea.data.statistics;

import java.util.Set;

import delta.common.framework.objects.data.DataProxy;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.selections.Selection;

/**
 * Gathers builders for place statistics.
 * @author DAM
 */
public class PlaceStatistics
{
  /**
   * Build birth place statistics.
   * @param selection Source selection.
   * @return A data collection.
   */
  public static QuanticDataCollection<Long> birthPlaceStats(Selection<Person> selection)
  {
    QuanticDataCollection<Long> stats=new QuanticDataCollection<Long>();
    Set<Person> persons=selection.getSelectedObjects();
    DataProxy<Place> proxy;
    for(Person person : persons)
    {
      proxy=person.getBirthPlaceProxy();
      if (proxy!=null)
      {
        long key=proxy.getPrimaryKey();
        stats.addSample(Long.valueOf(key));
      }
      else
      {
        stats.addSample(null);
      }
    }
    return stats;
  }

  /**
   * Build death place statistics.
   * @param selection Source selection.
   * @return A data collection.
   */
  public static QuanticDataCollection<Long> deathPlaceStats(Selection<Person> selection)
  {
    QuanticDataCollection<Long> stats=new QuanticDataCollection<Long>();
    Set<Person> persons=selection.getSelectedObjects();
    DataProxy<Place> proxy;
    for(Person person : persons)
    {
      proxy=person.getDeathPlaceProxy();
      if (proxy!=null)
      {
        long key=proxy.getPrimaryKey();
        stats.addSample(Long.valueOf(key));
      }
      else
      {
        stats.addSample(null);
      }
    }
    return stats;
  }
}
