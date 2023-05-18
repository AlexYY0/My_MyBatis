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

/**
 * Represents the content of a mapped statement read from an XML file or an annotation. It creates the SQL that will be
 * passed to the database out of the input parameter received from the user.
 *
 * @author Clinton Begin
 */
public interface SqlSource {

  /**
   * RawSqlSource 表示可能带有#{}占位符的sql
   * DynamicSqlSource 表示带有${}占位符的sql语句；DynamicSqlSource涵盖的操作比RawSqlSource多了一步，便是优先处理${}字符，其本身也会调用去解析#{}字符
   * StaticSqlSource 表示不带有占位符且可能会包含 ? 的sql语句
   * ProviderSqlSource 表示sql来自基于方法上的 @ProviderXXX 注解所定义的sql
   */
  BoundSql getBoundSql(Object parameterObject);

}
