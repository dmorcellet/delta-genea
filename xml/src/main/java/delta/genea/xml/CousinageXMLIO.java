package delta.genea.xml;

import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.helpers.AttributesImpl;

import delta.common.framework.objects.xml.DefaultXMLIO;
import delta.common.utils.xml.DOMParsingTools;
import delta.genea.data.Cousinage;
import delta.genea.data.Person;

/**
 * XML I/O for cousinages.
 * @author DAM
 */
public class CousinageXMLIO extends DefaultXMLIO<Cousinage>
{
  @Override
  public Cousinage readObject(Element tag, Long id)
  {
    Cousinage ret=new Cousinage(id);
    NamedNodeMap attrs=tag.getAttributes();
    // Cousin 1
    long cousin1=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.COUSIN1_ATTR,0);
    ret.setCousin1(_source.buildProxy(Person.class,Long.valueOf(cousin1)));
    // Cousin 2
    long cousin2=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.COUSIN2_ATTR,0);
    ret.setCousin2(_source.buildProxy(Person.class,Long.valueOf(cousin2)));
    return ret;
  }

  @Override
  public void writeMainAttributes(TransformerHandler hd, AttributesImpl objectAttrs, Cousinage object)
  {
    // Cousin 1
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.COUSIN1_ATTR,object.getCousin1());
    // Cousin 2
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.COUSIN2_ATTR,object.getCousin2());
  }
}
