package delta.genea.web.pages;

import java.util.List;

import delta.genea.data.Person;
import delta.genea.data.sources.GeneaDataSource;

/**
 * Gathers data needed for the 'cousins' page.
 * @author DAM
 */
public class CousinsPageData
{
  private long _key;
  private Person _main;
  private List<Person> _cousins;

  /**
   * Constructor.
   * @param key Main person key.
   */
  public CousinsPageData(long key)
  {
    _key=key;
  }

  /**
   * Get the main person.
   * @return the main person.
   */
  public Person getMainPerson()
  {
    return _main;
  }

  /**
   * Get the list of cousins.
   * @return a list of persons.
   */
  public List<Person> getCousins()
  {
    return _cousins;
  }

  /**
   * Load data from the given data source.
   * @param source Source to use.
   * @return <code>true</code> if data was successfully fetched,
   * <code>false</code> otherwise.
   */
  public boolean load(GeneaDataSource source)
  {
    _main=source.getPersonDataSource().load(_key);
    if (_main==null)
    {
      return false;
    }

    _cousins=source.getPersonDataSource().loadRelation(Person.COUSINS_RELATION, _key);
    return true;
  }
}
