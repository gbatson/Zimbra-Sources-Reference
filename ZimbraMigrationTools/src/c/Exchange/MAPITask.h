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
#pragma once

// MAPITaskException class
class MAPITaskException: public GenericException
{
public:
    MAPITaskException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    MAPITaskException(HRESULT hrErrCode, LPCWSTR lpszDescription, LPCWSTR lpszShortDescription, int nLine, LPCSTR strFile);
    virtual ~MAPITaskException() {}
};

// MAPITask class
class MAPITask : public MAPIRfc2445
{
private:
    //static bool m_bNamedPropsInitialized;
    bool m_bIsTaskReminderSet;

    // prop tags for named properties
    ULONG pr_isrecurringt, pr_recurstreamt, pr_status, pr_percentcomplete, pr_taskstart, pr_taskdue, pr_totalwork, pr_actualwork, pr_companies,
	  pr_mileage, pr_billinginfo, pr_taskreminderset, pr_taskflagdueby, pr_private;

    // index of props
    typedef enum _TaskPropIdx
    {
        N_ISRECURT, N_RECURSTREAMT, N_STATUS, N_PERCENTCOMPLETE, N_TASKSTART, N_TASKDUE, N_TOTALWORK, N_ACTUALWORK, N_NUMTASKPROPS
    } TaskPropIdx;

    typedef enum _CommonTPropIdx
    {
        N_COMPANIES, N_MILEAGE, N_BILLING, N_TASKREMINDERSET, N_TASKFLAGDUEBY, N_TPRIVATE, N_NUMCOMMONTPROPS
    } CommonTPropIdx;

    // this enum lists all the props
    enum
    {
        T_MESSAGE_FLAGS, T_SUBJECT, T_BODY, T_HTMLBODY, T_IMPORTANCE, T_ISRECURT, T_RECURSTREAMT, T_STATUS, T_PERCENTCOMPLETE,
        T_TASKSTART, T_TASKDUE, T_TOTALWORK, T_ACTUALWORK, T_COMPANIES, T_MILEAGE, T_BILLING, T_TASKREMINDERSET,
        T_TASKFLAGDUEBY, T_PRIVATE, T_NUMALLTASKPROPS
    };

    // these are the named property id's
    LONG nameIds[N_NUMTASKPROPS];
    LONG nameIdsC[N_NUMCOMMONTPROPS];

    // task data members (represented both by regular and named props
    wstring m_pSubject;
    wstring m_pImportance;
    wstring m_pTaskStart;
    wstring m_pTaskFilterDate;
    wstring m_pTaskDue;
    wstring m_pStatus;
    wstring m_pPercentComplete;
    wstring m_pTotalWork;
    wstring m_pActualWork;
    wstring m_pCompanies;
    wstring m_pMileage;
    wstring m_pBillingInfo;
    wstring m_pTaskFlagDueBy;
    wstring m_pPrivate;
    wstring m_pPlainTextFile;
    wstring m_pHtmlFile;
    //

public:
    MAPITask(Zimbra::MAPI::MAPISession &session, Zimbra::MAPI::MAPIMessage &mMessage);
    ~MAPITask();
    HRESULT InitNamedPropsForTask();
    HRESULT SetMAPITaskValues();
    void SetSubject(LPTSTR pStr);
    void SetImportance(long importance);
    void SetTaskStatus(long taskstatus);
    void SetPercentComplete(double percentcomplete);
    void SetTaskStart(FILETIME ft);
    void SetTaskDue(FILETIME ft);
    void SetTotalWork(long totalwork);
    void SetActualWork(long actualwork);
    void SetCompanies(LPTSTR pStr);
    void SetMileage(LPTSTR pStr);
    void SetBillingInfo(LPTSTR pStr);
    void SetTaskFlagDueBy(FILETIME ft);
    void SetPrivate(unsigned short usPrivate);
    void SetPlainTextFileAndContent();
    void SetHtmlFileAndContent();
    void SetRecurValues();

    bool IsTaskReminderSet();
    wstring GetSubject();
    wstring GetImportance();
    wstring GetTaskStatus();
    wstring GetPercentComplete();
    wstring GetTaskStart();
    wstring GetTaskFilterDate();
    wstring GetTaskDue();
    wstring GetTotalWork();
    wstring GetActualWork();
    wstring GetMileage();
    wstring GetCompanies();
    wstring GetBillingInfo();
    wstring GetTaskFlagDueBy();
    wstring GetPrivate();
    wstring GetPlainTextFileAndContent();
    wstring GetHtmlFileAndContent();
};
