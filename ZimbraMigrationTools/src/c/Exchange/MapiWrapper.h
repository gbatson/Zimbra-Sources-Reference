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
// MapiWrapper.h : Declaration of the CMapiWrapper

#pragma once
#include "MapiMigration.h"
#include "folderObject.h"
#include "..\Exchange\MAPIAccessAPI.h"

class ATL_NO_VTABLE CMapiWrapper: public CComObjectRootEx<CComSingleThreadModel>, public
    CComCoClass<CMapiWrapper, &CLSID_MapiWrapper>, public ISupportErrorInfo, public
    IDispatchImpl<IMapiWrapper, &IID_IMapiWrapper, &LIBID_Exchange,
    /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
    CMapiWrapper()
    {
        baseMigrationObj = new MapiMigration();
        exchadmin = new Zimbra::MAPI::ExchangeAdmin(L"10.117.82.161");
    }

    DECLARE_REGISTRY_RESOURCEID(IDR_MAPIWRAPPER) BEGIN_COM_MAP(
        CMapiWrapper) COM_INTERFACE_ENTRY(IMapiWrapper) COM_INTERFACE_ENTRY(
        IDispatch) COM_INTERFACE_ENTRY(ISupportErrorInfo) END_COM_MAP() STDMETHOD(
        InterfaceSupportsErrorInfo) (REFIID riid);

    DECLARE_PROTECT_FINAL_CONSTRUCT() HRESULT FinalConstruct() { return S_OK; }

    void FinalRelease() {}

    CMigration *baseMigrationObj;
    Zimbra::MAPI::ExchangeAdmin *exchadmin;

    STDMETHOD(ConnectToServer) (BSTR ServerHostName, BSTR Port, BSTR AdminID);
    STDMETHOD(GlobalInit) (BSTR pMAPITarget, BSTR pAdminUser, BSTR pAdminPassword,
        BSTR *pErrorText);
    STDMETHOD(ImportMailOptions) (BSTR OptionsTag);
    STDMETHOD(GetProfilelist) (VARIANT * Profiles,BSTR* status);

    std::vector<CComBSTR> m_vecColors;
    std::wstring str_to_wstr(const std::string &str);

    // /STDMETHOD(GetFolderObjects)(/*[in]*/ long start,
    // /*[in]*/ long length,
    // /*[out, retval]*/ SAFEARRAY **SequenceArr);

    STDMETHOD(GetFolderObjects) ( /*[out, retval]*/ VARIANT * vObjects);

    STDMETHOD(GlobalUninit) (BSTR *pErrorText);
    STDMETHOD(SelectExchangeUsers) (VARIANT * Users, BSTR *pErrorText);
    STDMETHOD(AvoidInternalErrors)(BSTR lpToCmp, LONG *lRetval);
protected:
    /*HRESULT SequenceByElement(long start, long length, SAFEARRAY *SequenceArr);
     * HRESULT SequenceByData(long start, long length, SAFEARRAY *SequenceArr);
     * HRESULT SequenceByItemElement(long start, long length, SAFEARRAY *SequenceArr);*/

    // HRESULT IsUDTFolderArray( SAFEARRAY *pUDTArr, bool &isDynamic );
};

OBJECT_ENTRY_AUTO(__uuidof(MapiWrapper), CMapiWrapper)
