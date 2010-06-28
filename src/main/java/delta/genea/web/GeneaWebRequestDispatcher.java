package delta.genea.web;

import delta.common.framework.web.WebRequestDispatcher;
import delta.genea.web.pages.ActPage;
import delta.genea.web.pages.ActPageParameters;
import delta.genea.web.pages.ActsFromPlacePage;
import delta.genea.web.pages.ActsFromPlaceParameters;
import delta.genea.web.pages.AncestorsListPage;
import delta.genea.web.pages.AncestorsListPageParameters;
import delta.genea.web.pages.AncestorsPage;
import delta.genea.web.pages.AncestorsPageParameters;
import delta.genea.web.pages.CommonAncestorsPage;
import delta.genea.web.pages.CommonAncestorsPageParameters;
import delta.genea.web.pages.CousinsPage;
import delta.genea.web.pages.CousinsPageParameters;
import delta.genea.web.pages.DatesToSearchPage;
import delta.genea.web.pages.DatestoSearchPageParameters;
import delta.genea.web.pages.DescendantsPage;
import delta.genea.web.pages.DescendantsPageParameters;
import delta.genea.web.pages.ImagePage;
import delta.genea.web.pages.ImagePageParameters;
import delta.genea.web.pages.NamePage;
import delta.genea.web.pages.NamePageParameters;
import delta.genea.web.pages.PersonPage;
import delta.genea.web.pages.PersonPageParameters;
import delta.genea.web.pages.PicturePage;
import delta.genea.web.pages.PicturePageParameters;
import delta.genea.web.pages.PicturesPage;
import delta.genea.web.pages.PlacesPage;
import delta.genea.web.pages.UnionsPage;
import delta.genea.web.pages.UnionsPageParameters;

public class GeneaWebRequestDispatcher extends WebRequestDispatcher
{
  public GeneaWebRequestDispatcher()
  {
    super();
    addNewActionPage(PersonPageParameters.ACTION_VALUE,PersonPage.class);
    addNewActionPage(ActPageParameters.ACTION_VALUE,ActPage.class);
    addNewActionPage(AncestorsPageParameters.ACTION_VALUE,AncestorsPage.class);
    addNewActionPage(AncestorsListPageParameters.ACTION_VALUE,AncestorsListPage.class);
    addNewActionPage(DescendantsPageParameters.ACTION_VALUE,DescendantsPage.class);
    addNewActionPage(NamePageParameters.ACTION_VALUE,NamePage.class);
    addNewActionPage(CousinsPageParameters.ACTION_VALUE,CousinsPage.class);
    addNewActionPage(CommonAncestorsPageParameters.ACTION_VALUE,CommonAncestorsPage.class);
    addNewActionPage(ImagePageParameters.ACTION_VALUE,ImagePage.class);
    addNewActionPage("PLACES",PlacesPage.class);
    addNewActionPage("PICTURES",PicturesPage.class);
    addNewActionPage(ActsFromPlaceParameters.ACTION_VALUE,ActsFromPlacePage.class);
    addNewActionPage(DatestoSearchPageParameters.ACTION_VALUE,DatesToSearchPage.class);
    addNewActionPage(UnionsPageParameters.ACTION_VALUE,UnionsPage.class);
    addNewActionPage(PicturePageParameters.ACTION_VALUE,PicturePage.class);
  }
}
