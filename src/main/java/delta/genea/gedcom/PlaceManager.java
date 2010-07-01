package delta.genea.gedcom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectSource;
import delta.common.utils.places.FrenchDepartment;
import delta.common.utils.places.FrenchDepartmentDirectory;
import delta.common.utils.text.StringSplitter;
import delta.genea.data.Place;
import delta.genea.data.PlaceLevel;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.misc.GenealogySoftware;
import delta.genea.utils.GeneaLoggers;

public class PlaceManager
{
  private static final Logger _logger=GeneaLoggers.getGeneaLogger();

  private GeneaDataSource _dataSource;
  private int _nbFields;
  private int[] _meanings;
  private int[] _fieldIndices;
  private int _placeKey;
  private Map<String,Place> _townPlaces;
  private Map<String,Place> _deptPlaces;
  private Map<String,Place> _countryPlaces;

  public static final int UNUSED=0;
  public static final int TOWN_NAME=1;
  public static final int POSTAL_CODE=2;
  public static final int DEPT_NAME=3;
  public static final int DEPT_CODE=4;
  public static final int REGION_NAME=5;
  public static final int COUNTRY_NAME=6;
  public static final int NB_FIELD_MEANINGS=6;
  public static final int FIRST_MEANING=1;
  public static final int LAST_MEANING=6;

  /**
   * Constructor.
   * @param nbFields Number of fields.
   */
  public PlaceManager(GeneaDataSource dataSource, int nbFields)
  {
    _dataSource=dataSource;
    _nbFields=nbFields;
    _meanings=new int[nbFields];
    _fieldIndices=new int[NB_FIELD_MEANINGS];
    _townPlaces=new HashMap<String,Place>();
    _deptPlaces=new HashMap<String,Place>();
    _countryPlaces=new HashMap<String,Place>();
    _placeKey=1;
  }

  /**
   *
   * @param fieldIndex From 1 to N.
   * @param meaning
   */
  public void indicateFieldMeaning(int fieldIndex, int meaning)
  {
    if ((fieldIndex>0) && (fieldIndex<=_nbFields)
        && (meaning<=LAST_MEANING) && (meaning!=UNUSED))
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

  public long decodePlaceName(String name)
  {
    String[] parts=StringSplitter.split(name,',');
    if (parts.length!=_nbFields)
    {
      _logger.error("Bad number of fields : "+parts.length+" instead of "+_nbFields);
      return -1;
    }

    //2 PLAC Lesquin,59,Nord,,France,

    String placeName=getPart(parts,TOWN_NAME);
    String deptCode=getPart(parts,DEPT_CODE);
    String deptName=getPart(parts,DEPT_NAME);
    String country=getPart(parts,COUNTRY_NAME);

    Place place=getPlace(placeName,deptName,deptCode,country);
    return place.getPrimaryKey();
  }

  private String getPart(String[] parts, int meaning)
  {
    int index=getFieldIndexByMeaning(meaning);
    if ((index>=0) && (index<parts.length)) return parts[index];
    return "";
  }

  private Place getPlace(String name,String deptName,String deptCode,String country)
  {
    ObjectSource<Place> placeDataSource=_dataSource.getPlaceDataSource();
    if ((country==null) || (country.length()==0)) country="FRANCE";
    Place countryPlace=_countryPlaces.get(country);
    if (countryPlace==null)
    {
      countryPlace=new Place(_placeKey,placeDataSource);
      countryPlace.setLevel(PlaceLevel.COUNTRY);
      countryPlace.setName(country);
      _countryPlaces.put(country,countryPlace);
      _placeKey++;
    }

    Place deptPlace=null;
    if (country.toUpperCase().equals("FRANCE"))
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
        deptPlace=new Place(_placeKey,placeDataSource);
        deptPlace.setLevel(PlaceLevel.DEPARTMENT);
        deptPlace.setName(deptName);
        deptPlace.setShortName(deptCode);
        deptPlace.setParentPlaceProxy(new DataProxy<Place>(countryPlace.getPrimaryKey(),placeDataSource));
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
      place=new Place(_placeKey,placeDataSource);
      place.setLevel(PlaceLevel.TOWN);
      place.setName(name);
      if (parent!=null)
      {
        place.setParentPlaceProxy(new DataProxy<Place>(parent.getPrimaryKey(),placeDataSource));
      }
      else
      {
        place.setParentPlaceProxy(null);
      }
      _townPlaces.put(name,place);
      _placeKey++;
    }
    return place;
  }

  public void getPlaces(List<Place> places)
  {
    places.addAll(_countryPlaces.values());
    places.addAll(_deptPlaces.values());
    places.addAll(_townPlaces.values());
  }

  public static PlaceManager buildFor(GeneaDataSource dataSource, int softwareType)
  {
    PlaceManager pm=null;
    if (softwareType==GenealogySoftware.PERSONAL_ANCESTRAL_FILE)
    {
      //   Personal Ancestral File (PAF 4.0) : 1 SOUR PAF 4.0
      //   Nom commune, Code postal, vide, Nom département, Pays
      pm=new PlaceManager(dataSource,5);
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
      pm=new PlaceManager(dataSource,3);
      pm.indicateFieldMeaning(1,TOWN_NAME);
      pm.indicateFieldMeaning(4,DEPT_CODE);
      pm.indicateFieldMeaning(5,COUNTRY_NAME);
    }
    else if (softwareType==GenealogySoftware.HEREDIS)
    {
      //   HEREDIS 6.1 PC : 1 SOUR HEREDIS 6.1 PC
      //   Nom ville, Code Postal, Nom département, Nom région, Pays, Subdivision
      pm=new PlaceManager(dataSource,6);
      pm.indicateFieldMeaning(1,TOWN_NAME);
      pm.indicateFieldMeaning(2,POSTAL_CODE);
      pm.indicateFieldMeaning(3,DEPT_NAME);
      pm.indicateFieldMeaning(4,REGION_NAME);
      pm.indicateFieldMeaning(5,COUNTRY_NAME);
      pm.indicateFieldMeaning(6,UNUSED);
    }
    else if (softwareType==GenealogySoftware.GENEATIQUE)
    {
      pm=new PlaceManager(dataSource,6);
      pm.indicateFieldMeaning(1,TOWN_NAME);
      pm.indicateFieldMeaning(2,DEPT_CODE);
      pm.indicateFieldMeaning(3,DEPT_NAME);
      pm.indicateFieldMeaning(4,UNUSED);
      pm.indicateFieldMeaning(5,UNUSED);
      pm.indicateFieldMeaning(6,UNUSED);
    }
    else if (softwareType==GenealogySoftware.GENEA)
    {
      pm=new PlaceManager(dataSource,3);
      pm.indicateFieldMeaning(1,TOWN_NAME);
      pm.indicateFieldMeaning(2,DEPT_NAME);
      pm.indicateFieldMeaning(3,COUNTRY_NAME);
    }
    return pm;
  }
}
