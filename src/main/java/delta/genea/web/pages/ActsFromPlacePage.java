package delta.genea.web.pages;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import delta.common.framework.web.WebPageTools;
import delta.common.utils.ParameterFinder;
import delta.genea.data.Act;
import delta.genea.data.ActType;
import delta.genea.data.Place;
import delta.genea.web.GeneaUserContext;

/**
 * Builder for the 'acts from place' HTML page.
 * @author DAM
 */
public class ActsFromPlacePage extends GeneaWebPage
{
  // HTML 4.01 strict validated
  private Long _key;
  private Map<String,List<Act>> _actsMap;
  private Place _place;
  private int _nbActs;

  @Override
  public void parseParameters() throws Exception
  {
    _key=ParameterFinder.getLongParameter(_request,"KEY",Long.valueOf(76));
  }

  @Override
  public void fetchData() throws Exception
  {
    _place=getDataSource().load(Place.class,_key);
    if (_place==null) return;
    _actsMap=new HashMap<String,List<Act>>();
    List<Act> acts=getDataSource().loadRelation(Act.class,Act.ACTS_FROM_PLACE,_key);
    if ((acts!=null) && (acts.size()>0))
    {
      _nbActs=acts.size();
      int nb=_nbActs;
      Act act;
      ActType type;
      List<Act> list;
      for(int i=0;i<nb;i++)
      {
        act=acts.get(i);
        type=act.getActType();
        if (type!=null)
        {
          list=_actsMap.get(type.getType());
          if (list==null)
          {
            list=new ArrayList<Act>();
            _actsMap.put(type.getType(),list);
          }
          list.add(act);
        }
      }
    }
  }

  @Override
  public void generate(PrintWriter pw)
  {
    String title="";
    if (_place!=null)
    {
      title="Actes de "+_place.getFullName();
    }
    WebPageTools.generatePageHeader(title,pw);
    if (_place!=null)
    {
      pw.print("<h1>"); pw.print(title);
      pw.print(" ("); pw.print(_nbActs); pw.print(')'); pw.println("</H1>");
      pw.println("<div>");
      generateActList(pw,ActType.BAPTEM,true);
      generateActList(pw,ActType.BIRTH,true);
      generateActList(pw,ActType.UNION,false);
      generateActList(pw,ActType.WEDDING_CONTRACT,false);
      generateActList(pw,ActType.BURIAL,true);
      generateActList(pw,ActType.DEATH,true);
      pw.println("</div>");
    }
    WebPageTools.generatePageFooter(pw);
  }

  private void generateActList(PrintWriter pw, Long typeKey, boolean useSexIcon)
  {
    ActType type=getDataSource().load(ActType.class,typeKey);
    if (type==null) return;
    List<Act> list=_actsMap.get(type.getType());
    if ((list==null) || (list.size()==0)) return;
    pw.print("<b>");
    pw.print(type.getType());
    pw.print(" (");
    pw.print(list.size());
    pw.print(")");
    pw.println("</b><br>");

    pw.println("<ul>");
    int nb=list.size();
    Act act;
    GeneaUserContext context=(GeneaUserContext)getUserContext();
    PageTools tools=new PageTools(context,pw);
    for(int i=0;i<nb;i++)
    {
      pw.println("<li>");
      act=list.get(i);
      tools.generateActLink(act,act.getP1(),act.getP2());
      pw.println("</li>");
    }
    pw.println("</ul>");
  }
}
