package delta.genea.webhoover.gennpdc;

import java.util.ArrayList;
import java.util.List;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectSource;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.Sex;
import delta.genea.data.Union;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.gedcom.PlaceManager;

/**
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
    String lastName="",firstName="";
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
    Person p=new Person(_counter,_dataSource.getPersonDataSource());
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
    long placeKey=p.decodePlaceName(act._place);
    Person person=new Person(_counter,_dataSource.getPersonDataSource());
    _counter++;
    person.setFirstname(act._firstName);
    person.setLastName(act._lastName);
    if (act._date!=null)
    {
      person.setBirthDate(act._date,null);
      person.setBirthPlaceProxy(new DataProxy<Place>(placeKey,_dataSource.getPlaceDataSource()));
    }
    Person[] parents=findCouple(act._father,act._mother,true);
    if (parents!=null)
    {
      Person father=parents[0];
      if (father!=null)
      {
        person.setFatherProxy(new DataProxy<Person>(father.getPrimaryKey(),father.getSource()));
      }
      Person mother=parents[1];
      if (mother!=null)
      {
        person.setMotherProxy(new DataProxy<Person>(mother.getPrimaryKey(),mother.getSource()));
      }
    }
    
    _data.getPersons().add(person);
  }

  private void doIt()
  {
    _dataSource=GeneaDataSource.getInstance("genea_tmp");
    _data=new RawDataManager(_dataSource);
    List<BirthAct> acts=BirthActsIO.readActs(Constants.ACTS_FILE);
    if ((acts!=null) && (acts.size()>0))
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
    ObjectSource<Person> dSource=_dataSource.getPersonDataSource();
    List<Person> persons=_data.getPersons();
    int nbPersons=persons.size();
    for(int i=0;i<nbPersons;i++)
    {
      dSource.create(persons.get(i));
    }
    // Unions
    List<Union> unions=_data.getUnions();
    ObjectSource<Union> uSource=_dataSource.getUnionDataSource();
    int nbUnions=unions.size();
    for(int i=0;i<nbUnions;i++)
    {
      uSource.create(unions.get(i));
    }
    // Places
    List<Place> places=_data.getPlaces();
    ObjectSource<Place> pSource=_dataSource.getPlaceDataSource();
    int nbPlaces=places.size();
    for(int i=0;i<nbPlaces;i++)
    {
      pSource.create(places.get(i));
    }
  }
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    new MainActsImporter().doIt();
  }
}
