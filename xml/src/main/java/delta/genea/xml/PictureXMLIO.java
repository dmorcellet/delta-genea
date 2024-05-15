package delta.genea.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import delta.common.framework.objects.xml.DefaultXMLIO;
import delta.common.utils.io.xml.XmlWriter;
import delta.common.utils.xml.DOMParsingTools;
import delta.genea.data.Person;
import delta.genea.data.PersonInPicture;
import delta.genea.data.Picture;

/**
 * XML I/O for pictures.
 * @author DAM
 */
public class PictureXMLIO extends DefaultXMLIO<Picture>
{
  @Override
  public Picture readObject(Element tag, Long id)
  {
    Picture ret=new Picture(id);
    NamedNodeMap attrs=tag.getAttributes();
    // Title
    String title=DOMParsingTools.getStringAttribute(attrs,GeneaXMLConstants.PICTURE_TITLE_ATTR,"");
    ret.setTitle(title);
    // Path
    String path=DOMParsingTools.getStringAttribute(attrs,GeneaXMLConstants.PICTURE_PATH_ATTR,"");
    ret.setPath(path);
    // Commentaire
    String comments=DOMParsingTools.getStringAttribute(attrs,GeneaXMLConstants.COMMENTS_ATTR,"");
    ret.setComment(comments);

    // Persons in picture
    List<Element> childTags=DOMParsingTools.getChildTagsByName(tag,GeneaXMLConstants.PERSON_IN_PICTURE_TAG);
    List<PersonInPicture> pips=new ArrayList<PersonInPicture>();
    for(Element childTag : childTags)
    {
      PersonInPicture pip=new PersonInPicture();
      NamedNodeMap childAttrs=childTag.getAttributes();
      // Person ID
      long personID=DOMParsingTools.getLongAttribute(childAttrs,GeneaXMLConstants.PERSON_IN_PICTURE_ID_ATTR,-1);
      pip.setPersonProxy(_source.buildProxy(Person.class,Long.valueOf(personID)));
      pips.add(pip);
    }
    ret.setPersonsInPicture(pips);
    return ret;
  }

  @Override
  public void writeMainAttributes(TransformerHandler hd, AttributesImpl objectAttrs, Picture object)
  {
    // Title
    String title=object.getTitle();
    if ((title!=null) && (title.length()>0))
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.PICTURE_TITLE_ATTR,XmlWriter.CDATA,title);
    }
    // Path
    String path=object.getPath();
    if ((path!=null) && (path.length()>0))
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.PICTURE_PATH_ATTR,XmlWriter.CDATA,path);
    }
    // Comment
    String comment=object.getComment();
    if ((comment!=null) && (comment.length()>0))
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.COMMENTS_ATTR,XmlWriter.CDATA,comment);
    }
  }

  @Override
  public void writeChildTags(TransformerHandler hd, Picture object) throws SAXException
  {
    List<PersonInPicture> pips=object.getPersonsInPicture();
    if ((pips!=null) && (!pips.isEmpty()))
    {
      for(PersonInPicture pip : pips)
      {
        AttributesImpl attrs=new AttributesImpl();
        // Person ID
        XMLUtils.writeProxy(attrs,GeneaXMLConstants.PERSON_IN_PICTURE_ID_ATTR,pip.getPersonProxy());
        hd.startElement("","",GeneaXMLConstants.PERSON_IN_PICTURE_TAG,attrs);
        hd.endElement("","",GeneaXMLConstants.PERSON_IN_PICTURE_TAG);
      }
    }
  }
}
