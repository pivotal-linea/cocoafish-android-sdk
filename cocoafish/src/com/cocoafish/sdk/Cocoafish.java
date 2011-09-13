package com.cocoafish.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.FileNameMap;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;
import oauth.signpost.signature.HmacSha1MessageSigner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

public class Cocoafish {
	private String appKey = null;
	private OAuthConsumer consumer = null;

	private HttpClient httpClient = null;

	public Cocoafish(String appKey) {
		this.appKey = appKey;
		httpClient = new DefaultHttpClient();
	}

	public Cocoafish(String consumerKey, String consumerSecret) {
		consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
		consumer.setMessageSigner(new HmacSha1MessageSigner());
		consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
		httpClient = new DefaultHttpClient();
	}

	/**
	 * 
	 * @param url
	 *            The last fragment of request url
	 * @param method
	 *            It only can be one of CCRequestMthod.GET, CCRequestMthod.POST,
	 *            CCRequestMthod.PUT, CCRequestMthod.DELETE.
	 * @param data
	 *            The name-value pairs which is ready to be sent to server, the
	 *            value only can be String type or java.io.File type
	 * @param useSecure
	 *            Decide whether use http or https protocol.
	 * @return
	 * @throws IOException
	 *             If there is network problem, the method will throw this type
	 *             of exception.
	 * @throws CocoafishError
	 *             If other problems cause the request cannot be fulfilled, the
	 *             CocoafishError will be threw.
	 */
	public CCResponse sendRequest(String url, CCRequestMethod method, Map<String, Object> data, boolean useSecure) throws CocoafishError {
		CCResponse response = null;

		try {
			// parameters
			List<NameValuePair> paramsPairs = null; // store all request
			Map<String, File> fileMap = null; // store the requested file and its parameter name

			if (data != null && !data.isEmpty()) {
				Iterator<String> it = data.keySet().iterator();
				while (it.hasNext()) {
					String name = it.next();
					Object value = data.get(name);
					
					if (value instanceof String) { 
						if (paramsPairs == null) 
							paramsPairs = new ArrayList<NameValuePair>();
						paramsPairs.add(new BasicNameValuePair(name, (String)value)); 
					} else if (value instanceof File) { 
						if (fileMap == null) 
							fileMap = new HashMap<String, File>();
						fileMap.put(name, (File) value);
					}
				}
			}

			StringBuffer requestUrl = null;
			if (useSecure) {
				requestUrl = new StringBuffer(CCConstants.BASE_URL_SECURE);
			} else {
				requestUrl = new StringBuffer(CCConstants.BASE_URL);
			}
			requestUrl.append(url);

			if (appKey != null) {
				requestUrl.append(CCConstants.KEY);
				requestUrl.append(appKey);
			}
			
			if(method == CCRequestMethod.GET || method == CCRequestMethod.DELETE) {
				if(paramsPairs != null && !paramsPairs.isEmpty()) {
					String queryString = URLEncodedUtils.format(paramsPairs, CCConstants.ENCODING_UTF8);
					if(requestUrl.toString().indexOf("?") > 0) {
						requestUrl.append("&");
						requestUrl.append(queryString);
					} else {
						requestUrl.append("?");
						requestUrl.append(queryString);
					}
				}
			}
			
			URI reqUri = new URL(requestUrl.toString()).toURI();
			
			HttpUriRequest request = null;
			if (method == CCRequestMethod.GET) {
				request = new HttpGet(reqUri);
			}
			if (method == CCRequestMethod.POST) {
				request = new HttpPost(reqUri);
			}
			if (method == CCRequestMethod.PUT) {
				request = new HttpPut(reqUri);
			}
			if (method == CCRequestMethod.DELETE) {
				request = new HttpDelete(reqUri);
			}

			if (request == null)
				throw new CocoafishError("The request method is invalid.");

			if (fileMap != null && fileMap.size() > 0) {
				CCMultipartEntity entity = new CCMultipartEntity();

				// Append the nameValuePairs to request's url string
				if (paramsPairs != null && !paramsPairs.isEmpty()) {
					for(NameValuePair pair: paramsPairs) {
						entity.addPart(URLEncoder.encode(pair.getName()), URLEncoder.encode(pair.getValue()));
					}
				}

				// Add up the file to request's entity.
				Set<String> nameSet = fileMap.keySet();
				Iterator<String> it = nameSet.iterator();
				if (it.hasNext()) {
					String name = it.next();
					File file = fileMap.get(name);

					FileNameMap fileNameMap = URLConnection.getFileNameMap();
					String mimeType = fileNameMap.getContentTypeFor(file.getName());
					// Assume there is only one file in the map.
					entity.addPart(name, new FileBody(file, mimeType));
				}
				if (request instanceof HttpEntityEnclosingRequestBase) {
					((HttpEntityEnclosingRequestBase) request).setEntity(entity);
				}
			} else {
				if (paramsPairs != null && !paramsPairs.isEmpty()) {
					if (request instanceof HttpEntityEnclosingRequestBase) {
						((HttpEntityEnclosingRequestBase) request).setEntity(new UrlEncodedFormEntity(paramsPairs));
					}
				}
			}

			if (consumer != null) {
				try {
					consumer.sign(request);
				} catch (OAuthMessageSignerException e) {
					throw new CocoafishError(e.getLocalizedMessage());
				} catch (OAuthExpectationFailedException e) {
					throw new CocoafishError(e.getLocalizedMessage());
				} catch (OAuthCommunicationException e) {
					throw new CocoafishError(e.getLocalizedMessage());
				}
			}

			HttpContext localContext = new BasicHttpContext();
			HttpResponse httpResponse = httpClient.execute(request, localContext);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);

				// A Simple JSONObject Creation
				JSONObject jsonObject = new JSONObject(result);
				response = new CCResponse(jsonObject);
			}
		} catch (Exception e) {
			throw new CocoafishError(e.getLocalizedMessage());
		}
		return response;
	}

	/*
	 * To convert the InputStream to String we use the
	 * BufferedReader.readLine() method. We iterate until the BufferedReader
	 * return null which means there's no more data to read. Each line will
	 * appended to a StringBuilder and returned as String.
	 */
	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}