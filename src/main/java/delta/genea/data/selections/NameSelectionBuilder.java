package delta.genea.data.selections;

import java.util.List;

import delta.common.framework.objects.data.ObjectSource;
import delta.genea.data.Person;
import delta.genea.data.sources.GeneaDataSource;

/**
 * Selection made of person with a given name.
 * @author DAM
 */
public class NameSelectionBuilder implements SelectionBuilder<Person>
{
  private String _dbName;
  private String _name;

  /**
   * Constructor.
   * @param dbName Source database.
   * @param namePattern Name to select.
   */
  public NameSelectionBuilder(String dbName, String namePattern)
  {
    _dbName=dbName;
    _name=namePattern;
  }
  
  public Selection<Person> build()
  {
    GeneaDataSource dataSource=GeneaDataSource.getInstance(_dbName);
    ObjectSource<Person> personsSource=dataSource.getPersonDataSource();
    List<Person> list=personsSource.loadObjectSet(Person.NAME_SET,new Object[]{_name});
    String selectionName="Personne dont le patronyme est "+_name;
    BasicSelection<Person> ret=new BasicSelection<Person>(selectionName);
    for(Person p : list)
    {
      ret.addObject(p);
    }
    return ret;
  }
}
