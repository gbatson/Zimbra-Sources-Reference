/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013, 2014 Zimbra, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.common.soap;

import org.dom4j.Namespace;
import org.dom4j.QName;

public final class ReplicationConstants {
    public static final String NAMESPACE_STR = "urn:zimbraRepl";
    public static final Namespace NAMESPACE = Namespace.get(NAMESPACE_STR);

    public static final String E_REPLICATION_STATUS_REQUEST = "ReplicationStatusRequest";
    public static final String E_REPLICATION_STATUS_RESPONSE = "ReplicationStatusResponse";
    public static final String E_BECOME_MASTER_REQUEST = "BecomeMasterRequest";
    public static final String E_BECOME_MASTER_RESPONSE = "BecomeMasterResponse";
    public static final String E_START_CATCHUP_REQUEST = "StartCatchupRequest";
    public static final String E_START_CATCHUP_RESPONSE = "StartCatchupResponse";

    public static final String E_BRING_UP_SERVICE_IP_REQUEST = "BringUpServiceIPRequest";
    public static final String E_BRING_UP_SERVICE_IP_RESPONSE = "BringUpServiceIPResponse";
    public static final String E_BRING_DOWN_SERVICE_IP_REQUEST = "BringDownServiceIPRequest";
    public static final String E_BRING_DOWN_SERVICE_IP_RESPONSE = "BringDownServiceIPResponse";
    public static final String E_START_HA_CLIENT_REQUEST = "StartFailoverClientRequest";
    public static final String E_START_HA_CLIENT_RESPONSE = "StartFailoverClientResponse";
    public static final String E_STOP_HA_CLIENT_REQUEST = "StopFailoverClientRequest";
    public static final String E_STOP_HA_CLIENT_RESPONSE = "StopFailoverClientResponse";
    public static final String E_START_HA_DAEMON_REQUEST = "StartFailoverDaemonRequest";
    public static final String E_START_HA_DAEMON_RESPONSE = "StartFailoverDaemonResponse";
    public static final String E_STOP_HA_DAEMON_REQUEST = "StopFailoverDaemonRequest";
    public static final String E_STOP_HA_DAEMON_RESPONSE = "StopFailoverDaemonResponse";

    public static final QName REPLICATION_STATUS_REQUEST = QName.get(E_REPLICATION_STATUS_REQUEST, NAMESPACE);
    public static final QName REPLICATION_STATUS_RESPONSE = QName.get(E_REPLICATION_STATUS_RESPONSE, NAMESPACE);
    public static final QName BECOME_MASTER_REQUEST = QName.get(E_BECOME_MASTER_REQUEST, NAMESPACE);
    public static final QName BECOME_MASTER_RESPONSE = QName.get(E_BECOME_MASTER_RESPONSE, NAMESPACE);
    public static final QName START_CATCHUP_REQUEST = QName.get(E_START_CATCHUP_REQUEST, NAMESPACE);
    public static final QName START_CATCHUP_RESPONSE = QName.get(E_START_CATCHUP_RESPONSE, NAMESPACE);

    public static final QName BRING_UP_SERVICE_IP_REQUEST = QName.get(E_BRING_UP_SERVICE_IP_REQUEST, NAMESPACE);
    public static final QName BRING_UP_SERVICE_IP_RESPONSE = QName.get(E_BRING_UP_SERVICE_IP_RESPONSE, NAMESPACE);
    public static final QName BRING_DOWN_SERVICE_IP_REQUEST = QName.get(E_BRING_DOWN_SERVICE_IP_REQUEST, NAMESPACE);
    public static final QName BRING_DOWN_SERVICE_IP_RESPONSE = QName.get(E_BRING_DOWN_SERVICE_IP_RESPONSE, NAMESPACE);
    public static final QName START_HA_CLIENT_REQUEST = QName.get(E_START_HA_CLIENT_REQUEST, NAMESPACE);
    public static final QName START_HA_CLIENT_RESPONSE = QName.get(E_START_HA_CLIENT_RESPONSE, NAMESPACE);
    public static final QName STOP_HA_CLIENT_REQUEST = QName.get(E_STOP_HA_CLIENT_REQUEST, NAMESPACE);
    public static final QName STOP_HA_CLIENT_RESPONSE = QName.get(E_STOP_HA_CLIENT_RESPONSE, NAMESPACE);
    public static final QName START_HA_DAEMON_REQUEST = QName.get(E_START_HA_DAEMON_REQUEST, NAMESPACE);
    public static final QName START_HA_DAEMON_RESPONSE = QName.get(E_START_HA_DAEMON_RESPONSE, NAMESPACE);
    public static final QName STOP_HA_DAEMON_REQUEST = QName.get(E_STOP_HA_DAEMON_REQUEST, NAMESPACE);
    public static final QName STOP_HA_DAEMON_RESPONSE = QName.get(E_STOP_HA_DAEMON_RESPONSE, NAMESPACE);

    public static final String E_MASTER_STATUS = "masterStatus";
    public static final String E_SLAVE_STATUS = "slaveStatus";
    public static final String E_CATCHUP_STATUS = "catchupStatus";

    public static final String A_REPLICATION_ENABLED = "replicationEnabled";    // whether replication is enabled
    public static final String A_CURRENT_ROLE = "currentRole";      // currently running as master or slave
    public static final String A_ORIGINAL_ROLE = "originalRole";    // master or slave as originally configured
    public static final String A_MASTER_OPERATING_MODE = "masterOperatingMode";  // RedoLogManager.OPMODE_*
    public static final String A_REMAINING_OPS   = "remainingOps";
    public static final String A_REMAINING_FILES = "remainingFiles";
    public static final String A_REMAINING_BYTES = "remainingBytes";

    public static final String ROLE_STANDALONE  = "standalone";
    public static final String ROLE_MASTER = "master";
    public static final String ROLE_SLAVE  = "slave";
}
