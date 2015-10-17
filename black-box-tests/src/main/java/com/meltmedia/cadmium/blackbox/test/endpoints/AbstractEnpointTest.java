/**
 *    Copyright 2012 meltmedia
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.meltmedia.cadmium.blackbox.test.endpoints;

import com.meltmedia.cadmium.blackbox.test.ApiRequest;
import com.meltmedia.cadmium.blackbox.test.ApiResponseValidator;
import sun.misc.BASE64Encoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class implementation of a Endpoint test to run in junit test case RestApiTest.
 *
 * @author John McEntire
 */
 
 
 
 /** 
 * 
 * ^^^^ I'm sure this seemed like a reasonable comment to the author. 
 * And the variables are all descriptively named, which is a huge plus.
 * The problem is, that even though this inline comment informs me what 
 * function this code is supposed to perform, it does not tell me how it 
 * performs it.  I have three classes that are being called, and a Map, 
 * (whatever that is, a custom class?  Something I have to dig around SackOverflow
 * for?) and then it goes into a ton of random method calls that I have no 
 * idea what they are for.  With nothing to go off of.  
 * 
 * Here's the thing.  Inline comments are not for people who have tons of time who are smarter
 * /more skilled than you. Inline comments are for people who are less skilled that you who 
 * want to try to make sense of what is in front of them in the shortest reasonable time
 * possible.  So pretend, just for moment, that the next monkey who reads your code is a little
 * less skilled than yourself, and would appreciate hand-holding inline comments.  
 * Otherwise, you end up like this: https://s3.amazonaws.com/uploads.hipchat.com/23199/2470072/fQlmuSda6SGxVmS/upload.png
 * Or this: https://s3.amazonaws.com/uploads.hipchat.com/23199/2470072/BX3KV1IVEPj1vF5/upload.png
 * In case you can't click those pictures, they are of a small monkey and a cat trying to untangle a ball of yarn, respectively.  
 * I'm sure this is great logic that makes a lot of sense.  It's is also clear as mud.  I coudl just bug a dev... 
 * But I know the senior devs are busy and I don't want to pester them
 * every ten minutes every time I don't understand some method call.  
 * 
 * A novel is not necessary, people, just something, a quick sentence.  One extra line every few methods.
 * To tell you what a given method is for, and maybe if it links to something else, or if changing it is likely to break something else.
 * Maybe a rationale for why it is there if that is not immidiately obvious?
 * 
 * Code is sharing low-level intelligence, not only between metallic computers but between biological comptuers, us monkey brains
 * too. And until us monkeys become metallic computers ourselves, it should be written as such. Half for the metallic brain we are interfacing
 * with more or less directly, and half for the biological brain of a fellow coder who we may be interfacing with indirectly when somebody else reads our code.
 * We, as coders, should spread the intelligence, and we should not obfuscate our code to those less skilled by leaving it in a lump.
 * Lack of inline comments raises the barrier to entry, and while that barrier may be easy for more senior coders familiar with the project to jump over, it's not
 * as easy to newcomers or those coming from another language.  Which is great if it's a program for yourself only.  But, if the code is on
 * GitHub and anybody can see it, or even if it's office code and eventually somebody else will come in and take over the project, then some other monkey brain 
 * is going to have to figure this out, and backtrack every single line.  (Besides, even the author of any given piece of code is going to come in with a hangover one day,
 * and due to the aforementioned hangover, is going to have no idea what he even wrote.  Even he will benefit from inline comments!)
 * 
 * This is a call to all programmers everywhere.  Civilization as we know it is an impossiblity without the things we create every day. 
 * Without computers we go back a century and we stay there.  Let's make it easier on ourselves, save every one of us a lot of 
 * trouble and wasted neural activity, and COMMENT OUR CODE!!!!
 * SAY it with me!
 * COMMENT 
 * OUR 
 * CODE
 *
 * Ok, probably could have deciphered 4 or 5 method calls in the time it took to rant about inline comments!  
 * 
 * All right, there are no inline comments here.  So, time to figure this out! To IntelliJ!
 * 
 * What makes the magic Cadmium button press, anyways? 
 * 
 * */

 
public abstract class AbstractEnpointTest implements EndpointTest {

  protected ApiRequest request;
  protected ApiResponseValidator validator;
  protected Map<String, String> authToken;

  public AbstractEnpointTest() {
    setupTest();
  }

  public AbstractEnpointTest(String token) {
    authToken = new HashMap<String, String>();
    authToken.put("Authorization", "token " + token);
    setupTest();
  }

  public abstract void setupTest();

  @Override
  public ApiRequest getRequest() {
    return request;
  }

  @Override
  public ApiResponseValidator getValidator() {
    return validator;
  }

  @Override
  public void preTest() throws Exception {}

  @Override
  public void postTest() throws Exception {}

  @Override
  public String toString() {
    return getClass().getName();
  }

  protected Map<String, String> getBasicAuthHeader(String username, String password, Map<String, String> addToMap) {
    if(addToMap == null) {
      addToMap = new HashMap<String, String>();
    }
    addToMap.put("Authorization", "Basic " + new String(new BASE64Encoder().encode((username + ":" + password).getBytes())));
    return addToMap;
  }
}
