#!/bin/bash

# Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

cd `dirname $0`

java -Djava.library.path=./ -Xms128m -Xmx1024m -Dapple.laf.useScreenMenuBar=true -Xdock:name="aTunes" -Dcom.apple.mrj.application.apple.menu.about.name=aTunes -Dcom.apple.mrj.application.growbox.intrudes=false -Dcom.apple.mrj.application.live-resize=true -Dcom.apple.macos.smallTabs=true -cp aTunes.jar:lib/* net.sourceforge.atunes.Main $1

