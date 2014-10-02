/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
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
#pragma once
#include "ReminderEventListener.h"
#include "mso.tlh"
#include "msoutl.tlh"

namespace Zimbra
{
namespace MAPI
{
class MAPIStoreException: public GenericException
{
public:
    MAPIStoreException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    MAPIStoreException(HRESULT hrErrCode, LPCWSTR lpszDescription, LPCWSTR lpszShortDescription, int nLine, LPCSTR strFile);
    virtual ~MAPIStoreException() {}
};

class MAPIFolder;

// Mapi Store class
class MAPIStore
{
private:
    LPMDB m_Store;
    LPMAPISESSION m_mapiSession;
	SBinaryArray m_specialFolderIds;
    Zimbra::Util::CriticalSection cs_store;
	Olk::_NameSpacePtr m_pOlkMapi;
	CReminderEventListener *m_pReminderEventHandler;
	LPDISPATCH m_pOlkReminderDisp;
	wstring m_wstrProfileName;
	int m_migtype;
	bool m_bdefaultStore;
public:
    MAPIStore(int migtype);
    ~MAPIStore();
    void Initialize(LPMAPISESSION mapisession, LPMDB pMdb, LPWSTR lpwstrProfileName, bool bdefaultStore=false);
    HRESULT CompareEntryIDs(SBinary *pBin1, SBinary *pBin2, ULONG &lpulResult);
    HRESULT GetRootFolder(MAPIFolder &rootFolder);

    LPMDB GetInternalMAPIStore() { return m_Store; }
    SBinaryArray GetSpecialFolderIds() { return m_specialFolderIds; }
    HRESULT OpenEntry(ULONG cbEntryID, LPENTRYID lpEntryID, LPCIID lpInterface, ULONG ulFlags,
        ULONG FAR *lpulObjType, LPUNKNOWN FAR *lppUnk);
	void UnRegisterReminderHandler();
	void RegisterReminderEventHandler();
	Olk::_NameSpacePtr GetOOMInstance() {return m_pOlkMapi;}
};
}
}
