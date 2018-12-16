/*
 * Copyright © 2018 www.cobwebos.com and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cobwebos.edge.impl;


import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.edge.rev170830.EdgeService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.edge.rev170830.HelloOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.edge.rev170830.HelloOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import java.util.concurrent.Future;
/**
 * 
 * @author syy
 *
 */
public class EdgeImpl implements EdgeService {

    @Override
    public Future<RpcResult<HelloOutput>> hello() {
        HelloOutputBuilder builder = new HelloOutputBuilder();
        builder.setStatus("ok");
        return RpcResultBuilder.success(builder.build()).buildFuture();
    }
}
