package jmail.ui;

public interface StatusListener 
{
  public void setStatus(String message);
  
  public void clearStatus();
}