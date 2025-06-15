package delta.genea.xml;

import java.util.ArrayList;
import java.util.List;

import delta.common.framework.objects.xml.ObjectXmlDriver;
import delta.genea.data.Cousinage;

/**
 * XML driver for cousinages.
 * @author DAM
 */
public class CousinageXMLDriver extends ObjectXmlDriver<Cousinage>
{
  @Override
  public List<Long> getRelatedObjectIDs(String relationName, Long primaryKey)
  {
    if (primaryKey==null)
    {
      throw new IllegalArgumentException("primaryKey is null");
    }
    List<Long> ret=null;
    if (relationName.equals(Cousinage.COUSINAGES_RELATION))
    {
      ret=getCousinagesForPerson(primaryKey.longValue());
    }
    return ret;
  }

  /**
   * Get the identifiers of the pictures with the person identified
   * by <code>primaryKey</code>.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of picture identifiers.
   */
  private List<Long> getCousinagesForPerson(long primaryKey)
  {
    List<Long> ret=new ArrayList<Long>();
    for(Cousinage cousinage : _objectsMgr.getCache().getAll())
    {
      long cousin1=cousinage.getCousin1ID();
      if (cousin1==primaryKey)
      {
        ret.add(cousinage.getPrimaryKey());
        continue;
      }
      long cousin2=cousinage.getCousin2ID();
      if (cousin2==primaryKey)
      {
        ret.add(cousinage.getPrimaryKey());
      }
    }
    return ret;
  }
}
