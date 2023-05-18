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
package org.apache.ibatis.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.session.Configuration;

/**
 * An actual SQL String got from an {@link SqlSource} after having processed any dynamic content. The SQL may have SQL
 * placeholders "?" and a list (ordered) of a parameter mappings with the additional information for each parameter (at
 * least the property name of the input object to read the value from).
 * <p>
 * Can also have additional parameters that are created by the dynamic language (for loops, bind...).
 * 经过处理一些动态sql的部分获取到的真实sql，这个sql可能还有占位符?和一个参数映射的有序集合，并且还有每个参数的额外信息
 *
 * @author Clinton Begin
 */
public class BoundSql {

  // 最终解析的sql，Mybatis将#{}和${}解析后的sql，其中#{}会被解析为?
  private final String sql;
  // 参数映射
  //这里的parameterMappings列表参数里的item个数, 以及每个item的属性名称等等, 都是和上面的sql中的 ? 完全一一对应的.
  private final List<ParameterMapping> parameterMappings;
  // 参数对象，用户传入的数据
  private final Object parameterObject;
  // 额外的参数集合：运行过程中产生的额外的参数，方便xml-sql后续的数据获取，主要是<foreach>中的每一个元素数据，还有<bind>
  private final Map<String, Object> additionalParameters;
  // additionalParameters的元对象封装
  private final MetaObject metaParameters;

  public BoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings,
      Object parameterObject) {
    this.sql = sql;
    this.parameterMappings = parameterMappings;
    this.parameterObject = parameterObject;
    this.additionalParameters = new HashMap<>();
    this.metaParameters = configuration.newMetaObject(additionalParameters);
  }

  public String getSql() {
    return sql;
  }

  public List<ParameterMapping> getParameterMappings() {
    return parameterMappings;
  }

  public Object getParameterObject() {
    return parameterObject;
  }

  public boolean hasAdditionalParameter(String name) {
    String paramName = new PropertyTokenizer(name).getName();
    return additionalParameters.containsKey(paramName);
  }

  public void setAdditionalParameter(String name, Object value) {
    metaParameters.setValue(name, value);
  }

  public Object getAdditionalParameter(String name) {
    return metaParameters.getValue(name);
  }

  public Map<String, Object> getAdditionalParameters() {
    return additionalParameters;
  }
}
