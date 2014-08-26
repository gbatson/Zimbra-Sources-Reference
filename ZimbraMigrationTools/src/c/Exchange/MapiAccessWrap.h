/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2012, 2013, 2014 Zimbra, Inc.
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
// MapiAccessWrap.h : Declaration of the CMapiAccessWrap


#pragma once
#include "resource.h"
#include "OaIdl.h"
#include "Wtypes.h"
#include "Exchange_i.h"
#include "MAPIDefs.h"
#include "Exchange.h"
#include "ExchangeAdmin.h"
#include "MAPIAccessAPI.h"

#define NUM_ATTACHMENT_ATTRS      4
#define NUM_EXCEPTION_ATTRS      18

// CMapiAccessWrap

class ATL_NO_VTABLE CMapiAccessWrap :
	public CComObjectRootEx<CComMultiThreadModel>,
	public CComCoClass<CMapiAccessWrap, &CLSID_MapiAccessWrap>,
	public ISupportErrorInfo,
	public IDispatchImpl<IMapiAccessWrap, &IID_IMapiAccessWrap, &LIBID_Exchange, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
	CMapiAccessWrap()
	{
          maapi = NULL;
	}

DECLARE_REGISTRY_RESOURCEID(IDR_MAPIACCESSWRAP)


BEGIN_COM_MAP(CMapiAccessWrap)
	COM_INTERFACE_ENTRY(IMapiAccessWrap)
	COM_INTERFACE_ENTRY(IDispatch)
	COM_INTERFACE_ENTRY(ISupportErrorInfo)
END_COM_MAP()

// ISupportsErrorInfo
	STDMETHOD(InterfaceSupportsErrorInfo)(REFIID riid);


	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
		return S_OK;
	}

	void FinalRelease()
	{
	}

public:
    Zimbra::MAPI::MAPIAccessAPI *maapi;

    STDMETHOD(UserInit) (BSTR userName, BSTR userAccount, BSTR *statusMsg);
    STDMETHOD(GetFolderList) (VARIANT * folders);
    STDMETHOD(GetItemsList) (IFolderObject * folderObj, VARIANT creationDate, VARIANT * vItems);
    STDMETHOD(GetData) (BSTR userId, VARIANT itemId, FolderType type, VARIANT * pVal);
    STDMETHOD(UserUninit) ();
    STDMETHODIMP GetOOOInfo(BSTR *OOOInfo);
    STDMETHODIMP GetRuleList(VARIANT *rules);
	STDMETHODIMP InitializePublicFolders(BSTR * statusMsg);
    void CreateAttachmentAttrs(BSTR attrs[], int num);
    void CreateExceptionAttrs(BSTR attrs[], int num);


};

OBJECT_ENTRY_AUTO(__uuidof(MapiAccessWrap), CMapiAccessWrap)
