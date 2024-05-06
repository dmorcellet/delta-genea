package delta.genea.gedcom;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectsSource;
import delta.common.framework.objects.sql.SqlObjectsSource;
import delta.common.utils.NumericTools;
import delta.common.utils.files.TextFileReader;
import delta.common.utils.misc.LatineNumbers;
import delta.common.utils.text.StringSplitter;
import delta.common.utils.text.ansel.AnselCharset;
import delta.common.utils.time.Month;
import delta.genea.GeneaApplication;
import delta.genea.data.GeneaDate;
import delta.genea.data.OccupationForPerson;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.PlaceManager;
import delta.genea.data.Sex;
import delta.genea.data.Union;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.time.FrenchRevolutionCalendar;
import delta.genea.time.FrenchRevolutionDate;
import delta.genea.time.FrenchRevolutionMonth;
import delta.genea.time.GregorianDate;

/**
 * Imports a GEDCOM file to a genea database.
 * @author DAM
 */
public class FromGEDCOM
{
  private static final Logger LOGGER=Logger.getLogger(FromGEDCOM.class);

  private static final String LINE_2_PLAC="2 PLAC ";
  private static final String LINE_2_DATE="2 DATE";
  private static final String FRENCH_REVOLUTION_DATE_SEED="@#DFRENCH R@";
  //private static final String JULIAN_DATE_SEED="@#DJULIAN@";

  /**
   * Main method for the GEDCOM import tool.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    GeneaApplication.getInstance();
    //new FromGEDCOM(new File("/home/dm/tmp/michel.ged"),"genea_michel");
    new FromGEDCOM(new File("D:\\dam\\Donnees\\docs\\genea\\maryvonne\\Lamour_31-10-2016.ged"),"genea_maryvonne");
    //new FromGEDCOM(new File("D:\\tmp\\Ninie au 20120925.ged"),"genea_ninie");
  }

  private File _fileName;
  private List<Person> _persons;
  private List<Union> _unions;
  private List<Place> _places;
  private PlaceManager _placeManager;
  private ObjectsSource _dataSource;
  private ArrayList<String> _lines;
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
    LOGGER.info("Time to import : "+(time2-time)+"ms");
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
    if (!fp.start()) return;

    String line;
    while (true)
    {
      line=fp.getNextLine();
      if (line==null) break;
      _lines.add(line);
    }
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
        if (LOGGER.isDebugEnabled())
        {
          LOGGER.debug("Ligne inexploitée ["+line+"]");
        }
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
                GeneaDate d=decodeDate(tmp);
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
                GeneaDate d=decodeDate(tmp);
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
          if (LOGGER.isDebugEnabled())
          {
            LOGGER.debug("Ligne inexploitée ["+line+"]");
          }
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
                GeneaDate d=decodeDate(tmp);
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
            System.err.println("Person ID: "+childKey+" not found!");
          }
        }
        // END OF CHIL
        else
        {
          if (LOGGER.isDebugEnabled())
          {
            LOGGER.debug("Ligne inexploitée ["+line+"]");
          }
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

  private GeneaDate decodeDate(String dateString)
  {
    GeneaDate ret=null;
    try
    {
      String stringToUse;
      String dateInfos="";
      Date date=null;
      int dateType=0;
      boolean isFrenchRevolutionDate=false;
      /*
      if (dateString.startsWith(JULIAN_DATE_SEED))
      {
        dateString=dateString.substring(JULIAN_DATE_SEED.length()).trim();
      }
      */
      if (dateString.startsWith("ABT"))
      {
        dateType=1;
        stringToUse=dateString.substring(3);
        dateInfos="ca";
      }
      else if (dateString.startsWith("AFT"))
      {
        dateType=2;
        stringToUse=dateString.substring(3);
        dateInfos="ap";
      }
      else if (dateString.startsWith("BEF"))
      {
        dateType=3;
        stringToUse=dateString.substring(3);
        dateInfos="av";
      }
      else if (dateString.startsWith(FRENCH_REVOLUTION_DATE_SEED))
      {
        dateType=0;
        stringToUse=dateString.substring(FRENCH_REVOLUTION_DATE_SEED.length()).trim();
        isFrenchRevolutionDate=true;
      }
      else
      {
        stringToUse=dateString;
      }
      stringToUse=stringToUse.trim();
      String[] strings=StringSplitter.split(stringToUse,' ');

      if (isFrenchRevolutionDate)
      {
        try
        {
          int day=NumericTools.parseInt(strings[0],-1);
          FrenchRevolutionMonth month=getMonth(strings[1]);
          int year=LatineNumbers.convert(strings[3]);
          if ((day!=-1) && (month!=null) && (year>0))
          {
            FrenchRevolutionDate frDate=new FrenchRevolutionDate((byte)day,month,year);
            GregorianDate gDate=FrenchRevolutionCalendar.convert(frDate);
            Calendar c=Calendar.getInstance();
            c.set(gDate.getYear(),gDate.getMonth()-1,gDate.getDayOfMonth());
            date=c.getTime();
          }
          else
          {
            dateInfos=stringToUse;
          }
        }
        catch(Exception e)
        {
          LOGGER.error("Unable to decode French Revolution date : "+stringToUse,e);
          dateInfos=stringToUse;
        }
      }
      else if (strings.length==4)
      {
        try
        {
          dateInfos=stringToUse;
          if (("BET".equals(strings[0])) && ("AND".equals(strings[2])))
          {
            int year1=NumericTools.parseInt(strings[1],0);
            int year2=NumericTools.parseInt(strings[3],0);

            if ((year1>0) && (year2>0))
            {
              dateInfos=year1+"/"+year2;
            }
            else
            {
              LOGGER.error("Invalid years in a between ["+stringToUse+"]");
            }
          }
          else
          {
            LOGGER.error("Bad 'between' structure ["+stringToUse+"]");
          }
        }
        catch (Exception e)
        {
          LOGGER.error("Unable to decode GEDCOM date : "+stringToUse,e);
        }
      }
      else if (strings.length==3)
      {
        // Full date ?
        try
        {
          int day=NumericTools.parseInt(strings[0],1);
          int month=Month.decodeEnglishMonth(strings[1]);
          int year=NumericTools.parseInt(strings[2],0);
          if ((day>0)&&(month>0)&&(year>0))
          {
            if (dateType==0)
            {
              Calendar c=Calendar.getInstance();
              c.set(year,month-1,day);
              date=c.getTime();
            }
            else
            {
              StringBuilder sb=new StringBuilder();
              sb.append(day);
              sb.append('-');
              sb.append(month);
              sb.append('-');
              sb.append(year);
              sb.append(dateInfos);
              dateInfos=sb.toString();
            }
          }
        }
        catch (Exception e)
        {
          dateInfos=stringToUse;
          LOGGER.error("Unable to decode GEDCOM date : "+stringToUse,e);
        }
      }
      else if (strings.length==2)
      {
        int month=Month.decodeEnglishMonth(strings[0]);
        int year=Integer.parseInt(strings[1]);
        StringBuilder sb=new StringBuilder();
        sb.append(month);
        sb.append('-');
        sb.append(year);
        sb.append(dateInfos);
        dateInfos=sb.toString()+dateInfos;
      }
      else if (strings.length==1)
      {
        dateInfos=strings[0]+dateInfos;
      }
      else
      {
        LOGGER.error("Pb : number of spaces="+strings.length+" ["+dateString+"]");
        dateInfos=dateString;
      }
      ret=new GeneaDate(date,dateInfos);
    }
    catch (Exception e)
    {
      LOGGER.error("Cannot parse date : "+dateString,e);
      ret=new GeneaDate((Long)null,dateString);
    }
    return ret;
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

  private FrenchRevolutionMonth getMonth(String month)
  {
    FrenchRevolutionMonth ret=null;
    if ("BRUM".equals(month)) ret=FrenchRevolutionMonth.BRUMAIRE;
    if ("FLOR".equals(month)) ret=FrenchRevolutionMonth.FLOREAL;
    if ("FRIM".equals(month)) ret=FrenchRevolutionMonth.FRIMAIRE;
    if ("FRUC".equals(month)) ret=FrenchRevolutionMonth.FRUCTIDOR;
    if ("GERM".equals(month)) ret=FrenchRevolutionMonth.GERMINAL;
    if ("MESS".equals(month)) ret=FrenchRevolutionMonth.MESSIDOR;
    if ("NIVO".equals(month)) ret=FrenchRevolutionMonth.NIVOSE;
    if ("PLUV".equals(month)) ret=FrenchRevolutionMonth.PLUVIOSE;
    if ("PRAI".equals(month)) ret=FrenchRevolutionMonth.PRAIRIAL;
    if ("THER".equals(month)) ret=FrenchRevolutionMonth.THERMIDOR;
    if ("VEND".equals(month)) ret=FrenchRevolutionMonth.VENDEMIAIRE;
    if ("VENT".equals(month)) ret=FrenchRevolutionMonth.VENTOSE;
    return ret;
  }
}
