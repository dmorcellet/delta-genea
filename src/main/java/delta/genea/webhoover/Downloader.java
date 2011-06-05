package delta.genea.webhoover;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import delta.common.utils.files.FileCopy;
import delta.genea.utils.GeneaLoggers;

public class Downloader
{
  private static final Logger _logger=GeneaLoggers.getGeneaLogger();

  private HttpClient _client;
  private long _files;
  private long _bytes;
  private HashMap<String,String> _cookies;

  public Downloader()
  {
    _client=new HttpClient(new MultiThreadedHttpConnectionManager());
    _client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
    _files=0;
    _bytes=0;
    _cookies=new HashMap<String,String>();
  }

  public HttpClient getHttpClient()
  {
    return _client;
  }

  public boolean downloadPage(String url, File to)
  {
    if (_logger.isInfoEnabled())
    {
      _logger.info("Downloading URL ["+url+"] to file ["+to+"]");
    }
    boolean ret=false;
    GetMethod get=new GetMethod(url);
    try
    {
      get.setFollowRedirects(true);
      int iGetResultCode=_client.executeMethod(get);
      if (_logger.isInfoEnabled())
      {
        _logger.info("Status code : "+iGetResultCode);
      }
      InputStream is=get.getResponseBodyAsStream();
      ret=FileCopy.copy(is,to);
      _bytes+=to.length();
      _files++;
      catchCookies(get);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      get.releaseConnection();
    }
    return ret;
  }

  public boolean downloadPageAsPost(String url, File to, Map<String,String> parameters)
  {
    if (_logger.isInfoEnabled())
    {
      _logger.info("Downloading URL ["+url+"] to file ["+to+"]");
    }
    boolean ret=false;
    PostMethod post=new PostMethod(url);
    for(Map.Entry<String,String> parameter : parameters.entrySet())
    {
      post.setParameter(parameter.getKey(),parameter.getValue());
    }
    try
    {
      //get.setFollowRedirects(true);
      int iGetResultCode=_client.executeMethod(post);
      if (_logger.isInfoEnabled())
      {
        _logger.info("Status code : "+iGetResultCode);
      }
      InputStream is=post.getResponseBodyAsStream();
      ret=FileCopy.copy(is,to);
      _bytes+=to.length();
      _files++;
      catchCookies(post);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      post.releaseConnection();
    }
    return ret;
  }

  private void catchCookies(HttpMethodBase method)
  {
    _cookies.clear();
    Header[] headers=method.getRequestHeaders();
    for(int i=0;i<headers.length;i++)
    {
      String name=headers[i].getName();
      if ("Cookie".equals(name))
      {
        String value=headers[i].getValue();
        int separator=value.indexOf(' ');
        if (separator!=-1)
        {
          String keyValue=value.substring(separator+1);
          separator=keyValue.indexOf('=');
          String cookieName=keyValue.substring(0,separator);
          String cookieValue=keyValue.substring(separator+1);
          separator=cookieValue.indexOf(';');
          if (separator!=-1)
          {
            cookieValue=cookieValue.substring(0,separator);
          }
          _cookies.put(cookieName,cookieValue);
        }
      }
    }
  }

  public String getCookieValue(String cookieName)
  {
    return _cookies.get(cookieName);
  }

  public void status()
  {
    System.out.println("Downloaded "+_files+" file(s) - "+((float)_bytes)/(1024*1024)+"Mo");
  }

  public void stop()
  {
    status();
  }
}
