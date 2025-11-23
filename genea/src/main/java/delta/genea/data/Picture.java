package delta.genea.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import delta.common.framework.objects.data.DataObject;
import delta.genea.ActFileResolver;
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
  /**
   * Relation that gives the pictures for a person.
   */
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

  /**
   * Get the title of this picture.
   * @return A title.
   */
  public String getTitle()
  {
    return _title;
  }

  /**
   * Set the title of this picture.
   * @param title Title to set.
   */
  public void setTitle(String title)
  {
    _title=title;
  }

  /**
   * Get the path of this picture.
   * @return A path.
   */
  public String getPath()
  {
    return _path;
  }

  /**
   * Set the path of this picture.
   * @param path Path to set.
   */
  public void setPath(String path)
  {
    _path=path;
  }

  /**
   * Set the comment of this picture.
   * @return A comment string.
   */
  public String getComment()
  {
    return _commentaire;
  }

  /**
   * Set the comment of this picture.
   * @param comment Comment to set.
   */
  public void setComment(String comment)
  {
    _commentaire=comment;
  }

  /**
   * Get all the persons in this picture.
   * @return A list of persons in picture.
   */
  public List<PersonInPicture> getPersonsInPicture()
  {
    return _personsInPicture;
  }

  /**
   * Set the list of persons in this picture.
   * @param list List to set.
   */
  public void setPersonsInPicture(List<PersonInPicture> list)
  {
   _personsInPicture=list;
  }

  /**
   * Get the filename of a picture part.
   * @param index Index of picture part (starting at 1).
   * @return A filename.
   */
  public String getPictureFilename(int index)
  {
    if (_path==null)
    {
      return null;
    }
    File rootDir=GeneaCfg.getInstance().getPicturesRootPath();
    return ActFileResolver.resolveFilename(rootDir,_path,index);
  }

  /**
   * Get the file path of a picture part.
   * @param index Index of picture part (starting at 1).
   * @return A file path.
   */
  public File getPictureFile(int index)
  {
    String imageName=getPictureFilename(index);
    File rootPath=GeneaCfg.getInstance().getPicturesRootPath();
    File file=new File(rootPath,imageName);
    return file;
  }

  /**
   * Check if the file for this picture exists or not.
   * @return <code>true</code> if it exists, <code>false</code> otherwise.
   */
  public boolean checkFiles()
  {
    File rootPath=GeneaCfg.getInstance().getPicturesRootPath();
    File file=new File(rootPath,getPictureFilename(0));
    return ((file.exists()) && (file.length()>0));
  }
}
