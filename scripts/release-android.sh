#! /bin/bash

source workspace.sh

./gradlew clean
./gradlew android:bundleRelease

ANDROID_AAB_FILE='android/build/outputs/bundle/release/android-release.aab'
readonly ANDROID_AAB_FILE
if [ -f "$ANDROID_AAB_FILE" ]; then
  open -R ${ANDROID_AAB_FILE}
  echo "✅ Android build successful!"
else
  echo "❌ Android build failed..."
  exit 1
fi

./gradlew wearos:bundleRelease

WEAROS_AAB_FILE='wearos/build/outputs/bundle/release/wearos-release.aab'
readonly WEAROS_AAB_FILE
if [ -f "${WEAROS_AAB_FILE}" ]; then
  open -R ${WEAROS_AAB_FILE}
  echo "✅ WearOS build successful!"
else
  echo "❌ WearOS build failed..."
  exit 1
fi
