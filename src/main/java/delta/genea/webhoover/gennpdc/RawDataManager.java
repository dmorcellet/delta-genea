package delta.genea.webhoover.gennpdc;

import java.util.ArrayList;
import java.util.List;

import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.Union;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.gedcom.PlaceManager;

/**
 * @author DAM
 */
public class RawDataManager
{
  private List<Person> _persons;
  private List<Union> _unions;
  private List<Place> _places;
  private PlaceManager _placeManager;
  private GeneaDataSource _dataSource;

  public RawDataManager(GeneaDataSource dataSource)
  {
    _dataSource=dataSource;
    _persons=new ArrayList<Person>();
    _unions=new ArrayList<Union>();
    _places=new ArrayList<Place>();
    _placeManager=new PlaceManager(dataSource,1);
    _placeManager.indicateFieldMeaning(1,PlaceManager.TOWN_NAME);
  }

  public List<Person> getPersons()
  {
    return _persons;
  }

  public List<Union> getUnions()
  {
    return _unions;
  }

  public List<Place> getPlaces()
  {
    _places.clear();
    _placeManager.getPlaces(_places);
    return _places;
  }
  
  public PlaceManager getPlacesManager()
  {
    return _placeManager;
  }
}
