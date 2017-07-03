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
package org.smartloli.hive.cube.web.service;

import java.util.List;
import java.util.Map;

import org.smartloli.hive.cube.common.pojo.HiveSyncStatus;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Hive service interface.
 * 
 * @author smartloli.
 *
 *         Created by May 27, 2017
 */
public interface HiveService {

	public int count();

	public int findTableByName(String tableName);

	public List<HiveSyncStatus> getHiveSyncStatus(Map<String, Object> params);

	public JSONArray getHiveTableColumnByName(String tableName);

	public int modifyColumnCommentByName(String tableName, String columnName, String comment);

	public JSONArray synchronizeAllTableFromHive();

	public JSONObject synchronizeTableFromHiveByName(String tableName, String aliasName);

}
