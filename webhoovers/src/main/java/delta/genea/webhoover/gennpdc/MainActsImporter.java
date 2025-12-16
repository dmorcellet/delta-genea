package delta.genea.webhoover.gennpdc;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.RawDataManager;
import delta.genea.data.Sex;
import delta.genea.data.places.PlacesDecoder;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.webhoover.expoactes.BirthAct;
import delta.genea.webhoover.expoactes.BirthActsIO;

/**
 * Imports acts from a file into the database.
 * @author DAM
 */
public class MainActsImporter
{
  private GeneaDataSource _dataSource;
  private RawDataManager _data;
  private PlacesDecoder _placesDecoder;
  private long _counter;
  private List<Person[]> _couples;

  private MainActsImporter()
  {
    _counter=1;
    _couples=new ArrayList<Person[]>();
  }

  private Person[] findCouple(String father, String mother, boolean doCreate)
  {
    String cfather=cleanupName(father);
    String cmother=cleanupName(mother);
    Person[] ret=null;
    for(Person[] couple : _couples)
    {
      Person fatherPerson=couple[0];
      String fatherName=fatherPerson.getLastName()+' '+fatherPerson.getFirstname();
      Person motherPerson=couple[1];
      String motherName=motherPerson.getLastName()+' '+motherPerson.getFirstname();
      if ((cfather.equals(fatherName)) && (cmother.equals(motherName)))
      {
        ret=couple;
        break;
      }
    }
    if ((doCreate) && (ret==null))
    {
      ret=new Person[2];
      ret[0]=buildPerson(cfather);
      ret[0].setSex(Sex.MALE);
      ret[1]=buildPerson(cmother);
      ret[1].setSex(Sex.FEMALE);
      _couples.add(ret);
    }
    return ret;
  }

  private Person buildPerson(String fullName)
  {
    int index=fullName.indexOf(' ');
    String lastName="";
    String firstName="";
    if (index!=-1)
    {
      lastName=fullName.substring(0,index).trim();
      firstName=fullName.substring(index+1).trim();
    }
    else
    {
      lastName=fullName.trim();
      firstName="";
    }
    Person p=new Person(Long.valueOf(_counter));
    p.setLastName(lastName);
    p.setFirstname(firstName);
    _counter++;
    _data.addPerson(p);
    return p;
  }

  private String cleanupName(String name)
  {
    int index=name.indexOf(',');
    if (index!=-1)
    {
      name=name.substring(0,index);
    }
    name=name.trim();
    return name;
  }

  private void importBirthAct(BirthAct act)
  {
    Long placeKey=_placesDecoder.getPlaceKey(act.getPlace());
    Person person=new Person(Long.valueOf(_counter));
    _counter++;
    person.setFirstname(act.getFirstName());
    person.setLastName(act.getLastName());
    Date date=act.getDate();
    if (date!=null)
    {
      person.setBirthDate(date,null);
      person.setBirthPlaceProxy(_dataSource.buildProxy(Place.class,placeKey));
    }
    Person[] parents=findCouple(act.getFather(),act.getMother(),true);
    Person father=parents[0];
    if (father!=null)
    {
      person.setFatherProxy(_dataSource.buildProxy(Person.class,father.getPrimaryKey()));
    }
    Person mother=parents[1];
    if (mother!=null)
    {
      person.setMotherProxy(_dataSource.buildProxy(Person.class,mother.getPrimaryKey()));
    }
    _data.addPerson(person);
  }

  private void doIt(File actsFile)
  {
    _dataSource=GeneaDataSource.getInstance("genea_tmp");
    _data=new RawDataManager(_dataSource.getObjectsSource());
    _placesDecoder=new PlacesDecoder(_data.getPlacesManager(),1);
    _placesDecoder.indicateFieldMeaning(1,PlacesDecoder.TOWN_NAME);

    List<BirthAct> acts=BirthActsIO.readActs(actsFile);
    if ((acts!=null) && (!acts.isEmpty()))
    {
      for(BirthAct act : acts)
      {
        importBirthAct(act);
      }
      
    }
    _data.writeDB();
  }

  /**
   * Main method for this tool.
   * @param args Input file.
   */
  public static void main(String[] args)
  {
    File actsFile=new File(args[0]);
    new MainActsImporter().doIt(actsFile);
  }
}
