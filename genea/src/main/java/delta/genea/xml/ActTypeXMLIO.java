package delta.genea.xml;

import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

import delta.common.framework.objects.xml.DefaultXMLIO;
import delta.common.utils.io.xml.XmlWriter;
import delta.common.utils.xml.DOMParsingTools;
import delta.genea.data.ActType;

/**
 * XML I/O for act types.
 * @author DAM
 */
public class ActTypeXMLIO extends DefaultXMLIO<ActType>
{
  @Override
  public ActType readObject(Element tag, Long id)
  {
    ActType ret=new ActType(id);
    // Type
    String type=DOMParsingTools.getStringAttribute(tag.getAttributes(),GeneaXMLConstants.NAME_ATTR,"");
    ret.setType(type);
    return ret;
  }

  @Override
  public void writeMainAttributes(TransformerHandler hd, AttributesImpl objectAttrs, ActType object)
  {
    String type=object.getType();
    objectAttrs.addAttribute("","",GeneaXMLConstants.NAME_ATTR,XmlWriter.CDATA,type);
  }
}
