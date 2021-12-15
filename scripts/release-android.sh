#! /bin/bash

source workspace.sh

./gradlew clean
./gradlew android:bundleRelease

AAB_FILE='android/build/outputs/bundle/release/android-release.aab'
readonly AAB_FILE
if [ -f "$AAB_FILE" ]; then
  open -R ${AAB_FILE}
  echo "✅ Build successful!"
else
  echo "❌ Build failed..."
fi
