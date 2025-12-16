package delta.genea.gedcom;

import delta.common.framework.objects.data.ObjectsSource;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.RawDataManager;
import delta.genea.data.places.PlaceManager;
import delta.genea.data.places.PlacesDecoder;

/**
 * Storage for data loaded from a GEDCOM file.
 * @author DAM
 */
public class GedcomDataStorage extends RawDataManager
{
  private PlaceManager _placesMgr;
  private PlacesDecoder _placesDecoder;

  /**
   * Constructor.
   * @param dataSource Data source.
   */
  public GedcomDataStorage(ObjectsSource dataSource)
  {
    super(dataSource);
    _placesMgr=new PlaceManager(dataSource);
  }

  /**
   * Initialize the GEDCOM software type.
   * This is to setup the places decoder.
   * @param softwareType Type to use.
   */
  public void initSource(int softwareType)
  {
    _placesDecoder=PlacesDecoder.buildFor(_placesMgr,softwareType);
  }

  /**
   * Get a person using its (GEDCOM) key.
   * @param key Key to search.
   * @return A person or <code>null</code> if not found.
   */
  public Person getPersonByGedcomKey(Long key)
  {
    return getPerson(key);
  }

  /**
   * Get a place from its description.
   * @param placeDescription Place description.
   * @return A place or <code>null</code> if decoding failed.
   */
  public Place getPlace(String placeDescription)
  {
    return _placesDecoder.decodePlace(placeDescription);
  }

  /**
   * Get the key for a place description.
   * @param placeDescription Place description.
   * @return A place key or <code>null</code> if decoding failed.
   */
  public Long getPlaceKey(String placeDescription)
  {
    return _placesDecoder.getPlaceKey(placeDescription);
  }
}
