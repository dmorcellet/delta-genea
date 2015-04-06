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
  public int _placeId;
  /**
   * Package index.
   */
  public int _packageIndex;
  /**
   * Index of the first page (starting at 1).
   */
  public int _minPageIndex;
  /**
   * Index of the last page.
   */
  public int _maxPageIndex;
  /**
   * Name of files to generate ("xxx.jpg").
   */
  public String _name;
  
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
