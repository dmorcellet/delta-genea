package delta.genea.web.pages;

import java.util.List;

import delta.common.framework.objects.data.ObjectsSource;
import delta.genea.data.Person;

/**
 * Gathers data needed for the 'cousins' page.
 * @author DAM
 */
public class CousinsPageData
{
  private Long _key;
  private Person _main;
  private List<Person> _cousins;

  /**
   * Constructor.
   * @param key Main person key.
   */
  public CousinsPageData(Long key)
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
  public boolean load(ObjectsSource source)
  {
    _main=source.load(Person.class,_key);
    if (_main==null)
    {
      return false;
    }

    _cousins=source.loadRelation(Person.class, Person.COUSINS_RELATION, _key);
    return true;
  }
}
