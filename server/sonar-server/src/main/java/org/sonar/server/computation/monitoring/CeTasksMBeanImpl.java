/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.computation.monitoring;

import org.picocontainer.Startable;
import org.sonar.ce.monitoring.CEQueueStatus;
import org.sonar.process.Jmx;
import org.sonar.process.systeminfo.SystemInfoSection;
import org.sonar.process.systeminfo.protobuf.ProtobufSystemInfo;
import org.sonar.server.computation.configuration.CeConfiguration;

public class CeTasksMBeanImpl implements CeTasksMBean, Startable, SystemInfoSection {
  private final CEQueueStatus queueStatus;
  private final CeConfiguration ceConfiguration;

  public CeTasksMBeanImpl(CEQueueStatus queueStatus, CeConfiguration ceConfiguration) {
    this.queueStatus = queueStatus;
    this.ceConfiguration = ceConfiguration;
  }

  @Override
  public void start() {
    Jmx.register(OBJECT_NAME, this);
  }

  /**
   * Unregister, if needed
   */
  @Override
  public void stop() {
    Jmx.unregister(OBJECT_NAME);
  }

  @Override
  public long getPendingCount() {
    return queueStatus.getPendingCount();
  }

  @Override
  public long getInProgressCount() {
    return queueStatus.getInProgressCount();
  }

  @Override
  public long getErrorCount() {
    return queueStatus.getErrorCount();
  }

  @Override
  public long getSuccessCount() {
    return queueStatus.getSuccessCount();
  }

  @Override
  public long getProcessingTime() {
    return queueStatus.getProcessingTime();
  }

  @Override
  public int getWorkerCount() {
    return ceConfiguration.getWorkerCount();
  }

  @Override
  public ProtobufSystemInfo.Section toProtobuf() {
    ProtobufSystemInfo.Section.Builder builder = ProtobufSystemInfo.Section.newBuilder();
    builder.setName("Compute Engine Tasks");
    builder.addAttributesBuilder().setKey("Pending").setLongValue(getPendingCount()).build();
    builder.addAttributesBuilder().setKey("In Progress").setLongValue(getInProgressCount()).build();
    builder.addAttributesBuilder().setKey("Processed With Error").setLongValue(getErrorCount()).build();
    builder.addAttributesBuilder().setKey("Processed With Success").setLongValue(getSuccessCount()).build();
    builder.addAttributesBuilder().setKey("Processing Time (ms)").setLongValue(getProcessingTime()).build();
    builder.addAttributesBuilder().setKey("Worker Count").setLongValue(getWorkerCount()).build();
    return builder.build();
  }
}
