#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2006, 2007, 2008, 2009, 2010, 2013, 2014 Zimbra, Inc.
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
use Migrate;


Migrate::verifyLoggerSchemaVersion(4);

addLogHostName();

Migrate::updateLoggerSchemaVersion(4,5);

exit(0);

#####################

sub addLogHostName() {
    Migrate::log("Adding loghostname");

	my $sql = <<EOF;
alter table mta add INDEX i_qid (qid);
EOF

    Migrate::runLoggerSql($sql);
}
