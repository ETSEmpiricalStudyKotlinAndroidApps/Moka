#! /bin/bash

source workspace.sh
source bump-version.sh

bumpVersion
bumpVersionForIos

source release-android.sh
source release-ios.sh
source release-desktop.sh
