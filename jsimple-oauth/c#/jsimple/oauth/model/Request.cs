using System;
using System.Diagnostics;
using System.Collections.Generic;

namespace jsimple.oauth.model
{

	using IOUtils = jsimple.io.IOUtils;
	using jsimple.net;
	using OAuthConnectionException = jsimple.oauth.exceptions.OAuthConnectionException;
	using OAuthException = jsimple.oauth.exceptions.OAuthException;



	/// <summary>
	/// Represents an HTTP Request object
	/// 
	/// @author Pablo Fernandez
	/// </summary>
	public class Request
	{
		public const string DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";

		private string url;
		private string verb;
		private ParameterList queryStringParams;
		private ParameterList bodyParams;
		private IDictionary<string, string> headers;
		private string payload = null;
		private HttpRequest httpRequest = null;
		private sbyte[] bytePayload = null; //@Nullable
		private int? timeout = null;

		/// <summary>
		/// Creates a new Http Request
		/// </summary>
		/// <param name="verb"> Http Verb (GET, POST, etc) </param>
		/// <param name="url">  url with optional querystring parameters. </param>
		public Request(string verb, string url)
		{
			this.verb = verb;
			this.url = url;
			this.queryStringParams = new ParameterList();
			this.bodyParams = new ParameterList();
			this.headers = new Dictionary<string, string>();
		}

		/// <summary>
		/// Execute the request and return a <seealso cref="OAuthResponse"/>
		/// </summary>
		/// <returns> Http Response </returns>
		/// <exception cref="RuntimeException"> if the connection cannot be created. </exception>
		public virtual OAuthResponse send()
		{
			try
			{
				createConnection();
				return doSend();
			}
			catch (Exception e)
			{
				throw new OAuthConnectionException(e);
			}
		}

		private void createConnection()
		{
			string completeUrl = CompleteUrl;
			if (httpRequest == null)
				httpRequest = new HttpRequest(completeUrl);
		}

		/// <summary>
		/// Returns the complete url (host + resource + encoded querystring parameters).
		/// </summary>
		/// <returns> the complete url. </returns>
		public virtual string CompleteUrl
		{
			get
			{
				return queryStringParams.appendTo(url);
			}
		}

		internal virtual OAuthResponse doSend()
		{
			HttpRequest httpReq = httpRequest;
			Debug.Assert(httpReq != null, "nullness");

			httpReq.Method = this.verb;

			if (timeout != null)
				httpReq.Timeout = (int)timeout;

			addHeaders(httpReq);

			if (verb.Equals("PUT") || verb.Equals("POST"))
			{
				int[] length = new int[1];
				sbyte[] bodyBytes = getByteBodyContents(length);
				addBody(httpReq, bodyBytes, 0, length[0]);
			}

			try
			{
				return new OAuthResponse(httpReq.send());
			}
			catch (UnknownHostException e)
			{
				throw new OAuthException("The IP address of a host could not be determined.", e);
			}
		}

		internal virtual void addHeaders(HttpRequest httpRequest)
		{
			foreach (string key in headers.Keys)
				httpRequest.setHeader(key, headers.GetValueOrNull(key));
		}

		internal virtual void addBody(HttpRequest httpRequest, sbyte[] content, int offset, int length)
		{
			httpRequest.setHeader(HttpRequest.HEADER_CONTENT_LENGTH, Convert.ToString(content.Length));

			// Set default content type if none is set
			if (httpRequest.getHeader(HttpRequest.HEADER_CONTENT_TYPE) == null)
				httpRequest.setHeader(HttpRequest.HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE);

			httpRequest.createRequestBodyStream().write(content, offset, length);
		}

		/// <summary>
		/// Add an HTTP Header to the Request
		/// </summary>
		/// <param name="key">   the header name </param>
		/// <param name="value"> the header value </param>
		public virtual void addHeader(string key, string value)
		{
			this.headers[key] = value;
		}

		/// <summary>
		/// Add a body Parameter (for POST/ PUT Requests)
		/// </summary>
		/// <param name="key">   the parameter name </param>
		/// <param name="value"> the parameter value </param>
		public virtual void addBodyParameter(string key, string value)
		{
			this.bodyParams.add(key, value);
		}

		/// <summary>
		/// Add a query string parameter
		/// </summary>
		/// <param name="key">   the parameter name </param>
		/// <param name="value"> the parameter value </param>
		public virtual void addQueryStringParameter(string key, string value)
		{
			this.queryStringParams.add(key, value);
		}

		/// <summary>
		/// Add the passed parameters to the query string
		/// </summary>
		/// <param name="params"> parameters </param>
		public virtual void addQueryStringParameters(HttpRequestParams @params)
		{
			foreach (string name in @params.Names)
				addQueryStringParameter(name, @params.getValue(name));
		}

		/// <summary>
		/// Add body payload.
		/// <p/>
		/// This method is used when the HTTP body is not a form-url-encoded string, but another thing. Like for example
		/// XML.
		/// <p/>
		/// Note: The contents are not part of the OAuth signature
		/// </summary>
		/// <param name="payload"> the body of the request </param>
		public virtual void addPayload(string payload)
		{
			this.payload = payload;
		}

		/// <summary>
		/// Overloaded version for byte arrays
		/// </summary>
		/// <param name="payload"> </param>
		public virtual void addPayload(sbyte[] payload)
		{
			this.bytePayload = payload;
		}

		/// <summary>
		/// Get a <seealso cref="ParameterList"/> with the query string parameters.
		/// </summary>
		/// <returns> a <seealso cref="ParameterList"/> containing the query string parameters. </returns>
		/// <exception cref="OAuthException"> if the request URL is not valid. </exception>
		public virtual ParameterList QueryStringParams
		{
			get
			{
				ParameterList result = new ParameterList();
				string queryString = (new Url(url)).Query;
				result.addQueryString(queryString);
				result.addAll(queryStringParams);
				return result;
			}
		}

		/// <summary>
		/// Obtains a <seealso cref="ParameterList"/> of the body parameters.
		/// </summary>
		/// <returns> a <seealso cref="ParameterList"/>containing the body parameters. </returns>
		public virtual ParameterList BodyParams
		{
			get
			{
				return bodyParams;
			}
		}

		/// <summary>
		/// Obtains the URL of the HTTP Request.
		/// </summary>
		/// <returns> the original URL of the HTTP Request </returns>
		public virtual string Url
		{
			get
			{
				return url;
			}
		}

		/// <summary>
		/// Returns the URL without the port and the query string part.
		/// </summary>
		/// <returns> the OAuth-sanitized URL </returns>
		public virtual string SanitizedUrl
		{
			get
			{
				// Old code: return url.replaceAll("\\?.*", "").replace("\\:\\d{4}", "");
    
				string sanitizedUrl = url;
    
				int queryStringStart = url.IndexOf('?');
				if (queryStringStart != -1)
					sanitizedUrl = sanitizedUrl.Substring(0, queryStringStart);
				int colonStart = url.IndexOf(':');
				if (colonStart != -1)
					sanitizedUrl = sanitizedUrl.Substring(0, colonStart);
    
				return sanitizedUrl;
			}
		}

		internal virtual sbyte[] getByteBodyContents(int[] length)
		{
			if (bytePayload != null)
			{
				length[0] = bytePayload.Length;
				return bytePayload;
			}

			string body = (payload != null) ? payload : bodyParams.asFormUrlEncodedString();
			return IOUtils.toUtf8BytesFromString(body, length);
		}

		/// <summary>
		/// Returns the HTTP Verb
		/// </summary>
		/// <returns> the verb </returns>
		public virtual string Verb
		{
			get
			{
				return verb;
			}
		}

		/// <summary>
		/// Returns the connection headers as a <seealso cref="Map"/>
		/// </summary>
		/// <returns> map of headers </returns>
		public virtual IDictionary<string, string> Headers
		{
			get
			{
				return headers;
			}
		}

		/// <summary>
		/// Sets the timeout for the underlying <seealso cref="HttpRequest"/>
		/// </summary>
		/// <param name="timeoutInMillis"> duration of the timeout </param>
		public virtual int Timeout
		{
			set
			{
				this.timeout = value;
			}
		}

		/*
		* We need this in order to stub the connection object for test cases
		*/
		internal virtual HttpRequest HttpRequest
		{
			set
			{
				this.httpRequest = value;
			}
		}

		public override string ToString()
		{
			return string.Format("@Request({0} {1})", Verb, Url);
		}
	}

}