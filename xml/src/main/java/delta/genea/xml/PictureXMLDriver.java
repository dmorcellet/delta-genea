package delta.genea.xml;

import java.util.ArrayList;
import java.util.List;

import delta.common.framework.objects.xml.ObjectXmlDriver;
import delta.genea.data.PersonInPicture;
import delta.genea.data.Picture;

/**
 * XML driver for pictures.
 * @author DAM
 */
public class PictureXMLDriver extends ObjectXmlDriver<Picture>
{
  @Override
  public List<Long> getRelatedObjectIDs(String relationName, Long primaryKey)
  {
    if (primaryKey==null)
    {
      throw new IllegalArgumentException("primaryKey is null");
    }
    List<Long> ret=null;
    if (relationName.equals(Picture.PICTURES_FOR_PERSON_RELATION))
    {
      ret=getPicturesForPerson(primaryKey.longValue());
    }
    return ret;
  }

  /**
   * Get the identifiers of the pictures with the person identified
   * by <code>primaryKey</code>.
   * @param primaryKey Identifier of the targeted person.
   * @return A list of picture identifiers.
   */
  private List<Long> getPicturesForPerson(long primaryKey)
  {
    List<Long> ret=new ArrayList<Long>();
    for(Picture picture : _objectsMgr.getCache().getAll())
    {
      for(PersonInPicture pip : picture.getPersonsInPicture())
      {
        Long personKey=pip.getPersonProxy().getPrimaryKey();
        if ((personKey!=null) && (personKey.longValue()==primaryKey))
        {
          ret.add(picture.getPrimaryKey());
          break;
        }
      }
    }
    return ret;
  }
}
