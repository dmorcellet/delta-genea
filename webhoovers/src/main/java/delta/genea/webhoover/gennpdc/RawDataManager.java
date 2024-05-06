package delta.genea.webhoover.gennpdc;

import java.util.ArrayList;
import java.util.List;

import delta.common.framework.objects.data.ObjectsSource;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.PlaceManager;
import delta.genea.data.Union;

/**
 * Manages raw data (persons, unions, places).
 * @author DAM
 */
public class RawDataManager
{
  private List<Person> _persons;
  private List<Union> _unions;
  private List<Place> _places;
  private PlaceManager _placeManager;

  /**
   * Constructor.
   * @param source Data source.
   */
  public RawDataManager(ObjectsSource source)
  {
    _persons=new ArrayList<Person>();
    _unions=new ArrayList<Union>();
    _places=new ArrayList<Place>();
    _placeManager=new PlaceManager(source,1);
    _placeManager.indicateFieldMeaning(1,PlaceManager.TOWN_NAME);
  }

  /**
   * Get the list of persons.
   * @return a list of persons.
   */
  public List<Person> getPersons()
  {
    return _persons;
  }

  /**
   * Get the list of unions.
   * @return a list of unions.
   */
  public List<Union> getUnions()
  {
    return _unions;
  }

  /**
   * Get the list of places.
   * @return a list of places.
   */
  public List<Place> getPlaces()
  {
    _places.clear();
    _placeManager.getPlaces(_places);
    return _places;
  }

  /**
   * Get the places manager.
   * @return the places manager.
   */
  public PlaceManager getPlacesManager()
  {
    return _placeManager;
  }
}
