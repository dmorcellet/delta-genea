package delta.genea.web.formatters;

import java.util.HashMap;

import delta.common.utils.tables.DataTable;
import delta.common.utils.tables.DataTableColumn;
import delta.common.utils.tables.DataTableRow;
import delta.common.utils.text.EndOfLine;
import delta.common.utils.text.TextFormatter;

/**
 * Formatter for an HTML data table.
 * @author DAM
 */
public class DataTableHtmlFormatter extends TextFormatter
{
  private HashMap<String,TextFormatter> _formatters;

  /**
   * Constructor.
   */
  public DataTableHtmlFormatter()
  {
    _formatters=new HashMap<String,TextFormatter>();
  }

  /**
   * Set the formatter for a column.
   * @param columnName Name of the targeted column.
   * @param formatter Formatter to use.
   */
  public void setFormatter(String columnName, TextFormatter formatter)
  {
    _formatters.put(columnName,formatter);
  }

  private TextFormatter[] buildFormatters(DataTable table)
  {
    int nbColumns=table.getNbColumns();
    TextFormatter[] ret=new TextFormatter[nbColumns];
    DataTableColumn<?> column;
    String columnName;
    TextFormatter formatter;
    for(int i=0;i<nbColumns;i++)
    {
      column=table.getColumn(i);
      columnName=column.getName();
      formatter=_formatters.get(columnName);
      ret[i]=formatter;
    }
    return ret;
  }

  /**
   * Format the specified object into the given <tt>StringBuilder</tt>.
   * @param o Object to format.
   * @param sb Output string builder.
   */
  @Override
  public void format(Object o, StringBuilder sb)
  {
    DataTable table=null;
    if (o instanceof DataTable) table=(DataTable)o;
    if (table==null) return;

    int nbColumns=table.getNbColumns();
    TextFormatter[] formatters=buildFormatters(table);
    sb.append("<table border=\"0\" width=\"95%\">");
    sb.append(EndOfLine.NATIVE_EOL);
    // Header
    sb.append("<tr>");
    DataTableColumn<?> column;
    for(int i=0;i<nbColumns;i++)
    {
      column=table.getColumn(i);
      sb.append("<th>");
      sb.append(column.getName());
      sb.append("</th>");
      sb.append(EndOfLine.NATIVE_EOL);
    }
    sb.append("</tr>");
    sb.append(EndOfLine.NATIVE_EOL);
    // Contents
    int nbRows=table.getNbRows();
    DataTableRow row;
    Object data;
    TextFormatter formatter;
    for(int j=0;j<nbRows;j++)
    {
      row=table.getRow(j);
      sb.append("<tr>");
      for(int i=0;i<nbColumns;i++)
      {
        formatter=formatters[i];
        sb.append("<td>");
        data=row.getData(i);
        if (formatter!=null)
        {
          formatter.format(data,sb);
        }
        else
        {
          sb.append(data);
        }
        sb.append("</td>");
        sb.append(EndOfLine.NATIVE_EOL);
      }
      sb.append("</tr>");
      sb.append(EndOfLine.NATIVE_EOL);
    }
    sb.append("</table>");
    sb.append(EndOfLine.NATIVE_EOL);
  }
}
