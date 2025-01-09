package delta.genea.gedcom;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectsSource;
import delta.common.framework.objects.sql.SqlObjectsSource;
import delta.common.utils.NumericTools;
import delta.common.utils.files.TextFileReader;
import delta.common.utils.text.TextUtils;
import delta.common.utils.text.ansel.AnselCharset;
import delta.genea.GeneaApplication;
import delta.genea.data.GeneaDate;
import delta.genea.data.OccupationForPerson;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.PlaceManager;
import delta.genea.data.Sex;
import delta.genea.data.Union;
import delta.genea.data.sources.GeneaDataSource;

/**
 * Imports a GEDCOM file to a genea database.
 * @author DAM
 */
public class FromGEDCOM
{
  private static final String UNUSED_LINE="Ligne inexploitée [{}]";

  private static final Logger LOGGER=LoggerFactory.getLogger(FromGEDCOM.class);

  private static final String LINE_2_PLAC="2 PLAC ";
  private static final String LINE_2_DATE="2 DATE";

  /**
   * Main method for the GEDCOM import tool.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    GeneaApplication.getInstance();
    new FromGEDCOM(new File("D:\\dam\\Donnees\\docs\\genea\\maryvonne\\Lamour_31-10-2016.ged"),"genea_maryvonne");
  }

  private File _fileName;
  private List<Person> _persons;
  private List<Union> _unions;
  private List<Place> _places;
  private PlaceManager _placeManager;
  private ObjectsSource _dataSource;
  private List<String> _lines;
  private int _index;
  private int _maxIndex;

  /**
   * Constructor.
   * @param fileName File to import.
   * @param dbName Name of the database to populate.
   */
  public FromGEDCOM(File fileName, String dbName)
  {
    long time=System.currentTimeMillis();
    _fileName=fileName;
    _dataSource=GeneaDataSource.getInstance(dbName).getObjectsSource();
    reset();
    parseFileLines();
    go();
    long time2=System.currentTimeMillis();
    long duration=time2-time;
    LOGGER.info("Time to import: {}ms",Long.valueOf(duration));
  }

  private void reset()
  {
    _persons=new ArrayList<Person>();
    _unions=new ArrayList<Union>();
    _places=new ArrayList<Place>();
    _lines=new ArrayList<String>();
    _index=0;
    _maxIndex=0;
    _placeManager=null;
  }

  private void parseFileLines()
  {
    TextFileReader fp=new TextFileReader(_fileName,new AnselCharset());
    _lines=TextUtils.readAsLines(fp);
    _maxIndex=_lines.size();
  }

  private void go()
  {
    String line;
    while (_index<_maxIndex)
    {
      line=_lines.get(_index).trim();
      if ((line.startsWith("0 "))&&(line.endsWith("INDI")))
      {
        try
        {
          handlePerson();
        }
        catch (Exception e)
        {
          LOGGER.error("Pb with person",e);
        }
      }
      else if ((line.startsWith("0 "))&&(line.endsWith("FAM")))
      {
        try
        {
          handleFamily();
        }
        catch (Exception e)
        {
          LOGGER.error("Pb with family",e);
        }
      }
      else if (line.startsWith("1 SOUR"))
      {
        handleSource();
      }
      else
      {
        LOGGER.debug(UNUSED_LINE,line);
        _index++;
      }
    }
    retrievePlaces();
    writeDB();
  }

  private void retrievePlaces()
  {
    _places.clear();
    if (_placeManager!=null) {
      _placeManager.getPlaces(_places);
    }
  }

  private void handleSource()
  {
    int softwareType=SourceDecoder.getSourceSoftware(_lines.get(_index));
    _index++;
    _placeManager=PlaceManager.buildFor(_dataSource,softwareType);
  }

  private void handlePerson()
  {
    Person p=new Person(null);
    {
      String keyString=_lines.get(_index);
      int atIndex=keyString.indexOf("@");
      if (atIndex!=-1) keyString=keyString.substring(atIndex+1);
      atIndex=keyString.indexOf("@");
      if (atIndex!=-1) keyString=keyString.substring(0,atIndex);
      keyString=keyString.replace("I","");
      Long key=NumericTools.parseLong(keyString);
      // TODO handle null
      p.setPrimaryKey(key);
    }

    while (true)
    {
      String line;
      _index++;
      if (_index<_maxIndex)
      {
        line=_lines.get(_index);
        if (line.startsWith("0 "))
          break;
        else if (line.startsWith("1 SEX"))
        {
          String sexString=line.substring(6).trim();
          Sex sex=Sex.getFromValue(sexString.charAt(0));
          p.setSex(sex);
        }
        else if (line.startsWith("1 NAME"))
        {
          String tmp=line.substring(7).trim();
          String firstName=tmp;
          String lastName=tmp;
          int slash=tmp.indexOf('/');
          if (slash!=-1)
          {
            firstName=tmp.substring(0,slash);
            lastName=tmp.substring(slash+1);
            while (true)
            {
              slash=lastName.indexOf('/');
              if (slash!=-1)
                lastName=lastName.substring(0,slash);
              else
                break;
            }
          }
          p.setFirstname(firstName);
          p.setLastName(lastName);
        }
        // END OF NAME
        else if ((line.startsWith("1 BIRT"))||(line.startsWith("1 CHR")))
        {
          while (true)
          {
            _index++;
            if (_index<_maxIndex)
            {
              line=_lines.get(_index);
              if ((line.startsWith("0 "))||(line.startsWith("1 ")))
              {
                _index--;
                break;
              }
              else if (line.startsWith(LINE_2_DATE))
              {
                String tmp=line.substring(7).trim();
                GeneaDate d=DateUtils.decodeDate(tmp);
                if (d!=null)
                {
                  p.setBirthDate(d.getDate(),d.getInfosDate());
                }
              }
              else if (line.startsWith(LINE_2_PLAC))
              {
                String tmp=line.substring(7).trim();
                Long key=_placeManager.decodePlaceName(tmp);
                DataProxy<Place> birthPlaceProxy=_dataSource.buildProxy(Place.class,key);
                p.setBirthPlaceProxy(birthPlaceProxy);
              }
            }
            else
              break;
          }
        }
        // END OF BIRT/CHR
        else if (line.startsWith("1 DEAT"))
        {
          while (true)
          {
            _index++;
            if (_index<_maxIndex)
            {
              line=_lines.get(_index);
              if ((line.startsWith("0 "))||(line.startsWith("1 ")))
              {
                _index--;
                break;
              }
              else if (line.startsWith(LINE_2_DATE))
              {
                String tmp=line.substring(7).trim();
                GeneaDate d=DateUtils.decodeDate(tmp);
                if (d!=null)
                {
                  p.setDeathDate(d.getDate(),d.getInfosDate());
                }
              }
              else if (line.startsWith(LINE_2_PLAC))
              {
                String tmp=line.substring(7).trim();
                Long key=_placeManager.decodePlaceName(tmp);
                p.setDeathPlaceProxy(_dataSource.buildProxy(Place.class,key));
              }
            }
            else
              break;
          }
        }
        // END OF DEAT
        else if (line.startsWith("1 OCCU"))
        {
          String occu=_lines.get(_index);
          String profession=occu.substring(6);
          OccupationForPerson o=new OccupationForPerson();
          o.setOccupation(profession);
          p.addOccupation(o);
        }
        // END OF OCCU
        else
        {
          LOGGER.debug(UNUSED_LINE,line);
        }
      }
    }
    _persons.add(p);
  }

  private void handleFamily()
  {
    Union u=new Union(null);
    {
      String keyString=_lines.get(_index);
      int atIndex=keyString.indexOf("@");
      if (atIndex!=-1) keyString=keyString.substring(atIndex+1);
      atIndex=keyString.indexOf("@");
      if (atIndex!=-1) keyString=keyString.substring(0,atIndex);
      keyString=keyString.replace("U","");
      keyString=keyString.replace("F","");
      Long key=Long.valueOf(keyString);
      u.setPrimaryKey(key);
    }

    Long manKey=null;
    Long womanKey=null;
    while (true)
    {
      String line;
      _index++;
      if (_index<_maxIndex)
      {
        line=_lines.get(_index);
        if (line.startsWith("0 "))
          break;
        else if (line.startsWith("1 HUSB "))
        {
          line=line.trim();
          String key=line.substring(8); /* "1 HUSB @" */
          manKey=decodePersonID(key);
          u.setManProxy(_dataSource.buildProxy(Person.class,manKey));
        }
        // END OF HUSB
        else if (line.startsWith("1 WIFE "))
        {
          line=line.trim();
          String key=line.substring(8); /* "1 WIFE @" */
          womanKey=decodePersonID(key);
          u.setWomanProxy(_dataSource.buildProxy(Person.class,womanKey));
        }
        // END OF WIFE
        else if (line.startsWith("1 MARR"))
        {
          while (true)
          {
            _index++;
            if (_index<_maxIndex)
            {
              line=_lines.get(_index);
              if (!line.startsWith("2 "))
              {
                _index--;
                break;
              }
              else if (line.startsWith(LINE_2_DATE))
              {
                String tmp=line.substring(7).trim();
                GeneaDate d=DateUtils.decodeDate(tmp);
                if (d!=null)
                {
                  u.setDate(d);
                }
              }
              else if (line.startsWith(LINE_2_PLAC))
              {
                String tmp=line.substring(7).trim();
                Long key=_placeManager.decodePlaceName(tmp);
                u.setPlaceProxy(_dataSource.buildProxy(Place.class,key));
              }
            }
            else
              break;
          }
        }
        // END OF MARR
        else if (line.startsWith("1 CHIL "))
        {
          line=line.trim();
          String key=line.substring(8); /* "1 CHIL @" */
          Long childKey=decodePersonID(key);

          int nbPersons=_persons.size();
          boolean found=false;
          Person tmp;
          for(int i=0;i<nbPersons;i++)
          {
            tmp=_persons.get(i);
            if (tmp.getPrimaryKey().equals(childKey))
            {
              found=true;
              if (manKey!=null)
              {
                tmp.setFatherProxy(_dataSource.buildProxy(Person.class,manKey));
              }
              if (womanKey!=null)
              {
                tmp.setMotherProxy(_dataSource.buildProxy(Person.class,womanKey));
              }
              break;
            }
          }
          if (!found)
          {
            LOGGER.warn("Person ID: {} not found!",childKey);
          }
        }
        // END OF CHIL
        else
        {
          LOGGER.debug(UNUSED_LINE,line);
        }
      }
    }
    _unions.add(u);
  }

  private Long decodePersonID(String id)
  {
    StringBuilder sb=new StringBuilder();
    int n=id.length();
    char[] c=id.toCharArray();
    for(int i=0;i<n;i++)
    {
      if ((c[i]>='0')&&(c[i]<='9')) sb.append(c[i]);
    }
    long ret=NumericTools.parseLong(sb.toString(),0);
    return (ret==0)?null:Long.valueOf(ret);
  }

  private void writeDB()
  {
    if (_dataSource instanceof SqlObjectsSource)
    {
      ((SqlObjectsSource)_dataSource).setForeignKeyChecks(false);
    }
    // Places
    int nbPlaces=_places.size();
    for(int i=0;i<nbPlaces;i++)
    {
      _dataSource.create(Place.class,_places.get(i));
    }
    // Persons
    int nbPersons=_persons.size();
    for(int i=0;i<nbPersons;i++)
    {
      _dataSource.create(Person.class,_persons.get(i));
    }
    // Unions
    int nbUnions=_unions.size();
    for(int i=0;i<nbUnions;i++)
    {
      _dataSource.create(Union.class,_unions.get(i));
    }
    if (_dataSource instanceof SqlObjectsSource)
    {
      ((SqlObjectsSource)_dataSource).setForeignKeyChecks(true);
    }
  }
}
