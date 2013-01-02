#!/bin/sh

# Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
#
# See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
#
# http://www.atunes.org
# http://sourceforge.net/projects/atunes
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
 
#Change to dir where aTunes is
cd `dirname $0`

# Parse arguments and add quotes to all
args=""
for arg in "$@"
do
  args="$args \"$arg\""
done

java -Dsubstancelaf.windowRoundedCorners=false -Dinsubstantial.looseTableCellRenderers=true -Djava.library.path=./ -Xms128m -Xmx256m -splash:splash.gif -cp aTunes.jar:lib/* net.sourceforge.atunes.Main $args
