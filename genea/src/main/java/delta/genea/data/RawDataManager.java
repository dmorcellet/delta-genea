package delta.genea.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import delta.common.framework.objects.data.ObjectsSource;
import delta.common.framework.objects.sql.SqlObjectsSource;
import delta.common.framework.objects.data.IdentifiableComparator;
import delta.genea.data.places.PlaceManager;

/**
 * Manages raw data (persons, unions, places).
 * @author DAM
 */
public class RawDataManager
{
  private ObjectsSource _dataSource;
  private Map<Long,Person> _persons;
  private List<Union> _unions;
  private PlaceManager _placeManager;

  /**
   * Constructor.
   * @param source Data source.
   */
  public RawDataManager(ObjectsSource source)
  {
    _dataSource=source;
    _persons=new HashMap<Long,Person>();
    _unions=new ArrayList<Union>();
    _placeManager=new PlaceManager(source);
  }

  /**
   * Get a person using its key.
   * @param key Key to search.
   * @return A person or <code>null</code> if not found.
   */
  public Person getPerson(Long key)
  {
    return _persons.get(key);
  }

  /**
   * Add a new person.
   * @param person Person to add.
   */
  public void addPerson(Person person)
  {
    Long key=person.getPrimaryKey();
    _persons.put(key,person);
  }

  /**
   * Add a new union.
   * @param union Union to add.
   */
  public void addUnion(Union union)
  {
    _unions.add(union);
  }

  /**
   * Get the list of places.
   * @return a list of places.
   */
  public List<Place> getPlaces()
  {
    return _placeManager.getPlaces();
  }

  /**
   * Get the places manager.
   * @return the places manager.
   */
  public PlaceManager getPlacesManager()
  {
    return _placeManager;
  }

  /**
   * Write data to the given source.
   */
  public void writeDB()
  {
    if (_dataSource instanceof SqlObjectsSource)
    {
      ((SqlObjectsSource)_dataSource).setForeignKeyChecks(false);
    }
    // Places
    List<Place> places=getPlaces();
    for(Place place : places)
    {
      _dataSource.create(Place.class,place);
    }
    // Persons
    List<Person> persons=new ArrayList<Person>(_persons.values());
    Collections.sort(persons,new IdentifiableComparator<>());
    for(Person person : persons)
    {
      _dataSource.create(Person.class,person);
    }
    // Unions
    for(Union union : _unions)
    {
      _dataSource.create(Union.class,union);
    }
    if (_dataSource instanceof SqlObjectsSource)
    {
      ((SqlObjectsSource)_dataSource).setForeignKeyChecks(true);
    }
  }
}
