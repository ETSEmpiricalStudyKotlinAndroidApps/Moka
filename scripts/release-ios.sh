#! /bin/bash

source workspace.sh

cd ios

# clean project
if [ -e "build" ]; then
  rm -rf build
fi
xcodebuild clean

# archive
xcodebuild archive -project ios.xcodeproj -scheme ios -archivePath build/moka.xcarchive

# build
# don't have a provisioning profile yet.
#xcodebuild -exportArchive -archivePath build/moka.xcarchive -exportPath ios -exportFormat ipa -exportProvisioningProfile "path-to-provisioning-profile"
