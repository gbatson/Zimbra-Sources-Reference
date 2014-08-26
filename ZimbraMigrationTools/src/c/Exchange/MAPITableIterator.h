/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
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
#pragma once

namespace Zimbra
{
namespace MAPI
{
class MAPITableIterator
{
protected:
    LPMAPIFOLDER m_pParentFolder;
    LPMAPITABLE m_pTable;
    LPSRowSet m_pRows;
    ULONG m_currRow;
    ULONG m_batchSize;
    ULONG m_rowsVisited;
    ULONG m_totalRows;
    MAPISession *m_session;

public:
    MAPITableIterator();
    virtual ~MAPITableIterator();

    virtual void Initialize(LPMAPITABLE pTable, LPMAPIFOLDER pFolder, MAPISession &session,
        ULONG ulItemTypeMask = ZCM_ALL);
    virtual LPSPropTagArray GetProps() = 0;
    virtual LPSSortOrderSet GetSortOrder() = 0;
    virtual LPSRestriction GetRestriction(ULONG TypeMask, FILETIME startDate) = 0;
    SRow *GetNext();
};
}
}
