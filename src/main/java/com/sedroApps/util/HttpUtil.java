 /*************************************************************************
 * Ledbetter CONFIDENTIAL
 * __________________
 * 
 * [2018] - [2020] Aaron Ledbetter
 * All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains
 * the property of Aaron Ledbetter. The intellectual and technical 
 * concepts contained herein are proprietary to Aaron Ledbetter and 
 * may be covered by U.S. and Foreign Patents, patents in process, 
 * and are protected by trade secret or copyright law. 
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Aaron Ledbetter.
 */

package main.java.com.sedroApps.util;

import org.apache.commons.httpclient.CircularRedirectException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.FuturePromise;
import org.eclipse.jetty.util.Jetty;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class HttpUtil {
	
    /**
     * Post data to url over https via http proxy that is using basic auth
     * 
     * @param urlString
     *            Url to post data to (will start with https://
     * @param post_data
     *            Data to post to URL
     * @param http_proxy
     *            Http proxy ip address or hostname
     * @param proxy_user
     *            proxy username to use
     * @param proxy_pass
     *            proxy password to use
     * @return Returns data from server, null on error
     */
    public static String postDataHttps(String urlString, String post_data, String http_proxy, String proxy_user, String proxy_pass, HashMap<String, String> headers) {
    	return postDataHttps(urlString, post_data, http_proxy, proxy_user, proxy_pass, false, headers);
    }
    public static String postDataHttpsJson(String urlString, String post_data, String http_proxy, String proxy_user, String proxy_pass, HashMap<String, String> headers) {
    	return postDataHttps(urlString, post_data, http_proxy, proxy_user, proxy_pass, true, headers);
    }

    /**
     * Get data from url over https via http proxy that is using basic auth
     * 
     * @param urlString
     *            Url to get data from (will start with https://
     * @param http_proxy
     *            Http proxy ip address or hostname
     * @param proxy_user
     *            proxy username to use
     * @param proxy_pass
     *            proxy password to use
     * @return Returns data from server, null on error
     */
    public static String getDataHttps(String urlString, String http_proxy, String proxy_user, String proxy_pass, HashMap<String, String> headers) {
    	return getDataHttps(urlString, http_proxy, proxy_user, proxy_pass, false, headers);
    }
    public static String getDataHttpsJson(String urlString, String http_proxy, String proxy_user, String proxy_pass, HashMap<String, String> headers) {
    	return getDataHttps(urlString, http_proxy, proxy_user, proxy_pass, true, headers);
    }

    
    /**
     * Post data to url over https via http proxy that is using basic auth
     *
     * @param url        Url to post data to (will start with https://
     * @param post_data  Data to post to URL
     * @param proxy_host Http proxy ip address or hostname
     * @param proxy_user proxy username to use
     * @param proxy_pass proxy password to use
     * @return Returns data from server, null on error
     */
    private static String postDataHttps(String url, String post_data, String proxy_host, String proxy_user, String proxy_pass, boolean json, HashMap<String, String> headers) {
        String response = null;
        try {
            HttpPost post = new HttpPost(url);
            post.setEntity(new StringEntity(post_data));
            response = executeRequest(post, proxy_host, proxy_user, proxy_pass, json, headers);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MalformedChallengeException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Get data from url over https via http proxy that is using basic auth
     *
     * @param url        Url to get data from (will start with https://
     * @param proxy_host Http proxy ip address or hostname
     * @param proxy_user proxy username to use
     * @param proxy_pass proxy password to use
     * @return Returns data from server, null on error
     */
    private static String getDataHttps(String url, String proxy_host, String proxy_user, String proxy_pass, boolean json, HashMap<String, String> headers) {
        String response = null;
        try {
            HttpGet request = new HttpGet(url);
            response = executeRequest(request, proxy_host, proxy_user, proxy_pass, json, headers);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MalformedChallengeException e) {
            e.printStackTrace();
        }
        return response;
    }

    private static String executeRequest(final HttpRequestBase request, final String proxy_host, final String proxy_user, final String proxy_pass, boolean json, HashMap<String, String> headers) throws IOException, MalformedChallengeException {
        final boolean isProxyAuthRequired = (proxy_user != null && proxy_pass != null);

        //Configure Proxy
        HttpHost proxy = null;
        CredentialsProvider credsProvider = null;
        if (proxy_host != null) {
            proxy = new HttpHost(proxy_host, 80);
            if (isProxyAuthRequired) {
                //Register credentials for proxy
                credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                        new AuthScope(proxy_host, 80),
                        new UsernamePasswordCredentials(proxy_user, proxy_pass));
            }
        }

        //Enable Preemptive authentication on the proxy
        AuthCache authCache = null;
        if (isProxyAuthRequired) {
            authCache = new BasicAuthCache();
            BasicScheme basicScheme = new BasicScheme();
            basicScheme.processChallenge(new BasicHeader(HttpHeaders.PROXY_AUTHENTICATE, "Basic "));
            authCache.put(proxy, basicScheme);
        }
        HttpClientContext localContext = HttpClientContext.create();
        if (authCache != null) {
            localContext.setAuthCache(authCache);
        }

        //Configure proxy on request if needed
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        if (proxy != null) {
            configBuilder.setProxy(proxy);
        }
        RequestConfig config = configBuilder.build();
        request.setConfig(config);
        
        if (json) {
	        request.addHeader("Content-Type", "application/json");
	        request.addHeader("Accept", "application/json");
        }
        if (headers != null && headers.size() > 0) {
            for (String hdr:headers.keySet()) {
            	request.addHeader(hdr, headers.get(hdr));
            }
         }


        // Create client
        HttpClientBuilder clientBuilder = HttpClients.custom();
        if (credsProvider != null) {
            clientBuilder.setDefaultCredentialsProvider(credsProvider);
        }
        CloseableHttpClient httpclient = clientBuilder.build();


        //Execute request
        HttpResponse rsp = httpclient.execute(request, localContext);
		HttpEntity entity = rsp.getEntity();
		if (entity == null) return null;
 		if (rsp.getStatusLine().getStatusCode() >= 200 && rsp.getStatusLine().getStatusCode() < 300 ){ 
 			return EntityUtils.toString(entity);
 		} else {
 			return EntityUtils.toString(entity);
 		}
      
    }
    
	

	public static String postDataDirect(String urlStr, String uriParams) {
		return postDataDirect(urlStr, uriParams, null);
	}

    public static String postDataDirect(String urlStr, String uriParams, HashMap<String, String> headers) {

        try {
            URL url = new URL(urlStr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setAllowUserInteraction(false);
    		connection.setDoInput(true);
    		connection.setDoOutput(true);
    		connection.setIfModifiedSince(0);
    		connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            if (headers != null) {
	            for (String hdr:headers.keySet()) {
	                connection.setRequestProperty(hdr, headers.get(hdr));
	            }
            }
 
    		// write the content and be sure to close          
            if (uriParams != null) {
//           		System.out.println("SynapsePay POST: "+urlStr+"\n" + uriParams);            	
				DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
				dos.writeBytes(uriParams);
				dos.close();
            }
            
            InputStream content = null;
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
            	content = (InputStream)connection.getInputStream();
            } else {
            	content = (InputStream)connection.getErrorStream();
            }                       
            
            BufferedReader in   =  new BufferedReader (new InputStreamReader(content));           
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = in.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            
            String resp = builder.toString();
 //           System.out.println("SynapsePay RESP:\n" + resp); 
            return resp;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }
    /*
    private static String getFileName(String url) {
    	String f = url;
    	try {   		
			URL u = new URL(url);
			f = u.getFile();
		} catch (MalformedURLException e) {}
    	
		int idx = f.lastIndexOf("/");
		if (idx < 0) return f;
		return f.substring(idx+1, f.length());
    }*/
    
    public static String postDataMulti(String urlStr, HashMap<String, String> headers, String fileurl, byte[] bytes) {
    	if (urlStr == null || bytes == null || fileurl == null) return null;
    	String filename = fileurl;
    	//String filename = getFileName(fileurl);
    	//System.out.println("postDataMulti["+filename+"] bytes: "+bytes.length+" to: " + urlStr);
    	
	    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	    
	    builder.addBinaryBody("file", bytes, ContentType.DEFAULT_BINARY, filename);
	    builder.addTextBody("path", "call through");
	    HttpEntity multipart = builder.build();

	    HttpPost post = new HttpPost(urlStr);    
	    post.setEntity(multipart);
        
        // set headers
        post.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:61.0) Gecko/20100101 Firefox/29.0");
      //  post.addHeader("Content-type", "multipart/form-data"); 
      //  post.addHeader("Accept", "application/json");
        if (headers != null && headers.size() > 0) {
            for (String hdr:headers.keySet()) {
            	post.addHeader(hdr, headers.get(hdr));
            }
         }

        // Create client
    	CloseableHttpClient httpclient = HttpClients.createDefault();

        //Execute request
        HttpResponse rsp = null;
		try {
			rsp = httpclient.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (rsp == null) return null;

 		HttpEntity entity = rsp.getEntity();
 		try {
			return EntityUtils.toString(entity);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		return  null;
    }
    
	/*
	 * Get the content of the file as a String for this path
	 * (this will work only for TEXT or HTML
	 */
	 public static String getURLContent(String path, boolean retry) {
		 String res = getURLContent(path);
		 if (res == null && retry) {
			 try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
			 return getURLContent(path);
		 }
		 return res;
	 }
	 
	public static String getURLContent(String path) {
		return getURLContent(path, null);
	}
    public static String getURLContent(String path, HashMap<String, String> headers) {
    	if (path == null) {
    		return null;
    	}

		// Create an instance of HttpClient.
	    HttpClient client = new HttpClient();

	    // Create a method instance.
	    GetMethod method = new GetMethod(path);
	    method.setRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:61.0) Gecko/20100101 Firefox/29.0");
        if (headers != null) {
            for (String hdr:headers.keySet()) {
            	method.addRequestHeader(hdr, headers.get(hdr));
            }
        }
	    
        //System.err.println("getURLContent() URL: "  + path);
	    int sCode = 0;
        try {
            // Execute the method.
            sCode = client.executeMethod(method);

            if (sCode != HttpStatus.SC_OK) {
              //System.err.println("getURLContent() failed: " + method.getStatusLine() + " for: " + path);
              return null;
            } else {
	            // Read the response body.
	            byte[] responseBody = method.getResponseBody();
	            return new String(responseBody);
            }
          } catch (CircularRedirectException e) {
        	 // System.err.println("getURLContent("+sCode+") Circular Redirect ERROR: "  + path);
        	  return null;
          } catch (Exception e) {
        	  System.err.println("getURLContent("+sCode+") exception: "  + path);
        	  //e.printStackTrace();
            return null;
          } finally {
            // Release the connection.
            method.releaseConnection();
          }
    }
	public static InputStream getURLContentInputStream(String path) {
		return getURLContentInputStream(path, null);
	}
    public static InputStream getURLContentInputStream(String path, HashMap<String, String> headers) {
    	if (path == null) {
    		return null;
    	}

		// Create an instance of HttpClient.
	    HttpClient client = new HttpClient();
	    

	    // Create a method instance.
	    GetMethod method = new GetMethod(path);
	   
	    method.setRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:61.0) Gecko/20100101 Firefox/29.0");
        if (headers != null) {
            for (String hdr:headers.keySet()) {
            	method.addRequestHeader(hdr, headers.get(hdr));
            }
        }
	    
        //System.err.println("getURLContent() URL: "  + path);
	    int sCode = 0;
        try {
            // Execute the method.
            sCode = client.executeMethod(method);

            if (sCode != HttpStatus.SC_OK) {
              //System.err.println("getURLContent() failed: " + method.getStatusLine() + " for: " + path);
              return null;
            } else {
	            // Read the response body.
            	return method.getResponseBodyAsStream();
	          //  byte[] responseBody = method.getResponseBody();
	          //  return new String(responseBody);
            }
          } catch (CircularRedirectException e) {
        	 // System.err.println("getURLContent("+sCode+") Circular Redirect ERROR: "  + path);
        	  return null;
          } catch (Exception e) {
        	  System.err.println("getURLContent("+sCode+") v:"+method.getEffectiveVersion()+" exception: "  + path);
        	  //e.printStackTrace();
            return null;
          } finally {
            // Release the connection.
            method.releaseConnection();
          }
    }
    
	/*
	 * Get the content of the file as a String for this path
	 * This will work for images
	 */
    public static byte [] getURLContentBinary(String path) {
    	if (path == null) return null;

		// Create an instance of HttpClient.
	    HttpClient client = new HttpClient();
	    
	    // Create a method instance.
	    GetMethod method = new GetMethod(path);
	    method.getParams().setVersion(HttpVersion.HTTP_1_1);
	    method.setRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:61.0) Gecko/20100101 Firefox/61.0");
	    method.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//	    method.setRequestHeader("Accept-Language", "en-US,en;q=0.5");
//	    method.setRequestHeader("Accept-Encoding", "gzip, deflate");
    
        try {
            // Execute the method.
            int sCode = client.executeMethod(method);

            if (sCode != HttpStatus.SC_OK) {
              System.err.println("getURLContent("+sCode+") v:"+method.getEffectiveVersion()+"  failed: " + method.getStatusLine() + " for: " + path);
              return null;
            } else {
	            // Read the response body.
	            byte[] responseBody = method.getResponseBody();
	            return responseBody;
            }
          } catch (Exception e) {
              System.err.println("getURLContent(UNK) v:"+method.getEffectiveVersion()+"  failed: " + method.getStatusLine() + " for: " + path);

            return null;
          } finally {
            // Release the connection.
            method.releaseConnection();
          }
    }
    
    public static byte [] getURLContentBinary2(String path) {
    	if (path == null) {
    		return null;
    	}
    	/*
    	MetaData.Request request = new MetaData.Request("GET", new HttpURI(path), HttpVersion.HTTP_2, requestFields);
    	HeadersFrame headersFrame = new HeadersFrame(request, null, true);
    	session.newStream(headersFrame, new FuturePromise<>(), responseListener);
    	Stream.Listener responseListener = new Stream.Listener.Adapter() {
    	    @Override
    	    public void onData(Stream stream, DataFrame frame, Callback callback) {
    	        // ... do something with frame.getData()
    	        callback.succeeded();
    	    }
    	}
    	*/
        // Connect to host.
    	URL url = null;
    	try {
			url = new URL(path);
		} catch (MalformedURLException e) {
			return null;
		}
    	
        String host = url.getHost();
        int port = url.getPort();
        if (port <= 0) port = url.getDefaultPort();
        System.out.println("getURLContentBinary2["+host+":"+port+"]");
    	long startTime = System.nanoTime();
    	byte [] data = null;
    	
    	// create a low-level Jetty HTTP/2 client
    	HTTP2Client client2 = new HTTP2Client();

        SslContextFactory sslContextFactory = new SslContextFactory(true);
        client2.addBean(sslContextFactory);
        try {
			client2.start();

	        FuturePromise<Session> sessionPromise = new FuturePromise<>();
	        client2.connect(sslContextFactory, new InetSocketAddress(host, port), new ServerSessionListener.Adapter(), sessionPromise);

	        // Obtain the client Session object.
	        Session session = sessionPromise.get(5, TimeUnit.SECONDS);

	        // Prepare the HTTP request headers.
	        HttpFields requestFields = new HttpFields();
	        requestFields.put("User-Agent", client2.getClass().getName() + "/" + Jetty.VERSION);
	        // Prepare the HTTP request object.
	        MetaData.Request request = new MetaData.Request("GET", new HttpURI(path), org.eclipse.jetty.http.HttpVersion.HTTP_2, requestFields);
	        // Create the HTTP/2 HEADERS frame representing the HTTP request.
	        HeadersFrame headersFrame = new HeadersFrame(request, null, true);

	        // Prepare the listener to receive the HTTP response frames.
	        Stream.Listener responseListener = new Stream.Listener.Adapter()
	        {
	            @Override
	            public void onData(Stream stream, DataFrame frame, Callback callback)
	            {
	                byte[] bytes = new byte[frame.getData().remaining()];
	                frame.getData().get(bytes);
	                int duration = (int) TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime);
	                System.out.println("After " + duration + " seconds: " + new String(bytes));
	         //       data = bytes;
	                callback.succeeded();
	            }
	        };

	        session.newStream(headersFrame, new FuturePromise<>(), responseListener);
	        session.newStream(headersFrame, new FuturePromise<>(), responseListener);
	        session.newStream(headersFrame, new FuturePromise<>(), responseListener);

	        Thread.sleep(TimeUnit.SECONDS.toMillis(20));

	        client2.stop();
        } catch (Throwable t) {
        	t.printStackTrace();
        }
        return data;
    }
    
    /*
    * general POST method with auth to use for heroku
    */
   public static String postData(String urlStr, String uriParams, HashMap<String, String> hdrs) {

       try {
           URL url = new URL(urlStr);

           HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           
           connection.setRequestMethod("POST");
           connection.setAllowUserInteraction(false);
   		   connection.setDoInput(true);
   		   connection.setDoOutput(true);
   		   connection.setIfModifiedSince(0);
   	       connection.setUseCaches(false);
   		   connection.setRequestProperty( "Content-Length", "" + uriParams.length());	
        
   		// add headers
   		if (hdrs != null && hdrs.size() > 0) {
   			for (String s:hdrs.keySet()) {
   		   		connection.setRequestProperty(s, hdrs.get(s));   		   		
   			}
   		}
             
   		// write the content and be sure to close          
           if (uriParams != null) {
				DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
				dos.writeBytes(uriParams);
				dos.close();
           }
           
           InputStream content = (InputStream)connection.getInputStream();
           if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
           	content = (InputStream)connection.getInputStream();
           } else {
           	content = (InputStream)connection.getErrorStream();
           }                       
           
           BufferedReader in   =  new BufferedReader (new InputStreamReader(content));           
           StringBuilder builder = new StringBuilder();
           for (String line = null; (line = in.readLine()) != null;) {
               builder.append(line).append("\n");
           }
           return builder.toString();
       } catch(Exception e) {
           e.printStackTrace();
           return null;
       }
       
   }

}
