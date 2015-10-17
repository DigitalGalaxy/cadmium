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
package com.meltmedia.cadmium.deployer;

import com.meltmedia.cadmium.core.commands.AbstractMessageBean;
/*  
this is what is called a "pojo" or "plain old Java object"  This is passing information from one method to another.  
It is not actually doing anything by itself. The question: where it is pulling from, and which method wants this information?


To find this out, we can either search github for this information with the "t" command, or we can use inlellij to scrounge through the code and find it.



Why are we passing this information?  Why not request it directly?  Are we segragatig it to avoid it getting contaminated?  Are we asking for a separate copy?
*/

public class DeployRequest extends AbstractMessageBean {
  protected String domain;
  protected String branch;
  protected String repo;
  protected String configBranch;
  protected String configRepo;
  protected String context;
  protected String artifact;
  private boolean secure;
  public String getDomain() {
    return domain;
  }
  
  
  /* RRAAAGGGGEEEEE
  
  THIS IS SOOO CONFUSING
  
  public void setDomain(String domain) {
    this.domain = domain

why are different varaibles named the same thing!?!!?  Blarg!!!  This is mostly stylistic difference as opposed to an actual problem 
or poor code, but this is still a huge drag.  Why would you name 2 different variables the same thing, ever????? That's just asking to 
get them mixed up!  I know they are just local to this 3-line method, but still!  Ugh!  Just a slight variation is fine.

maybe, oh I don't know:

public void setDomain(String domainIn) {
    this.domain = domainIn

Again, not a real problem or bad code, just a diffenre style, but it just makes me rage.  It's confusing.  Needlessly confusing.
It's 4 characters, people.  4 characters.  How many calories does it take for your cells to create ATP, to move your fingers 4 extra times, 
versus how many calories it takes neurons to figure out that is going on here?  I don't know, but it's frustarting. 

*/
  


  public void setDomain(String domain) {
    this.domain = domain;
  }
  public String getBranch() {
    return branch;
  }
  public void setBranch(String branch) {
    this.branch = branch;
  }
  public String getRepo() {
    return repo;
  }
  public void setRepo(String repo) {
    this.repo = repo;
  }
  public String getConfigBranch() {
    return configBranch;
  }
  public void setConfigBranch(String configBranch) {
    this.configBranch = configBranch;
  }
  public String getConfigRepo() {
    return configRepo;
  }
  public void setConfigRepo(String configRepo) {
    this.configRepo = configRepo;
  }
  public String getContext() {
    return context;
  }
  public void setContext(String context) {
    this.context = context;
  }
  public String getArtifact() {
    return artifact;
  }
  public void setArtifact(String artifact) {
    this.artifact = artifact;
  }
  public boolean getSecure() {
    return secure;
  }
  public void setSecure(boolean secure) {
    this.secure = secure;
  }
}
