JSON = function(){};

(function() {
    function f(n) {
        return n < 10 ? '0' + n : n;
    }

    if (typeof Date.prototype.toJSON !== 'function') {

        Date.prototype.toJSON = function (key) {

            return isFinite(this.valueOf()) ?
                   this.getUTCFullYear()   + '-' +
                 f(this.getUTCMonth() + 1) + '-' +
                 f(this.getUTCDate())      + 'T' +
                 f(this.getUTCHours())     + ':' +
                 f(this.getUTCMinutes())   + ':' +
                 f(this.getUTCSeconds())   + 'Z' : null;
        };
	}

        String.prototype.toJSON =
        Number.prototype.toJSON =
        Boolean.prototype.toJSON = function(key) {
            return this.valueOf();
        };
})();

JSON.cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
JSON.escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
//JSON.gap;
//JSON.indent;
JSON.meta = {
        '\b': '\\b',
        '\t': '\\t',
        '\n': '\\n',
        '\f': '\\f',
        '\r': '\\r',
        '"' : '\\"',
        '\\': '\\\\'
    };
JSON.rep;


JSON.quote = function(string) {
    JSON.escapable.lastIndex = 0;
    return JSON.escapable.test(string) ?
        '"' + string.replace(JSON.escapable, function (a) {
            var c = JSON.meta[a];
            return typeof c === 'string' ? c :
                '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
        }) + '"' :
        '"' + string + '"';
};

JSON.str = function(key, holder) {

    var i,          // The loop counter.
        k,          // The member key.
        v,          // The member value.
        length,
        mind = JSON.gap,
        partial,
        value = holder[key];

    if (value && typeof value === 'object' &&
            typeof value.toJSON === 'function') {
        value = value.toJSON(key);
    }

    if (typeof JSON.rep === 'function') {
        value = JSON.rep.call(holder, key, value);
    }

    switch (typeof value) {
	    case 'string':
	        return JSON.quote(value);
	    case 'number':
	        return isFinite(value) ? String(value) : 'null';
	    case 'boolean':
	    case 'null':
	        return String(value);
	    case 'object':
	        if (!value) {
	            return 'null';
	        }

	        JSON.gap += JSON.indent;
	        partial = [];

	        if (Object.prototype.toString.apply(value) === '[object Array]') {

	            length = value.length;
	            for (i = 0; i < length; i += 1) {
	                partial[i] = str(i, value) || 'null';
	            }

	            v = partial.length === 0 ? '[]' :
	                JSON.gap ? '[\n' + JSON.gap +
	                        partial.join(',\n' + JSON.gap) + '\n' +
	                            mind + ']' :
	                      '[' + partial.join(',') + ']';
	            JSON.gap = mind;
	            return v;
	        }

	        if (JSON.rep && typeof JSON.rep === 'object') {
	            length = JSON.rep.length;
	            for (i = 0; i < length; i += 1) {
	                k = JSON.rep[i];
	                if (typeof k === 'string') {
	                    v = str(k, value);
	                    if (v) {
	                        partial.push(JSON.quote(k) + (JSON.gap ? ': ' : ':') + v);
	                    }
	                }
	            }
	        } else {

	            for (k in value) {
	                if (Object.hasOwnProperty.call(value, k)) {
	                    v = str(k, value);
	                    if (v) {
	                        partial.push(JSON.quote(k) + (JSON.gap ? ': ' : ':') + v);
	                    }
	                }
	            }
	        }

	        v = partial.length === 0 ? '{}' :
	            JSON.gap ? '{\n' + JSON.gap + partial.join(',\n' + JSON.gap) + '\n' + mind + '}' : '{' + partial.join(',') + '}';
	        JSON.gap = mind;
	        return v;
    }
};


JSON.stringify = function(value, replacer, space) {

    var i;
    JSON.gap = '';
    JSON.indent = '';

    if (typeof space === 'number') {
        for (i = 0; i < space; i += 1) {
            JSON.indent += ' ';
        }

    } else if (typeof space === 'string') {
        JSON.indent = space;
    }

    JSON.rep = replacer;
    if (replacer && typeof replacer !== 'function' &&
            (typeof replacer !== 'object' ||
             typeof replacer.length !== 'number')) {
        throw new Error('JSON.stringify');
    }

    return JSON.str('', {'': value});
};

JSON.parse = function(text, reviver) {
    var j;
    var walk = function walk(holder, key) {

        var k, v, value = holder[key];
        if (value && typeof value === 'object') {
            for (k in value) {
                if (Object.hasOwnProperty.call(value, k)) {
                    v = walk(value, k);
                    if (v !== undefined) {
                        value[k] = v;
                    } else {
                        delete value[k];
                    }
                }
            }
        }
        return reviver.call(holder, key, value);
    };

    text = String(text);
    JSON.lastIndex = 0;
    if (JSON.cx.test(text)) {
        text = text.replace(JSON.cx, function (a) {
            return '\\u' +
                ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
        });
    }

    if (/^[\],:{}\s]*$/.test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@').replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

        j = eval('(' + text + ')');
        return typeof reviver === 'function' ?
            walk({'': j}, '') : j;
    }

    throw new SyntaxError('JSON.parse');
};

