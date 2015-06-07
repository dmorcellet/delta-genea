package delta.genea.web.pages;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import delta.common.framework.web.WebPageTools;
import delta.common.utils.ParameterFinder;
import delta.genea.data.Act;
import delta.genea.data.HomeForPerson;
import delta.genea.data.OccupationForPerson;
import delta.genea.data.Person;
import delta.genea.data.Picture;
import delta.genea.data.Place;
import delta.genea.data.Sex;
import delta.genea.data.Union;
import delta.genea.data.comparators.PersonComparator;
import delta.genea.data.comparators.PersonComparator.COMPARISON_CRITERIA;
import delta.genea.web.GeneaUserContext;
import delta.genea.web.pages.tools.PersonTools;

/**
 * Builder for the 'person' HTML page.
 * @author DAM
 */
public class PersonPage extends GeneaWebPage
{
  // HTML 4.01 strict validated
  private PersonPageData _data;

  @Override
  public void parseParameters() throws Exception
  {
    Long key=ParameterFinder.getLongParameter(_request,PersonPageParameters.PERSON_KEY,Long.valueOf(76));
    Long deCujus=ParameterFinder.getLongParameter(_request,GeneaUserContext.DE_CUJUS,key);
    _data=new PersonPageData(key,deCujus);
  }

  @Override
  public void fetchData() throws Exception
  {
    _data.load(getDataSource());
  }

  @Override
  public void generate(PrintWriter pw)
  {
    GeneaUserContext context=(GeneaUserContext)getUserContext();
    // Person tools for related persons
    PersonTools pTools=new PersonTools(context,pw);
    pTools.setUseIsAncestorIcon(true);
    pTools.setUseSexIcon(false);
    pTools.setUseNoDescendants(true);
    pTools.setAsLink(true);
    // Person tools for main
    PersonTools pToolsMain=new PersonTools(context,pw);
    pToolsMain.setUseIsAncestorIcon(false);
    pToolsMain.setUseSexIcon(true);
    pToolsMain.setUseNoDescendants(true);
    pToolsMain.setAsLink(false);

    PageTools tools=new PageTools(context,pw);
    Person main=_data.getMain();
    if (main==null) return;
    String title="Fiche de "+main.getFullName();
    WebPageTools.generatePageHeader(title,pw);
    WebPageTools.generateHorizontalRuler(pw);
    pw.println("<div>");
    pw.print("<b>Fiche de ");
    String sosas=_data.getSosas(main.getPrimaryKey());
    if (sosas.length()>0)
    {
      pw.print('(');
      pw.print(sosas);
      pw.print(") ");
    }
    pToolsMain.generatePersonName(main);
    pw.print("</b>");
    String signature=main.getSignature();
    if ((signature!=null)&&(signature.length()>0))
    {
      pw.print(" (");
      pw.print(main.getSignature());
      pw.print(')');
    }
    pw.println();

    // Links to ancestors trees
    {
      AncestorsPageParameters ancestorsPage=new AncestorsPageParameters(main
          .getPrimaryKey(),100);
      ancestorsPage.setParameter(GeneaUserContext.DB_NAME,context.getDbName());
      pw.println("<b>");
      pw.print("<a href=\"");
      pw.print(ancestorsPage.build());
      pw.println("\">Ascendance</a> ");
      for(int i=1;i<=9;i++)
      {
        pw.print("<a href=\"");
        ancestorsPage.setDepth(i);
        pw.print(ancestorsPage.build());
        pw.print("\">");
        pw.print(i);
        pw.println("</a> ");
      }
      pw.println("</b>");
    }
    // Links to descendants trees
    {
      DescendantsPageParameters descendantsPage=new DescendantsPageParameters();
      descendantsPage.setParameter(GeneaUserContext.DB_NAME,context.getDbName());
      descendantsPage.setKey(main.getPrimaryKey());
      pw.println("<b>");
      pw.print("<a href=\"");
      pw.print(descendantsPage.build());
      pw.println("\">Descendance</a> ");
      for(int i=1;i<=9;i++)
      {
        pw.print("<a href=\"");
        descendantsPage.setDepth(i);
        pw.print(descendantsPage.build());
        pw.print("\">");
        pw.print(i);
        pw.println("</a> ");
      }
      pw.println("</b>");
    }
    // Patronyme
    NamePageParameters namePage=new NamePageParameters(main.getLastName());
    namePage.setParameter(GeneaUserContext.DB_NAME,context.getDbName());
    pw.println(" <b>");
    pw.print("<a href=\"");
    pw.print(namePage.build());
    pw.println("\">Patronyme</a>");
    pw.println("</b>");

    // Cousins
    CousinsPageParameters cousinsPage=new CousinsPageParameters(main
        .getPrimaryKey());
    cousinsPage.setParameter(GeneaUserContext.DB_NAME,context.getDbName());
    pw.println(" <b>");
    pw.print("<a href=\"");
    pw.print(cousinsPage.build());
    pw.println("\">Cousins</a>");
    pw.println("</b>");

    pw.println("</div>");
    WebPageTools.generateHorizontalRuler(pw);

    pw.println("<div>");
    // Birth
    Act birth=_data.getActs().getBirthAct();
    pw.print("<b>");
    tools.generateActLink(birth,"Naissance");
    pw.print(" : </b>");
    String bDate=PageTools.generateDate(main.getBirthDate(),main
        .getBirthInfos());
    pw.print(bDate);
    tools.generatePlace(main.getBirthPlace()," ",null);
    pw.println("<br>");

    // Death
    Act death=_data.getActs().getDeathAct();
    pw.print("<b>");
    tools.generateActLink(death,"Decès");
    pw.print(" : </b>");
    String dDate=PageTools.generateDate(main.getDeathDate(),main
        .getDeathInfos());
    pw.print(dDate);
    tools.generatePlace(main.getDeathPlace()," ",null);
    pw.println("<br>");

    // Father & Mother
    pw.println("<br>");
    // Father
    Person father=_data.getFather();
    pw.print("<b>Père : </b>");
    pTools.generatePersonName(father);
    pw.println("<br>");

    // Mother
    Person mother=_data.getMother();
    pw.print("<b>Mère : </b>");
    pTools.generatePersonName(mother);
    pw.println("<br>");

    // Comments
    {
      String comments=main.getComments();
      if ((comments!=null)&&(comments.length()>0))
      {
        pw.println("<br>");
        pw.print("<b>Infos complémentaires :</b> ");
        pw.print(comments);
        pw.println("<br>");
      }
    }

    // Parrains et marraine
    {
      Person godFather=_data.getGodFather();
      Person godMother=_data.getGodMother();

      if ((godFather!=null)||(godMother!=null))
      {
        pw.println("<br>");
        if (godFather!=null)
        {
          pw.print("<b>Parrain : </b>");
          pTools.generatePersonName(godFather);
          pw.println("<br>");
        }
        if (godMother!=null)
        {
          pw.print("<b>Marraine : </b>");
          pTools.generatePersonName(godMother);
          pw.println("<br>");
        }
      }
    }

    boolean previousWasAList=false;

    // Professions
    List<OccupationForPerson> occupations=main.getOccupations();
    if ((occupations!=null)&&(occupations.size()>0))
    {
      pw.println("<br>");
      pw.println("<b>Professions :</b>");
      pw.println("<ul>");
      OccupationForPerson occupation;
      Place place;
      for(Iterator<OccupationForPerson> it=occupations.iterator();it.hasNext();)
      {
        occupation=it.next();
        place=occupation.getPlace();
        pw.print("<li>");
        pw.print(occupation.getOccupation());
        int year=occupation.getYear();
        if (year!=0)
        {
          pw.print(" (");
          pw.print(year);
          pw.print(")");
        }
        tools.generatePlace(place," (",")");
        pw.println("</li>");
      }
      pw.println("</ul>");
      previousWasAList=true;
    }

    // Homes
    List<HomeForPerson> homes=main.getHomes();
    if ((homes!=null)&&(homes.size()>0))
    {
      if (!previousWasAList)
      {
        pw.println("<br>");
      }
      pw.println("<b>Résidences :</b>");
      pw.println("<ul>");
      Place place;
      for(HomeForPerson home : homes)
      {
        place=home.getPlace();
        pw.print("<li>");
        int year=home.getYear();
        if (year!=0)
        {
          pw.print("(");
          pw.print(year);
          pw.print(") ");
        }
        String placeDetails=home.getPlaceDetails();
        if ((placeDetails!=null) && (placeDetails.length()>0))
        {
          pw.print(placeDetails);
          pw.print(' ');
        }
        tools.generatePlace(place,"","");
        pw.println("</li>");
      }
      pw.println("</ul>");
      previousWasAList=true;
    }

    // Unions
    {
      List<Union> unions=_data.getActs().getUnions();
      List<Act> unionActs=_data.getActs().getUnionActs();

      if ((unions!=null)&&(unions.size()>0))
      {
        if (!previousWasAList)
        {
          pw.println("<br>");
        }
        pw.println("<b>Mariages :</b>");
        pw.println("<ul>");
        int i=0;
        for(Union u : unions)
        {
          Person other;
          if (main.getPrimaryKey().equals(u.getManKey()))
          {
            other=u.getWoman();
          }
          else
          {
            other=u.getMan();
          }
          pw.print("<li>");
          String uDate=PageTools.generateDate(u.getDate(),u.getInfos());
          Act unionAct=unionActs.get(i);
          if (unionAct!=null)
          {
            tools.generateActLink(unionAct,uDate);
          }
          else
          {
            pw.print(uDate);
          }
          tools.generatePlace(u.getPlace()," ",null);
          pw.print(' ');
          Act weddingContract=u.getWeddingContract();
          if (weddingContract!=null)
          {
            pw.print("<b>");
            tools.generateActLink(weddingContract,"(CM)");
            pw.print("</b>");
            pw.print(' ');
          }
          pTools.generatePersonName(other);
          pw.println("</li>");
          i++;
        }
        pw.println("</ul>");
        previousWasAList=true;
      }
    }

    // Children
    {
      List<Person> children=_data.getChildren();
      if ((children!=null)&&(children.size()>0))
      {
        Collections.sort(children,new PersonComparator(COMPARISON_CRITERIA.BIRTH_DATE));
        if (!previousWasAList)
        {
          pw.println("<br>");
        }
        pw.println("<b>Enfants :</b>");
        pw.println("<ul>");
        pTools.setUseSexIcon(true);
        for(Person child : children)
        {
          pw.print("<li>");
          pTools.generatePersonName(child);
          pw.println("</li>");
        }
        pTools.setUseSexIcon(false);
        pw.println("</ul>");
        previousWasAList=true;
      }
    }

    // God children
    {
      List<Person> godChildren=_data.getGodChildren();

      if ((godChildren!=null)&&(godChildren.size()>0))
      {
        if (!previousWasAList)
        {
          pw.println("<br>");
        }
        String pm="Parrain";
        if (main.getSex()==Sex.FEMALE)
        {
          pm="Marraine";
        }
        pw.print("<b>");
        pw.print(pm);
        pw.print(" de :</b>");
        pw.println("<ul>");
        Person godChild;
        pTools.setUseSexIcon(true);
        for(Iterator<Person> it=godChildren.iterator();it.hasNext();)
        {
          godChild=it.next();
          pw.print("<li>");
          PageTools.generateDate(godChild.getBirthDate(),godChild
              .getBirthInfos());
          pw.print(' ');
          pTools.generatePersonName(godChild);
          pw.println("</li>");
        }
        pTools.setUseSexIcon(false);
        pw.println("</ul>");
        previousWasAList=true;
      }
    }

    // Acts
    {
      List<Act> oActs=_data.getActs().getOtherActs();

      if (!previousWasAList)
      {
        pw.println("<br>");
      }
      if ((oActs!=null)&&(oActs.size()>0))
      {
        pw.println("<b>Actes :</b>");
        pw.println("<ul>");
        for(Act act : oActs)
        {
          pw.print("<li>");
          tools.generateActLink(act,act.getP1(),act.getP2());
          pw.println("</li>");
        }
        pw.println("</ul>");
        previousWasAList=true;
      }
      else
      {
        pw.println("<b>Pas d'autres actes.</b>");
        pw.println("<br>");
        previousWasAList=false;
      }
    }

    // Pictures
    {
      List<Picture> pictures=_data.getPictures();

      if (!previousWasAList)
      {
        pw.println("<br>");
      }
      if ((pictures!=null)&&(pictures.size()>0))
      {
        pw.println("<b>Photos :</b>");
        pw.println("<ul>");
        Picture picture;
        for(Iterator<Picture> it=pictures.iterator();it.hasNext();)
        {
          picture=it.next();
          pw.print("<li>");
          tools.generatePictureLink(picture);
          pw.println("</li>");
        }
        pw.println("</ul>");
        previousWasAList=true;
      }
      else
      {
        pw.println("<b>Pas de photos.</b>");
        pw.println("<br>");
        previousWasAList=false;
      }
    }

    pw.println("</div>");
    WebPageTools.generatePageFooter(pw);
  }
}
