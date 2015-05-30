#!/bin/bash
export FSLOUTPUTTYPE=NIFTI
for file in $(ls *.nii)
do
    name=${file%.*}
    bet $name ${name}_brain
done
zip brain.zip *_brain.nii
echo "$(pwd)/brain.zip" 
