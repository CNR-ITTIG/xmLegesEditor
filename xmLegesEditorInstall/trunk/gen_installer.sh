#! /bin/sh

#######################################################
#                                                     #
#  gen_installer.sh - Installatore per xmLegesEditor  #
#                                                     #
#######################################################

IZPACK_HOME=$HOME/IzPack


echo "#######################################################"
echo "#                                                     #"
echo "#  gen_installer.sh - Installatore per xmLegesEditor  #"
echo "#                                                     #"
echo "#######################################################"
echo ""

echo "Creazione dei link alle librerie..."
echo ""
ln -s ../xmLegesEditor/xmLegesEditor.xml xmLegesEditor.xml
ln -s ../xmLegesEditor/lib lib
ln -s ../xmLegesCoreImpl/lib corelib
ln -s ../xmLegesEditorImpl/lib editorlib
ln -s ../xmLegesEditor/antiword antiword
ln -s ../xmLegesEditor/help help

echo "Creazione del link al file jar..."
echo ""
ln -s ../xmLegesEditor/dist/xmLegesCoreApi.jar xmLegesCoreApi.jar
ln -s ../xmLegesEditor/dist/xmLegesCoreImpl.jar xmLegesCoreImpl.jar
ln -s ../xmLegesEditor/dist/xmLegesEditorApi.jar xmLegesEditorApi.jar
ln -s ../xmLegesEditor/dist/xmLegesEditorImpl.jar xmLegesEditorImpl.jar

echo "Creazione dell'installer..."
echo ""
$IZPACK_HOME/bin/compile install-linux.xml -b . -o install-linux.jar -k standard
#$IZPACK_HOME/bin/compile install-win32.xml -b . -o install-win32.jar -k standard


echo "Rimozione dei link..."

rm xmLegesCoreApi.jar xmLegesCoreImpl.jar xmLegesEditorApi.jar xmLegesEditorImpl.jar xmLegesEditor.xml lib  corelib editorlib antiword help
