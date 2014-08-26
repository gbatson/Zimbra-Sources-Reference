/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
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
// UserObject.h : Declaration of the CUserObject
#pragma once
#include "resource.h"
#include "BaseUser.h"
#include "Logger.h"
#include "Exchange_i.h"
#include "MapiAccessWrap.h"
#include "MAPIAccessAPI.h"

// CUserObject

class ATL_NO_VTABLE CUserObject :
	public CComObjectRootEx<CComMultiThreadModel>,
	public CComCoClass<CUserObject, &CLSID_UserObject>,public BaseUser,
	public ISupportErrorInfo,
	public IDispatchImpl<IUserObject, &IID_IUserObject, &LIBID_Exchange, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
	CUserObject()
	{
	}

DECLARE_REGISTRY_RESOURCEID(IDR_USEROBJECT)


BEGIN_COM_MAP(CUserObject)
	COM_INTERFACE_ENTRY(IUserObject)
	COM_INTERFACE_ENTRY(IDispatch)
	COM_INTERFACE_ENTRY(ISupportErrorInfo)
END_COM_MAP()

// ISupportsErrorInfo
	STDMETHOD(InterfaceSupportsErrorInfo)(REFIID riid);


	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
		CComObject<CMapiAccessWrap> *obj = NULL;

        CComObject<CMapiAccessWrap>::CreateInstance(&obj);
        mapiObj = obj;
            return S_OK;
	}

	void FinalRelease()
	{
	}

public:
     STDMETHOD(Init) (BSTR host, BSTR location, BSTR accountName, long PublicFlag, BSTR *pErrorText);
    STDMETHOD(GetFolders) (VARIANT * vObjects);
    STDMETHOD(GetItemsForFolder) (IFolderObject * folderObj, VARIANT creationDate, VARIANT *
        vItems);
    STDMETHOD(GetMapiAccessObject) (BSTR userID, IMapiAccessWrap * *pVal);
    STDMETHOD(Uninit) (void);
    STDMETHOD(GetOOO) (BSTR *pOOO);
    STDMETHOD(GetRules) (VARIANT * vRules);

    /*
     * virtual long Init(BSTR id);
     * virtual long GetFolders(VARIANT *folders);
     * virtual long GetItems(VARIANT *items);
     * virtual void Uninit(void);
     */

    CComQIPtr<IMapiAccessWrap, &IID_IMapiAccessWrap> mapiObj;


};

OBJECT_ENTRY_AUTO(__uuidof(UserObject), CUserObject)
