#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2012, 2013, 2014 Zimbra, Inc.
# 
# This program is free software: you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by the Free Software Foundation,
# version 2 of the License.
# 
# This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU General Public License for more details.
# You should have received a copy of the GNU General Public License along with this program.
# If not, see <http://www.gnu.org/licenses/>.
# ***** END LICENSE BLOCK *****
# 
use strict;
use lib '/opt/zimbra/zimbramon/lib';
use Net::LDAP;
use XML::Simple;

if ( ! -d "/opt/zimbra/openldap/etc" ) {
  print STDERR "ERROR: openldap does not appear to be installed - exiting\n";
  exit(1);
}

my $id = getpwuid($<);
chomp $id;
if ($id ne "zimbra") {
    print STDERR "Error: must be run as zimbra user\n";
    exit (1);
}


my $localxml = XMLin("/opt/zimbra/conf/localconfig.xml");
my $ldap_root_password = $localxml->{key}->{ldap_root_password}->{value};
chomp($ldap_root_password);

my $ldap = Net::LDAP->new('ldapi://%2fopt%2fzimbra%2fdata%2fldap%2fstate%2frun%2fldapi/') or die "$@";

my $mesg = $ldap->bind("cn=config", password=>"$ldap_root_password");

$mesg->code && die "Bind: ". $mesg->error . "\n"; 

my $dn="cn=config";

$mesg = $ldap->modify(
    $dn,
    add =>[
             olcTLSProtocolMin=> '3.1',
             olcTLSCipherSuite=> 'MEDIUM:HIGH',
          ],
  );

$ldap->unbind;
