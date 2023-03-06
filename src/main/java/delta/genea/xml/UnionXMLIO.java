package delta.genea.xml;
// Date

import java.util.Date;

import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.helpers.AttributesImpl;

import delta.common.framework.objects.xml.DefaultXMLIO;
import delta.common.utils.io.xml.XmlWriter;
import delta.common.utils.xml.DOMParsingTools;
import delta.genea.data.Act;
import delta.genea.data.GeneaDate;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.Union;

/**
 * XML I/O for union.
 * @author DAM
 */
public class UnionXMLIO extends DefaultXMLIO<Union>
{
  @Override
  public Union readObject(Element tag, Long id)
  {
    Union ret=new Union(id);
    NamedNodeMap attrs=tag.getAttributes();

    // Date
    Node timestampStr=attrs.getNamedItem(GeneaXMLConstants.DATE_ATTR);
    if (timestampStr!=null)
    {
      long timestamp=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.DATE_ATTR,-1);
      String dateInfos=DOMParsingTools.getStringAttribute(attrs,GeneaXMLConstants.DATE_INFOS_ATTR,"");
      GeneaDate date=new GeneaDate(new Date(timestamp),dateInfos);
      ret.setDate(date);
    }
    // Place
    long placeKey=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.PLACE_ATTR,-1);
    if (placeKey>=0)
    {
      ret.setPlaceProxy(_source.buildProxy(Place.class,Long.valueOf(placeKey)));
    }
    // Man
    long manKey=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.UNION_MAN_ATTR,-1);
    if (manKey>=0)
    {
      ret.setManProxy(_source.buildProxy(Person.class,Long.valueOf(manKey)));
    }
    // Woman
    long womanKey=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.UNION_WOMAN_ATTR,-1);
    if (womanKey>=0)
    {
      ret.setWomanProxy(_source.buildProxy(Person.class,Long.valueOf(womanKey)));
    }
    // Wedding contract ID
    long weddingContractKey=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.UNION_WEDDING_CONTRACT_ID_ATTR,-1);
    if (weddingContractKey>=0)
    {
      ret.setWeddingContractProxy(_source.buildProxy(Act.class,Long.valueOf(weddingContractKey)));
    }
    // Comments
    String comments=DOMParsingTools.getStringAttribute(attrs,GeneaXMLConstants.COMMENTS_ATTR,"");
    ret.setComments(comments);
    return ret;
  }

  @Override
  public void writeMainAttributes(TransformerHandler hd, AttributesImpl objectAttrs, Union object)
  {
    // Date
    GeneaDate date=object.getGeneaDate();
    Long timestamp=date.getDate();
    if (timestamp!=null)
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.DATE_ATTR,XmlWriter.CDATA,timestamp.toString());
    }
    String dateInfos=date.getInfosDate();
    if ((dateInfos!=null) && (dateInfos.length()>0))
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.DATE_INFOS_ATTR,XmlWriter.CDATA,dateInfos);
    }
    // Place
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.PLACE_ATTR,object.getPlaceProxy());
    // Man
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.UNION_MAN_ATTR,object.getManProxy());
    // Woman
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.UNION_WOMAN_ATTR,object.getWomanProxy());
    // Wedding contract
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.UNION_WEDDING_CONTRACT_ID_ATTR,object.getWeddingContractProxy());
    // Comments
    String comments=object.getComments();
    if ((comments!=null) && (comments.length()>0))
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.COMMENTS_ATTR,XmlWriter.CDATA,comments);
    }
  }
}
