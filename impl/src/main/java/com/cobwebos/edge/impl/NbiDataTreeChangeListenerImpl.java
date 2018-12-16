/*
 * Copyright © 2018 www.cobwebos.com and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cobwebos.edge.impl;

import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
/**
 * 
 * @author syy
 *
 */
public class NbiDataTreeChangeListenerImpl implements DataTreeChangeListener {
    private Logger logger = LoggerFactory.getLogger(NbiDataTreeChangeListenerImpl.class);
	public NbiDataTreeChangeListenerImpl(ProviderContext session) {
		
	}

	@Override
    public void onDataTreeChanged(@Nonnull Collection changes) {
		logger.info("NbiDataTreeChangeListenerImpl onDataTreeChanged:{}",changes.toString());
    }
}
