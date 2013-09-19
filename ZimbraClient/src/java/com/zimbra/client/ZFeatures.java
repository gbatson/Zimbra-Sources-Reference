/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.client;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Iterables;
import com.zimbra.common.account.ProvisioningConstants;
import com.zimbra.common.account.ZAttrProvisioning;

public class ZFeatures {

    private Map<String, Collection<String>> mAttrs;

    public ZFeatures(Map<String, Collection<String>> attrs) {
        mAttrs = attrs;
    }

    /**
     * @param name name of attr to get
     * @return null if unset, or first value in list
     */
    private String get(String name) {
        Collection<String> value = mAttrs.get(name);
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Iterables.get(value, 0);

    }

    public boolean getBool(String name) {
        return ProvisioningConstants.TRUE.equals(get(name));
    }

    public Map<String, Collection<String>> getAttrs() { return mAttrs; }
    
    public boolean getContacts() { return getBool(ZAttrProvisioning.A_zimbraFeatureContactsEnabled); }

    public boolean getMail() { return getBool(ZAttrProvisioning.A_zimbraFeatureMailEnabled); }

    public boolean getAdminMail() {return getBool(ZAttrProvisioning.A_zimbraFeatureAdminMailEnabled); }

    public boolean getVoice() { return getBool(ZAttrProvisioning.A_zimbraFeatureVoiceEnabled); }

    public boolean getCalendar() { return getBool(ZAttrProvisioning.A_zimbraFeatureCalendarEnabled); }

    public boolean getCalendarUpsell() { return getBool(ZAttrProvisioning.A_zimbraFeatureCalendarUpsellEnabled); }

    public String getCalendarUpsellURL() { return get(ZAttrProvisioning.A_zimbraFeatureCalendarUpsellURL); }    

    public boolean getTasks() { return getBool(ZAttrProvisioning.A_zimbraFeatureTasksEnabled); }

    public boolean getTagging() { return getBool(ZAttrProvisioning.A_zimbraFeatureTaggingEnabled); }

    public boolean getOptions() { return getBool(ZAttrProvisioning.A_zimbraFeatureOptionsEnabled); }
    
    public boolean getAdvancedSearch() { return getBool(ZAttrProvisioning.A_zimbraFeatureAdvancedSearchEnabled); }

    public boolean getSavedSearches() { return getBool(ZAttrProvisioning.A_zimbraFeatureSavedSearchesEnabled); }

    public boolean getConversations() { return getBool(ZAttrProvisioning.A_zimbraFeatureConversationsEnabled); }

    public boolean getChangePassword() { return getBool(ZAttrProvisioning.A_zimbraFeatureChangePasswordEnabled); }

    public boolean getInitialSearchPreference() { return getBool(ZAttrProvisioning.A_zimbraFeatureInitialSearchPreferenceEnabled); }

    public boolean getFilters() { return getBool(ZAttrProvisioning.A_zimbraFeatureFiltersEnabled); }

    public boolean getGal() { return getBool(ZAttrProvisioning.A_zimbraFeatureGalEnabled); }

    public boolean getHtmlCompose() { return getBool(ZAttrProvisioning.A_zimbraFeatureHtmlComposeEnabled); }

    public boolean getIM() { return getBool(ZAttrProvisioning.A_zimbraFeatureIMEnabled); }

    public boolean getViewInHtml() { return getBool(ZAttrProvisioning.A_zimbraFeatureViewInHtmlEnabled); }

    public boolean getSharing() { return getBool(ZAttrProvisioning.A_zimbraFeatureSharingEnabled); }

    public boolean getMailForwarding() { return getBool(ZAttrProvisioning.A_zimbraFeatureMailForwardingEnabled); }

    public boolean getMailForwardingInFilter() { return getBool(ZAttrProvisioning.A_zimbraFeatureMailForwardingInFiltersEnabled); }

    public boolean getMobileSync() { return getBool(ZAttrProvisioning.A_zimbraFeatureMobileSyncEnabled); }

    public boolean getSkinChange() { return getBool(ZAttrProvisioning.A_zimbraFeatureSkinChangeEnabled); }

    public boolean getNotebook() { return false; } //bug:56196 getBool(ZAttrProvisioning.A_zimbraFeatureNotebookEnabled);

    public boolean getBriefcases() { return getBool(ZAttrProvisioning.A_zimbraFeatureBriefcasesEnabled); }

    public boolean getGalAutoComplete() { return getBool(ZAttrProvisioning.A_zimbraFeatureGalAutoCompleteEnabled); }

    public boolean getOutOfOfficeReply() { return getBool(ZAttrProvisioning.A_zimbraFeatureOutOfOfficeReplyEnabled); }

    public boolean getNewMailNotification() { return getBool(ZAttrProvisioning.A_zimbraFeatureNewMailNotificationEnabled); }

    public boolean getIdentities() { return getBool(ZAttrProvisioning.A_zimbraFeatureIdentitiesEnabled); }

    public boolean getPop3DataSource() { return getBool(ZAttrProvisioning.A_zimbraFeaturePop3DataSourceEnabled); }
    
    public boolean getGroupcalendarEnabled() { return getBool(ZAttrProvisioning.A_zimbraFeatureGroupCalendarEnabled); }

    public boolean getDataSourceImportOnLogin() { return getBool(ZAttrProvisioning.A_zimbraDataSourceImportOnLogin); }

    public boolean getFlagging() { return getBool(ZAttrProvisioning.A_zimbraFeatureFlaggingEnabled); }

    public boolean getMailPriority() { return getBool(ZAttrProvisioning.A_zimbraFeatureMailPriorityEnabled); }

    public boolean getPortalEnabled() { return getBool(ZAttrProvisioning.A_zimbraFeaturePortalEnabled); }

    public boolean getContactsDetailedSearch() { return getBool(ZAttrProvisioning.A_zimbraFeatureContactsDetailedSearchEnabled); }

    public boolean getDiscardFilterEnabled() { return getBool(ZAttrProvisioning.A_zimbraFeatureDiscardInFiltersEnabled); }
    
    // defaults to TRUE
    public boolean getWebSearchEnabled() { return get(ZAttrProvisioning.A_zimbraFeatureWebSearchEnabled) == null ||
    											  getBool(ZAttrProvisioning.A_zimbraFeatureWebSearchEnabled); }

    // defaults to TRUE
    public boolean getWebClientShowOfflineLink() { return get(ZAttrProvisioning.A_zimbraWebClientShowOfflineLink) == null ||
                                                          getBool(ZAttrProvisioning.A_zimbraWebClientShowOfflineLink); }

	// defaults to TRUE
	public boolean getNewAddrBookEnabled() { return get(ZAttrProvisioning.A_zimbraFeatureNewAddrBookEnabled) == null ||
													getBool(ZAttrProvisioning.A_zimbraFeatureNewAddrBookEnabled); }
	// defaults to TRUE
	public boolean getPop3Enabled() { return	get(ZAttrProvisioning.A_zimbraPop3Enabled) == null ||
												getBool(ZAttrProvisioning.A_zimbraPop3Enabled); }
	// defaults to TRUE
	public boolean getSpam() { return	get(ZAttrProvisioning.A_zimbraFeatureAntispamEnabled) == null ||
												getBool(ZAttrProvisioning.A_zimbraFeatureAntispamEnabled); }
}

