/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2006, 2007, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */

#pragma once

// ReminderEventListner.h : header file for the CReminderEventListener class
class CReminderEventListener: public IDispatch
{
protected:
    int m_refCount;
    IConnectionPoint *m_pConnectionPoint;
    DWORD m_dwConnection;

public:
    // Constructor.
    CReminderEventListener();
    // Destructor.
    ~CReminderEventListener();

    // IUnknown Methods
    STDMETHODIMP QueryInterface(REFIID riid, void **ppvObj);

    STDMETHODIMP_(ULONG) AddRef();
    STDMETHODIMP_(ULONG) Release();

    // IDispatch Methods
    STDMETHODIMP GetTypeInfoCount(UINT *iTInfo);
    STDMETHODIMP GetTypeInfo(UINT iTInfo, LCID lcid, ITypeInfo **ppTInfo);
    STDMETHODIMP GetIDsOfNames(REFIID riid, OLECHAR **rgszNames, UINT cNames, LCID lcid,
        DISPID *rgDispId);
    STDMETHODIMP Invoke(DISPID dispIdMember, REFIID riid, LCID lcid, WORD wFlags,
        DISPPARAMS *pDispParams, VARIANT *pVarResult, EXCEPINFO *pExcepInfo, UINT *puArgErr);

    // Attach/Detach from event source i.e. outlook
    STDMETHODIMP AttachToOutlook(IUnknown *pEventSource);
    STDMETHODIMP DetachFromOutlook();
};
