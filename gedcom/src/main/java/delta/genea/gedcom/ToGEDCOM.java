package delta.genea.gedcom;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.framework.objects.data.DataObject;
import delta.common.utils.files.TextFileWriter;
import delta.common.utils.text.StringSplitter;
import delta.genea.data.OccupationForPerson;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.PlaceLevel;
import delta.genea.data.Union;
import delta.genea.data.sources.GeneaDataSource;

/**
 * Write a GEDCOM file from database data.
 * @author DAM
 */
public class ToGEDCOM
{
  private static final Logger LOGGER=LoggerFactory.getLogger(ToGEDCOM.class);

  private Calendar _calendar;
  private String[] _months={"JAN","FEB","MAR","APR",
      "MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};

  /**
   * Constructor.
   */
  public ToGEDCOM()
  {
    _calendar=Calendar.getInstance();
  }

  /**
   * Transformation d'une date complète ou partielle du format Interne GENEA au format GEDCOM :
   * Format de départ :
   *    JJ-MM-AAAA : date complète
   *    MM-AAAA : mois et année
   *    AAAA : année
   * Algorithme :
   *    - On calcule de nombre de séparateurs '-' :
   *    - Si nbSeparateurs>2 : chaineTransformee=chaineEncodee
   *    - Sinon, on évalue les parties :
   *         - les jours sont transformés en "0" si non numériques. Valeur non transformée sinon (sauf recentrage sur 0-255 !).
   *         - les années sont transformées en "0" si non numériques. Valeur non transformée sinon.
   *         - les mois sont transformés en trigramme anglais si numériques et compris entre 1 et 12, sinon "???".
   *    - On recolle les morceaux transformés dans l'ordre avec " " comme séparateur.
   *    - Exemples :
   *           JJ-MM-AAAA : "12-04-2003" -> "12 APR 2003"
   *           MM-AAAA : "05-2002" -> "MAY 2002"
   *                     "ffg-2003" -> "??? 2003"
   *           AAAA : "fff" -> "0"
   *                  "2000" -> "2000"
   *           aaa-bbb-ccc-ddd : "a-b-c-d" -> "a-b-c-d" + message d'erreur !
   *
   * @param date
   * @return GEDCOM encoded date from internal info-date string.
   */
  private String buildDate(String date)
  {
    String[] parts=StringSplitter.split(date,'-');

    StringBuilder sb=new StringBuilder();

    if (parts.length==3)
    {
      // Full date
      sb.append(parts[0]);
      sb.append(' ');
      int month=Integer.parseInt(parts[1]);
      sb.append(_months[month-1]);
      sb.append(' ');
      sb.append(parts[2]);
    }
    else if (parts.length==2)
    {
      // Month+year
      int month=Integer.parseInt(parts[0]);
      sb.append(_months[month-1]);
      sb.append(' ');
      sb.append(parts[1]);
    }
    else if (parts.length==1)
    {
      // Year
      sb.append(parts[0]);
    }
    else
    {
      LOGGER.error("Pb:nbParts="+parts.length);
      sb.append(date);
    }
    return sb.toString();
  }

  /**
   * Encodage d'une date complète ou d'informations sur une date vers le format GEDCOM.
   * date : date complète à traduire
   * infosDate : date incomplète ou renseignements sur une date
   * chaineEncodee : résultat de l'encodage GEDCOM.
   *
   * Si la date complète est renseignée :
   *    - Exemple : 12 Avril 2003 -> "12 APR 2003"
   *    - Retour = VRAI
   * Si la date complète n'est pas renseignée, on travaille sur infosDate :
   *    - Si infosDate est vide on retourne FAUX, sinon :
   *    - On enlève un éventuel "?" final
   *    - On recherche un "/" séparateur
   *    - Si on le trouve, c'est une information de type "entre date1 et date2", donc on
   *      décode les 2 morceaux grâce à la méthode TransformerDate et on construit la phrase GEDCOM
   *          "BET dateTransformee1 AND dateTransformee2"
   *    - Sinon : on recherche des termes terminaux "av", "ap" ou "ca". Si on en trouve un, on transforme
   *      la date qui précède grâce à la méthode TransformerDate et on précède la date transformée du
   *      mot clé GEDCOM ad hoc :
   *         - "DATEav" => "BEF dateTransformee"
   *         - "DATEap" => "AFT dateTransformee"
   *         - "DATEca" => "ABT dateTransformee"
   *    - Sinon, on considère que c'est une information incomplète mais certaine, donc on applique juste
   *      la méthode TransformerDate.
   *    - Retour = VRAI
   *
   * Exemples :
   *       - "10-1688/04-1689" => "BET OCT 1688 AND APR 1689"
   *       - "1752ca" => "ABT 1752"
   *       - "01-10-1987ap" => "AFT 01 OCT 1987"
   *       - "1973av" => "BEF 1973"
   *       - "1789" => "1789"
   * @param dateInfos
   * @param date
   * @return GEDCOM encoded date
   */
  private String encodeDate(String dateInfos, Long date)
  {
    StringBuilder sb=new StringBuilder();
    if (date!=null)
    {
      _calendar.setTimeInMillis(date.longValue());
      sb.append(_calendar.get(Calendar.DAY_OF_MONTH));
      sb.append(' ');
      sb.append(_months[_calendar.get(Calendar.MONTH)]);
      sb.append(' ');
      sb.append(_calendar.get(Calendar.YEAR));
    }
    else if ((dateInfos!=null) && (dateInfos.length()>0))
    {
      if (dateInfos.endsWith("?"))
      {
        dateInfos=dateInfos.substring(0,dateInfos.length()-1);
      }

      // Decipher what kind of date we have
      // (precise, about, before, after, between)
      int slash=dateInfos.indexOf('/');
      if (slash>=0)
      {
        String firstDate=dateInfos.substring(0,slash);
        String secondDate=dateInfos.substring(slash+1,dateInfos.length());
        sb.append("BET ");
        sb.append(buildDate(firstDate));
        sb.append(" AND ");
        sb.append(buildDate(secondDate));
      }
      else if (dateInfos.endsWith("av"))
      {
        String tmpDate=dateInfos.substring(0,dateInfos.length()-2);
        sb.append("BEF ");
        sb.append(buildDate(tmpDate));
      }
      else if (dateInfos.endsWith("ap"))
      {
        String tmpDate=dateInfos.substring(0,dateInfos.length()-2);
        sb.append("AFT ");
        sb.append(buildDate(tmpDate));
      }
      else if (dateInfos.endsWith("ca"))
      {
        String tmpDate=dateInfos.substring(0,dateInfos.length()-2);
        sb.append("ABT ");
        sb.append(buildDate(tmpDate));
      }
      else
      {
        sb.append(buildDate(dateInfos));
      }
    }
    return sb.toString();
  }

  private String encodePlace(Place place)
  {
    String ret=null;
    if (place!=null)
    {
      StringBuilder sb=new StringBuilder();
      Place current=place.getParentPlaceForLevel(PlaceLevel.TOWN);
      if (current!=null) sb.append(current.getName());
      sb.append(',');
      current=place.getParentPlaceForLevel(PlaceLevel.DEPARTMENT);
      if (current!=null) sb.append(current.getName());
      sb.append(',');
      current=place.getParentPlaceForLevel(PlaceLevel.COUNTRY);
      if (current!=null) sb.append(current.getName());

      ret=sb.toString();
    }
    return ret;
  }

  private List<Union> getParentsFamilyForEachPerson(List<Person> persons, List<Union> unions)
  {
    List<Union> ret=new ArrayList<Union>();
    long unionKey=5000;

    for(Person p : persons)
    {
      Long fatherKey=p.getFatherKey();
      Long motherKey=p.getMotherKey();
      if ((DataObject.isNotNull(fatherKey)) || (DataObject.isNotNull(motherKey)))
      {
        boolean found=false;
        for(Union u : unions)
        {
          if ((DataObject.keysAreEqual(u.getManKey(),fatherKey)) ||
              (DataObject.keysAreEqual(u.getWomanKey(),motherKey)))
          {
            ret.add(u);
            found=true;
            break;
          }
        }
        if (!found)
        {
          Union newUnion=new Union(Long.valueOf(unionKey));
          unionKey++;
          newUnion.setManProxy(p.getFatherProxy());
          newUnion.setWomanProxy(p.getMotherProxy());
          unions.add(newUnion);
          ret.add(newUnion);
        }
      }
      else
      {
        ret.add(new Union(null));
      }
    }
    return ret;
  }

  private List<List<Union>> getFamiliesListForEachPerson(List<Person> persons, List<Union> unions)
  {
    List<List<Union>> ret=new ArrayList<List<Union>>();

    for(Person p : persons)
    {
      List<Union> families=null;
      for(Union u : unions)
      {
        if (DataObject.keysAreEqual(u.getManKey(),p.getPrimaryKey())) // Only men?
        {
          if (families==null)
          {
            families=new ArrayList<Union>();
          }
          families.add(u);
        }
	  }
      ret.add(families);
    }
    return ret;
  }

  private List<List<Person>> getChildrenListForEachUnion(List<Person> persons, List<Union> unions)
  {
    List<List<Person>> ret=new ArrayList<List<Person>>();

    for(Union u : unions)
    {
      List<Person> children=null;
      for(Person p : persons)
      {
        if ((DataObject.keysAreEqual(p.getFatherKey(),u.getManKey())) &&
            (DataObject.keysAreEqual(p.getMotherKey(),u.getWomanKey())))
        {
          if (children==null)
          {
            children=new ArrayList<Person>();
          }
          children.add(p);
        }
      }
      ret.add(children);
    }
    return ret;
  }

  private void writeDateAndPlace(TextFileWriter to, String type, String date, String place)
  {
    if ((date!=null) && (date.length()>0))
    {
      String typeLine="1 "+type;
      to.writeNextLine(typeLine);
      String dateLine="2 DATE "+date;
      to.writeNextLine(dateLine);
      if ((place!=null) && (place.length()>0))
      {
        String placeLine="2 PLAC "+place;
        to.writeNextLine(placeLine);
      }
    }
  }

  private void writeGEDCOMFile(File filename,
      List<Person> persons, List<Union> unions,
      List<List<Union>> families, List<Union> parents,
      List<List<Person>> childrens)
  {
    TextFileWriter writer=new TextFileWriter(filename,"ISO8859-1");
    if (writer.start())
    {
      writer.writeNextLine("0 HEAD");
      writer.writeNextLine("1 SOUR GENEA");

      int index=0;
      for(Person p : persons)
      {
        try
        {
          String id="0 @"+p.getPrimaryKey();
          writer.writeNextLine(id+"@ INDI");
          String name="1 NAME "+p.getFirstname()+'/'+p.getLastName()+"/";
          writer.writeNextLine(name);
          String givenName="2 GIVN "+p.getFirstname();
          writer.writeNextLine(givenName);
          String surName="2 SURN "+p.getLastName();
          writer.writeNextLine(surName);
          String sex="1 SEX "+p.getSex().getValue();
          writer.writeNextLine(sex);

          // Professions
          List<OccupationForPerson> occupations=p.getOccupations();
          if ((occupations!=null) && (!occupations.isEmpty()))
          {
            int nbOccupations=occupations.size();
            StringBuilder sb=new StringBuilder("1 OCCU ");
            for(int i=0;i<nbOccupations;i++)
            {
              if (i>0) sb.append(", ");
              sb.append(occupations.get(i).getLabel());
            }
            writer.writeNextLine(sb.toString());
          }

          // Birth date
          String birthDate=encodeDate(p.getBirthInfos(),p.getBirthDate());
          String birthPlace=encodePlace(p.getBirthPlace());
          writeDateAndPlace(writer,"BIRT",birthDate,birthPlace);

          // Death date
          String deathDate=encodeDate(p.getDeathInfos(),p.getDeathDate());
          String deathPlace=encodePlace(p.getDeathPlace());
          writeDateAndPlace(writer,"DEAT",deathDate,deathPlace);

          // Families
          List<Union> familiesForPerson=families.get(index);
          if (familiesForPerson!=null)
          {
            String familyLine;
            for(Union u : familiesForPerson)
            {
              familyLine="1 FAMS @"+u.getPrimaryKey()+"@";
              writer.writeNextLine(familyLine);
            }
          }
          // FAMC
          Union parentsFamily=parents.get(index);
          Long pk=(parentsFamily!=null)?parentsFamily.getPrimaryKey():null;
          if (pk!=null)
          {
            String familyLine="1 FAMC @"+pk.longValue()+"@";
            writer.writeNextLine(familyLine);
          }
        }
        catch(Exception e)
        {
          LOGGER.error("Pb avec personne : "+p.getPrimaryKey()+": "+p.getFullName(),e);
        }
        index++;
      }

      int indexUnions=0;
      for(Union u : unions)
      {
        try
        {
          String id="0 @"+u.getPrimaryKey()+"@ FAM";
          writer.writeNextLine(id);
          Long manKey=u.getManKey();
          Long womanKey=u.getWomanKey();
          if (DataObject.isNotNull(manKey))
          {
            String manLine="1 HUSB @"+manKey+"@";
            writer.writeNextLine(manLine);
          }
          if (DataObject.isNotNull(womanKey))
          {
            String womanLine="1 WIFE @"+womanKey+"@";
            writer.writeNextLine(womanLine);
          }

          // Children
          List<Person> childs=childrens.get(indexUnions);
          if (childs!=null)
          {
            String childLine;
            for(Person child : childs)
            {
              childLine="1 CHIL @"+child.getPrimaryKey()+"@";
              writer.writeNextLine(childLine);
            }
            String nbChildsLine="1 NCHI "+Integer.toString(childs.size());
            writer.writeNextLine(nbChildsLine);
          }
          else
          {
            writer.writeNextLine("1 NCHI 0");
          }

          String unionDate=encodeDate(u.getInfos(),u.getDate());
          String unionPlace=encodePlace(u.getPlace());
          writeDateAndPlace(writer,"MARR",unionDate,unionPlace);
        }
        catch(Exception e)
        {
          Person man=u.getMan();
          String label="";
          if (man!=null) label=man.getFullName();
          Person woman=u.getWoman();
          if (woman!=null)
          {
            if (label.length()>0) label=label+"/";
            label=label+woman.getFullName();
          }
          LOGGER.error("Pb avec union "+label,e);
        }
        indexUnions++;
      }
      writer.writeNextLine("0 TRLR");
      writer.terminate();
    }
  }

  private void go(File fileName, List<Person> persons, List<Union> unions)
  {
    // Add missing families and get parent's family for each person
    List<Union> parentsFamily=getParentsFamilyForEachPerson(persons,unions);
    // Build family list for each person
    List<List<Union>> families=getFamiliesListForEachPerson(persons, unions);
    // Build children list for each family
    List<List<Person>> familyToChildren=getChildrenListForEachUnion(persons,unions);

    try
    {
      writeGEDCOMFile(fileName,persons,unions,families,parentsFamily,familyToChildren);
    }
    catch(Exception e)
    {
      LOGGER.error("",e);
    }
  }

  /**
   * Do the job.
   * @param dataSource Source to use.
   * @param gedcomFile File to write.
   */
  public void go(GeneaDataSource dataSource, File gedcomFile)
  {
    try
    {
      List<Person> persons=dataSource.getManager(Person.class).loadAll();
      List<Union> unions=dataSource.getManager(Union.class).loadAll();
      System.out.println("Loaded "+persons.size()+" persons.");
      System.out.println("Loaded "+unions.size()+" unions.");
      go(gedcomFile,persons,unions);
      dataSource.close();
    }
    catch (Exception e)
    {
      LOGGER.error("Error!",e);
    }
  }
}
