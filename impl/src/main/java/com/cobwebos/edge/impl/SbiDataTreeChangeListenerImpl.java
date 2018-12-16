/*
 * Copyright © 2018 www.cobwebos.com and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cobwebos.edge.impl;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.binding.api.NotificationService;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNodeConnectionStatus.ConnectionStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.network.topology.topology.topology.types.TopologyNetconf;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.yang.binding.Identifier;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author syy
 *
 */

public class SbiDataTreeChangeListenerImpl implements DataTreeChangeListener {
    private Logger logger = LoggerFactory.getLogger(SbiDataTreeChangeListenerImpl.class);
    private MountPointService mountPointService;

    public SbiDataTreeChangeListenerImpl(BindingAwareBroker.ProviderContext session){
            this.mountPointService = session.getSALService(MountPointService.class);
    }

    @Override
    public void onDataTreeChanged(@Nonnull Collection changes) {
    	logger.info("SbiDataTreeChangeListenerImpl onDataTreeChanged changes:{ }", changes.toString());
    	for(DataTreeModification<Node> change : (Collection<DataTreeModification<Node>>)changes) {
    		logger.info("change:{ }",change.toString());
    		DataObjectModification<Node> rootNode = change.getRootNode() ;
    		InstanceIdentifier path = (InstanceIdentifier) rootNode.getIdentifier();
            NodeId nodeId = getNodeId(path);
            String nodeIdValue = nodeId.getValue();
            logger.info("nodeId: {} " ,nodeIdValue);
            if(null!=nodeId && rootNode.getModificationType() == DataObjectModification.ModificationType.WRITE){
                NetconfNode netconfNode = (NetconfNode) rootNode.getDataAfter();
                ConnectionStatus status = netconfNode.getConnectionStatus();
                switch (status){
                    case Connected:
                        logger.info("netconf node  {} is Connected .",nodeIdValue);
                        InstanceIdentifier<Node> netconfTopoIID = InstanceIdentifier.builder(NetworkTopology.class)
                                .child(Topology.class,new TopologyKey(new TopologyId(TopologyNetconf.QNAME.getLocalName())))
                                .child(Node.class, new NodeKey(new NodeId(nodeIdValue))).build();
                        registerAlarmNotification(nodeIdValue,netconfTopoIID);
                        break;
                    case Connecting:
                        logger.info("netconf node  {} is Connecting ...",nodeIdValue);
                        break;
                    case UnableToConnect:
                        logger.info("netconf node  {} is UnableToConnect !",nodeIdValue);
                }
            }else{
                logger.info("netconf node  {} is null !");
            }
    	}
    }

    private NodeId getNodeId(final InstanceIdentifier<?> path) {
        for (InstanceIdentifier.PathArgument pathArgument : path.getPathArguments()) {
            if (pathArgument instanceof InstanceIdentifier.IdentifiableItem<?, ?>) {
                final Identifier key = ((InstanceIdentifier.IdentifiableItem) pathArgument).getKey();
                if (key instanceof NodeKey) {
                    return ((NodeKey) key).getNodeId();
                }
            }
        }
        return null;
    }
    private String getNodeIdValue(final InstanceIdentifier<?> path) {
        for (InstanceIdentifier.PathArgument pathArgument : path.getPathArguments()) {
            if (pathArgument instanceof InstanceIdentifier.IdentifiableItem<?, ?>) {
                final Identifier key = ((InstanceIdentifier.IdentifiableItem) pathArgument).getKey();
                if (key instanceof NodeKey) {
                    return ((NodeKey) key).getNodeId().getValue();
                }
            }
        }
        return null;
    }

    private void registerAlarmNotification(String nodeId, InstanceIdentifier<Node> netconfTopoIID) {
        NotificationService notificationService = mountPointService.getMountPoint(netconfTopoIID).get()
                .getService(NotificationService.class).get();
        notificationService.registerNotificationListener(new AlarmNotificationListener(nodeId));

    }
   

}
