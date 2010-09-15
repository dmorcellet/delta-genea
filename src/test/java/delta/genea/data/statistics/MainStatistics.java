package delta.genea.data.statistics;

import java.util.List;

import delta.common.framework.objects.data.ObjectSource;
import delta.genea.data.Place;
import delta.genea.data.selections.AncestorsSelection;
import delta.genea.data.sources.GeneaDataSource;

/**
 * Test some statistics.
 * @author DAM
 */
public class MainStatistics
{
  private static final String DATA_SOURCE="genea";

  /**
   * Test some statistics on the main genea database.
   * @param args Not used.
   */
  public static void main(String args[])
  {
    AncestorsSelection selection=new AncestorsSelection(DATA_SOURCE,76,1000);
    selection.build();
    System.out.println(selection.getSelectedObjects().size());
    QuanticDataCollection<Long> birthPlaceStats=PlaceStatistics.birthPlaceStats(selection);
    showStats("Birth place",birthPlaceStats);
    QuanticDataCollection<Long> deathPlaceStats=PlaceStatistics.deathPlaceStats(selection);
    showStats("Death place",deathPlaceStats);
  }

  private static void showStats(String name, QuanticDataCollection<Long> stats)
  {
    // Show stats
    System.out.println(name);
    GeneaDataSource source=GeneaDataSource.getInstance(DATA_SOURCE);
    ObjectSource<Place> placesSource=source.getPlaceDataSource();
    List<Long> keys=stats.getOrderedKeys();
    String placeName;
    Place place;
    for(Long key : keys)
    {
      placeName="";
      if (key!=null)
      {
        place=placesSource.load(key.longValue());
        if (place!=null)
        {
          placeName=place.getFullName();
        }
      }
      int nb=stats.getValue(key);
      System.out.println(placeName+": "+nb);
    }
  }
}
