package delta.genea.webhoover.ad01;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import delta.common.utils.files.TextFileReader;

public class PlacePageParser
{
	private static final String START_OF_PLACE_ENTRY="<td valign=\"top\" class=\"nom_commune\" style=\"width:55%;\"><span>";
	private static final String START_OF_LINE_4="<td valign=\"top\" align=\"middle\" style=\"width:8%;\"><a href=\"";
	private static final String END_OF_SPAN="</span>";
	private static final String END_OF_TD="</td>";

	public List<ActsPackage> parseFile(File file)
	{
		List<ActsPackage> ret=new ArrayList<ActsPackage>();
		List<String> lines=TextFileReader.readAsLines(file);
		String line;
		ActsPackage actsPackage;
		for(Iterator<String> it=lines.iterator();it.hasNext();)
		{
			line=it.next();
			if (line.startsWith(START_OF_PLACE_ENTRY))
			{
				List<String> linesOfInterest=new ArrayList<String>();
				linesOfInterest.add(line);
				for(int i=0;i<4;i++)
				{
					linesOfInterest.add(it.next());
				}
				actsPackage=handleEntry(linesOfInterest);
				ret.add(actsPackage);
			}
		}
		return ret;
	}

	private ActsPackage handleEntry(List<String> lines)
	{
		String line0=lines.get(0);
		String placeName=line0.substring(START_OF_PLACE_ENTRY.length());
		int index=placeName.indexOf(END_OF_SPAN);
		placeName=placeName.substring(0,index);
		System.out.println("Place name : "+placeName);
		String actType="R";
		String period=lines.get(2);
		period=removeUntil(period,">");
		index=period.indexOf(END_OF_TD);
		period=period.substring(0,index);
		System.out.println("Period : "+period);
		String link=lines.get(3);
		link=link.substring(START_OF_LINE_4.length());
		index=link.indexOf("\"");
		link=link.substring(0,index);
		System.out.println("link : "+link);
		if (link.equals("#"))
		{
			link=null;
		}
		else
		{
			link=Constants.ROOT_SITE+link;
		}
		ActsPackage ret=new ActsPackage();
		ret._placeName=placeName;
		ret._actType=actType;
		ret._period=period;
		ret._link=link;
		return ret;
	}

	private String removeUntil(String source, String limit)
	{
		String ret=source;
		int index=source.indexOf(limit);
		if (index!=-1)
		{
			ret=ret.substring(index+limit.length());
		}
		return ret;
	}
}
