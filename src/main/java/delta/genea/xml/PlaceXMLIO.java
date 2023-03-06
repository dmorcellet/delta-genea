package delta.genea.xml;

import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.helpers.AttributesImpl;

import delta.common.framework.objects.xml.DefaultXMLIO;
import delta.common.utils.io.xml.XmlWriter;
import delta.common.utils.xml.DOMParsingTools;
import delta.genea.data.Place;
import delta.genea.data.PlaceLevel;

/**
 * XML I/O for places.
 * @author DAM
 */
public class PlaceXMLIO extends DefaultXMLIO<Place>
{
  @Override
  public Place readObject(Element tag, Long id)
  {
    /*
  private String _name;
  private String _shortName;
  private PlaceLevel _level;
  private DataProxy<Place> _parent;
     */
    Place ret=new Place(id);
    NamedNodeMap attrs=tag.getAttributes();
    // Name
    String name=DOMParsingTools.getStringAttribute(attrs,GeneaXMLConstants.NAME_ATTR,"");
    ret.setName(name);
    // Short name
    String shortName=DOMParsingTools.getStringAttribute(attrs,GeneaXMLConstants.PLACE_SHORT_NAME_ATTR,"");
    ret.setShortName(shortName);
    // Place level
    PlaceLevel placeLevel=null;
    int placeLevelCode=DOMParsingTools.getIntAttribute(attrs,GeneaXMLConstants.PLACE_LEVEL_ATTR,-1);
    if (placeLevelCode>=0)
    {
      placeLevel=PlaceLevel.getFromValue(placeLevelCode);
    }
    ret.setLevel(placeLevel);
    // Parent
    long parentPlaceKey=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.PLACE_PARENT_PLACE_ATTR,-1);
    if (parentPlaceKey>=0)
    {
      ret.setParentPlaceProxy(_source.buildProxy(Place.class,Long.valueOf(parentPlaceKey)));
    }
    return ret;
  }

  @Override
  public void writeMainAttributes(TransformerHandler hd, AttributesImpl objectAttrs, Place object)
  {
    // Name
    String name=object.getName();
    if ((name!=null) && (name.length()>0))
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.NAME_ATTR,XmlWriter.CDATA,name);
    }
    // Short name
    String shortName=object.getShortName();
    if ((shortName!=null) && (shortName.length()>0))
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.PLACE_SHORT_NAME_ATTR,XmlWriter.CDATA,shortName);
    }
    // Place level
    PlaceLevel placeLevel=object.getLevel();
    if (placeLevel!=null)
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.PLACE_LEVEL_ATTR,XmlWriter.CDATA,String.valueOf(placeLevel.getValue()));
    }
    // Parent
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.PLACE_PARENT_PLACE_ATTR,object.getParentPlaceProxy());
  }
}
