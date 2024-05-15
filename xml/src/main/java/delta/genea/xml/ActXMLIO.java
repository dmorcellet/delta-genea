package delta.genea.xml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.xml.DefaultXMLIO;
import delta.common.utils.io.xml.XmlWriter;
import delta.common.utils.xml.DOMParsingTools;
import delta.genea.data.Act;
import delta.genea.data.ActText;
import delta.genea.data.ActType;
import delta.genea.data.Person;
import delta.genea.data.PersonInAct;
import delta.genea.data.Place;

/**
 * XML I/O for acts.
 * @author DAM
 */
public class ActXMLIO extends DefaultXMLIO<Act>
{
  @Override
  public Act readObject(Element tag, Long id)
  {
    Act ret=new Act(id);
    NamedNodeMap attrs=tag.getAttributes();
    // Type
    int type=DOMParsingTools.getIntAttribute(attrs,GeneaXMLConstants.TYPE_ATTR,0);
    DataProxy<ActType> typeProxy=_source.buildProxy(ActType.class,Long.valueOf(type));
    ret.setActTypeProxy(typeProxy);
    // Date
    if (attrs.getNamedItem(GeneaXMLConstants.DATE_ATTR)!=null)
    {
      long date=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.DATE_ATTR,-1);
      ret.setDate(new Date(date));
    }
    // Place
    long placeKey=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.PLACE_ATTR,-1);
    if (placeKey>=0)
    {
      ret.setPlaceProxy(_source.buildProxy(Place.class,Long.valueOf(placeKey)));
    }
    // Person 1
    long p1Key=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.PERSON1_ATTR,-1);
    if (p1Key>=0)
    {
      ret.setP1Proxy(_source.buildProxy(Person.class,Long.valueOf(p1Key)));
    }
    // Person 2
    long p2Key=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.PERSON2_ATTR,-1);
    if (p2Key>=0)
    {
      ret.setP2Proxy(_source.buildProxy(Person.class,Long.valueOf(p2Key)));
    }
    // Text ID
    long actTextKey=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.ACT_TEXT_ATTR,-1);
    if (actTextKey>=0)
    {
      ret.setTextProxy(_source.buildProxy(ActText.class,Long.valueOf(actTextKey)));
    }
    // Path
    String path=DOMParsingTools.getStringAttribute(attrs,GeneaXMLConstants.ACT_FILE_PATH_ATTR,"");
    ret.setPath(path);
    // Nb parts
    int nbParts=DOMParsingTools.getIntAttribute(attrs,GeneaXMLConstants.ACT_NB_PARTS_ATTR,0);
    ret.setNbFiles(nbParts);
    // Traite
    boolean traite=DOMParsingTools.getBooleanAttribute(attrs,GeneaXMLConstants.ACT_TRAITE_ATTR,false);
    ret.setTraite(traite);
    // Comment
    String comment=DOMParsingTools.getStringAttribute(attrs,GeneaXMLConstants.COMMENTS_ATTR,"");
    ret.setComment(comment);

    // Persons in act
    List<Element> childTags=DOMParsingTools.getChildTagsByName(tag,GeneaXMLConstants.PERSON_IN_ACT_TAG);
    List<PersonInAct> pias=new ArrayList<PersonInAct>();
    for(Element childTag : childTags)
    {
      PersonInAct pia=new PersonInAct();
      NamedNodeMap childAttrs=childTag.getAttributes();
      // Person ID
      long personID=DOMParsingTools.getLongAttribute(childAttrs,GeneaXMLConstants.PERSON_IN_ACT_ID_ATTR,-1);
      pia.setPersonProxy(_source.buildProxy(Person.class,Long.valueOf(personID)));
      // Presence
      String presence=DOMParsingTools.getStringAttribute(childAttrs,GeneaXMLConstants.PERSON_IN_ACT_PRESENCE_ATTR,"");
      pia.setPresence(presence);
      // Signature
      String signature=DOMParsingTools.getStringAttribute(childAttrs,GeneaXMLConstants.PERSON_IN_ACT_SIGNATURE_ATTR,"");
      pia.setSignature(signature);
      // Link
      String link=DOMParsingTools.getStringAttribute(childAttrs,GeneaXMLConstants.PERSON_IN_ACT_LINK_ATTR,"");
      pia.setLink(link);
      // Other person ID
      long linkedPersonKey=DOMParsingTools.getLongAttribute(childAttrs,GeneaXMLConstants.PERSON_IN_ACT_OTHER_ID_ATTR,-1);
      if (linkedPersonKey>=0)
      {
        pia.setLinkRefProxy(_source.buildProxy(Person.class,Long.valueOf(linkedPersonKey)));
      }
      pias.add(pia);
    }
    ret.setPersonsInAct(pias);
    return ret;
  }

  @Override
  public void writeMainAttributes(TransformerHandler hd, AttributesImpl objectAttrs, Act object)
  {
    // Type
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.TYPE_ATTR,object.getActTypeProxy());
    // Date
    Long date=object.getDate();
    if (date!=null)
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.DATE_ATTR,XmlWriter.CDATA,date.toString());
    }
    // Place
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.PLACE_ATTR,object.getPlaceProxy());
    // Person 1
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.PERSON1_ATTR,object.getP1Proxy());
    // Person 2
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.PERSON2_ATTR,object.getP2Proxy());
    // Text ID
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.ACT_TEXT_ATTR,object.getTextProxy());
    // Path
    String path=object.getPath();
    if ((path!=null) && (path.length()>0))
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.ACT_FILE_PATH_ATTR,XmlWriter.CDATA,path);
    }
    // Nb parts
    int nbParts=object.getNbFiles();
    if (nbParts>0)
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.ACT_NB_PARTS_ATTR,XmlWriter.CDATA,String.valueOf(nbParts));
    }
    // Traite
    boolean traite=object.getTraite();
    if (traite)
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.ACT_TRAITE_ATTR,XmlWriter.CDATA,String.valueOf(traite));
    }
    // Comment
    String comment=object.getComment();
    if ((comment!=null) && (comment.length()>0))
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.COMMENTS_ATTR,XmlWriter.CDATA,comment);
    }
  }

  @Override
  public void writeChildTags(TransformerHandler hd, Act object) throws SAXException
  {
    List<PersonInAct> pias=object.getPersonsInAct();
    if ((pias!=null) && (!pias.isEmpty()))
    {
      for(PersonInAct pia : pias)
      {
        AttributesImpl attrs=new AttributesImpl();
        // Person ID
        XMLUtils.writeProxy(attrs,GeneaXMLConstants.PERSON_IN_ACT_ID_ATTR,pia.getPersonProxy());
        // Presence
        String presence=pia.getPresence();
        if (presence.length()>0)
        {
          attrs.addAttribute("","",GeneaXMLConstants.PERSON_IN_ACT_PRESENCE_ATTR,XmlWriter.CDATA,presence);
        }
        // Signature
        String signature=pia.getSignature();
        if ((signature!=null) && (signature.length()>0))
        {
          attrs.addAttribute("","",GeneaXMLConstants.PERSON_IN_ACT_SIGNATURE_ATTR,XmlWriter.CDATA,signature);
        }
        // Link
        String link=pia.getLink();
        if ((link!=null) && (link.length()>0))
        {
          attrs.addAttribute("","",GeneaXMLConstants.PERSON_IN_ACT_LINK_ATTR,XmlWriter.CDATA,link);
        }
        // Other person ID
        XMLUtils.writeProxy(attrs,GeneaXMLConstants.PERSON_IN_ACT_OTHER_ID_ATTR,pia.getLinkRefProxy());
        hd.startElement("","",GeneaXMLConstants.PERSON_IN_ACT_TAG,attrs);
        hd.endElement("","",GeneaXMLConstants.PERSON_IN_ACT_TAG);
      }
    }
  }
}
