/*
Plugin to implement the vCard extension.
http://xmpp.org/extensions/xep-0054.html

Author: Nathan Zorn (nathan.zorn@gmail.com)
CoffeeScript port: Andreas Guth (guth@dbis.rwth-aachen.de)
*/
/* jslint configuration:
*/
/* global document, window, setTimeout, clearTimeout, console,
    XMLHttpRequest, ActiveXObject,
    Base64, MD5,
    Strophe, $build, $msg, $iq, $pres
*/
var buildIq;

buildIq = function(type, jid, vCardEl) {
  var iq;
  iq = $iq(jid ? {
    type: type,
    to: jid
  } : {
    type: type
  });
  iq.c("vCard", {
    xmlns: Strophe.NS.VCARD
  });
  if (vCardEl) iq.cnode(vCardEl);
  return iq;
};

Strophe.addConnectionPlugin('vcard', {
  _connection: null,
  init: function(conn) {
    this._connection = conn;
    return Strophe.addNamespace('VCARD', 'vcard-temp');
  },
  /*Function
    Retrieve a vCard for a JID/Entity
    Parameters:
    (Function) handler_cb - The callback function used to handle the request.
    (String) jid - optional - The name of the entity to request the vCard
       If no jid is given, this function retrieves the current user's vcard.
  */
  get: function(handler_cb, jid) {
    var iq;
    iq = buildIq("get", jid);
    return this._connection.sendIQ(iq.tree(), handler_cb);
  },
  /* Function
      Set an entity's vCard.
  */
  set: function(handler_cb, vCardEl, jid) {
    var iq;
    iq = buildIq("set", jid, vCardEl);
    return this._connection.sendIQ(iq.tree(), handler_cb);
  }
});
