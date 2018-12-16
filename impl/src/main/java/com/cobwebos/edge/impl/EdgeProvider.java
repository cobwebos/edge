/*
 * Copyright © 2018 www.cobwebos.com and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cobwebos.edge.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.binding.api.NotificationService;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.network.topology.topology.topology.types.TopologyNetconf;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.edge.rev170830.EdgeService;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author syy
 *
 */

public class EdgeProvider implements BindingAwareProvider, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(EdgeProvider.class);
    private final DataBroker dataBroker;
    private RpcProviderRegistry rpcProviderRegistry;
    private NotificationService notificationService;
    private EdgeService edgeService;
    private BindingAwareBroker.RpcRegistration<EdgeService> rpcRegistration;
    private MountPointService mountPointService;
    private DataTreeChangeListener sbiDataTreeChangeListener;
    private DataTreeChangeListener nbiDataTreeChangeListener;
    private ListenerRegistration sbiListenerRegistration;
    private ListenerRegistration nbiListenerRegistration;
    



    public static final InstanceIdentifier<Topology> NETCONF_TOPO_IID =
            InstanceIdentifier
                    .create(NetworkTopology.class)
                    .child(Topology.class,
                            new TopologyKey(new TopologyId(TopologyNetconf.QNAME.getLocalName())));

    public EdgeProvider(final DataBroker dataBroker,
                        final RpcProviderRegistry rpcProviderRegistry,
                        final NotificationService notificationService) {
        this.dataBroker = dataBroker;
        LOG.info("edge - dataBroker  service injected - " + dataBroker.toString());
        this.rpcProviderRegistry = rpcProviderRegistry;
        LOG.info("edge - rpcProviderRegistry service injected - " + rpcProviderRegistry.toString());
        this.notificationService = notificationService;
        LOG.info("edge - notificationService service injected - " + notificationService.toString());
    }

    /**
     * Method called when the blueprint container is created.
     */
    public void init() {
        edgeService = new EdgeImpl();
        rpcRegistration = rpcProviderRegistry.addRpcImplementation(EdgeService.class,edgeService);
        LOG.info("EdgeProvider Session Initiated");
    }
    
    @Override
	public void onSessionInitiated(ProviderContext session) {
		this.mountPointService = session.getSALService(MountPointService.class);
        this.sbiDataTreeChangeListener = new SbiDataTreeChangeListenerImpl(session);      
        this.nbiDataTreeChangeListener = new NbiDataTreeChangeListenerImpl(session);      
        this.sbiListenerRegistration = this.dataBroker.registerDataTreeChangeListener(new DataTreeIdentifier<>(LogicalDatastoreType.OPERATIONAL,NETCONF_TOPO_IID.child(Node.class)),sbiDataTreeChangeListener);
       
	}

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
        rpcRegistration.close();
        sbiListenerRegistration.close();
        nbiListenerRegistration.close();
        LOG.info("EdgeProvider Closed");
    }


}