
package jmail.options;

public final class ReplyType 
{
  private ReplyType(String name)
  {
    type = name;
  }

  public final String toString()
  {
    return type;
  }
  
  public static ReplyType fromString(String value)
  {
    if (_INCLUDE.equals(value))
      return INCLUDE;
    else if (_ATTACH.equals(value))
      return ATTACH;
    else if (_INDENT.equals(value))
      return INDENT;
    else if (_PREFIX.equals(value))
      return PREFIX;
    return null;
  }
  
  private static final String _INCLUDE = "include";
  public static final ReplyType INCLUDE = new ReplyType(_INCLUDE);
  
  private static final String _ATTACH = "attach";
  public static final ReplyType ATTACH = new ReplyType(_ATTACH);
  
  private static final String _INDENT = "indent";
  public static final ReplyType INDENT = new ReplyType(_INDENT);
  
  private static final String _PREFIX = "prefix";
  public static final ReplyType PREFIX = new ReplyType(_PREFIX);
  
  private String type;
}