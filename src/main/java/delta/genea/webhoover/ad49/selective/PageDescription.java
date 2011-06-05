package delta.genea.webhoover.ad49.selective;

/**
 * @author dm
 */
public class PageDescription
{
  public int _placeId;
  public int _packageIndex;
  public int _minPageIndex;
  public int _maxPageIndex;
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
