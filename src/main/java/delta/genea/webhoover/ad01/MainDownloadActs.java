package delta.genea.webhoover.ad01;

import java.io.File;
import java.util.List;

import delta.genea.webhoover.Downloader;

public class MainDownloadActs
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Downloader downloader=new Downloader();
		File mainPage=Constants.MAIN_PAGE_FILE;
		File parent=mainPage.getParentFile();
		parent.mkdirs();
System.out.println("Main page : "+Constants.MAIN_PAGE);
		downloader.downloadPage(Constants.MAIN_PAGE, Constants.MAIN_PAGE_FILE);
		List<ActsPackage> actsPackages=new PlacePageParser().parseFile(Constants.MAIN_PAGE_FILE);
		int nb=actsPackages.size();
		boolean doIt=false;
		for(int i=0;i<nb;i++)
		{
			ActsPackage a=actsPackages.get(i);
			//if (a._period.startsWith("1845"))
			{
				doIt=true;
			}
			if (doIt)
			{
				PackagePageParser parser=new PackagePageParser(downloader,a);

				parser.parse();
			}
		}
		//mainPage.delete();

	}
}
