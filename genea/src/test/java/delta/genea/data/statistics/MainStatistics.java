package delta.genea.data.statistics;

import java.util.ArrayList;
import java.util.List;

import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.selections.AncestorsSelectionBuilder;
import delta.genea.data.selections.CompoundSelection;
import delta.genea.data.selections.CompoundSelection.Operator;
import delta.genea.data.selections.NameSelectionBuilder;
import delta.genea.data.selections.Selection;
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
  public static void main(String[] args)
  {
    ArrayList<Selection<Person>> selections=new ArrayList<Selection<Person>>();
    AncestorsSelectionBuilder ancestorsBuilder=new AncestorsSelectionBuilder(DATA_SOURCE,Long.valueOf(76));
    Selection<Person> ancestors=ancestorsBuilder.build();
    selections.add(ancestors);
    System.out.println("Ancestors: "+ancestors.getSize());
    NameSelectionBuilder nameSelectionBuilder=new NameSelectionBuilder(DATA_SOURCE,"SAUVAGE");
    Selection<Person> namedSelection=nameSelectionBuilder.build();
    System.out.println("SAUVAGE: "+namedSelection.getSize());
    selections.add(namedSelection);
    CompoundSelection<Person> compoundSelection=new CompoundSelection<Person>("compound",Operator.AND,selections);
    System.out.println("SAUVAGE ancestors: "+compoundSelection.getSize());
    int i=0;
    for(Person p : compoundSelection.getSelectedObjects())
    {
      System.out.println("#"+(i+1)+": "+p.getFullName());
      i++;
    }

    QuanticDataCollection<Long> birthPlaceStats=PlaceStatistics.birthPlaceStats(compoundSelection);
    showStats("Birth place",birthPlaceStats);
    QuanticDataCollection<Long> deathPlaceStats=PlaceStatistics.deathPlaceStats(compoundSelection);
    showStats("Death place",deathPlaceStats);
  }

  private static void showStats(String name, QuanticDataCollection<Long> stats)
  {
    // Show stats
    System.out.println(name);
    GeneaDataSource source=GeneaDataSource.getInstance(DATA_SOURCE);
    List<Long> keys=stats.getOrderedKeys();
    String placeName;
    Place place;
    for(Long key : keys)
    {
      placeName="???";
      if (key!=null)
      {
        place=source.load(Place.class,key);
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
