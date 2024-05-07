package delta.genea.xml;

import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.helpers.AttributesImpl;

import delta.common.framework.objects.xml.DefaultXMLIO;
import delta.common.utils.io.xml.XmlWriter;
import delta.common.utils.xml.DOMParsingTools;
import delta.genea.data.ActText;

/**
 * XML I/O for act texts.
 * @author DAM
 */
public class ActTextXMLIO extends DefaultXMLIO<ActText>
{
  @Override
  public ActText readObject(Element tag, Long id)
  {
    ActText ret=new ActText(id);
    NamedNodeMap attrs=tag.getAttributes();
    // Text
    String text=DOMParsingTools.getStringAttribute(attrs,GeneaXMLConstants.ACT_TEXT_ATTR,"");
    ret.setText(text);
    return ret;
  }

  @Override
  public void writeMainAttributes(TransformerHandler hd, AttributesImpl objectAttrs, ActText object)
  {
    // Text
    String text=object.getText();
    if ((text!=null) && (text.length()>0))
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.ACT_TEXT_ATTR,XmlWriter.CDATA,text);
    }
  }
}
