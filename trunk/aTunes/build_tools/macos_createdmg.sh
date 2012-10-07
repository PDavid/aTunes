echo "SetFile in Volume icon"
SetFile -c icnC $1/.VolumeIcon.icns
echo "Creating DMG"
hdiutil create -srcfolder $1 -volname "$2" -format UDRW -ov $3

echo "Mount dmg and set icon"
export DMG="$1/DMG"
mkdir $DMG
hdiutil attach $3 -mountpoint $DMG
SetFile -a C $DMG

#Include link to Applications
ln -s /Applications $DMG/Applications

echo "Umount dmg"
hdiutil detach $DMG

echo "convert to compressed"
hdiutil convert $3 -format UDZO -o $4

echo "Clean"
rm -rf $3
rm -rf $DMG

echo "Finished"
