/**
 *   Copyright 2012 meltmedia
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.meltmedia.cadmium.core.messaging.jgroups;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Vector;

import org.jgroups.Address;
import org.jgroups.stack.IpAddress;
import org.junit.Test;

import com.meltmedia.cadmium.core.messaging.ChannelMember;
import com.meltmedia.cadmium.core.messaging.Message;
import com.meltmedia.cadmium.core.messaging.ProtocolMessage;

public class JGroupsMessageSenderTest {
  private static final String serializedMessage = "{\"command\":\"UPDATE\",\"protocolParameters\":{\"branch\":\"master\",\"rev\":\"HEAD\"},\"requestTime\":233434800}";
  private static final Message deserializedMessage = new Message();
  static {
    deserializedMessage.setCommand(ProtocolMessage.UPDATE);
    deserializedMessage.setRequestTime(new Long(233434800));
    deserializedMessage.setProtocolParameters(new LinkedHashMap<String, String>());
    deserializedMessage.getProtocolParameters().put("branch", "master");
    deserializedMessage.getProtocolParameters().put("rev", "HEAD");
  }

  @Test
  public void testSendMessage() throws Exception {
    Address me = new IpAddress(12345);
    Vector<Address> viewMems = new Vector<Address>();
    viewMems.add(me);
    
    DummyJChannel channel = new DummyJChannel(me, viewMems);
    
    JGroupsMessageSender sender = new JGroupsMessageSender();
    sender.channel = channel;
    
    sender.sendMessage(deserializedMessage, new ChannelMember(me));
    
    assertTrue("Failed to send message", channel.getMessageList().size() == 1 && channel.getMessageList().get(0) != null);
    assertEquals("Incorrect Destination", me.toString(), channel.getMessageList().get(0).getDest().toString());
    assertEquals("Incorrect Message sent", serializedMessage, channel.getMessageList().get(0).getObject().toString());
  }
}
