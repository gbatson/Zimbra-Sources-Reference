/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014 Zimbra, Inc.
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
package com.zimbra.cs.offline.common;

import org.dom4j.Namespace;
import org.dom4j.QName;

import com.zimbra.common.util.Constants;
import com.zimbra.common.util.StringUtil;

public interface OfflineConstants {

    public static final String NAMESPACE_STR = "urn:zimbraOffline";
    public static final Namespace NAMESPACE = Namespace.get(NAMESPACE_STR);

    public static final QName SYNC_REQUEST = QName.get("SyncRequest", NAMESPACE);
    public static final QName SYNC_RESPONSE = QName.get("SyncResponse", NAMESPACE);

    public static final QName CLIENT_EVENT_NOTIFY_REQUEST = QName.get("ClientEventNotifyRequest", NAMESPACE);
    public static final QName CLIENT_EVENT_NOTIFY_RESPONSE = QName.get("ClientEventNotifyResponse", NAMESPACE);

    public static final QName GET_EXTENSIONS_REQUEST = QName.get("GetExtensionsRequest", NAMESPACE);
    public static final QName GET_EXTENSIONS_RESPONSE = QName.get("GetExtensionsResponse", NAMESPACE);

    public static final QName ACCOUNT_BACKUP_REQUEST = QName.get("AccountBackupRequest", NAMESPACE);
    public static final QName ACCOUNT_BACKUP_RESPONSE = QName.get("AccountBackupResponse", NAMESPACE);

    public static final QName ACCOUNT_BACKUP_ENUM_REQUEST = QName.get("AccountBackupEnumerationRequest", NAMESPACE);
    public static final QName ACCOUNT_BACKUP_ENUM_RESPONSE = QName.get("AccountBackupEnumerationResponse", NAMESPACE);

    public static final QName ACCOUNT_RESTORE_REQUEST = QName.get("AccountRestoreRequest", NAMESPACE);
    public static final QName ACCOUNT_RESTORE_RESPONSE = QName.get("AccountRestoreResponse", NAMESPACE);

    public static final QName CHANGE_PASSWORD_REQUEST = QName.get("ChangePasswordRequest", NAMESPACE);
    public static final QName CHANGE_PASSWORD_RESPONSE = QName.get("ChangePasswordResponse", NAMESPACE);

    public static final QName RESET_GAL_ACCOUNT_REQUEST = QName.get("ResetGalAccountRequest", NAMESPACE);
    public static final QName RESET_GAL_ACCOUNT_RESPONSE = QName.get("ResetGalAccountResponse", NAMESPACE);

    public static final QName DIALOG_ACTION_REQUEST = QName.get("DialogActionRequest", NAMESPACE);
    public static final QName DIALOG_ACTION_RESPONSE = QName.get("DialogActionResponse", NAMESPACE);

    public static final String A_Event = "e";
    public static final String EVENT_UI_LOAD_BEGIN = "ui_load_begin";
    public static final String EVENT_UI_LOAD_END = "ui_load_end";
    public static final String EVENT_NETWORK_UP = "network_up";
    public static final String EVENT_NETWORK_DOWN = "network_down";
    public static final String EVENT_SHUTTING_DOWN = "shutting_down";

    public static final String EXTENSION = "ext";
    public static final String EXTENSION_NAME = "name";
    public static final String EXTENSION_XSYNC = "xsync";

    public static enum SyncStatus {
        unknown, offline, online, running, authfail, error
    }

    public enum SyncMsgOptions {
        SYNCEVERYTHING("0"), SYNCTOFIXEDDATE("1"), SYNCTORELATIVEDATE("2");

        private String option;

        SyncMsgOptions(String option) {
            this.option = option;
        }

        public String getOption() {
            return this.option;
        }

        public static SyncMsgOptions getOption(String value) {
            for (SyncMsgOptions opt : SyncMsgOptions.values()) {
                if (StringUtil.equalIgnoreCase(value, opt.option)) {
                    return opt;
                }
            }
            return SYNCEVERYTHING;
        }
    }

    public static final String A_offlineAccountsOrder = "offlineAccountsOrder";

    public static final String A_offlineRemoteServerVersion = "offlineRemoteServerVersion";
    public static final String A_offlineRemotePassword = "offlineRemotePassword";
    public static final String A_offlineRemoteServerUri = "offlineRemoteServerUri";
    public static final String A_offlineWebappUri = "offlineWebappUri";

    public static final String A_offlineAccountName = "offlineAccountName";
    public static final String A_offlineAccountFlavor = "offlineAccountFlavor";

    public static final String A_offlineDataSourceType = "offlineDataSourceType";
    public static final String A_offlineDataSourceName = "offlineDataSourceName";

    public static final String A_offlineFeatureSmtpEnabled = "offlineFeatureSmtpEnabled";

    public static final String A_offlineGalAccountId = "offlineGalAccountId";
    public static final String A_offlineGalAccountSyncToken = "offlineGalAccountSyncToken";
    public static final String A_offlineGalAccountLastFullSync = "offlineGalAccountLastFullSync"; // deprecated
    public static final String A_offlineGalAccountLastRefresh = "offlineGalAccountLastRefresh";
    public static final String A_offlineGalAccountDataSourceId = "offlineGalAccountDataSourceId";
    public static final String A_offlineGalGroupMembersPopulated = "offlineGalGroupMembersPopulated";

    public static final String A_offlineSyncFreq = "offlineSyncFreq";
    public static final String A_offlineSyncStatus = "offlineSyncStatus";
    public static final String A_offlineSyncStatusErrorCode = "offlineSyncStatusErrorCode";
    public static final String A_offlineSyncStatusErrorMsg = "offlineSyncStatusErrorMsg";
    public static final String A_offlineSyncStatusException = "offlineSyncStatusException";

    public static final String A_offlineLastSync = "offlineLastSync";
    public static final String A_offlineEnableTrace = "offlineEnableTrace";
    public static final String A_offlineEnableExpireOldEmails = "offlineEnableExpireOldEmails";

    public static final String A_offlineSslCertAlias = "offlineSslCertAlias";
    public static final String A_offlineAccountSetup = "offlineAccountSetup";

    public static final String A_offlineYContactTokenReady = "offlineYContactTokenReady";
    public static final String A_offlinesyncFieldName = "offlinesyncFieldName";
    public static final String A_offlinesyncFixedDate = "offlinesyncFixedDate";
    public static final String A_offlinesyncRelativeDate = "offlinesyncRelativeDate";
    public static final String A_offlinesyncEmailDate = "offlinesyncEmailDate";
    public static final String A_zimbraDataSourceSmtpEnabled = "zimbraDataSourceSmtpEnabled";
    public static final String A_zimbraDataSourceSmtpHost = "zimbraDataSourceSmtpHost";
    public static final String A_zimbraDataSourceSmtpPort = "zimbraDataSourceSmtpPort";
    public static final String A_zimbraDataSourceSmtpConnectionType = "zimbraDataSourceSmtpConnectionType";
    public static final String A_zimbraDataSourceSmtpAuthRequired = "zimbraDataSourceSmtpAuthRequired";
    public static final String A_zimbraDataSourceSmtpAuthUsername = "zimbraDataSourceSmtpAuthUsername";
    public static final String A_zimbraDataSourceSmtpAuthPassword = "zimbraDataSourceSmtpAuthPassword";

    public static final String A_zimbraDataSourceUseProxy = "zimbraDataSourceUseProxy";
    public static final String A_zimbraDataSourceProxyHost = "zimbraDataSourceProxyHost";
    public static final String A_zimbraDataSourceProxyPort = "zimbraDataSourceProxyPort";

    public static final String A_zimbraDataSourceSyncFreq = "zimbraDataSourceSyncFreq";
    public static final String A_zimbraDataSourceSyncStatus = "zimbraDataSourceSyncStatus";
    public static final String A_zimbraDataSourceSyncStatusErrorCode = "A_zimbraDataSourceSyncStatusErrorCode";
    public static final String A_zimbraDataSourceLastSync = "zimbraDataSourceLastSync";

    public static final String A_zimbraDataSourceContactSyncEnabled = "zimbraDataSourceContactSyncEnabled";
    public static final String A_zimbraDataSourceCalendarSyncEnabled = "zimbraDataSourceCalendarSyncEnabled";
    public static final String A_zimbraDataSourceTaskSyncEnabled = "zimbraDataSourceTaskSyncEnabled";

    public static final String A_zimbraDataSourceCalendarFolderId = "zimbraDataSourceCalendarFolderId";

    public static final String A_zimbraDataSourceSyncAllServerFolders = "zimbraDataSourceSyncAllServerFolders";

    public static final String A_zimbraDataSourceSslCertAlias = "zimbraDataSourceSslCertAlias";
    public static final String A_zimbraDataSourceAccountSetup = "zimbraDataSourceAccountSetup";

    public static final long DEFAULT_SYNC_FREQ = 15 * Constants.MILLIS_PER_MINUTE;
    public static final long MIN_SYNC_FREQ = Constants.MILLIS_PER_MINUTE;

    public static final String LOCAL_ACCOUNT_ID = "ffffffff-ffff-ffff-ffff-ffffffffffff";
    public static final String GAL_ACCOUNT_SUFFIX = "__OFFLINE_GAL__";
    public static final String GAL_LDAP_DN = "GAL_LDAP_DN";
    public static final String YMAIL_PARTNER_NAME = "Zimbra";
    public static final String CALDAV_DS = "caldav:";
    public static final String YAB_DS = "yab:";
    public static final String SYNC_SERVER_PREFIX = "offline_sync_server_";

    public static final String E_BACKUP = "backup";
    public static final String A_RESOLVE = "resolve";
}
