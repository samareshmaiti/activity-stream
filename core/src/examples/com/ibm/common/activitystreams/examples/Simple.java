/**
 * Copyright 2013 OpenSocial Foundation
 * Copyright 2013 International Business Machines Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Utility library for working with Activity Streams Actions
 * Requires underscorejs.
 *
 * @author James M Snell (jasnell@us.ibm.com)
 */
package com.ibm.common.activitystreams.examples;

import static com.ibm.common.activitystreams.IO.makeDefaultPrettyPrint;
import static com.ibm.common.activitystreams.Makers.activity;
import static com.ibm.common.activitystreams.Makers.object;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.google.gson.JsonParser;
import com.ibm.common.activitystreams.Activity;
import com.ibm.common.activitystreams.IO;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
public final class Simple {
  
  // The IO object handles all of the reading and writing of the object
  private static final IO io = makeDefaultPrettyPrint();
  
  private Simple() {}
  
  /**
   * @param args String[]
  
   * @throws Exception */
  public static void main(String... args) throws Exception {
    // Demonstrates the creation and parsing of a simple Activity Object
    DefaultHttpClient httpClient = new DefaultHttpClient();
    try {
      //Define a HttpGet request; You can choose between HttpPost, HttpDelete or HttpPut also.
      //Choice depends on type of method you will be invoking.
      HttpGet getRequest = new HttpGet("http://localhost:3002/users");

      //Set the API media type in http accept header
      getRequest.addHeader("accept", "application/json");

      //Send the request; It will immediately return the response in HttpResponse object
      HttpResponse response = httpClient.execute(getRequest);

      //verify the valid error code first
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != 200) {
        throw new RuntimeException("Failed with HTTP error code : " + statusCode);
      }
      HttpEntity httpEntity = response.getEntity();
      String apiOutput = EntityUtils.toString(httpEntity);
       JsonParser jsonParser = new JsonParser();
       Object obj = jsonParser.parse(apiOutput);

      //Lets see what we got from API
      System.out.println(obj.toString());
    }
      finally
      {
        //Important: Close the connect
        httpClient.getConnectionManager().shutdown();
      }
      // Create the Activity... The API uses a Fluent Generator pattern
    Activity activity = 
      activity()
        .verb("get")
        .actor("acct:joe@example.org")
        .object(object("application/json")
                .url("http://localhost:3002/users")
                .title("This is the title"))
        .get();

    
    // let's write it out to our outputstream
    ByteArrayOutputStream out = 
      new ByteArrayOutputStream();
    activity.writeTo(out, io);
    
    // now let's parse it back in
    ByteArrayInputStream in = 
      new ByteArrayInputStream(
        out.toByteArray());
    
    activity = io.readAsActivity(in);
    activity.writeTo(System.out, io);
    
    
  }
  
}
