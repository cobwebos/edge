/*
 * Copyright © 2018 www.cobwebos.com and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cobwebos.edge.impl;

import org.opendaylight.yang.gen.v1.http.www.raisecom.com.netconf.alarm.rev180601.CurrentAlarmReport;
import org.opendaylight.yang.gen.v1.http.www.raisecom.com.netconf.alarm.rev180601.StandardAlarmListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author syy
 *
 */
public class AlarmNotificationListener implements StandardAlarmListener {
	private Logger logger = LoggerFactory.getLogger(AlarmNotificationListener.class);
	
	private String nodeId;
	
	public AlarmNotificationListener(String nodeId) {
		this.nodeId = nodeId;
	}
	
	@Override
	public void onCurrentAlarmReport(CurrentAlarmReport notification) {
		logger.info("nodeId:{},notification:{}",nodeId,notification.toString());
		
	}

}
