/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/**
* @constructor
* @class ZaSearchListController This is a singleton class that controls all the user interaction with the list of ZaAccount objects
* @param appCtxt
* @param container
* @param app
* @extends ZaController
* @author Greg Solovyev
**/
ZaSearchListController = function(appCtxt, container) {
	ZaListViewController.call(this, appCtxt, container,"ZaSearchListController");
    //Account operations
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();			
   	
	this._currentPageNum = 1;
	this._currentQuery = null;
	this._currentSortField = ZaAccount.A_uid;
	this._currentSortOrder = "1";
	this.searchTypes = [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS, ZaSearch.RESOURCES, ZaSearch.DOMAINS];
	this.pages = new Object();
	this._searchPanel = null;
	this._searchField = null;
	this._helpURL = ZaSearchListController.helpURL;
	this._UICreated = false;
	this._isAdvancedSearch = false ;
	this._searchFieldInput = null ; //the input of the search field for basic search, it is also be used as the tab title
	this.objType = ZaEvent.S_ACCOUNT;	
	this.fetchAttrs = ZaSearch.standardAttributes;
}

ZaSearchListController.prototype = new ZaListViewController();
ZaSearchListController.prototype.constructor = ZaSearchListController;
ZaSearchListController.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/provisioning_accounts.htm?locid="+AjxEnv.DEFAULT_LOCALE;
ZaController.initToolbarMethods["ZaSearchListController"] = new Array();
ZaController.initPopupMenuMethods["ZaSearchListController"] = new Array();
ZaController.changeActionsStateMethods["ZaSearchListController"] = new Array();
ZaSearchListController.prototype.show = function (doPush) {
	var busyId = Dwt.getNextId();
	var callback = new AjxCallback(this, this.searchCallback, {limit:this.RESULTSPERPAGE,CONS:null,show:doPush, busyId:busyId});
	/*
	if (this._currentQuery == null) {
		this._currentQuery =  (ZaSearch._currentQuery ? ZaSearch._currentQuery : "");
	}*/
	
	var searchParams = {
			query: this._currentQuery, 
			types:this.searchTypes,
			sortBy:this._currentSortField,
			offset:this.RESULTSPERPAGE*(this._currentPageNum-1),
			sortAscending:this._currentSortOrder,
			limit:this.RESULTSPERPAGE,
			attrs:this.fetchAttrs,
			callback:callback,
			controller: this,
			showBusy:true,
			busyId:busyId,
			busyMsg:ZaMsg.BUSY_SEARCHING,
			skipCallbackIfCancelled:false			
	}
	ZaSearch.searchDirectory(searchParams);
}

ZaSearchListController.prototype._show = 
function (list, openInNewTab, openInSearchTab) {
	this._updateUI(list, openInNewTab, openInSearchTab);
	//ZaApp.getInstance().pushView(ZaZimbraAdmin._SEARCH_LIST_VIEW);
	ZaApp.getInstance().pushView(this.getContentViewId());
}

/**
* searh panel
*/	
ZaSearchListController.prototype.getSearchPanel = 
function () {
	if(!this._searchPanel) {
	    this._searchPanel = new DwtComposite(ZaApp.getInstance().getAppCtxt().getShell(), "SearchPanel", DwtControl.ABSOLUTE_STYLE);
	    
		// Create search toolbar and setup browse tool bar button handlers
		this._searchToolBar = new ZaSearchToolBar(this._searchPanel, null);
	    
		// Setup search field handler
		this._searchField = this._searchToolBar.getSearchField();
		this._searchField.registerCallback(ZaSearchListController.prototype._searchFieldCallback, this);	
		this._searchPanel.zShow(true);		
	}
	return this._searchPanel;
}

ZaSearchListController.prototype.set = 
function(accountList) {
	this.show(accountList);
}

ZaSearchListController.prototype.setPageNum = 
function (pgnum) {
	this._currentPageNum = Number(pgnum);
}

ZaSearchListController.prototype.getPageNum = 
function () {
	return this._currentPageNum;
}

ZaSearchListController.prototype.getTotalPages = 
function () {
	return this.numPages;
}

ZaSearchListController.prototype.setFetchAttrs = 
function (fetchAttrs) {
	this.fetchAttrs = fetchAttrs;
}

ZaSearchListController.prototype.getFetchAttrs = 
function () {
	return this.fetchAttrs;
}

ZaSearchListController.prototype.setQuery = 
function (query) {
	this._currentQuery = query;
}

ZaSearchListController.prototype.getQuery = 
function () {
	return this._currentQuery;
}

ZaSearchListController.prototype.setSearchTypes = 
function (searchTypes) {
	this.searchTypes = searchTypes;
}

ZaSearchListController.prototype.getSearchTypes = 
function () {
	return this.searchTypes;
}

ZaSearchListController.prototype.setSortOrder = 
function (sortOrder) {
	this._currentSortOrder = sortOrder;
}

ZaSearchListController.prototype.getSortOrder = 
function () {
	return this._currentSortOrder;
}

ZaSearchListController.prototype.setSortField = 
function (sortField) {
	this._currentSortField = sortField;
}

ZaSearchListController.prototype.getSortField = 
function () {
	return this._currentSortField;
}

/*********** Search Field Callback */
ZaSearchListController.prototype._searchFieldCallback =
function(params) {
	var controller = this;
	if(controller.setSearchTypes)
		controller.setSearchTypes(params.types);
	
	controller._currentQuery = params.query ;
	var busyId = Dwt.getNextId();	
	var callback = new AjxCallback(controller, controller.searchCallback, {limit:controller.RESULTSPERPAGE,show:true, openInSearchTab: true,busyId:busyId});
	var searchParams = {
			query:params.query, 
			types:params.types,
			showBusy:true,
			busyId:busyId,
			busyMsg:ZaMsg.BUSY_SEARCHING,
			skipCallbackIfCancelled:false,
			sortBy:params.sortBy,
			offset:this.RESULTSPERPAGE*(this._currentPageNum-1),
			sortAscending:this._currentSortOrder,
			limit:this.RESULTSPERPAGE,
			attrs:ZaSearch.standardAttributes,
			callback:callback,
			controller: controller
	}
	ZaSearch.searchDirectory(searchParams);
}


ZaSearchListController.initPopupMenuMethod =
function () {
    this._popupOperations[ZaOperation.EDIT]=new ZaOperation(ZaOperation.EDIT,ZaMsg.TBB_Edit, ZaMsg.ACTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaSearchListController.prototype._editButtonListener));
	this._popupOperations[ZaOperation.DELETE]=new ZaOperation(ZaOperation.DELETE,ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaSearchListController.prototype._deleteButtonListener));
	this._popupOperations[ZaOperation.CHNG_PWD]=new ZaOperation(ZaOperation.CHNG_PWD,ZaMsg.ACTBB_ChngPwd, ZaMsg.ACTBB_ChngPwd_tt, "Padlock", "PadlockDis", new AjxListener(this, ZaAccountListController.prototype._chngPwdListener));
	this._popupOperations[ZaOperation.EXPIRE_SESSION] = new ZaOperation(ZaOperation.EXPIRE_SESSION, ZaMsg.ACTBB_ExpireSessions, ZaMsg.ACTBB_ExpireSessions_tt, "ExpireSession", "ExpireSessionDis", new AjxListener(this, ZaAccountListController.prototype._expireSessionListener));
	this._popupOperations[ZaOperation.VIEW_MAIL]=new ZaOperation(ZaOperation.VIEW_MAIL,ZaMsg.ACTBB_ViewMail, ZaMsg.ACTBB_ViewMail_tt, "ReadMailbox", "ReadMailbox", new AjxListener(this, ZaAccountListController.prototype._viewMailListener));		
	this._popupOperations[ZaOperation.MOVE_ALIAS]=new ZaOperation(ZaOperation.MOVE_ALIAS,ZaMsg.ACTBB_MoveAlias, ZaMsg.ACTBB_MoveAlias_tt, "MoveAlias", "MoveAlias", new AjxListener(this, ZaAccountListController.prototype._moveAliasListener));		    	
	
}
ZaController.initPopupMenuMethods["ZaSearchListController"].push(ZaSearchListController.initPopupMenuMethod);

/**
* This method is called from {@link ZaController#_initToolbar}
**/
ZaSearchListController.initToolbarMethod =
function () {
	// first button in the toolbar is a menu.
    this._toolbarOperations[ZaOperation.EDIT]=new ZaOperation(ZaOperation.EDIT,ZaMsg.TBB_Edit, ZaMsg.ACTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaSearchListController.prototype._editButtonListener));
	this._toolbarOperations[ZaOperation.DELETE]=new ZaOperation(ZaOperation.DELETE,ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaSearchListController.prototype._deleteButtonListener));
	this._toolbarOperations[ZaOperation.CHNG_PWD]=new ZaOperation(ZaOperation.CHNG_PWD,ZaMsg.ACTBB_ChngPwd, ZaMsg.ACTBB_ChngPwd_tt, "Padlock", "PadlockDis", new AjxListener(this, ZaAccountListController.prototype._chngPwdListener));
	this._toolbarOperations[ZaOperation.EXPIRE_SESSION] = new ZaOperation(ZaOperation.EXPIRE_SESSION, ZaMsg.ACTBB_ExpireSessions, ZaMsg.ACTBB_ExpireSessions_tt, "ExpireSession", "ExpireSessionDis", new AjxListener(this, ZaAccountListController.prototype._expireSessionListener));
	this._toolbarOperations[ZaOperation.VIEW_MAIL]=new ZaOperation(ZaOperation.VIEW_MAIL,ZaMsg.ACTBB_ViewMail, ZaMsg.ACTBB_ViewMail_tt, "ReadMailbox", "ReadMailbox", new AjxListener(this, ZaAccountListController.prototype._viewMailListener));		
	this._toolbarOperations[ZaOperation.MOVE_ALIAS]=new ZaOperation(ZaOperation.MOVE_ALIAS,ZaMsg.ACTBB_MoveAlias, ZaMsg.ACTBB_MoveAlias_tt, "MoveAlias", "MoveAlias", new AjxListener(this, ZaAccountListController.prototype._moveAliasListener));		    	
	
	
	this._toolbarOrder.push(ZaOperation.EDIT);
	this._toolbarOrder.push(ZaOperation.DELETE);
	this._toolbarOrder.push(ZaOperation.CHNG_PWD);
	this._toolbarOrder.push(ZaOperation.EXPIRE_SESSION);
	this._toolbarOrder.push(ZaOperation.VIEW_MAIL);
	this._toolbarOrder.push(ZaOperation.MOVE_ALIAS);
	
	this._toolbarOrder.push(ZaOperation.NONE);
    this._toolbarOrder.push(ZaOperation.PAGE_BACK);
	this._toolbarOrder.push(ZaOperation.PAGE_FORWARD);
    this._toolbarOrder.push(ZaOperation.HELP);
}
ZaController.initToolbarMethods["ZaSearchListController"].push(ZaSearchListController.initToolbarMethod);

ZaSearchListController.prototype.reset =
function () {
	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();
    this._toolbarOrder = [] ;

    this._currentPageNum = 1;
	this._currentQuery = null;
	this._currentSortField = ZaAccount.A_uid;
	this._currentSortOrder = "1";
	this.pages = new Object();
	this._UICreated = false;
	this.objType = ZaEvent.S_ACCOUNT;	
}

//private and protected methods
ZaSearchListController.prototype._createUI = 
function () {
	//create accounts list view
	// create the menu operations/listeners first	
	this._contentView = new ZaSearchListView(this._container);
	ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	this._newDLListener = new AjxListener(this, ZaSearchListController.prototype._newDistributionListListener);
	this._newAcctListener = new AjxListener(this, ZaSearchListController.prototype._newAccountListener);
	this._newResListener = new AjxListener(this, ZaSearchListController.prototype._newResourceListener);

    this._initToolbar();
	//always add Help and navigation buttons at the end of the toolbar    
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);	
	this._toolbarOperations[ZaOperation.PAGE_BACK]=new ZaOperation(ZaOperation.PAGE_BACK,ZaMsg.Previous, ZaMsg.PrevPage_tt, "LeftArrow", "LeftArrowDis",  new AjxListener(this, this._prevPageListener));
	//add the acount number counts
	ZaSearch.searchResultCountsView(this._toolbarOperations, this._toolbarOrder);
	this._toolbarOperations[ZaOperation.PAGE_FORWARD]=new ZaOperation(ZaOperation.PAGE_FORWARD,ZaMsg.Next, ZaMsg.NextPage_tt, "RightArrow", "RightArrowDis", new AjxListener(this, this._nextPageListener));
	this._toolbarOperations[ZaOperation.HELP]=new ZaOperation(ZaOperation.HELP,ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));				

	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder);    
		
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
	//ZaApp.getInstance().createView(ZaZimbraAdmin._SEARCH_LIST_VIEW, elements);
	//always open the search list view in the search tab
	var tabParams = {
		openInNewTab: false,
		tabId: this.getContentViewId(),
		tab: ZaApp.getInstance().getTabGroup().getSearchTab ()
	}
	ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
	
	this._initPopupMenu();
	this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations);
	
	//set a selection listener on the account list view
	this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
	this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
	this._removeConfirmMessageDialog = ZaApp.getInstance().dialogs["ConfirmMessageDialog"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON]);			
	this._UICreated = true;
}

ZaSearchListController.prototype.closeButtonListener =
function(ev, noPopView, func, obj, params) {
	if (noPopView) {
		func.call(obj, params) ;
	}else{
		ZaApp.getInstance().popView () ;
	}
	
	//reset the search text when the search list view/tab is closed
	if(this._searchField) {
		var searchFieldXForm = this._searchField._localXForm ;
		var searchFieldItem = searchFieldXForm.getItemsById(ZaSearch.A_query)[0];
		searchFieldItem.getElement().value = "" ;
	}
}

// new account button was pressed
ZaSearchListController.prototype._newAccountListener =
function(ev) {

	try {
		var newAccount = new ZaAccount();
		if(!ZaApp.getInstance().dialogs["newAccountWizard"])
			ZaApp.getInstance().dialogs["newAccountWizard"] = new ZaNewAccountXWizard(this._container);	

		ZaApp.getInstance().dialogs["newAccountWizard"].setObject(newAccount);
		ZaApp.getInstance().dialogs["newAccountWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaSearchListController.prototype._newAccountListener", null, false);
	}
}

ZaSearchListController.prototype._newDistributionListListener =
function(ev) {
	try {
		var newDL = new ZaDistributionList();
		ZaApp.getInstance().getDistributionListController().show(newDL);
	} catch (ex) {
		this._handleException(ex, "ZaSearchListController.prototype._newDistributionListListener", null, false);
	}

};

ZaSearchListController.prototype._newResourceListener =
function(ev) {
	try {
		var newResource = new ZaResource();
		if(!ZaApp.getInstance().dialogs["newResourceWizard"])
			ZaApp.getInstance().dialogs["newResourceWizard"] = new ZaNewResourceXWizard(this._container);	

		ZaApp.getInstance().dialogs["newResourceWizard"].setObject(newResource);
		ZaApp.getInstance().dialogs["newResourceWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaSearchListController.prototype._newResourceListener", null, false);
	}
}


/**
* This listener is called when the item in the list is double clicked. It call ZaAccountViewController.show method
* in order to display the Account View
**/
ZaSearchListController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._editItem(ev.item);
		}
	} else {
		this.changeActionsState();
	}
}

ZaSearchListController.prototype._listActionListener =
function (ev) {
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}

/**
* This listener is called when the Edit button is clicked. 
* It call ZaAccountViewController.show method
* in order to display the Account View
**/
ZaSearchListController.prototype._editButtonListener =
function(ev) {
	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		this._editItem(item);
	}
}

/**
* This listener is called when the Delete button is clicked. 
* It call ZaAccountViewController.show method
* in order to display the Account View
**/
ZaSearchListController.prototype._deleteButtonListener =
function(ev) {
	ZaAccountListController.prototype._deleteButtonListener.call(this, ev);
}

ZaSearchListController.prototype._deleteAccountsInRemoveList =
function (ev) {
	ZaAccountListController.prototype._deleteAccountsInRemoveList.call (this, ev) ;
}

ZaSearchListController.prototype._editItem = function (item) {
	var type = item.type;
	if (type == ZaItem.ACCOUNT) {
		ZaApp.getInstance().getAccountViewController().show(item);
	} else if (type == ZaItem.DL) {
		ZaApp.getInstance().getDistributionListController().show(item);
	} else if(type == ZaItem.ALIAS) {
		var targetObj = item.getAliasTargetObj() ;
		
		if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_ACCOUNT) {			
			ZaApp.getInstance().getAccountViewController().show(targetObj, true);
		}else if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_DL){
			ZaApp.getInstance().getDistributionListController().show(targetObj, true);
		}
	} else if (type == ZaItem.RESOURCE ){
		ZaApp.getInstance().getResourceController().show(item);
	} else if (type==ZaItem.DOMAIN) {
		ZaApp.getInstance().getDomainController().show(item);
	}
};


ZaSearchListController.changeActionsStateMethod = 
function () {
	var cnt = this._contentView.getSelectionCount();
	if(cnt == 1) {
		var item = this._contentView.getSelection()[0];
		if(item) {
			if(item.type != ZaItem.ALIAS) {
				if(this._toolbarOperations[ZaOperation.MOVE_ALIAS]) {
					this._toolbarOperations[ZaOperation.MOVE_ALIAS].enabled = false;
				}					
			}
            if (item.type == ZaItem.ALIAS || item.type == ZaItem.DL) {
                if(this._toolbarOperations[ZaOperation.CHNG_PWD]) {
                    this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;
                }

                if(this._popupOperations[ZaOperation.CHNG_PWD]) {
                    this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
                }	
            }

            if (((item.type == ZaItem.ALIAS) && (item.attrs[ZaAlias.A_targetType] == ZaItem.DL))
                || (item.type == ZaItem.DL)) {
                if (this._toolbarOperations[ZaOperation.VIEW_MAIL]) {
                    this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;                                        
                }

                if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
                    this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
                }
            } else if (item.type == ZaItem.DL) {
                if(this._popupOperations[ZaOperation.MOVE_ALIAS])	{
                    this._popupOperations[ZaOperation.MOVE_ALIAS].enabled = false;
                }
                if(this._popupOperations[ZaOperation.VIEW_MAIL])
				 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
					 
				if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
				 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;   
                	    
            } else if (item.type == ZaItem.ACCOUNT) {
				var enable = false;
				if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.rights)) {
					//console.log("loading effective rights for a list item");
					item.loadEffectiveRights("id", item.id, false);
					//console.log("loaded rights for a list item");
				}
				if(!enable) {
					if(!ZaItem.hasRight(ZaAccount.VIEW_MAIL_RIGHT,item)) {
						 if(this._popupOperations[ZaOperation.VIEW_MAIL])
						 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
						 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;   
					}
					if(!ZaItem.hasRight(ZaAccount.DELETE_ACCOUNT_RIGHT,item)) {
						 if(this._popupOperations[ZaOperation.DELETE])
						 	this._popupOperations[ZaOperation.DELETE].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.DELETE])
						 	this._toolbarOperations[ZaOperation.DELETE].enabled = false;   
					}	
					if(!ZaItem.hasRight(ZaAccount.SET_PASSWORD_RIGHT, item)) {
						 if(this._popupOperations[ZaOperation.CHNG_PWD])
						 	this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.CHNG_PWD])
						 	this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;   
					}		
					if(!ZaItem.hasWritePermission(ZaAccount.A_zimbraAuthTokenValidityValue,item)) {    
					   	if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
							this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
						}
					   	if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
							this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
						}						
					}
				}
			} else if ((item.type == ZaItem.ALIAS) && (item.attrs[ZaAlias.A_targetType] == ZaItem.ACCOUNT))  {
				if(!item.targetObj)
					item.targetObj = item.getAliasTargetObj() ;
					
				var enable = false;
				if (ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.targetObj.rights)) {
					item.targetObj.loadEffectiveRights("id", item.id, false);
				}
				if(!enable) {
					if(!ZaItem.hasRight(ZaAccount.VIEW_MAIL_RIGHT,item.targetObj)) {
						 if(this._popupOperations[ZaOperation.VIEW_MAIL])
						 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
						 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;   
					}	
					if(!ZaItem.hasRight(ZaAccount.DELETE_ACCOUNT_RIGHT,item.targetObj)) {
						 if(this._popupOperations[ZaOperation.DELETE])
						 	this._popupOperations[ZaOperation.DELETE].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.DELETE])
						 	this._toolbarOperations[ZaOperation.DELETE].enabled = false;   
					}
					if(!ZaItem.hasRight(ZaAccount.SET_PASSWORD_RIGHT,item.targetObj)) {
						 if(this._popupOperations[ZaOperation.CHNG_PWD])
						 	this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.CHNG_PWD])
						 	this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;   
					}			
					if(!ZaItem.hasWritePermission(ZaAccount.A_zimbraAuthTokenValidityValue,item.targetObj)) {    
					   	if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
							this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
						}
					   	if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
							this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
						}						
					}
				}
			} else if ((item.type == ZaItem.ALIAS) && (item.attrs[ZaAlias.A_targetType] == ZaItem.RESOURCE))  {
			   	if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
					this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
				}				
				if(!item.targetObj)
					item.targetObj = item.getAliasTargetObj() ;
					
				var enable = false;
				if (ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.targetObj.rights)) {
					item.targetObj.loadEffectiveRights("id", item.id, false);
				}
				if(!enable) {
					if(!enable) {
						if(!ZaItem.hasRight(ZaResource.VIEW_RESOURCE_MAIL_RIGHT,item.targetObj)) {
							 if(this._popupOperations[ZaOperation.VIEW_MAIL])
							 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
							 
							 if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
							 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;   
						}
						if(!ZaItem.hasRight(ZaResource.DELETE_CALRES_RIGHT,item.targetObj)) {
							 if(this._popupOperations[ZaOperation.DELETE])
							 	this._popupOperations[ZaOperation.DELETE].enabled = false;
							 
							 if(this._toolbarOperations[ZaOperation.DELETE])
							 	this._toolbarOperations[ZaOperation.DELETE].enabled = false;   
						}	
						if(!ZaItem.hasRight(ZaResource.SET_CALRES_PASSWORD_RIGHT, item.targetObj)) {
							 if(this._popupOperations[ZaOperation.CHNG_PWD])
							 	this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
							 
							 if(this._toolbarOperations[ZaOperation.CHNG_PWD])
							 	this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;   
						}		
					}
				}
			} else if(item.type == ZaItem.RESOURCE) {
			   	if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
					this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
				}
				var enable = false;
				if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.rights)) {
					item.loadEffectiveRights("id", item.id, false);
				}
				if(!enable) {
					if(!ZaItem.hasRight(ZaResource.VIEW_RESOURCE_MAIL_RIGHT,item)) {
						 if(this._popupOperations[ZaOperation.VIEW_MAIL])
						 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
						 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;   
					}
					if(!ZaItem.hasRight(ZaResource.DELETE_CALRES_RIGHT,item)) {
						 if(this._popupOperations[ZaOperation.DELETE])
						 	this._popupOperations[ZaOperation.DELETE].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.DELETE])
						 	this._toolbarOperations[ZaOperation.DELETE].enabled = false;   
					}	
					if(!ZaItem.hasRight(ZaResource.SET_CALRES_PASSWORD_RIGHT, item)) {
						 if(this._popupOperations[ZaOperation.CHNG_PWD])
						 	this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.CHNG_PWD])
						 	this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;   
					}		
				}				
			}
        } else {
			if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
				this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
			}	        	
			if(this._toolbarOperations[ZaOperation.VIEW_MAIL]) {
				this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;
			}
			if(this._toolbarOperations[ZaOperation.EDIT]) {	
				this._toolbarOperations[ZaOperation.EDIT].enabled = false;
			}	
			if(this._toolbarOperations[ZaOperation.CHNG_PWD]) {
				this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;
			}
			if(this._toolbarOperations[ZaOperation.MOVE_ALIAS]) {
				this._toolbarOperations[ZaOperation.MOVE_ALIAS].enabled = false;
			}	
			if(this._toolbarOperations[ZaOperation.DELETE]) {	
				this._toolbarOperations[ZaOperation.DELETE].enabled = false;
			}
			
			if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
				this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
			}				
			if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
				this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
			}
			if(this._popupOperations[ZaOperation.EDIT]) {	
				this._popupOperations[ZaOperation.EDIT].enabled = false;
			}	
			if(this._popupOperations[ZaOperation.CHNG_PWD]) {
				this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
			}
			if(this._popupOperations[ZaOperation.MOVE_ALIAS]) {
				this._popupOperations[ZaOperation.MOVE_ALIAS].enabled = false;
			}	
			if(this._popupOperations[ZaOperation.DELETE]) {	
				this._popupOperations[ZaOperation.DELETE].enabled = false;
			}				
		}		
	} else if (cnt > 1){
		if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
			this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
		}			
		if(this._toolbarOperations[ZaOperation.EDIT]) {	
			this._toolbarOperations[ZaOperation.EDIT].enabled = false;
		}		
		if(this._toolbarOperations[ZaOperation.CHNG_PWD]) {
			this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;
		}		
		if(this._toolbarOperations[ZaOperation.VIEW_MAIL]) {
			this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;
		}	
		if(this._toolbarOperations[ZaOperation.MOVE_ALIAS]) {
			this._toolbarOperations[ZaOperation.MOVE_ALIAS].enabled = false;		
		}
				
		if(this._popupOperations[ZaOperation.EDIT]) {	
			this._popupOperations[ZaOperation.EDIT].enabled = false;
		}		
		if(this._popupOperations[ZaOperation.CHNG_PWD]) {
			this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
		}		
		if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
			this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
		}	
		if(this._popupOperations[ZaOperation.MOVE_ALIAS]) {
			this._popupOperations[ZaOperation.MOVE_ALIAS].enabled = false;		
		}
		if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
			this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
		}			
	} else {
		if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
			this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
		}			
		if(this._toolbarOperations[ZaOperation.EDIT]) {	
			this._toolbarOperations[ZaOperation.EDIT].enabled = false;
		}	
		if(this._toolbarOperations[ZaOperation.DELETE]) {
			this._toolbarOperations[ZaOperation.DELETE].enabled = false;
		}		
		if(this._toolbarOperations[ZaOperation.CHNG_PWD]) {
			this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;
		}	
		if(this._toolbarOperations[ZaOperation.VIEW_MAIL]) {
			this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;
		}
		if(this._toolbarOperations[ZaOperation.MOVE_ALIAS])	{
			this._toolbarOperations[ZaOperation.MOVE_ALIAS].enabled = false;
		}	

		if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
			this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
		}					
		if(this._popupOperations[ZaOperation.EDIT]) {	
			this._popupOperations[ZaOperation.EDIT].enabled = false;
		}	
		if(this._popupOperations[ZaOperation.DELETE]) {
			this._popupOperations[ZaOperation.DELETE].enabled = false;
		}		
		if(this._popupOperations[ZaOperation.CHNG_PWD]) {
			this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
		}	
		if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
			this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
		}
		if(this._popupOperations[ZaOperation.MOVE_ALIAS])	{
			this._popupOperations[ZaOperation.MOVE_ALIAS].enabled = false;
		}	
	}
}
ZaController.changeActionsStateMethods["ZaSearchListController"].push(ZaSearchListController.changeActionsStateMethod);