package delta.genea.xml;

import java.util.Date;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.helpers.AttributesImpl;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.Identifiable;
import delta.common.utils.io.xml.XmlWriter;
import delta.common.utils.xml.DOMParsingTools;
import delta.genea.data.GeneaDate;

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

  /**
   * Parse a genea date.
   * @param attrs Attributes to read from.
   * @param dateAttrName Timestamp attribute.
   * @param dateInfosAttr Date infos attribute.
   * @return the loaded genea date.
   */
  public static GeneaDate parseDate(NamedNodeMap attrs, String dateAttrName, String dateInfosAttr)
  {
    GeneaDate date=new GeneaDate();
    Node timestampStr=attrs.getNamedItem(dateAttrName);
    if (timestampStr!=null)
    {
      long timestamp=DOMParsingTools.getLongAttribute(attrs,dateAttrName,0);
      date.setDate(new Date(timestamp));
    }
    String dateInfos=DOMParsingTools.getStringAttribute(attrs,dateInfosAttr,"");
    date.setInfosDate(dateInfos);
    return date;
  }

  /**
   * Write a genea date.
   * @param date Date data.
   * @param attrs Attributes to write to.
   * @param dateAttrName Timestamp tag.
   * @param dateInfosAttr Date infos tag.
   */
  public static void writeDate(GeneaDate date, AttributesImpl attrs, String dateAttrName, String dateInfosAttr)
  {
    Long timestamp=date.getDate();
    if (timestamp!=null)
    {
      attrs.addAttribute("","",dateAttrName,XmlWriter.CDATA,timestamp.toString());
    }
    String dateInfos=date.getInfosDate();
    if ((dateInfos!=null) && (dateInfos.length()>0))
    {
      attrs.addAttribute("","",dateInfosAttr,XmlWriter.CDATA,dateInfos);
    }
  }
}
