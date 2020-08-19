package jmail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.Hashtable;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.MultipartDataSource;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import jmail.options.MailOptions;
import jmail.options.ReplyType;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public final class MessageHelper 
{

  private static void copyContent(InputStream istream, OutputStream ostream)
    throws IOException
  {
    final byte[] buffer = new byte[4096];
    int bytes;
    while ((bytes = istream.read(buffer)) > 0)
      ostream.write(buffer, 0 , bytes);
  }
  
  private static final String DELIMITER = "------ Original Message -----";
  
  private static void copyContent(Part part, String file, String prefix)
    throws Exception
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream()));
    PrintWriter writer = new PrintWriter(new FileWriter (file));
    
    writer.println(DELIMITER);
    
    String line;
    while ( (line = reader.readLine()) != null)
    {
      writer.print(prefix);
      writer.println(line);
    }
    writer.close();
  }
  
  private static BodyPart fromBuffer(Buffer buffer) throws MessagingException
  {
    String text = buffer.getText(0, buffer.getLength());
    MimeBodyPart part = new MimeBodyPart();
    String mode = buffer.getMode().getName();
    
    String contentType;
    if (mode.equals("html"))
      contentType = "text/html";
    else if (mode.equals("xml"))
      contentType = "text/xml";
    else
      contentType = "text/plain";
    part.setContent(text, contentType);
    return part;
  }
  
  private static BodyPart fromFile(File file) throws MessagingException
  {
    final FileDataSource source = new FileDataSource(file);
    final MimeBodyPart part = new MimeBodyPart();
    part.setDataHandler(new DataHandler(source));
    part.setFileName(file.getName());
    return part;
  }
   
  public static void saveAttachment(Part part, String file) throws Exception
  {
    saveAttachment(part, new File(file));
  }
  
  public static void saveAttachment(Part part, File file) throws Exception
  {
    if (file.isDirectory())
    {
      String name = part.getFileName();
      if (name == null)
      {
        file = File.createTempFile("attachment", ".dat", file);
      }
      else
      {
        file = new File(file, part.getFileName());
      }
    }
    
    FileOutputStream ostream = new FileOutputStream(file);
    InputStream istream = part.getInputStream();
    copyContent(istream, ostream);
    ostream.close();
  }
  
  public static void fillMessage(Message message, Buffer buffer)
    throws Exception
  {
    BodyPart part = fromBuffer(buffer);
    MimeMultipart content = (MimeMultipart) message.getContent();
    content.addBodyPart(part, 0);
  }
  
  public static Message createReply(Session session, Message message, MessageType type)
    throws Exception
  {
    MimeMessage reply;

    String propertyName = null;
    if (type == MessageType.REPLY || type == MessageType.REPLY_ALL)
    {
      boolean replyAll = false;
      if (type == MessageType.REPLY_ALL)
        replyAll = true;
      reply = (MimeMessage) message.reply(replyAll);
      propertyName = MailOptions.REPLY_MESSAGE_TYPE;
    }
    else 
    {
      reply = new MimeMessage(session);
      String subject = (message == null ) ? "No Subject" : "Fwd: " + message.getSubject();
      reply.setSubject(subject);
  
      if (type == MessageType.FORWARD)
        propertyName = MailOptions.REPLY_MESSAGE_TYPE;
    }

    final String name = jEdit.getProperty(MailOptions.NAME, System.getProperty("user.name"));
    final String email = jEdit.getProperty(MailOptions.MAIL_ADDRESS);
    final InternetAddress from = new InternetAddress(email, name);
    reply.setSender(from);
    
    final MimeMultipart content = new MimeMultipart();
    reply.setContent(content);
    
    if (propertyName != null)
    {
      ReplyType replyType = ReplyType.fromString(jEdit.getProperty(propertyName, "include"));
      if (replyType == ReplyType.ATTACH)
      {
      }
    }
    return reply;
  }
  
  public static final Part getMailContent(Part part)
  {
    try
    {
      final DataSource source = part.getDataHandler().getDataSource();
      if (source instanceof MultipartDataSource)
      {
        MultipartDataSource dataSource = (MultipartDataSource)source;
        return getMailContent(dataSource.getBodyPart(0));
      }
      else
      {
        return part;
      }
    }
    catch (MessagingException e)
    {
      return null;
    }        
  }

  /**
   * Adds the given file as an attachment to the message.
   * @param message the message to which the attachment is to be added
   * @param file the file to add.
   * @throws Exception 
   */
  public static void addAttachment(Message message, File file, String contentId)
    throws MessagingException, IOException
  {
    final MimeMultipart content = (MimeMultipart) message.getContent();
    BodyPart attachment = fromFile(file);
    if (contentId != null)
      attachment.setHeader("Content-ID", contentId);
    content.addBodyPart(attachment);
  }
  
  
  public static final void setContentType(Part message, Buffer buffer)
  {
    try
    {
      String type = message.getContentType().toLowerCase();
      if (type.startsWith("text"))
      {
        int index = type.indexOf(';');
        String mode = type.substring(5, index);
        if (mode == null || mode.equals("plain"))
        {
          mode = DEFAULT_MODE;
        }
        buffer.setMode(mode);
      }
      else
      {
        buffer.setMode(DEFAULT_MODE);
      }
    }
    catch(Exception me)
    {
      buffer.setMode(DEFAULT_MODE);
    }
  }
  
  public static final Buffer getBuffer(View view, Part message, String file)
    throws Exception
  {
    Buffer buffer = getReplyBuffer(view, message, file, MessageType.NORMAL);
    buffer.setReadOnly(true);
    return buffer;
  }
  
  public static final Buffer getReplyBuffer(View view, Part message, String file, MessageType type)
    throws Exception
  {
    String tempDir = jEdit.getSettingsDirectory();
    File tempFile = new File(tempDir, file);
    if (type == MessageType.NORMAL)
    {
      saveAttachment(message, tempFile.getAbsolutePath());
    }    
    else
    {
      String propertyName = MailOptions.REPLY_MESSAGE_TYPE;
      if (type == MessageType.FORWARD)
        propertyName = MailOptions.FORWARD_MESSAGE_TYPE;
      ReplyType indentType = 
        ReplyType.fromString(jEdit.getProperty(propertyName, "include"));
      copyContent(message, tempFile.getAbsolutePath(), getIndentString(indentType) );
    }
    
    Buffer buffer = jEdit.openFile(view, tempDir, tempFile.getPath(), false, new Hashtable());
    if (type != MessageType.NORMAL)
      buffer.setReadOnly(false);
    setContentType(message, buffer);
    return buffer;    
  }
  
  
  private static final String getIndentString(ReplyType type)
  {
    if (type == ReplyType.INDENT)
      return INDENT_STRING;
    else if (type == ReplyType.PREFIX)
      return PREFIX_STRING;
    return "";
  }
  
  public static final String DEFAULT_MODE = "mail";
  public static final String INDENT_STRING = "\t";
  public static final String PREFIX_STRING = ">>>>";
  
}