package delta.genea.web.pages;

import java.util.List;

import delta.genea.data.Person;
import delta.genea.data.sources.GeneaDataSource;

public class CousinsPageData
{
  private long _key;
  private Person _main;
  private List<Person> _cousins;

  public CousinsPageData(long key)
  {
    _key=key;
  }

  public Person getMainPerson()
  {
    return _main;
  }

  public List<Person> getCousins()
  {
    return _cousins;
  }

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
