/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2014 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "License");
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at: http://www.zimbra.com/license
 * The License is based on the Mozilla Public License Version 1.1 but Sections 14 and 15 
 * have been added to cover use of software over a computer network and provide for limited attribution 
 * for the Original Developer. In addition, Exhibit A has been modified to be consistent with Exhibit B. 
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. 
 * See the License for the specific language governing rights and limitations under the License. 
 * The Original Code is Zimbra Open Source Web Client. 
 * The Initial Developer of the Original Code is Zimbra, Inc. 
 * All portions of the code are Copyright (C) 2007, 2014 Zimbra, Inc. All Rights Reserved. 
 * ***** END LICENSE BLOCK *****
 */
var isIE = navigator.userAgent.match("MSIE");

function $(id) { return document.getElementById(id); }

function reparent(comp, idOrElement) {
    if (!comp) return;

    if (comp instanceof DwtControl) {
        comp.reparentHtmlElement(idOrElement);
    }
    else {
        var el = typeof idOrElement == "string" ? $(idOrElement) : idOrElement;
        el.appendChild(comp);
    }
}

function showToast(s) {
    clearTimeout(window.toastId);
    var el = document.getElementById("toast");
    if (!el) {
        el = document.createElement("DIV");
        el.id = "toast";
        document.body.appendChild(el);
    }
    el.innerHTML = s;
    el.style.display = "block";
    window.toastId = setTimeout(hideToast, 3000);
}
function hideToast() {
    var el = document.getElementById("toast");
    if (el) {
        el.style.display = "none";
    }
}

if (!window.console) {
    window.console = {};
}
if (!console.log) {
    console.log = function() {
        var a = [];
        for (var i = 0; i < arguments.length; i++) {
            a.push(arguments[i]);
        }
        var s = a.join("");
        showToast(s);
    };
}
