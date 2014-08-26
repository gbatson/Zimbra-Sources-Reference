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
#include "common.h"
#include "Exchange.h"
#include "MapiMigration.h"

MapiMigration::MapiMigration(void) {}

MapiMigration::~MapiMigration(void) {}

void MapiMigration::Connecttoserver()
{
    DisplayMessageBox(L"Connectiong to the server \n");
}

void MapiMigration::ImportMail()
{
    DisplayMessageBox(L"importing mails \n");
}

void MapiMigration::ImportContacts()
{
    DisplayMessageBox(L"importing contacts \n");
}

void MapiMigration::ImportCalendar()
{
    DisplayMessageBox(L"importing Calendar \n");
}
