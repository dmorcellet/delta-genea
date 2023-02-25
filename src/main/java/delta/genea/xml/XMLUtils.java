package delta.genea.xml;

import org.xml.sax.helpers.AttributesImpl;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.Identifiable;
import delta.common.utils.io.xml.XmlWriter;

/**
 * Utility methods for XML I/O.
 * @author DAM
 */
public class XMLUtils
{
  /**
   * Write a proxy attribute.
   * @param <E> Type of managed proxy.
   * @param attrs XML attributes to write to.
   * @param name Attribute name.
   * @param proxy Proxy.
   */
  public static <E extends Identifiable<Long>> void writeProxy(AttributesImpl attrs, String name, DataProxy<E> proxy)
  {
    if (proxy!=null)
    {
      Long type=proxy.getPrimaryKey();
      if (type!=null)
      {
        attrs.addAttribute("","",name,XmlWriter.CDATA,type.toString());
      }
    }
  }
}
