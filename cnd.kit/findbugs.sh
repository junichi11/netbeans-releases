#!/bin/sh -x
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
#
# Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
#
# Oracle and Java are registered trademarks of Oracle and/or its affiliates.
# Other names may be trademarks of their respective owners.
#
# The contents of this file are subject to the terms of either the GNU
# General Public License Version 2 only ("GPL") or the Common
# Development and Distribution License("CDDL") (collectively, the
# "License"). You may not use this file except in compliance with the
# License. You can obtain a copy of the License at
# http://www.netbeans.org/cddl-gplv2.html
# or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
# specific language governing permissions and limitations under the
# License.  When distributing the software, include this License Header
# Notice in each file and include the License file at
# nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
# particular file as subject to the "Classpath" exception as provided
# by Oracle in the GPL Version 2 section of the License file that
# accompanied this code. If applicable, add the following below the
# License Header, with the fields enclosed by brackets [] replaced by
# your own identifying information:
# "Portions Copyrighted [year] [name of copyright owner]"
#
# If you wish your version of this file to be governed by only the CDDL
# or only the GPL Version 2, indicate your decision by adding
# "[Contributor] elects to include this software in this distribution
# under the [CDDL or GPL Version 2] license." If you do not indicate a
# single choice of license, a recipient has the option to distribute
# your version of this file under either the CDDL, the GPL Version 2 or
# to extend the choice of license to its licensees as provided above.
# However, if you add GPL Version 2 code and therefore, elected the GPL
# Version 2 license, then the option applies only if the new code is
# made subject to such option by the copyright holder.
#
# Contributor(s):

fb_home="$1"
workspace="$2"
out="$3"

#don't want to clean everything - for example, jars are needed...
rm ${workspace}/cnd.apt/src/org/netbeans/modules/cnd/apt/impl/support/generated/* 2>/dev/null
rm ${workspace}/cnd.apt/build/classes/org/netbeans/modules/cnd/apt/impl/support/generated/* 2>/dev/null
rm ${workspace}/cnd.modelimpl/src/org/netbeans/modules/cnd/modelimpl/parser/generated/* 2>/dev/null
rm ${workspace}/cnd.modelimpl/build/classes/org/netbeans/modules/cnd/modelimpl/parser/generated/* 2>/dev/null
rm ${workspace}/cnd.modelimpl/build/classes/org/netbeans/modules/cnd/modelimpl/parser/FortranLexicalPrepass* 2>/dev/null

PLIST=`ls -d ${workspace}/cnd*/build/classes ${workspace}/remotefs*/build/classes ${workspace}/dlight*/build/classes ${workspace}/git.remote*/build/classes ${workspace}/subversion.remote/build/classes ${workspace}/mercurial.remote/build/classes ${workspace}/lib.terminalemulator/build/classes ${workspace}/terminal/build/classes | egrep -v "/cnd.antlr/|/cnd.debugger.common/|/cnd.debugger.gdb/|/cnd.debugger.dbx/"`
PR=""
for d in ${PLIST}; do
   s=`echo $d | sed 's/build\/classes/src/'`
   PR="${PR}
      <Jar>$d</Jar>
      <AuxClasspathEntry>$s</AuxClasspathEntry>"
done

prj="/tmp/cnd.fbp"
cat << EOF > ${prj}
<Project filename="CND" projectName="CND">
${PR}
  <SuppressionFilter>
    <LastVersion value="-1" relOp="NEQ"/>
  </SuppressionFilter>
</Project>
EOF

${fb_home}/bin/findbugs -maxHeap 1536 -textui -project ${prj} -xml -effort:max -low -output ${out}
