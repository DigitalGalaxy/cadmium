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
package com.meltmedia.cadmium.core.worker;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meltmedia.cadmium.core.CoordinatedWorkerListener;
import com.meltmedia.cadmium.core.commands.ContentUpdateRequest;

public class NotifyListenerTask implements Callable<Boolean> {
  private final Logger log = LoggerFactory.getLogger(getClass());
  
  private CoordinatedWorkerListener listener;
  private Future<Boolean> previousTask;
  private ContentUpdateRequest body;
  
  public NotifyListenerTask(CoordinatedWorkerListener listener, ContentUpdateRequest body, Future<Boolean> previousTask) {
    this.listener = listener;
    this.previousTask = previousTask;
    this.body = body;
  }

  @Override
  public Boolean call() throws Exception {
    if(previousTask != null) {
      Boolean lastResponse = previousTask.get();
      if(lastResponse != null && !lastResponse.booleanValue() ) {
        log.warn("Notification of work failed!");
        listener.workFailed(body);
        throw new Exception("Previous task failed");
      }
    }
    
    Thread.sleep(1000l);
    log.info("Notifying listener of updating being done successfully");
    listener.workDone(body);
    
    return true;
  }

}
