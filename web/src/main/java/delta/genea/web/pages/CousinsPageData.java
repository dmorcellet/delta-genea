package delta.genea.web.pages;

import java.util.List;

import delta.common.framework.objects.data.ObjectsSource;
import delta.genea.data.Cousinage;
import delta.genea.data.Person;

/**
 * Gathers data needed for the 'cousins' page.
 * @author DAM
 */
public class CousinsPageData
{
  private Long _key;
  private Person _main;
  private List<Cousinage> _cousinages;

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
   * Get the list of cousinages.
   * @return a list of cousinages.
   */
  public List<Cousinage> getCousinages()
  {
    return _cousinages;
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

    _cousinages=source.loadRelation(Cousinage.class, Cousinage.COUSINAGES_RELATION, _key);
    return true;
  }
}
