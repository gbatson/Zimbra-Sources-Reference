/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2009, 2010, 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
/*
 * Package: NotebookCore
 * 
 * Supports: Loading of a notebook
 * 
 * Loaded:
 * 	- When a notebook object arrives in a <refresh> block
 * 	- When a search for pages/documents returns data
 */
AjxPackage.require("zimbraMail.notebook.model.ZmNotebook");
AjxPackage.require("zimbraMail.notebook.model.ZmNotebookItem");
AjxPackage.require("zimbraMail.notebook.model.ZmDocument");
AjxPackage.require("zimbraMail.notebook.model.ZmPage");
AjxPackage.require("zimbraMail.notebook.model.ZmPageList");
AjxPackage.require("zimbraMail.notebook.model.ZmNotebookCache");
