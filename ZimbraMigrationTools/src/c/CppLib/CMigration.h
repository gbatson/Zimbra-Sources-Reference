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
// main base class for migration
// This will be a absatrct class with all virtual functions

class CMigration
{
protected:
    ATL::CComBSTR XMLConfigFileName;
    ATL::CComBSTR USerMapfilename;

public:
    virtual void Connecttoserver() = 0;         // do we need separate logon method or can we include it here..TBD
    virtual void ImportMail() = 0;
    virtual void ImportContacts() = 0;
    virtual void ImportCalendar() = 0;

    void DisplayMessageBox(ATL::CComBSTR Msg)
    {
        MessageBox(NULL, Msg, _T("Migartion tool"), MB_OK | MB_ICONEXCLAMATION);
    }

    virtual void SetConfigXMLFile(ATL::CComBSTR filename)
    {
        XMLConfigFileName = filename;
    }

    virtual void SetUserMapFile(ATL::CComBSTR filename)
    {
        USerMapfilename = filename;
    }
};
