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
import delta.genea.data.GeneaDate;
import delta.genea.data.HomeForPerson;
import delta.genea.data.OccupationForPerson;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.Sex;

/**
 * XML I/O for persons.
 * @author DAM
 */
public class PersonXMLIO extends DefaultXMLIO<Person>
{
  @Override
  public Person readObject(Element tag, Long id)
  {
    Person ret=new Person(id);

    NamedNodeMap attrs=tag.getAttributes();
    // Last name
    String lastName=DOMParsingTools.getStringAttribute(attrs,GeneaXMLConstants.PERSON_LASTNAME_ATTR,"");
    ret.setLastName(lastName);
    // First name
    String firstName=DOMParsingTools.getStringAttribute(attrs,GeneaXMLConstants.PERSON_FIRSTNAME_ATTR,"");
    ret.setFirstname(firstName);
    // Sex
    String sexStr=DOMParsingTools.getStringAttribute(attrs,GeneaXMLConstants.PERSON_SEX_ATTR,null);
    if ((sexStr!=null) && (sexStr.length()==1))
    {
      Sex sex=Sex.getFromValue(sexStr.charAt(0));
      ret.setSex(sex);
    }
    // Signature
    String signature=DOMParsingTools.getStringAttribute(attrs,GeneaXMLConstants.PERSON_SIGNATURE_ATTR,"");
    ret.setSignature(signature);
    // Birth date
    GeneaDate birthDate=XMLUtils.parseDate(attrs,GeneaXMLConstants.BIRTH_DATE_ATTR,GeneaXMLConstants.BIRTH_DATE_INFOS_ATTR);
    ret.setBirthDate(birthDate);
    // Birth place
    long birthPlaceKey=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.BIRTH_PLACE_ATTR,-1);
    if (birthPlaceKey>=0)
    {
      ret.setBirthPlaceProxy(_source.buildProxy(Place.class,Long.valueOf(birthPlaceKey)));
    }
    // Death date
    GeneaDate deathDate=XMLUtils.parseDate(attrs,GeneaXMLConstants.DEATH_DATE_ATTR,GeneaXMLConstants.DEATH_DATE_INFOS_ATTR);
    ret.setDeathDate(deathDate);
    // Death place
    long deathPlaceKey=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.DEATH_PLACE_ATTR,-1);
    if (deathPlaceKey>=0)
    {
      ret.setDeathPlaceProxy(_source.buildProxy(Place.class,Long.valueOf(deathPlaceKey)));
    }
    // No descendants
    boolean noDescendants=DOMParsingTools.getBooleanAttribute(attrs,GeneaXMLConstants.NO_DESCENDANTS_ATTR,false);
    ret.setNoDescendants(noDescendants);
    // Father
    long fatherKey=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.FATHER_ATTR,-1);
    if (fatherKey>=0)
    {
      ret.setFatherProxy(_source.buildProxy(Person.class,Long.valueOf(fatherKey)));
    }
    // Mother
    long motherKey=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.MOTHER_ATTR,-1);
    if (motherKey>=0)
    {
      ret.setMotherProxy(_source.buildProxy(Person.class,Long.valueOf(motherKey)));
    }
    // God father
    long godFatherKey=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.GODFATHER_ATTR,-1);
    if (godFatherKey>=0)
    {
      ret.setGodFatherProxy(_source.buildProxy(Person.class,Long.valueOf(godFatherKey)));
    }
    // God mother
    long godMotherKey=DOMParsingTools.getLongAttribute(attrs,GeneaXMLConstants.GODMOTHER_ATTR,-1);
    if (godMotherKey>=0)
    {
      ret.setGodMotherProxy(_source.buildProxy(Person.class,Long.valueOf(godMotherKey)));
    }

    // Known occupations
    readOccupations(tag,ret);

    // Known homes
    readHomes(tag,ret);
    return ret;
  }

  private void readOccupations(Element tag, Person person)
  {
    List<Element> childTags=DOMParsingTools.getChildTagsByName(tag,GeneaXMLConstants.PERSON_OCCUPATION_TAG);
    List<OccupationForPerson> occupationsForPerson=new ArrayList<OccupationForPerson>();
    for(Element childTag : childTags)
    {
      OccupationForPerson occupationForPerson=new OccupationForPerson();
      NamedNodeMap childAttrs=childTag.getAttributes();
      // Occupation
      String occupation=DOMParsingTools.getStringAttribute(childAttrs,GeneaXMLConstants.OCCUPATION_ATTR,"");
      occupationForPerson.setOccupation(occupation);
      // Year
      int year=DOMParsingTools.getIntAttribute(childAttrs,GeneaXMLConstants.OCCUPATION_YEAR_ATTR,0);
      occupationForPerson.setYear(year);
      // Place
      long placeKey=DOMParsingTools.getLongAttribute(childAttrs,GeneaXMLConstants.OCCUPATION_PLACE_ATTR,-1);
      if (placeKey>=0)
      {
        occupationForPerson.setPlaceProxy(_source.buildProxy(Place.class,Long.valueOf(placeKey)));
      }
      occupationsForPerson.add(occupationForPerson);
    }
    person.setOccupations(occupationsForPerson);
  }

  private void readHomes(Element tag, Person person)
  {
    List<Element> childTags=DOMParsingTools.getChildTagsByName(tag,GeneaXMLConstants.PERSON_HOME_TAG);
    List<HomeForPerson> homesForPerson=new ArrayList<HomeForPerson>();
    for(Element childTag : childTags)
    {
      HomeForPerson homeForPerson=new HomeForPerson();
      NamedNodeMap childAttrs=childTag.getAttributes();
      // Year
      int year=DOMParsingTools.getIntAttribute(childAttrs,GeneaXMLConstants.HOME_YEAR_ATTR,0);
      homeForPerson.setYear(year);
      // Place details
      String placeDetails=DOMParsingTools.getStringAttribute(childAttrs,GeneaXMLConstants.HOME_DETAILS_ATTR,"");
      homeForPerson.setPlaceDetails(placeDetails);
      // Place
      long placeKey=DOMParsingTools.getLongAttribute(childAttrs,GeneaXMLConstants.HOME_PLACE_ATTR,-1);
      if (placeKey>=0)
      {
        homeForPerson.setPlaceProxy(_source.buildProxy(Place.class,Long.valueOf(placeKey)));
      }
      homesForPerson.add(homeForPerson);
    }
    person.setHomes(homesForPerson);
  }

  @Override
  public void writeMainAttributes(TransformerHandler hd, AttributesImpl objectAttrs, Person object)
  {
    // Last name
    String lastName=object.getLastName();
    if ((lastName!=null) && (lastName.length()>0))
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.PERSON_LASTNAME_ATTR,XmlWriter.CDATA,lastName);
    }
    // First name
    String firstName=object.getFirstname();
    if ((firstName!=null) && (firstName.length()>0))
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.PERSON_FIRSTNAME_ATTR,XmlWriter.CDATA,firstName);
    }
    // Sex
    Sex sex=object.getSex();
    if (sex!=null)
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.PERSON_SEX_ATTR,XmlWriter.CDATA,String.valueOf(sex.getValue()));
    }
    // Signature
    String signature=object.getSignature();
    if ((signature!=null) && (signature.length()>0))
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.PERSON_SIGNATURE_ATTR,XmlWriter.CDATA,signature);
    }

    // Birth date
    GeneaDate birthDate=object.getBirthGeneaDate();
    XMLUtils.writeDate(birthDate,objectAttrs,GeneaXMLConstants.BIRTH_DATE_ATTR,GeneaXMLConstants.BIRTH_DATE_INFOS_ATTR);
    // Birth place
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.BIRTH_PLACE_ATTR,object.getBirthPlaceProxy());
    // Death date
    GeneaDate deathDate=object.getDeathGeneaDate();
    XMLUtils.writeDate(deathDate,objectAttrs,GeneaXMLConstants.DEATH_DATE_ATTR,GeneaXMLConstants.DEATH_DATE_INFOS_ATTR);
    // Death place
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.DEATH_PLACE_ATTR,object.getDeathPlaceProxy());
    // No descendants
    boolean noDescendants=object.getNoDescendants();
    if (noDescendants)
    {
      objectAttrs.addAttribute("","",GeneaXMLConstants.NO_DESCENDANTS_ATTR,XmlWriter.CDATA,String.valueOf(true));
    }
    // Father
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.FATHER_ATTR,object.getFatherProxy());
    // Mother
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.MOTHER_ATTR,object.getMotherProxy());
    // God father
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.GODFATHER_ATTR,object.getGodFatherProxy());
    // God mother
    XMLUtils.writeProxy(objectAttrs,GeneaXMLConstants.GODMOTHER_ATTR,object.getGodMotherProxy());
  }

  @Override
  public void writeChildTags(TransformerHandler hd, Person object) throws SAXException
  {
    writeOccupations(hd,object);
    writeHomes(hd,object);
  }

  private void writeOccupations(TransformerHandler hd, Person object) throws SAXException
  {
    List<OccupationForPerson> occupations=object.getOccupations();
    if ((occupations!=null) && (!occupations.isEmpty()))
    {
      for(OccupationForPerson occupationForPerson : occupations)
      {
        AttributesImpl attrs=new AttributesImpl();
        // Occupation
        String occupation=occupationForPerson.getOccupation();
        if (occupation.length()>0)
        {
          attrs.addAttribute("","",GeneaXMLConstants.OCCUPATION_ATTR,XmlWriter.CDATA,occupation);
        }
        // Year
        int year=occupationForPerson.getYear();
        if (year>0)
        {
          attrs.addAttribute("","",GeneaXMLConstants.OCCUPATION_YEAR_ATTR,XmlWriter.CDATA,String.valueOf(year));
        }
        // Place
        XMLUtils.writeProxy(attrs,GeneaXMLConstants.OCCUPATION_PLACE_ATTR,occupationForPerson.getPlaceProxy());
        hd.startElement("","",GeneaXMLConstants.PERSON_OCCUPATION_TAG,attrs);
        hd.endElement("","",GeneaXMLConstants.PERSON_OCCUPATION_TAG);
      }
    }
  }

  private void writeHomes(TransformerHandler hd, Person object) throws SAXException
  {
    List<HomeForPerson> homes=object.getHomes();
    if ((homes!=null) && (!homes.isEmpty()))
    {
      for(HomeForPerson home : homes)
      {
        AttributesImpl attrs=new AttributesImpl();
        // Year
        int year=home.getYear();
        if (year>0)
        {
          attrs.addAttribute("","",GeneaXMLConstants.HOME_YEAR_ATTR,XmlWriter.CDATA,String.valueOf(year));
        }
        // Place details
        String placeDetails=home.getPlaceDetails();
        if ((placeDetails!=null) && (placeDetails.length()>0))
        {
          attrs.addAttribute("","",GeneaXMLConstants.HOME_DETAILS_ATTR,XmlWriter.CDATA,placeDetails);
        }
        // Place
        XMLUtils.writeProxy(attrs,GeneaXMLConstants.HOME_PLACE_ATTR,home.getPlaceProxy());
        hd.startElement("","",GeneaXMLConstants.PERSON_HOME_TAG,attrs);
        hd.endElement("","",GeneaXMLConstants.PERSON_HOME_TAG);
      }
    }
  }
}
