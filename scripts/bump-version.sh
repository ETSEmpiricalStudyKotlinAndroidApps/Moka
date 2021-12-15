#! /bin/bash

source workspace.sh

function bumpVersion() {
  lines=()
  while IFS= read -r line; do
    lines+=("${line}")
  done <version.properties

  if [ ${#lines[@]} != 2 ]; then
    echo "Unexpected line count of version file"
    exit
  fi

  IFS='='
  code_kv=()
  read -ra ADDR <<<"${lines[0]}"
  for i in "${ADDR[@]}"; do
    code_kv+=("${i}")
  done

  name_kv=()
  read -ra ADDR <<<"${lines[1]}"
  for i in "${ADDR[@]}"; do
    name_kv+=("${i}")
  done

  if [[ (${#code_kv[@]} != 2) || ("${code_kv[0]}" != "versionCode") ]]; then
    echo "Unexpected versionCode"
    exit
  fi

  if [[ (${#name_kv[@]} != 2) || ("${name_kv[0]}" != "versionName") ]]; then
    echo "Unexpected versionName"
    exit
  fi

  code_v="${code_kv[1]}"
  code_v_int=$((code_v + 0))

  name_v="${name_kv[1]}"
  name_v_integers=()
  IFS='.'
  read -ra ADDR <<<"${name_v}"
  for i in "${ADDR[@]}"; do
    name_v_integers+=("${i}")
  done

  if [[ ${#name_v_integers[@]} != 3 ]]; then
    echo "Unexpected versionName format"
    exit
  fi

  cat >version.properties <<EOF
${code_kv[0]}=$((code_v_int + 1))
${name_kv[0]}=${name_v_integers[0]}.${name_v_integers[1]}.$((name_v_integers[2] + 1))
EOF
}

function bumpVersionForIos() {
  PLIST_FILE_PATH="ios/ios/Info.plist"
  readonly PLIST_FILE_PATH
  if [ ! -f "${PLIST_FILE_PATH}" ]; then
    echo "plist file not found."
    exit 1
  fi

  plutil -replace CFBundleShortVersionString -string "${versionName}" ${PLIST_FILE_PATH}
  plutil -replace CFBundleVersion -string "${versionCode}" ${PLIST_FILE_PATH}
}
