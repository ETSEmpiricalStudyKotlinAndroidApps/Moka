#! /bin/bash

# make sure we are in the right workspace.
current_dir="${PWD##*/}"
if [ "${current_dir}" == "scripts" ]; then
  cd ..
else
  echo "Unexpected work directory."
  exit
fi
