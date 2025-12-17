package delta.genea.gedcom;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectsSource;
import delta.common.utils.NumericTools;
import delta.common.utils.files.TextFileReader;
import delta.common.utils.text.TextUtils;
import delta.common.utils.text.ansel.AnselCharset;
import delta.genea.GeneaApplication;
import delta.genea.data.GeneaDate;
import delta.genea.data.OccupationForPerson;
import delta.genea.data.Person;
import delta.genea.data.Place;
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
    File from=new File(args[0]);
    String dbName=args[1];
    new FromGEDCOM(from,dbName);
  }

  private File _fileName;
  private GedcomDataStorage _storage;
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
    _storage=new GedcomDataStorage(_dataSource);
    parseFileLines();
    go();
    long time2=System.currentTimeMillis();
    long duration=time2-time;
    LOGGER.info("Time to import: {}ms",Long.valueOf(duration));
  }

  private void parseFileLines()
  {
    TextFileReader fp=new TextFileReader(_fileName,new AnselCharset());
    _lines=TextUtils.readAsLines(fp);
    _index=0;
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
    _storage.writeDB();
  }

  private void handleSource()
  {
    int softwareType=SourceDecoder.getSourceSoftware(_lines.get(_index));
    _index++;
    _storage.initSource(softwareType);
  }

  private void handlePerson()
  {
    Person p=new Person(null);
    String idLine=_lines.get(_index);
    Long key=parseIdLine(idLine);
    // TODO handle null
    p.setPrimaryKey(key);

    while (true)
    {
      String line=getNextLine();
      if (line==null)
      {
        return;
      }
      if (line.startsWith("0 "))
      {
        break;
      }
      if (line.startsWith("1 SEX"))
      {
        String sexString=line.substring(6).trim();
        Sex sex=Sex.getFromValue(sexString.charAt(0));
        p.setSex(sex);
      }
      else if (line.startsWith("1 NAME"))
      {
        handleName(line,p);
      }
      else if ((line.startsWith("1 BIRT"))||(line.startsWith("1 CHR")))
      {
        handleBirth(p);
      }
      else if (line.startsWith("1 DEAT"))
      {
        handleDeath(p);
      }
      else if (line.startsWith("1 OCCU"))
      {
        String occu=_lines.get(_index);
        String profession=occu.substring(6);
        OccupationForPerson o=new OccupationForPerson();
        o.setOccupation(profession);
        p.addOccupation(o);
      }
      else
      {
        LOGGER.debug(UNUSED_LINE,line);
      }
    }
    _storage.addPerson(p);
  }

  private Long parseIdLine(String idLine)
  {
    int atIndex=idLine.indexOf("@");
    if (atIndex!=-1) idLine=idLine.substring(atIndex+1);
    atIndex=idLine.indexOf("@");
    if (atIndex!=-1) idLine=idLine.substring(0,atIndex);
    idLine=idLine.replace("I","");
    Long key=NumericTools.parseLong(idLine);
    return key;
  }

  private void handleName(String line, Person p)
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

  private void handleBirth(Person p)
  {
    while (true)
    {
      String line=getNextLine();
      if (line==null)
      {
        return;
      }
      if ((line.startsWith("0 "))||(line.startsWith("1 ")))
      {
        _index--;
        return;
      }
      if (line.startsWith(LINE_2_DATE))
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
        Long key=_storage.getPlaceKey(tmp);
        DataProxy<Place> birthPlaceProxy=_dataSource.buildProxy(Place.class,key);
        p.setBirthPlaceProxy(birthPlaceProxy);
      }
    }
  }

  private void handleDeath(Person p)
  {
    while (true)
    {
      String line=getNextLine();
      if (line==null)
      {
        return;
      }
      if ((line.startsWith("0 "))||(line.startsWith("1 ")))
      {
        _index--;
        return;
      }
      if (line.startsWith(LINE_2_DATE))
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
        Long key=_storage.getPlaceKey(tmp);
        p.setDeathPlaceProxy(_dataSource.buildProxy(Place.class,key));
      }
    }
  }

  private String getNextLine()
  {
    _index++;
    if (_index<_maxIndex)
    {
      return _lines.get(_index);
    }
    return null;
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
          handleMarriage(u);
        }
        else if (line.startsWith("1 CHIL "))
        {
          handleChild(line,manKey,womanKey);
        }
        else
        {
          LOGGER.debug(UNUSED_LINE,line);
        }
      }
    }
    _storage.addUnion(u);
  }

  private void handleMarriage(Union u)
  {
    while(true)
    {
      String line=getNextLine();
      if (line==null)
      {
        return;
      }
      if (!line.startsWith("2 "))
      {
        _index--;
        return;
      }
      if (line.startsWith(LINE_2_DATE))
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
        Long key=_storage.getPlaceKey(tmp);
        u.setPlaceProxy(_dataSource.buildProxy(Place.class,key));
      }
    }
  }

  private void handleChild(String line, Long manKey, Long womanKey)
  {
    line=line.trim();
    String key=line.substring(8); /* "1 CHIL @" */
    Long childKey=decodePersonID(key);

    Person child=_storage.getPersonByGedcomKey(childKey);
    if (child!=null)
    {
        if (manKey!=null)
        {
          child.setFatherProxy(_dataSource.buildProxy(Person.class,manKey));
        }
        if (womanKey!=null)
        {
          child.setMotherProxy(_dataSource.buildProxy(Person.class,womanKey));
        }
    }
    else
    {
      LOGGER.warn("Person ID (child): {} not found!",childKey);
    }
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
}
