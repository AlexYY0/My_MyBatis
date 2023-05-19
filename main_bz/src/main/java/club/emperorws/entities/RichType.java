/*
 *    Copyright 2009-2023 the original author or authors.
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
package club.emperorws.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RichType<T> {

  private RichType<String> richType;

  private String richField;

  private String richProperty;

  private Map richMap = new HashMap<>();

  private T tester;

  private List<RichType<String>> richList = new ArrayList() {
    private static final long serialVersionUID = 1L;
    {
      //add(new RichTypeSon());
      add(null);
    }
  };

  private RichType<String>[] richArray = new RichType[]{null};

  public RichType() {
  }

  public RichType(T tester) {
    this.tester = tester;
  }

  public RichType<String> getRichType() {
    return richType;
  }

  public void setRichType(RichType<String> richType) {
    this.richType = richType;
  }

  public String getRichProperty() {
    return richProperty;
  }

  public void setRichProperty(String richProperty) {
    this.richProperty = richProperty;
  }

  public List<RichType<String>> getRichList() {
    return richList;
  }

  public void setRichList(List<RichType<String>> richList) {
    this.richList = richList;
  }

  public Map getRichMap() {
    return richMap;
  }

  public void setRichMap(Map richMap) {
    this.richMap = richMap;
  }
}
