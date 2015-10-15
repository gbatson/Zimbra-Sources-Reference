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

#include "OaIdl.h"
#include "Wtypes.h"

class BaseUser
{
public:
    BaseUser(void) {}
    virtual ~BaseUser(void) {}

    /*
    virtual long Init(BSTR Id) = 0;
    virtual long GetFolders(VARIANT *folders) = 0;
    virtual long GetItems(VARIANT *Items) = 0;
    virtual void Uninit(void) = 0;
    */

protected:
    BSTR MailType;
    BSTR UserID;
};