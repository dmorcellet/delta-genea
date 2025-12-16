package delta.genea.data.places;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import delta.common.framework.objects.data.ObjectsSource;
import delta.common.utils.places.FrenchDepartment;
import delta.common.utils.places.FrenchDepartmentDirectory;
import delta.genea.data.Place;
import delta.genea.data.PlaceLevel;

/**
 * Manages the allocation of places.
 * @author DAM
 */
public class PlaceManager
{
  private ObjectsSource _dataSource;
  private long _placeKey;
  private Map<String,Place> _townPlaces;
  private Map<String,Place> _deptPlaces;
  private Map<String,Place> _countryPlaces;

  /**
   * Constructor.
   * @param dataSource Data source.
   */
  public PlaceManager(ObjectsSource dataSource)
  {
    _dataSource=dataSource;
    _townPlaces=new HashMap<String,Place>();
    _deptPlaces=new HashMap<String,Place>();
    _countryPlaces=new HashMap<String,Place>();
    _placeKey=1;
  }

  /**
   * Get a place from its external identifiers.
   * @param name Place name.
   * @param deptName Department name.
   * @param deptCode Department code.
   * @param country Country.
   * @return A place (may be a new one if needed).
   */
  public Place getPlace(String name,String deptName,String deptCode,String country)
  {
    if ((country==null) || (country.length()==0)) country="FRANCE";
    Place countryPlace=_countryPlaces.get(country);
    if (countryPlace==null)
    {
      countryPlace=new Place(Long.valueOf(_placeKey));
      countryPlace.setLevel(PlaceLevel.COUNTRY);
      countryPlace.setName(country);
      _countryPlaces.put(country,countryPlace);
      _placeKey++;
    }

    Place deptPlace=null;
    if (country.equalsIgnoreCase("FRANCE"))
    {
      FrenchDepartment frenchDept;
      if ((deptName==null) || (deptName.length()==0)) deptName="Nord";
      if ((deptCode==null) || (deptCode.length()==0))
      {
        deptCode="";
        frenchDept=FrenchDepartmentDirectory.getInstance().getByName(deptName);
      }
      else
      {
        frenchDept=FrenchDepartmentDirectory.getInstance().getByCode(deptCode);
      }
      if (frenchDept!=null)
      {
        deptCode=frenchDept.getShortLabel();
        deptName=frenchDept.getLabel();
      }
      String deptKey=deptName+"/"+deptCode;
      deptPlace=_deptPlaces.get(deptKey);
      if (deptPlace==null)
      {
        deptPlace=new Place(Long.valueOf(_placeKey));
        deptPlace.setLevel(PlaceLevel.DEPARTMENT);
        deptPlace.setName(deptName);
        deptPlace.setShortName(deptCode);
        deptPlace.setParentPlaceProxy(_dataSource.buildProxy(Place.class,countryPlace.getPrimaryKey()));
        _deptPlaces.put(deptKey,deptPlace);
        _placeKey++;
      }
    }
    Place parent=countryPlace;
    if (deptPlace!=null)
    {
      parent=deptPlace;
    }
    Place place=_townPlaces.get(name);
    if (place==null)
    {
      place=new Place(Long.valueOf(_placeKey));
      place.setLevel(PlaceLevel.TOWN);
      place.setName(name);
      if (parent!=null)
      {
        place.setParentPlaceProxy(_dataSource.buildProxy(Place.class,parent.getPrimaryKey()));
      }
      _townPlaces.put(name,place);
      _placeKey++;
    }
    return place;
  }

  /**
   * Get all the known places.
   * @return A list of places.
   */
  public List<Place> getPlaces()
  {
    List<Place> places=new ArrayList<Place>();
    places.addAll(_countryPlaces.values());
    places.addAll(_deptPlaces.values());
    places.addAll(_townPlaces.values());
    return places;
  }
}
