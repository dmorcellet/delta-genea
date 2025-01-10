package delta.genea.webhoover.ad49.selective;

/**
 * Page description.
 * @author DAM
 */
public class PageDescription
{
  /**
   * Place identifier.
   */
  private int _placeId;
  /**
   * Package index.
   */
  private int _packageIndex;
  /**
   * Index of the first page (starting at 1).
   */
  private int _minPageIndex;
  /**
   * Index of the last page.
   */
  private int _maxPageIndex;
  /**
   * Name of files to generate ("xxx.jpg").
   */
  private String _name;

  /**
   * @return the placeId
   */
  public int getPlaceId()
  {
    return _placeId;
  }

  /**
   * @param placeId the placeId to set
   */
  public void setPlaceId(int placeId)
  {
    _placeId=placeId;
  }

  /**
   * @return the packageIndex
   */
  public int getPackageIndex()
  {
    return _packageIndex;
  }

  /**
   * @param packageIndex the packageIndex to set
   */
  public void setPackageIndex(int packageIndex)
  {
    _packageIndex=packageIndex;
  }

  /**
   * @return the minPageIndex
   */
  public int getMinPageIndex()
  {
    return _minPageIndex;
  }

  /**
   * @param minPageIndex the minPageIndex to set
   */
  public void setMinPageIndex(int minPageIndex)
  {
    _minPageIndex=minPageIndex;
  }

  /**
   * @return the maxPageIndex
   */
  public int getMaxPageIndex()
  {
    return _maxPageIndex;
  }

  /**
   * @param maxPageIndex the maxPageIndex to set
   */
  public void setMaxPageIndex(int maxPageIndex)
  {
    _maxPageIndex=maxPageIndex;
  }

  /**
   * @return the name
   */
  public String getName()
  {
    return _name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name)
  {
    _name=name;
  }

  @Override
  public String toString()
  {
    StringBuilder sb=new StringBuilder();
    sb.append("name=").append(_name).append(" / ");
    sb.append("placeId=").append(_placeId).append(" / ");
    sb.append("packageId=").append(_packageIndex).append(" / ");
    sb.append("minPage=").append(_minPageIndex).append(" / ");
    sb.append("maxPage=").append(_maxPageIndex);
    return sb.toString();
  }
}
