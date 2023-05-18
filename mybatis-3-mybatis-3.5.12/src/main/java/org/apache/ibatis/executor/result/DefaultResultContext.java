/*
 *    Copyright 2009-2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.executor.result;

import org.apache.ibatis.session.ResultContext;

/**
 * @author Clinton Begin
 */
public class DefaultResultContext<T> implements ResultContext<T> {

  /**
   * 用来保存当前解析的这条记录的结果
   */
  private T resultObject;
  /**
   * 结果集的数量，每解析完一条记录，该值加一
   */
  private int resultCount;
  /**
   * 是否停止解析，当到最后一条记录时，该值为false
   */
  private boolean stopped;

  public DefaultResultContext() {
    resultObject = null;
    resultCount = 0;
    stopped = false;
  }

  @Override
  public T getResultObject() {
    return resultObject;
  }

  @Override
  public int getResultCount() {
    return resultCount;
  }

  @Override
  public boolean isStopped() {
    return stopped;
  }

  public void nextResultObject(T resultObject) {
    resultCount++;
    this.resultObject = resultObject;
  }

  @Override
  public void stop() {
    this.stopped = true;
  }

}
