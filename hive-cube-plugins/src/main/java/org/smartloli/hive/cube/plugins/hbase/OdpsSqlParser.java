/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartloli.hive.cube.plugins.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Condition;
import com.alibaba.druid.stat.TableStat.Name;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The client requests the HBase operation to parse the SQL and obtain the
 * fields and conditions.
 * 
 * @author smartloli.
 *
 *         Created by Jun 29, 2017
 */
public class OdpsSqlParser {

	private static final Logger LOG = LoggerFactory.getLogger(OdpsSqlParser.class);

	/** Parser sql mapper hbase tree. */
	public static HBaseScanSpec sqlParser(String sql) throws JsonParseException, JsonMappingException, IOException {
		String dbType = JdbcConstants.HBASE;
		String result = SQLUtils.format(sql, dbType);
		LOG.info("SQL[" + result.replaceAll("\n", " ") + "]");
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
		SQLStatement stmt = stmtList.get(0);
		OdpsSchemaStatVisitor visitor = new OdpsSchemaStatVisitor();
		stmt.accept(visitor);
		Map<Name, TableStat> tabmap = visitor.getTables();
		String tableName = "";
		for (Iterator iterator = tabmap.keySet().iterator(); iterator.hasNext();) {
			Name name = (Name) iterator.next();
			tableName = name.toString();
		}

		String startRow = "", stopRow = "";
		int offset = 0;
		for (Condition condition : visitor.getConditions()) {
			if (condition.getOperator().equals(HConstants.EQUAL)) {
				startRow = stopRow = condition.getValues().get(0).toString();
			} else {
				if (offset == 0) {
					startRow = condition.getValues().get(0).toString();
				} else {
					stopRow = condition.getValues().get(0).toString();
				}
				offset++;
			}
		}
		JSONObject object = new JSONObject();
		object.put("tableName", tableName);
		object.put("startRow", Bytes.toBytes(startRow));
		object.put("stopRow", Bytes.toBytes(stopRow));
		object.put("filterString", "");
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(object.toJSONString(), HBaseScanSpec.class);
	}

	private static String getMatchedString(String regex, String text) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			return matcher.group(2);
		}
		return null;
	}

	/** Get sql schema. */
	public static List<String> parseColumns(String sql) {
		String columns = getMatchedString(HConstants.FIELD_REGEX, sql);
		if (columns.replaceAll(HConstants.SPACE, "").equals(HConstants.STAR)) {
			return Arrays.asList(HConstants.STAR);
		} else {
			List<String> fields = new ArrayList<String>();
			for (String column : columns.split(HConstants.COMMA)) {
				if (column.toLowerCase().contains(HConstants.AS)) {
					fields.add(column.split(HConstants.AS)[1].replaceAll(HConstants.SPACE, "").replaceAll(HConstants.QUOTES, ""));
				} else {
					if (column.toLowerCase().contains(HConstants.AVG) || column.toLowerCase().contains(HConstants.COUNT) || column.toLowerCase().contains(HConstants.SUM)
							|| column.toLowerCase().contains(HConstants.MAX) || column.toLowerCase().contains(HConstants.MIN)) {
						fields.add(HConstants.AGG_DEFAULT_FIELD);
					} else {
						fields.add(column.replaceAll(HConstants.SPACE, "").replaceAll(HConstants.QUOTES, ""));
					}
				}
			}
			return fields;
		}
	}

}
