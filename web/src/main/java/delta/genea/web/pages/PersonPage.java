package delta.genea.web.pages;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import delta.common.framework.web.PageParameters;
import delta.common.framework.web.WebPageTools;
import delta.common.utils.ParameterFinder;
import delta.common.utils.html.HtmlConstants;
import delta.genea.data.Act;
import delta.genea.data.HomeForPerson;
import delta.genea.data.OccupationForPerson;
import delta.genea.data.Person;
import delta.genea.data.Picture;
import delta.genea.data.Place;
import delta.genea.data.Sex;
import delta.genea.data.TitleForPerson;
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
    pw.println(HtmlConstants.DIV);
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
      doLink(pw,ancestorsPage,"Ascendance");
      for(int i=1;i<=9;i++)
      {
        ancestorsPage.setDepth(i);
        doLink(pw,ancestorsPage,String.valueOf(i));
      }
      pw.println("</b>");
    }
    // Links to descendants trees
    {
      DescendantsPageParameters descendantsPage=new DescendantsPageParameters();
      descendantsPage.setParameter(GeneaUserContext.DB_NAME,context.getDbName());
      descendantsPage.setKey(main.getPrimaryKey());
      pw.println("<b>");
      doLink(pw,descendantsPage,"Descendance");
      for(int i=1;i<=9;i++)
      {
        descendantsPage.setDepth(i);
        doLink(pw,descendantsPage,String.valueOf(i));
      }
      pw.println("</b>");
    }
    // Patronyme
    NamePageParameters namePage=new NamePageParameters(main.getLastName());
    namePage.setParameter(GeneaUserContext.DB_NAME,context.getDbName());
    pw.println("<b>");
    doLink(pw,namePage,"Patronyme");
    pw.println("</b>");

    // Cousins
    CousinsPageParameters cousinsPage=new CousinsPageParameters(main
        .getPrimaryKey());
    cousinsPage.setParameter(GeneaUserContext.DB_NAME,context.getDbName());
    pw.println("<b>");
    doLink(pw,cousinsPage,"Cousins");
    pw.println("</b>");

    pw.println(HtmlConstants.END_DIV);
    WebPageTools.generateHorizontalRuler(pw);

    pw.println(HtmlConstants.DIV);
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
    previousWasAList=doProfessions(pw,tools,main,previousWasAList);
    // Titles
    previousWasAList=doTitles(pw,main,previousWasAList);
    // Homes
    previousWasAList=doHomes(pw,tools,main,previousWasAList);
    // Unions
    previousWasAList=doUnions(pw,pTools,tools,main,previousWasAList);
    // Children
    previousWasAList=doChildren(pw,pTools,previousWasAList);
    // God children
    previousWasAList=doGodChildren(pw,pTools,main,previousWasAList);
    // Acts
    previousWasAList=doActs(pw,tools,previousWasAList);
    // Pictures
    doPictures(pw,tools,previousWasAList);

    pw.println(HtmlConstants.END_DIV);
    WebPageTools.generatePageFooter(pw);
  }

  private boolean doProfessions(PrintWriter pw, PageTools tools, Person main, boolean previousWasAList)
  {
    List<OccupationForPerson> occupations=main.getOccupations();
    if ((occupations!=null)&&(!occupations.isEmpty()))
    {
      pw.println("<br>");
      pw.println("<b>Professions :</b>");
      pw.println(HtmlConstants.UL);
      for(OccupationForPerson occupation : occupations)
      {
        Place place=occupation.getPlace();
        pw.print(HtmlConstants.LI);
        pw.print(occupation.getOccupation());
        int year=occupation.getYear();
        if (year!=0)
        {
          pw.print(" (");
          pw.print(year);
          pw.print(")");
        }
        tools.generatePlace(place," (",")");
        pw.println(HtmlConstants.END_LI);
      }
      pw.println(HtmlConstants.END_UL);
      previousWasAList=true;
    }
    return previousWasAList;
  }

  private boolean doHomes(PrintWriter pw, PageTools tools, Person main, boolean previousWasAList)
  {
    List<HomeForPerson> homes=main.getHomes();
    if ((homes!=null)&&(!homes.isEmpty()))
    {
      if (!previousWasAList)
      {
        pw.println("<br>");
      }
      pw.println("<b>Résidences :</b>");
      pw.println(HtmlConstants.UL);
      Place place;
      for(HomeForPerson home : homes)
      {
        place=home.getPlace();
        pw.print(HtmlConstants.LI);
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
        pw.println(HtmlConstants.END_LI);
      }
      pw.println(HtmlConstants.END_UL);
      previousWasAList=true;
    }
    return previousWasAList;
  }

  private boolean doTitles(PrintWriter pw, Person main, boolean previousWasAList)
  {
    List<TitleForPerson> titles=main.getTitles();
    if ((titles!=null)&&(!titles.isEmpty()))
    {
      if (!previousWasAList)
      {
        pw.println("<br>");
      }
      pw.println("<b>Titres :</b>");
      pw.println(HtmlConstants.UL);
      for(TitleForPerson title : titles)
      {
        pw.print(HtmlConstants.LI);
        Integer year=title.getYear();
        if (year!=null)
        {
          pw.print("(");
          pw.print(year);
          pw.print(") ");
        }
        String text=title.getTitle();
        pw.print(text);
        pw.println(HtmlConstants.END_LI);
      }
      pw.println(HtmlConstants.END_UL);
      previousWasAList=true;
    }
    return previousWasAList;
  }

  private boolean doUnions(PrintWriter pw, PersonTools pTools, PageTools tools, Person main, boolean previousWasAList)
  {
    List<Union> unions=_data.getActs().getUnions();
    List<Act> unionActs=_data.getActs().getUnionActs();

    if ((unions!=null)&&(!unions.isEmpty()))
    {
      if (!previousWasAList)
      {
        pw.println("<br>");
      }
      pw.println("<b>Mariages :</b>");
      pw.println(HtmlConstants.UL);
      int i=0;
      for(Union u : unions)
      {
        Act unionAct=unionActs.get(i);
        doUnion(pw,main,u,unionAct,pTools,tools);
        i++;
      }
      pw.println(HtmlConstants.END_UL);
      previousWasAList=true;
    }
    return previousWasAList;
  }

  private void doUnion(PrintWriter pw, Person main, Union u, Act unionAct, PersonTools pTools, PageTools tools)
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
    pw.print(HtmlConstants.LI);
    String uDate=PageTools.generateDate(u.getDate(),u.getInfos());
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
    pw.println(HtmlConstants.END_LI);
  }

  private boolean doChildren(PrintWriter pw, PersonTools pTools, boolean previousWasAList)
  {
    List<Person> children=_data.getChildren();
    if ((children!=null)&&(!children.isEmpty()))
    {
      Collections.sort(children,new PersonComparator(COMPARISON_CRITERIA.BIRTH_DATE));
      if (!previousWasAList)
      {
        pw.println("<br>");
      }
      pw.println("<b>Enfants :</b>");
      pw.println(HtmlConstants.UL);
      pTools.setUseSexIcon(true);
      for(Person child : children)
      {
        pw.print(HtmlConstants.LI);
        pTools.generatePersonName(child);
        pw.println(HtmlConstants.END_LI);
      }
      pTools.setUseSexIcon(false);
      pw.println(HtmlConstants.END_UL);
      previousWasAList=true;
    }
    return previousWasAList;
  }

  private boolean doGodChildren(PrintWriter pw, PersonTools pTools, Person main, boolean previousWasAList)
  {
    List<Person> godChildren=_data.getGodChildren();

    if ((godChildren!=null)&&(!godChildren.isEmpty()))
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
      pw.println(HtmlConstants.UL);
      pTools.setUseSexIcon(true);
      for(Person godChild : godChildren)
      {
        pw.print(HtmlConstants.LI);
        PageTools.generateDate(godChild.getBirthDate(),godChild
            .getBirthInfos());
        pw.print(' ');
        pTools.generatePersonName(godChild);
        pw.println(HtmlConstants.END_LI);
      }
      pTools.setUseSexIcon(false);
      pw.println(HtmlConstants.END_UL);
      previousWasAList=true;
    }
    return previousWasAList;
  }

  private boolean doActs(PrintWriter pw, PageTools tools, boolean previousWasAList)
  {
    List<Act> oActs=_data.getActs().getOtherActs();

    if (!previousWasAList)
    {
      pw.println("<br>");
    }
    if ((oActs!=null)&&(!oActs.isEmpty()))
    {
      pw.println("<b>Actes :</b>");
      pw.println(HtmlConstants.UL);
      for(Act act : oActs)
      {
        pw.print(HtmlConstants.LI);
        tools.generateActLink(act,act.getP1(),act.getP2());
        pw.println(HtmlConstants.END_LI);
      }
      pw.println(HtmlConstants.END_UL);
      previousWasAList=true;
    }
    else
    {
      pw.println("<b>Pas d'autres actes.</b>");
      pw.println("<br>");
      previousWasAList=false;
    }
    return previousWasAList;
  }

  private void doPictures(PrintWriter pw, PageTools tools, boolean previousWasAList)
  {
    List<Picture> pictures=_data.getPictures();

    if (!previousWasAList)
    {
      pw.println("<br>");
    }
    if ((pictures!=null)&&(!pictures.isEmpty()))
    {
      pw.println("<b>Photos :</b>");
      pw.println(HtmlConstants.UL);
      for(Picture picture : pictures)
      {
        pw.print(HtmlConstants.LI);
        tools.generatePictureLink(picture);
        pw.println(HtmlConstants.END_LI);
      }
      pw.println(HtmlConstants.END_UL);
    }
    else
    {
      pw.println("<b>Pas de photos.</b>");
      pw.println("<br>");
    }
  }

  private static void doLink(PrintWriter pw, PageParameters parameters, String linkText)
  {
    pw.print("<a href=\"");
    pw.print(parameters.build());
    pw.println("\">"+linkText+"</a>");
  }
}
