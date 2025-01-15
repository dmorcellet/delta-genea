package delta.genea.webhoover.gennpdc;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import delta.common.framework.objects.data.ObjectsManager;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.PlaceManager;
import delta.genea.data.Sex;
import delta.genea.data.Union;
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
    _data.getPersons().add(p);
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
    PlaceManager p=_data.getPlacesManager();
    Long placeKey=p.decodePlaceName(act.getPlace());
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
    _data.getPersons().add(person);
  }

  private void doIt(File actsFile)
  {
    _dataSource=GeneaDataSource.getInstance("genea_tmp");
    _data=new RawDataManager(_dataSource.getObjectsSource());
    List<BirthAct> acts=BirthActsIO.readActs(actsFile);
    if ((acts!=null) && (!acts.isEmpty()))
    {
      for(BirthAct act : acts)
      {
        importBirthAct(act);
      }
      
    }
    writeDB();
  }

  private void writeDB()
  {
    // Persons
    ObjectsManager<Person> dSource=_dataSource.getManager(Person.class);
    List<Person> persons=_data.getPersons();
    int nbPersons=persons.size();
    for(int i=0;i<nbPersons;i++)
    {
      dSource.create(persons.get(i));
    }
    // Unions
    List<Union> unions=_data.getUnions();
    ObjectsManager<Union> uSource=_dataSource.getManager(Union.class);
    int nbUnions=unions.size();
    for(int i=0;i<nbUnions;i++)
    {
      uSource.create(unions.get(i));
    }
    // Places
    List<Place> places=_data.getPlaces();
    ObjectsManager<Place> pSource=_dataSource.getManager(Place.class);
    int nbPlaces=places.size();
    for(int i=0;i<nbPlaces;i++)
    {
      pSource.create(places.get(i));
    }
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
