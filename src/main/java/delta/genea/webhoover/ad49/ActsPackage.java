package delta.genea.webhoover.ad49;

import java.io.File;

public class ActsPackage
{
  public String _id;
	public String _placeName;
	public String _church;
  public String _actType;
  public String _period;
  public String _source;
  public String _comments;

  public File getDirFile(File rootDir)
  {
    File rootPlaceDir=new File(rootDir,_placeName);
    String dirName=_period;
    String what=_actType;
    if ((what!=null) && (what.length()>0))
    {
      dirName=dirName+" - "+what;
      dirName=dirName.replace("Baptêmes, mariages, sépultures","BMS");
      dirName=dirName.replace("Naissances, mariages, décès","NMD");
      dirName=dirName.replace("Tables décennales","TD");
      dirName=dirName.replace("Mariages, sépultures","MS");
      dirName=dirName.replace("Baptêmes, sépultures","BS");
    }
    File ret=new File(rootPlaceDir,dirName);
    return ret;
  }
}
