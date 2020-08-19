package jmail;

public final class MessageType 
{

  public static final MessageType NORMAL = new MessageType("Normal");
  
  public static final MessageType REPLY = new MessageType("Reply");

  public static final MessageType REPLY_ALL = new MessageType("Reply All");

  public static final MessageType FORWARD = new MessageType("Forward");
  
  private MessageType(String type)
  {
    this.type = type;
  }
  
  public final String toString()
  {
    return type;
  }
  private String type;
}