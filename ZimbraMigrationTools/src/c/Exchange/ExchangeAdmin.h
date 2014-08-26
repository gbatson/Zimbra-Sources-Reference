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
#pragma once
#include "Util.h"

namespace Zimbra
{
namespace MAPI
{
#define EXCH_UNINITIALIZED              0
#define EXCH_INITIALIZED_PROFCREATE     1
#define EXCH_INITIALIZED_PROFEXIST      2

class ExchangeAdminException: public GenericException
{
public:
    ExchangeAdminException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    ExchangeAdminException(HRESULT hrErrCode, LPCWSTR lpszDescription, LPCWSTR lpszShortDescription, int nLine, LPCSTR
        strFile);
    virtual ~ExchangeAdminException() {}
};

class ExchangeAdmin
{
private:
    LPPROFADMIN m_pProfAdmin;
    wstring m_strServer;

private:
    HRESULT Init();

public:
    ExchangeAdmin(wstring strExchangeServer);
    ~ExchangeAdmin();
    HRESULT CreateProfile(wstring strProfileName, wstring strMailboxName, wstring strPassword);
    HRESULT DeleteProfile(wstring strProfile);
    HRESULT GetAllProfiles(vector<string> &vProfileList);
    HRESULT SetDefaultProfile(wstring strProfile);
    HRESULT CreateExchangeMailBox(LPCWSTR lpwstrNewUser, LPCWSTR lpwstrNewUserPwd, LPCWSTR
        lpwstrlogonuser, LPCWSTR lpwstrLogonUsrPwd);
    HRESULT DeleteExchangeMailBox(LPCWSTR lpwstrMailBox, LPCWSTR lpwstrlogonuser, LPCWSTR
        lpwstrLogonUsrPwd);
};

class ExchangeMigrationSetup
{
private:
    ExchangeAdmin *m_exchAdmin;
    wstring m_strServer;
    wstring m_ExchangeAdminName;
    wstring m_ExchangeAdminPwd;

public:
    ExchangeMigrationSetup(LPCWSTR strExhangeHost, LPCWSTR ExchangeAdminName, LPCWSTR
        ExchangeAdminPwd);
    ~ExchangeMigrationSetup();
    HRESULT Setup();
    HRESULT Clean();
    HRESULT GetAllProfiles(vector<string> &vProfileList);
};

class ExchangeOps
{
private:
    static ExchangeMigrationSetup *m_exchmigsetup;
    static int Initialized;
    static MAPISession *m_zmmapisession;
	static void internalEOInit();
	static LPCWSTR _GlobalInit(LPCWSTR lpMAPITarget, LPCWSTR lpAdminUsername = NULL, LPCWSTR
        lpAdminPassword = NULL);
public:
    static LPCWSTR GlobalInit(LPCWSTR lpMAPITarget, LPCWSTR lpAdminUsername = NULL, LPCWSTR
        lpAdminPassword = NULL);
    static LPCWSTR GlobalUninit();
    static LPCWSTR SelectExchangeUsers(vector<ObjectPickerData> &vUserList);
    static BOOL AvoidInternalErrors(LPCWSTR lpToCmp);
};

const LPCWSTR DEFAULT_ADMIN_PROFILE_NAME = L"zmprof";
const LPCWSTR DEFAULT_ADMIN_MAILBOX_NAME = L"zmmbox";
}
}
