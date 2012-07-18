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
package com.meltmedia.cadmium.cli;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.meltmedia.cadmium.core.git.GitService;
import com.meltmedia.cadmium.status.Status;

@Parameters(commandDescription="Commits content from a specific directory into a repo:branch that a site is serving from then updates the site to serve the new content.", separators="=")
public class CommitCommand extends AbstractAuthorizedOnly implements CliCommand {

  @Parameter(names={"--content-directory", "-cd"}, description="The directory to pull content from.", required=false)
  private String content = ".";
  
  @Parameter(description="<site>", required=true)
  private List<String> site;
  
  @Parameter(names="--repo", description="Overrides the repository url from the server.", required=false)
  private String repo;
  
  @Parameter(names={"--message", "-m"}, description="comment", required=true)  
  private String comment;
  
  @Parameter(names="--quiet-auth", description="Option to skip updating/prompting for new credentials.", required=false)
  private Boolean skipAuth = false;
  
  public void execute() throws Exception {
       
	String siteUrl = site.get(0);
	  
    GitService git = null;
    try {
      System.out.println("Getting status of ["+siteUrl+"]");
      Status status = StatusCommand.getSiteStatus(siteUrl, token);
  
      if(repo != null) {
        status.setRepo(repo);
      }
      
      System.out.println("Cloning repository that ["+siteUrl+"] is serving");
      git = CloneCommand.cloneSiteRepo(status);
      
      String revision = status.getRevision();
      String branch = status.getBranch();
      
      if(git.isTag(branch)) {
        throw new Exception("Cannot commit to a tag!");
      }
      System.out.println("Cloning content from ["+content+"] to ["+siteUrl+":"+branch+"]");
      revision = CloneCommand.cloneContent(content, git, comment);
      
      System.out.println("Switching content on ["+siteUrl+"]");
      UpdateCommand.sendUpdateMessage(siteUrl, branch, revision, comment, token);
      
    } catch(Exception e) {
      System.err.println("Failed to commit changes to ["+siteUrl+"]: "+e.getMessage());
    } finally {
      if(git != null) {
        git.close();
      }
    }
    
  }

  @Override
  public String getCommandName() {
    return "commit";
  }

  @Override
  public boolean isAuthQuiet() {
    return skipAuth;
  }
}