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
package org.apache.ibatis.scripting;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;

/**
 * Mybatis默认XML驱动类为XMLLanguageDriver，其主要作用于解析select|update|insert|delete节点为完整的SQL语句。
 * RawLanguageDriver继承自XMLLanguageDriver，是一个简单的语言驱动，只能针对静态SQL的处里，如果出现动态SQL标签会抛出异常
 * 默认XML语言是能够识别静态语句并创建一个{@link RawSqlSource}，所以没有需要使用原始的,除非你想确保没有任何动态标记任何理由。
 */
public interface LanguageDriver {

  /**
   * Creates a {@link ParameterHandler} that passes the actual parameters to the the JDBC statement.
   *
   * @author Frank D. Martinez [mnesarco]
   *
   * @param mappedStatement
   *          The mapped statement that is being executed
   * @param parameterObject
   *          The input parameter object (can be null)
   * @param boundSql
   *          The resulting SQL once the dynamic language has been executed.
   *
   * @return the parameter handler
   *
   * @see DefaultParameterHandler
   */
  ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql);

  /**
   * Creates an {@link SqlSource} that will hold the statement read from a mapper xml file. It is called during startup,
   * when the mapped statement is read from a class or an xml file.
   *
   * @param configuration
   *          The MyBatis configuration
   * @param script
   *          XNode parsed from a XML file
   * @param parameterType
   *          input parameter type got from a mapper method or specified in the parameterType xml attribute. Can be
   *          null.
   *
   * @return the sql source
   */
  SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType);

  /**
   * Creates an {@link SqlSource} that will hold the statement read from an annotation. It is called during startup,
   * when the mapped statement is read from a class or an xml file.
   *
   * @param configuration
   *          The MyBatis configuration
   * @param script
   *          The content of the annotation
   * @param parameterType
   *          input parameter type got from a mapper method or specified in the parameterType xml attribute. Can be
   *          null.
   *
   * @return the sql source
   */
  SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType);

}
