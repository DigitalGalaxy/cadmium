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
package com.meltmedia.cadmium.core.commands;

import java.util.List;

import com.meltmedia.cadmium.core.config.ConfigManager;
import com.meltmedia.cadmium.core.messaging.ChannelMember;
import com.meltmedia.cadmium.core.messaging.jgroups.JGroupsMembershipTracker;

public class DummyMembershipTracker extends JGroupsMembershipTracker {
  public DummyMembershipTracker(){
    super(null,null,null,null,null,null);
  }
  
  public void setMembers(List<ChannelMember> members) {
    this.members = members;
  }
  
  public List<ChannelMember> getMembers() {
    return this.members;
  }
  
  public void setConfigManager(ConfigManager configManager) {
    this.configManager = configManager; 
  }
}
