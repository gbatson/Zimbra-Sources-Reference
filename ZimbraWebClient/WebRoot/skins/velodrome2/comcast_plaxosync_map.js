Comcast_plaxosync_map = {};

Comcast_plaxosync_map.z2p = {
	"anniversary": "anniversary",
	"assistantPhone": "assistantPhone",
	"birthday": "birthday",
	"callbackPhone": "callbackPhone",
	"carPhone": "carPhone",
	"company": "company",
	"companyPhone": "companyPhone",
	"department": "deptName",
	"email": "homeEmail1",
	"email2": "homeEmail2",
	"email3": "homeEmail3",
	"firstName": "firstName",
	"homeCity": "homeCity",
	"homeCountry": "homeCountry",
	"homeFax": "homeFax",
	"homePhone": "homePhone1",
	"homePhone2": "homePhone2",
	"homePostalCode": "homePostalCode",
	"homeState": "homeState",
	"homeStreet": "homeAddress",
	"homeURL": "homeWebPage",
	"image": "workPhotoURL",
	"jobTitle": "jobTitle",
	"lastName": "lastName",
	"middleName": "middleName",
	"mobilePhone": "homeMobile",
	"namePrefix": "nameTitle",
	"nameSuffix": "nameSuffix",
	"nickname": "nickname",
	"otherCity": "otherCity",
	"otherCountry": "otherCountry",
	"otherFax": "otherFax",
	"otherPhone": "otherPhone",
	"otherPostalCode": "otherPostalCode",
	"otherState": "otherState",
	"otherStreet": "otherAddress",
	"pager": "workPager",
	"workCity": "workCity",
	"workCountry": "workCountry",
	"workEmail1": "workEmail1",
	"workEmail2": "workEmail2",
	"workEmail3": "workEmail3",
	"workFax": "workFax",
	"workMobile": "workMobile",
	"workPhone": "workPhone1",
	//"workPhone2": "workPhone2",
	"workAltPhone": "workPhone2",
	"workPostalCode": "workPostalCode",
	"workState": "workState",
	"workStreet": "workStreet",
	"workURL": "workWebPage",
	"workCardMessage": "workCardMessage",
	"homeCardMessage": "homeCardMessage",
	"fullName": null, // fullName will not be propagated
	"itemId": "itemId" // itemId must be propagated
};

Comcast_plaxosync_map.p2z = {};
for (var key in Comcast_plaxosync_map.z2p) // Flip the map
	if (Comcast_plaxosync_map.z2p[key])
		Comcast_plaxosync_map.p2z[Comcast_plaxosync_map.z2p[key]] = key;
			
Comcast_plaxosync_map.p2z["email"] = "email";

Comcast_plaxosync_map.im_z2p = {
	"imAddress": "homeIMList",
	"workIM": "workIMList"
};

Comcast_plaxosync_map.imPattern = /(\w+):\/\/(.+)/;

Comcast_plaxosync_map.map = function(inAttrs, attrmap) {
	if (AjxUtil.isObject(attrmap)) {
		if (AjxUtil.isArray(inAttrs)) {
			var outAttrs = [];
			for (var i=0; i<inAttrs.length; i++) {
				outAttrs.push(Comcast_plaxosync_map.map(inAttrs[i], attrmap));
			}
			return outAttrs;
		} else if (AjxUtil.isObject(inAttrs)) {
			var outAttrs = {};
			for (var inKey in inAttrs) {
				if (inKey != null) {
					var outKey = attrmap[inKey] || null;
					if (outKey != null) {
						outAttrs[outKey] = inAttrs[inKey];
					}
				}
			}
			return outAttrs;
		} else if (AxjUtil.isString(inAttrs)) {
			return attrmap[inAttrs];
		}
	}
	return null;
}

Comcast_plaxosync_map.map_z2p = function(zimbraAttrs) {
	if (AjxUtil.isArray(zimbraAttrs)) {
		var plaxoAttrs = [];
		for (var i=0; i<zimbraAttrs.length; i++) {
			var zimbraAttrsItem = zimbraAttrs[i];
			var plaxoAttrsItem = Comcast_plaxosync_map.map(zimbraAttrsItem, Comcast_plaxosync_map.z2p);
			var plaxoIMAttrsItem = Comcast_plaxosync_map.mapIM_z2p(zimbraAttrsItem);
			plaxoAttrsItem = AjxUtil.hashUpdate(plaxoAttrsItem, plaxoIMAttrsItem, true);
			plaxoAttrs.push(plaxoAttrsItem);
		}
		return plaxoAttrs;
	} else if (AjxUtil.isObject(zimbraAttrs)) {
		var plaxoAttrs = Comcast_plaxosync_map.map(zimbraAttrs, Comcast_plaxosync_map.z2p);
		var plaxoIMAttrs = Comcast_plaxosync_map.mapIM_z2p(zimbraAttrs);
		plaxoAttrs = AjxUtil.hashUpdate(plaxoAttrs, plaxoIMAttrs, true);
		return plaxoAttrs;
	}
}

Comcast_plaxosync_map.map_p2z = function(plaxoAttrs) {
	if (AjxUtil.isArray(plaxoAttrs)) {
		var zimbraAttrs = [];
		for (var i=0; i<plaxoAttrs.length; i++) {
			var plaxoAttrsItem = plaxoAttrs[i];
			var zimbraAttrsItem = Comcast_plaxosync_map.map(plaxoAttrsItem, Comcast_plaxosync_map.p2z);
			var zimbraIMAttrsItem = Comcast_plaxosync_map.mapIM_p2z(plaxoAttrsItem);
			zimbraAttrsItem = AjxUtil.hashUpdate(zimbraAttrsItem, zimbraIMAttrsItem, true);
			zimbraAttrs.push(zimbraAttrsItem);
		}
		return zimbraAttrs;
	} else if (AjxUtil.isObject(plaxoAttrs)) {
			var zimbraAttrs = Comcast_plaxosync_map.map(plaxoAttrs, Comcast_plaxosync_map.p2z);
			var zimbraIMAttrs = Comcast_plaxosync_map.mapIM_p2z(plaxoAttrs);
			zimbraAttrs = AjxUtil.hashUpdate(zimbraAttrs, zimbraIMAttrs, true);
			return zimbraAttrs;
	}
}

Comcast_plaxosync_map.mapIM_z2p = function(zimbraAttrs) {
	if (AjxUtil.isObject(zimbraAttrs)) {
		var plaxoAttrs = {};
		for (var zkey in Comcast_plaxosync_map.im_z2p) {
			var pkey = Comcast_plaxosync_map.im_z2p[zkey];
			var p = new RegExp(zkey+"(\\d+)");
			var imList = [];
			for (var key in zimbraAttrs) {
				var m = p.exec(key);
				if (m && m.length>=2) {
					var i = parseInt(m[1])-1;
					var address = zimbraAttrs[key];
					if (address != null) {
						var pim = Comcast_plaxosync_map.mapIM_z2p_single(address);
						if (pim!=null) {
			 				imList[i] = pim;
						}
					}
				}
			}
			if (imList.length>0) {
				plaxoAttrs[pkey] = {imAddress: imList};
			}
		}
		return plaxoAttrs;
	}
}



Comcast_plaxosync_map.mapIM_z2p_single = function(imAddress) {
	var m = Comcast_plaxosync_map.imPattern.exec(imAddress);
	if (m && m.length>=3) {
		var service = m[1];
		var address = m[2];
		var pim = {};
		pim["service"] = m[1];
		pim["address"] = m[2];
		return pim;
	}
	return null;
}

Comcast_plaxosync_map.mapIM_p2z = function(plaxoAttrs) {
	var zimbraAttrs = {};
	for (var zkey in Comcast_plaxosync_map.im_z2p) {
		var pkey = Comcast_plaxosync_map.im_z2p[zkey];
		try {
			var homeIMList = plaxoAttrs[pkey];
			var addresses = homeIMList["imAddress"];
			var j=1;
			for (var i=0; i<addresses.length; i++) {
				var address = addresses[i];
				var zim = Comcast_plaxosync_map.mapIM_p2z_single(address);
				zimbraAttrs[zkey+j] = zim;
				j++;
			}
		} catch (e) {}
	}
	return zimbraAttrs;
}

Comcast_plaxosync_map.mapIM_p2z_single = function(imAddress) {
	return imAddress["service"].toLowerCase() + "://" + imAddress["address"];
}

