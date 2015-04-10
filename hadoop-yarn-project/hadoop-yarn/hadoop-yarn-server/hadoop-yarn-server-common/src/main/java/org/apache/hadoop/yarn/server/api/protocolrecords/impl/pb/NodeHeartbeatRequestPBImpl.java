/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationIdPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos.ApplicationIdProto;
import org.apache.hadoop.yarn.proto.YarnProtos.NodeIdToLabelsProto;
import org.apache.hadoop.yarn.proto.YarnProtos.StringArrayProto;
import org.apache.hadoop.yarn.proto.YarnServerCommonProtos.MasterKeyProto;
import org.apache.hadoop.yarn.proto.YarnServerCommonProtos.NodeStatusProto;
import org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.AppCollectorsMapProto;
import org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.LogAggregationReportProto;
import org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.LogAggregationReportsForAppsProto;
import org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.NodeHeartbeatRequestProto;
import org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos.NodeHeartbeatRequestProtoOrBuilder;
import org.apache.hadoop.yarn.server.api.protocolrecords.LogAggregationReport;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.yarn.server.api.records.NodeStatus;
import org.apache.hadoop.yarn.server.api.records.impl.pb.MasterKeyPBImpl;
import org.apache.hadoop.yarn.server.api.records.impl.pb.NodeStatusPBImpl;

public class NodeHeartbeatRequestPBImpl extends NodeHeartbeatRequest {
  NodeHeartbeatRequestProto proto = NodeHeartbeatRequestProto.getDefaultInstance();
  NodeHeartbeatRequestProto.Builder builder = null;
  boolean viaProto = false;
  
  private NodeStatus nodeStatus = null;
  private MasterKey lastKnownContainerTokenMasterKey = null;
  private MasterKey lastKnownNMTokenMasterKey = null;
  private Set<String> labels = null;
  private Map<ApplicationId, LogAggregationReport>
      logAggregationReportsForApps = null;
  Map<ApplicationId, String> registeredCollectors = null;
  
  public NodeHeartbeatRequestPBImpl() {
    builder = NodeHeartbeatRequestProto.newBuilder();
  }

  public NodeHeartbeatRequestPBImpl(NodeHeartbeatRequestProto proto) {
    this.proto = proto;
    viaProto = true;
  }
  
  public NodeHeartbeatRequestProto getProto() {
      mergeLocalToProto();
    proto = viaProto ? proto : builder.build();
    viaProto = true;
    return proto;
  }

  @Override
  public int hashCode() {
    return getProto().hashCode();
  }
  
  @Override
  public boolean equals(Object other) {
    if (other == null)
      return false;
    if (other.getClass().isAssignableFrom(this.getClass())) {
      return this.getProto().equals(this.getClass().cast(other).getProto());
    }
    return false;
  }

  private void mergeLocalToBuilder() {
    if (this.nodeStatus != null) {
      builder.setNodeStatus(convertToProtoFormat(this.nodeStatus));
    }
    if (this.lastKnownContainerTokenMasterKey != null) {
      builder.setLastKnownContainerTokenMasterKey(
          convertToProtoFormat(this.lastKnownContainerTokenMasterKey));
    }
    if (this.lastKnownNMTokenMasterKey != null) {
      builder.setLastKnownNmTokenMasterKey(
          convertToProtoFormat(this.lastKnownNMTokenMasterKey));
    }
    if (this.labels != null) {
      builder.clearNodeLabels();
      builder.setNodeLabels(StringArrayProto.newBuilder()
          .addAllElements(this.labels).build());
    }
    if (this.logAggregationReportsForApps != null) {
      addLogAggregationStatusForAppsToProto();
    }
    if (this.registeredCollectors != null) {
      addRegisteredCollectorsToProto();
    }
  }

  private void addLogAggregationStatusForAppsToProto() {
    maybeInitBuilder();
    builder.clearLogAggregationReportsForApps();
    for (Entry<ApplicationId, LogAggregationReport> entry : logAggregationReportsForApps
      .entrySet()) {
      builder.addLogAggregationReportsForApps(LogAggregationReportsForAppsProto
        .newBuilder().setAppId(convertToProtoFormat(entry.getKey()))
        .setLogAggregationReport(convertToProtoFormat(entry.getValue())));
    }
  }

  private void addRegisteredCollectorsToProto() {
    maybeInitBuilder();
    builder.clearRegisteredCollectors();
    for (Map.Entry<ApplicationId, String> entry : registeredCollectors.entrySet()) {
      builder.addRegisteredCollectors(AppCollectorsMapProto.newBuilder()
        .setAppId(convertToProtoFormat(entry.getKey()))
        .setAppCollectorAddr(entry.getValue()));
    }
  }

  private LogAggregationReportProto convertToProtoFormat(
      LogAggregationReport value) {
    return ((LogAggregationReportPBImpl) value).getProto();
  }

  private void mergeLocalToProto() {
    if (viaProto) 
      maybeInitBuilder();
    mergeLocalToBuilder();
    proto = builder.build();
    viaProto = true;
  }

  private void maybeInitBuilder() {
    if (viaProto || builder == null) {
      builder = NodeHeartbeatRequestProto.newBuilder(proto);
    }
    viaProto = false;
  }
    
  
  @Override
  public NodeStatus getNodeStatus() {
    NodeHeartbeatRequestProtoOrBuilder p = viaProto ? proto : builder;
    if (this.nodeStatus != null) {
      return this.nodeStatus;
    }
    if (!p.hasNodeStatus()) {
      return null;
    }
    this.nodeStatus = convertFromProtoFormat(p.getNodeStatus());
    return this.nodeStatus;
  }

  @Override
  public void setNodeStatus(NodeStatus nodeStatus) {
    maybeInitBuilder();
    if (nodeStatus == null) 
      builder.clearNodeStatus();
    this.nodeStatus = nodeStatus;
  }

  @Override
  public MasterKey getLastKnownContainerTokenMasterKey() {
    NodeHeartbeatRequestProtoOrBuilder p = viaProto ? proto : builder;
    if (this.lastKnownContainerTokenMasterKey != null) {
      return this.lastKnownContainerTokenMasterKey;
    }
    if (!p.hasLastKnownContainerTokenMasterKey()) {
      return null;
    }
    this.lastKnownContainerTokenMasterKey =
        convertFromProtoFormat(p.getLastKnownContainerTokenMasterKey());
    return this.lastKnownContainerTokenMasterKey;
  }

  @Override
  public void setLastKnownContainerTokenMasterKey(MasterKey masterKey) {
    maybeInitBuilder();
    if (masterKey == null) 
      builder.clearLastKnownContainerTokenMasterKey();
    this.lastKnownContainerTokenMasterKey = masterKey;
  }

  @Override
  public MasterKey getLastKnownNMTokenMasterKey() {
    NodeHeartbeatRequestProtoOrBuilder p = viaProto ? proto : builder;
    if (this.lastKnownNMTokenMasterKey != null) {
      return this.lastKnownNMTokenMasterKey;
    }
    if (!p.hasLastKnownNmTokenMasterKey()) {
      return null;
    }
    this.lastKnownNMTokenMasterKey =
        convertFromProtoFormat(p.getLastKnownNmTokenMasterKey());
    return this.lastKnownNMTokenMasterKey;
  }

  @Override
  public void setLastKnownNMTokenMasterKey(MasterKey masterKey) {
    maybeInitBuilder();
    if (masterKey == null) 
      builder.clearLastKnownNmTokenMasterKey();
    this.lastKnownNMTokenMasterKey = masterKey;
  }

  @Override
  public Map<ApplicationId, String> getRegisteredCollectors() {
    if (this.registeredCollectors != null) {
      return this.registeredCollectors;
    }
    initRegisteredCollectors();
    return registeredCollectors;
  }

  private void initRegisteredCollectors() {
    NodeHeartbeatRequestProtoOrBuilder p = viaProto ? proto : builder;
    List<AppCollectorsMapProto> list = p.getRegisteredCollectorsList();
    this.registeredCollectors = new HashMap<ApplicationId, String> ();
    for (AppCollectorsMapProto c : list) {
      ApplicationId appId = convertFromProtoFormat(c.getAppId());
      this.registeredCollectors.put(appId, c.getAppCollectorAddr());
    }
  }

  @Override
  public void setRegisteredCollectors(
      Map<ApplicationId, String> registeredCollectors) {
    if (registeredCollectors == null || registeredCollectors.isEmpty()) {
      return;
    }
    maybeInitBuilder();
    this.registeredCollectors = new HashMap<ApplicationId, String>();
    this.registeredCollectors.putAll(registeredCollectors);
  }

  private NodeStatusPBImpl convertFromProtoFormat(NodeStatusProto p) {
    return new NodeStatusPBImpl(p);
  }

  private NodeStatusProto convertToProtoFormat(NodeStatus t) {
    return ((NodeStatusPBImpl)t).getProto();
  }

  private MasterKeyPBImpl convertFromProtoFormat(MasterKeyProto p) {
    return new MasterKeyPBImpl(p);
  }

  private MasterKeyProto convertToProtoFormat(MasterKey t) {
    return ((MasterKeyPBImpl)t).getProto();
  }

  @Override
  public Set<String> getNodeLabels() {
    initNodeLabels();
    return this.labels;
  }

  @Override
  public void setNodeLabels(Set<String> nodeLabels) {
    maybeInitBuilder();
    builder.clearNodeLabels();
    this.labels = nodeLabels;
  }
  
  private void initNodeLabels() {
    if (this.labels != null) {
      return;
    }
    NodeHeartbeatRequestProtoOrBuilder p = viaProto ? proto : builder;
    if (!p.hasNodeLabels()) {
      labels = null;
      return;
    }
    StringArrayProto nodeLabels = p.getNodeLabels();
    labels = new HashSet<String>(nodeLabels.getElementsList());
  }

  private ApplicationIdPBImpl convertFromProtoFormat(ApplicationIdProto p) {
    return new ApplicationIdPBImpl(p);
  }

  private ApplicationIdProto convertToProtoFormat(ApplicationId t) {
    return ((ApplicationIdPBImpl) t).getProto();
  }

  @Override
  public Map<ApplicationId, LogAggregationReport>
      getLogAggregationReportsForApps() {
    if (this.logAggregationReportsForApps != null) {
      return this.logAggregationReportsForApps;
    }
    initLogAggregationReportsForApps();
    return logAggregationReportsForApps;
  }

  private void initLogAggregationReportsForApps() {
    NodeHeartbeatRequestProtoOrBuilder p = viaProto ? proto : builder;
    List<LogAggregationReportsForAppsProto> list =
        p.getLogAggregationReportsForAppsList();
    this.logAggregationReportsForApps =
        new HashMap<ApplicationId, LogAggregationReport>();
    for (LogAggregationReportsForAppsProto c : list) {
      ApplicationId appId = convertFromProtoFormat(c.getAppId());
      LogAggregationReport report =
          convertFromProtoFormat(c.getLogAggregationReport());
      this.logAggregationReportsForApps.put(appId, report);
    }
  }

  private LogAggregationReport convertFromProtoFormat(
      LogAggregationReportProto logAggregationReport) {
    return new LogAggregationReportPBImpl(logAggregationReport);
  }

  @Override
  public void setLogAggregationReportsForApps(
      Map<ApplicationId, LogAggregationReport> logAggregationStatusForApps) {
    if (logAggregationStatusForApps == null
        || logAggregationStatusForApps.isEmpty()) {
      return;
    }
    maybeInitBuilder();
    this.logAggregationReportsForApps =
        new HashMap<ApplicationId, LogAggregationReport>();
    this.logAggregationReportsForApps.putAll(logAggregationStatusForApps);
  }
}  
