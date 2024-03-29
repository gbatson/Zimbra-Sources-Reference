/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013, 2014 Zimbra, Inc.
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
package com.zimbra.cs.ldap.unboundid;

import com.unboundid.ldap.sdk.SearchScope;

import com.zimbra.cs.ldap.ZSearchScope;

public class UBIDSearchScope extends ZSearchScope {

    final private SearchScope searchScope;
    
    private UBIDSearchScope(SearchScope searchScope) {
        this.searchScope = searchScope;
    }
    
    SearchScope getNative() {
        return searchScope;
    }

    public static class UBIDSearchScopeFactory extends ZSearchScope.ZSearchScopeFactory {
        @Override
        protected ZSearchScope getBaseSearchScope() {
            return new UBIDSearchScope(SearchScope.BASE);
        }
        
        @Override
        protected ZSearchScope getOnelevelSearchScope() {
            return new UBIDSearchScope(SearchScope.ONE);
        }
        
        @Override
        protected ZSearchScope getSubtreeSearchScope() {
            return new UBIDSearchScope(SearchScope.SUB);
        }

        @Override
        protected ZSearchScope getChildrenSearchScope() {
            return new UBIDSearchScope(SearchScope.SUBORDINATE_SUBTREE);
        }
    }

}
