package delta.genea.data;

import delta.common.framework.objects.data.DataObject;
import delta.common.framework.objects.data.ObjectSource;

public class ActText extends DataObject<ActText>
{
  // Relations
  // ...
  // Sets
  // ...

  public static final String CLASS_NAME="ACT_TEXT";

  private String _text;

  @Override
  public String getClassName() { return CLASS_NAME; }

  public ActText(long primaryKey, ObjectSource<ActText> source)
  {
    super(primaryKey,source);
    _text="";
  }

  public String getText()
  {
    return _text;
  }

  public void setText(String text)
  {
    if (text==null)
    {
      text="";
    }
    _text=text;
  }

  @Override
  public String toString()
  {
    return _text;
  }
}
