package delta.genea.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import delta.common.framework.objects.data.DataObject;
import delta.genea.misc.GeneaCfg;

/**
 * Picture.
 * @author DAM
 */
public class Picture extends DataObject<Picture>
{
  /**
   * Class name.
   */
  public static final String CLASS_NAME="PICTURE";
  public static final String PICTURES_FOR_PERSON_RELATION="PICTURES_FOR_PERSON";

  private String _title;
  private String _path;
  private String _commentaire;
  private List<PersonInPicture> _personsInPicture;

  @Override
  public String getClassName()
  {
    return CLASS_NAME;
  }

  /**
   * Constructor.
   * @param primaryKey Primary key.
   */
  public Picture(Long primaryKey)
  {
    super();
    setPrimaryKey(primaryKey);
    _title="";
    _path="";
    _commentaire="";
    _personsInPicture=new ArrayList<PersonInPicture>();
  }

  public String getTitle()
  {
    return _title;
  }

  public void setTitle(String title)
  {
    _title=title;
  }

  public String getPath()
  {
    return _path;
  }

  public void setPath(String path)
  {
    _path=path;
  }

  public String getComment()
  {
    return _commentaire;
  }

  public void setComment(String comment)
  {
    _commentaire=comment;
  }

  public List<PersonInPicture> getPersonsInPicture()
  {
    return _personsInPicture;
  }

  public void setPersonsInPicture(List<PersonInPicture> list)
  {
   _personsInPicture=list;
  }

  public String getPictureFilename()
  {
    if (_path==null)
    {
      return null;
    }
    StringBuffer sb=new StringBuffer(_path);
    sb.append(".jpg");
    String imgName=sb.toString();
    return imgName;
  }

  public boolean checkFiles()
  {
    File rootPath=GeneaCfg.getInstance().getPicturesRootPath();
    File file=new File(rootPath,getPictureFilename());
    if (!file.exists())
    {
      return false;
    }
    if (file.length()==0)
    {
      return false;
    }
    return true;
  }
}
