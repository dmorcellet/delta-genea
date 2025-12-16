package delta.genea.data.places;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.text.StringSplitter;
import delta.genea.data.Place;
import delta.genea.misc.GenealogySoftware;

/**
 * Decodes places descriptions.
 * @author DAM
 */
public class PlacesDecoder
{
  private static final Logger LOGGER=LoggerFactory.getLogger(PlacesDecoder.class);

  private int _nbFields;
  private int[] _meanings;
  private int[] _fieldIndices;
  private PlaceManager _placesMgr;

  /**
   * Identifier for an unused field.
   */
  public static final int UNUSED=0;
  /**
   * Identifier for the 'town' field of a place record.
   */
  public static final int TOWN_NAME=1;
  /**
   * Identifier for the 'postal code' field of a place record.
   */
  public static final int POSTAL_CODE=2;
  /**
   * Identifier for the 'department name' field of a place record.
   */
  public static final int DEPT_NAME=3;
  /**
   * Identifier for the 'department code' field of a place record.
   */
  public static final int DEPT_CODE=4;
  /**
   * Identifier for the 'region name' field of a place record.
   */
  public static final int REGION_NAME=5;
  /**
   * Identifier for the 'country name' field of a place record.
   */
  public static final int COUNTRY_NAME=6;
  /**
   * Number of fields in a place record.
   */
  private static final int NB_FIELD_MEANINGS=6;

  /**
   * Constructor.
   * @param placesMgr Places manager.
   * @param nbFields Number of fields.
   */
  public PlacesDecoder(PlaceManager placesMgr, int nbFields)
  {
    _placesMgr=placesMgr;
    _nbFields=nbFields;
    _meanings=new int[nbFields];
    _fieldIndices=new int[NB_FIELD_MEANINGS];
  }

  /**
   *
   * @param fieldIndex From 1 to N.
   * @param meaning
   */
  public void indicateFieldMeaning(int fieldIndex, int meaning)
  {
    if ((fieldIndex>0) && (fieldIndex<=_nbFields)
        && (meaning<=NB_FIELD_MEANINGS) && (meaning!=UNUSED))
    {
      _meanings[fieldIndex-1]=meaning;
      _fieldIndices[meaning-1]=fieldIndex;
    }
  }

  /**
   * Find field index for meaning.
   * @param meaning
   * @return 0..N-1 if found. -1 if not found.
   */
  public int getFieldIndexByMeaning(int meaning)
  {
    if (meaning==UNUSED) return -1;
    return _fieldIndices[meaning-1]-1;
  }

  /**
   * Find field meaning for index.
   * @param fieldIndex Between 1 and N.
   * @return 1..N if found. 0 if not used.
   */
  public int getFieldMeaning(int fieldIndex)
  {
    if ((fieldIndex>=0) && (fieldIndex<=_nbFields))
      return _meanings[fieldIndex];
    return 0;
  }

  /**
   * Decode a place.
   * @param placeDescription Place description.
   * @return A place or <code>null</code> if decoding failed.
   */
  public Place decodePlace(String placeDescription)
  {
    String[] parts=StringSplitter.split(placeDescription,',');
    if (parts.length!=_nbFields)
    {
      LOGGER.error("Bad number of fields: {} instead of {}",Integer.valueOf(parts.length),Integer.valueOf(_nbFields));
      return null;
    }

    String placeName=getPart(parts,TOWN_NAME);
    String deptCode=getPart(parts,DEPT_CODE);
    String deptName=getPart(parts,DEPT_NAME);
    String country=getPart(parts,COUNTRY_NAME);

    Place place=_placesMgr.getPlace(placeName,deptName,deptCode,country);
    return place;
  }

  /**
   * Get the key for the given place.
   * @param placeName Place description.
   * @return A place key or <code>null</code>.
   */
  public Long getPlaceKey(String placeName)
  {
    Place place=decodePlace(placeName);
    return (place!=null)?place.getPrimaryKey():null;
  }

  private String getPart(String[] parts, int meaning)
  {
    int index=getFieldIndexByMeaning(meaning);
    if ((index>=0) && (index<parts.length)) return parts[index];
    return "";
  }

  /**
   * Build a places manager and configure it for a given genealogy software.
   * @param placesMgr Places manager.
   * @param softwareType Genealogy software to use.
   * @return A places manager.
   */
  public static PlacesDecoder buildFor(PlaceManager placesMgr, int softwareType)
  {
    PlacesDecoder pm=null;
    if (softwareType==GenealogySoftware.PERSONAL_ANCESTRAL_FILE)
    {
      //   Personal Ancestral File (PAF 4.0) : 1 SOUR PAF 4.0
      //   Nom commune, Code postal, vide, Nom département, Pays
      pm=new PlacesDecoder(placesMgr,5);
      pm.indicateFieldMeaning(1,TOWN_NAME);
      pm.indicateFieldMeaning(2,POSTAL_CODE);
      pm.indicateFieldMeaning(3,UNUSED);
      pm.indicateFieldMeaning(4,DEPT_NAME);
      pm.indicateFieldMeaning(5,COUNTRY_NAME);
    }
    else if (softwareType==GenealogySoftware.BASGEN_98)
    {
      //   Base Gén 98 : 1 SOUR BASGEN98
      //   Nom commune, Code départment, Pays
      pm=new PlacesDecoder(placesMgr,3);
      pm.indicateFieldMeaning(1,TOWN_NAME);
      pm.indicateFieldMeaning(4,DEPT_CODE);
      pm.indicateFieldMeaning(5,COUNTRY_NAME);
    }
    else if ((softwareType==GenealogySoftware.HEREDIS) || (softwareType==GenealogySoftware.GENEATIQUE))
    {
      //   HEREDIS 6.1 PC : 1 SOUR HEREDIS 6.1 PC
      //   Nom ville, Code Postal, Nom département, Nom région, Pays, Subdivision
      //   2 PLAC Lassigny, 60350, Oise, Picardie, France, 
      pm=new PlacesDecoder(placesMgr,6);
      pm.indicateFieldMeaning(1,TOWN_NAME);
      pm.indicateFieldMeaning(2,POSTAL_CODE);
      pm.indicateFieldMeaning(3,DEPT_NAME);
      pm.indicateFieldMeaning(4,REGION_NAME);
      pm.indicateFieldMeaning(5,COUNTRY_NAME);
      pm.indicateFieldMeaning(6,UNUSED);
    }
    else if (softwareType==GenealogySoftware.GENEA)
    {
      pm=new PlacesDecoder(placesMgr,3);
      pm.indicateFieldMeaning(1,TOWN_NAME);
      pm.indicateFieldMeaning(2,DEPT_NAME);
      pm.indicateFieldMeaning(3,COUNTRY_NAME);
    }
    return pm;
  }
}
