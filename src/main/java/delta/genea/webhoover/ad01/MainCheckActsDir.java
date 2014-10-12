package delta.genea.webhoover.ad01;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainCheckActsDir
{
	public static String getImageFileName(int nb)
	{
		String nbStr=String.valueOf(nb);
		if (nb<10) nbStr="0"+nbStr;
		if (nb<100) nbStr="0"+nbStr;
		return nbStr+".jpg";
	}

	public static String getBigImageFileName(int nb)
	{
		String nbStr=String.valueOf(nb);
		if (nb<10) nbStr="0"+nbStr;
		if (nb<100) nbStr="0"+nbStr;
		return "big"+nbStr+".png";
	}

	public static void handleChildDir(File childDir)
	{
		String[] childs=childDir.list();
		int nbchilds=childs.length;
		if (nbchilds%2!=0)
		{
			System.err.println("Odd number of files in "+childDir);
		}
		List<String> names=new ArrayList<String>(nbchilds);
		for(int i=0;i<nbchilds;i++) names.add(childs[i]);
		Collections.sort(names);
		int nb=nbchilds/2;
		for(int i=0;i<nb;i++)
		{
			String expected=getImageFileName(i);
			if (!names.get(i).equals(expected))
			{
				System.err.println("Expected : "+expected);
			}
			String expected2=getBigImageFileName(i);
			if (!names.get(i+nb).equals(expected2))
			{
				System.err.println("Expected : "+expected2);
			}
		}
	}
	public static void doIt(File dir)
	{
		File[] childs=dir.listFiles();
		File child;
		for(int i=0;i<childs.length;i++)
		{
			child=childs[i];
			if (!child.isDirectory())
			{
				System.err.println("Not a directory : "+child);
			}
			else
			{
				System.out.println("Handling : "+child);
				handleChildDir(child);
			}
		}
	}
	public static void main(String[] args)
	{
		doIt(Constants.ROOT_DIR);
		doIt(new File("D:\\ArchivesDeLAin\\stGermain"));
		doIt(new File("D:\\ArchivesDeLAin\\stBois"));
		doIt(new File("D:\\ArchivesDeLAin\\colomieu"));
	}
}
