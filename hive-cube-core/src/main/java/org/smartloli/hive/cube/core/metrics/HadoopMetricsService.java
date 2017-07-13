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
/**
 * 
 */
package org.smartloli.hive.cube.core.metrics;

import java.util.List;

import org.smartloli.hive.cube.common.pojo.DataNode;
import org.smartloli.hive.cube.common.pojo.HdfsDir;
import org.smartloli.hive.cube.common.pojo.NameNode;
import org.smartloli.hive.cube.common.pojo.RegionServer;

/**
 * Hadoop cluster information,such as namenode,datanode etc.
 * 
 * @author smartloli.
 *
 *         Created by Jul 13, 2017
 */
public interface HadoopMetricsService {

	/** Get hadoop datanodes information. */
	public List<DataNode> datanodes();

	/** Get hadoop namenodes information. */
	public NameNode namenodes();

	/** Get hbase region servers. */
	public List<RegionServer> regionServers();

	/** Get hdfs menu. */
	public List<HdfsDir> browseDirectory(String path);

}
