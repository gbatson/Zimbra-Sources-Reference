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
#include "common.h"
#include "Exchange.h"
#include "MAPIStore.h"
#include "Logger.h"
#include "MAPIAccessAPI.h"
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// Exception class
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIStoreException::MAPIStoreException(HRESULT hrErrCode, LPCWSTR
    lpszDescription): GenericException(hrErrCode, lpszDescription)
{
    //
}

MAPIStoreException::MAPIStoreException(HRESULT hrErrCode, LPCWSTR lpszDescription, LPCWSTR lpszShortDescription, 
	int nLine, LPCSTR strFile): GenericException(hrErrCode, lpszDescription, lpszShortDescription, nLine, strFile)
{
    //
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MAPIStore
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIStore::MAPIStore(int migtype): m_Store(NULL), m_mapiSession(NULL),m_pReminderEventHandler(NULL),m_pOlkReminderDisp(NULL),m_pOlkMapi(NULL),
	m_bdefaultStore(false)
{
	m_migtype = migtype;
	m_specialFolderIds.cValues = 0;
	m_specialFolderIds.lpbin = NULL;
}

MAPIStore::~MAPIStore()
{
    Zimbra::Util::AutoCriticalSection autocriticalsection(cs_store);

	//ULONG flags = 0;//LOGOFF_ORDERLY;
	if ((m_specialFolderIds.cValues != 0) && (m_specialFolderIds.lpbin != NULL))
	{
		Zimbra::MAPI::Util::FreeAllSpecialFolders(&m_specialFolderIds);
		m_specialFolderIds.cValues = 0;
		m_specialFolderIds.lpbin = NULL;
	}
		
    if (m_Store)
    {
		//hangs on OL 2007
        //m_Store->StoreLogoff(&flags);
        m_Store->Release();
    }
    m_Store = NULL;
	if ((!m_bdefaultStore) || (Zimbra::MAPI::MAPIAccessAPI::GetMigrationType() != MAILBOX_MIG))
	{
		UnRegisterReminderHandler();
		//MAPIUninitialize();
	}
}


void MAPIStore::UnRegisterReminderHandler()
{
	dlogi("UnRegisterReminderHandler");
	if(Zimbra::MAPI::MAPIAccessAPI::GetMigrationType() == PST_MIG)
	{
		dlogi("Release OOM Event Handlers.");
		// Detach the connection point if any
		if (m_pReminderEventHandler != NULL)
		{
			m_pReminderEventHandler->DetachFromOutlook();
			m_pReminderEventHandler->Release();
			m_pReminderEventHandler = NULL;
		}
		if (m_pOlkReminderDisp)
		{
			m_pOlkReminderDisp->Release();
			m_pOlkReminderDisp = NULL;
		}
	}

	try
	{
		if(m_pOlkMapi)
		{
			m_pOlkMapi->Logoff();
		}
	}
	catch(...)
	{
		//
	}
}

void MAPIStore::RegisterReminderEventHandler()
{   
	dlogi("RegisterReminderHandler");
    UnRegisterReminderHandler();
	    
    try
	{
        Olk::_ApplicationPtr pOlkApp(L"Outlook.Application", NULL, CLSCTX_LOCAL_SERVER);
        m_pOlkMapi = pOlkApp->GetNamespace(L"MAPI");

		m_pOlkMapi->Logon(m_wstrProfileName.c_str(), L"", false, true);

		if(Zimbra::MAPI::MAPIAccessAPI::GetMigrationType() == PST_MIG)
		{
			dlogi("Set OOM Event Handlers.");
			// Register CReminderEventListener with Outlook Connection point container
			// So that we can capture outlook events
			m_pReminderEventHandler = new CReminderEventListener;

			Olk::_Reminders *pReminders = NULL;

			m_pOlkMapi->GetApplication()->get_Reminders(&pReminders);
			if (pReminders)
			{
				pReminders->QueryInterface(IID_IDispatch, (LPVOID *)&m_pOlkReminderDisp);
				if (m_pOlkReminderDisp)
					m_pReminderEventHandler->AttachToOutlook(m_pOlkReminderDisp);
				pReminders->Release();
			}
		}

    }
    catch (_com_error)
    {
        dloge("MAPISession::RegisterReminderEventHandler(): Outlook Reminder Event Handler could not be registered");
    }
}

void MAPIStore::Initialize(LPMAPISESSION mapisession, LPMDB pMdb, LPWSTR lpwstrProfileName, bool bdefaultStore)
{
    Zimbra::Util::AutoCriticalSection autocriticalsection(cs_store);
	m_bdefaultStore = bdefaultStore;
    m_Store = pMdb;
    m_mapiSession = mapisession;
	m_wstrProfileName = lpwstrProfileName;

	if ((!m_bdefaultStore) || (Zimbra::MAPI::MAPIAccessAPI::GetMigrationType() != MAILBOX_MIG))
	{
		//MAPIINIT_0 MAPIInit = { 0, MAPI_MULTITHREAD_NOTIFICATIONS };
		HRESULT hr=CoInitialize(NULL);
		hr= MAPIInitialize(NULL);//(&MAPIInit);

		RegisterReminderEventHandler();
	}
	g_ulIMAPHeaderInfoPropTag = Zimbra::MAPI::Util::IMAPHeaderInfoPropTag(m_Store);

    Zimbra::MAPI::Util::GetAllSpecialFolders(m_Store, &m_specialFolderIds);
}

HRESULT MAPIStore::CompareEntryIDs(SBinary *pBin1, SBinary *pBin2, ULONG &lpulResult)
{
    Zimbra::Util::AutoCriticalSection autocriticalsection(cs_store);
    HRESULT hr = S_OK;

    hr = m_Store->CompareEntryIDs(pBin1->cb, (LPENTRYID)(pBin1->lpb), pBin2->cb,
        (LPENTRYID)(pBin2->lpb), 0, &lpulResult);
    return hr;
}

HRESULT MAPIStore::GetRootFolder(MAPIFolder &rootFolder)
{
    Zimbra::Util::AutoCriticalSection autocriticalsection(cs_store);
    HRESULT hr = S_OK;
    SBinary bin;
    ULONG objtype = 0;
	bin.cb = 0;
	bin.lpb = NULL;
    LPMAPIFOLDER pFolder = NULL;
	
	if (FAILED(hr = Zimbra::MAPI::Util::HrMAPIFindIPMSubtree(m_Store, bin)))
		throw MAPIStoreException(hr, L"GetRootFolder(): HrMAPIFindIPMSubtree Failed.", 
		ERR_ROOT_FOLDER, __LINE__, __FILE__);
	if (FAILED(hr = m_Store->OpenEntry(bin.cb, (LPENTRYID)bin.lpb, NULL, MAPI_BEST_ACCESS,
			&objtype, (LPUNKNOWN *)&pFolder)))
		throw MAPIStoreException(hr, L"GetRootFolder(): OpenEntry Failed.", 
		ERR_ROOT_FOLDER, __LINE__, __FILE__);
	
    // Init root folder object
    rootFolder.Initialize(pFolder, _TEXT("/"), &bin);
    return hr;
}

HRESULT MAPIStore::OpenEntry(ULONG cbEntryID, LPENTRYID lpEntryID, LPCIID lpInterface, ULONG
    ulFlags, ULONG FAR *lpulObjType, LPUNKNOWN FAR *lppUnk)
{
    Zimbra::Util::AutoCriticalSection autocriticalsection(cs_store);

    return m_Store->OpenEntry(cbEntryID, lpEntryID, lpInterface, ulFlags, lpulObjType, lppUnk);
}
