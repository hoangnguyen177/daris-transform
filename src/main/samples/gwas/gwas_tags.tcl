##
##  Procedure: get_dicom_element_value
##  Description: get the value of the specifed tag in the DICOM dataset/series. This is for the tags with single value.
##  Arguments: 
##      dataset_cid  - the citeable id of the DICOM dataset/series.
##      tag          - the DICOM tag in the format of ggggeeee, e.g. 00100020
##  Returns:
##      the value of the specified tag.
##
proc get_dicom_element_value { dataset_cid tag } {
    set asset_id [xvalue id [asset.query :where "cid='${dataset_cid}'"]]
    return [xvalue de\[@tag='${tag}'\]/value [dicom.metadata.get :id ${asset_id}]]
}

##
##  Procedure: get_dicom_element_values
##  Description: get the values of the specifed tag in the DICOM dataset/series. This is for the tags with multiple values.
##  Arguments: 
##      dataset_cid  - the citeable id of the DICOM dataset/series.
##      tag          - the DICOM tag in the format of ggggeeee, e.g. 00100020
##  Returns:
##      the list contains the values of the specified tag.
##
proc get_dicom_element_values { dataset_cid tag } {
    set asset_id [xvalue id [asset.query :where "cid='${dataset_cid}'"]]
    return [xvalues de\[@tag='${tag}'\]/value [dicom.metadata.get :id ${asset_id}]]
}

##
##  Procedure: get_contract_bolus_agent
##  Description: Get the value of Contrast/Bolus Agent (0018,0010) from the DICOM header of the specified dataset/series.
##  Arguments: 
##      dataset_cid  - the citeable id of the DICOM dataset/series.
##  Returns:
##      the value of Contrast/Bolus Agent field in the DICOM header.
##
proc get_contrast_bolus_agent { dataset_cid } {
    return [get_dicom_element_value $dataset_cid "00180010"]
}

##
##  Procedure: get_accquisition_type
##  Description: Get the value of accquisition type (0018,0023) from the DICOM header of the specified dataset/series.
##  Arguments: 
##      dataset_cid  - the citeable id of the DICOM dataset/series.
##  Returns:
##      the accquisition type (2D or 3D).
##
proc get_accquisition_type { dataset_cid } {
    return [get_dicom_element_value $dataset_cid "00180023"]
}

##
##  Procedure: get_slice_thickness
##  Description: Get the value of slice thickness (0018,0050) from the DICOM header of the specified dataset/series.
##  Arguments: 
##      dataset_cid  - the citeable id of the DICOM dataset/series.
##  Returns:
##      the slice thickness.
##
proc get_slice_thickness { dataset_cid } {
    return [get_dicom_element_value $dataset_cid "00180050"]
}

##
##  Procedure: get_repitition_time
##  Description: Get the value of repitition time (0018,0080) from the DICOM header of the specified dataset/series.
##  Arguments: 
##      dataset_cid  - the citeable id of the DICOM dataset/series.
##  Returns:
##      the repitition time.
##
proc get_repitition_time { dataset_cid } {
    return [get_dicom_element_value $dataset_cid "00180080"]
}

##
##  Procedure: get_echo_time
##  Description: Get the value of echo time (0018,0081) from the DICOM header of the specified dataset/series.
##  Arguments: 
##      dataset_cid  - the citeable id of the DICOM dataset/series.
##  Returns:
##      the echo time.
##
proc get_echo_time { dataset_cid } {
    return [get_dicom_element_value $dataset_cid "00180081"]
}

##
##  Procedure: get_inversion_time
##  Description: Get the value of inversion time (0018,0082) from the DICOM header of the specified dataset/series.
##  Arguments: 
##      dataset_cid  - the citeable id of the DICOM dataset/series.
##  Returns:
##      the inversion time.
##
proc get_inversion_time { dataset_cid } {
    return [get_dicom_element_value $dataset_cid "00180082" ]
}

##
##  Procedure: get_field_strength
##  Description: Get the value of magnetic field strength (0018,0087) from the DICOM header of the specified dataset/series.
##  Arguments: 
##      dataset_cid  - the citeable id of the DICOM dataset/series.
##  Returns:
##      the value of magnetic field strength.
##
proc get_field_strength { dataset_cid } {
    return [get_dicom_element_value $dataset_cid "00180087"]
}

##
##  Procedure: get_spacing_between_slices
##  Description: Get the value of spacing between slices (0018,0088) from the DICOM header of the specified dataset/series.
##  Arguments: 
##      dataset_cid  - the citeable id of the DICOM dataset/series.
##  Returns:
##      the value of spacing between slices.
##
proc get_spacing_between_slices { dataset_cid } {
    return [get_dicom_element_value $dataset_cid "00180088"]
}

##
##  Procedure: get_pixel_spacing
##  Description: Get the value of pixel spacing (0028,0030) from the DICOM header of the specified dataset/series.
##  Arguments: 
##      dataset_cid  - the citeable id of the DICOM dataset/series.
##  Returns:
##      the pixel spacing value.
##
proc get_pixel_spacing { dataset_cid } {
    return [get_dicom_element_value $dataset_cid "00280030"]
}

##
##  Procedure: normalize_orientation_values
##  Description: normalize the three values from image orientation field 0020,0037 of the DICOM header.
##               According to algorithm described by Julian, e.g. [0.3, 0.4, 1.1] will be normalize_orientation_valuesd to [0, 0, 1]
##  Arguments: 
##      values  - the list contains three unnormalised values.
##  Returns:
##      the list contains normalised values.
##
proc normalize_orientation_values { values } {
     set abs_values [list]
     foreach value $values {
         lappend abs_values [expr "abs(${value})"]
     }
     set v1 [lindex $abs_values 0]
     set v2 [lindex $abs_values 1]
     set v3 [lindex $abs_values 2]
     if { $v1>$v2 } {
        set v2 0
        if { $v1>$v3 } {
            set v1 1
            set v3 0
        } else {
            set v1 0
            set v3 1
        }
    } else {
        set v1 0
        if { $v2>$v3 } {
            set v2 1
            set v3 0
        } else {
            set v2 0
            set v3 1
        }
    }
    return [list $v1 $v2 $v3]
}

##
##  Procedure: get_orientaion
##  Description: Get the orientation (name) by assessing the values from 0020,0037 of the DICOM header.
##  Arguments: 
##      dataset_cid  - the citeable id of the dataset.
##  Returns:
##      the orientation name. (Axial, Sgittal or Coronal)
##
proc get_orientation { dataset_cid } {
    set values [get_dicom_element_values ${dataset_cid} "00200037"]
    set row1 [normalize_orientation_values [lrange $values 0 2]]
    set row2 [normalize_orientation_values [lrange $values 3 5]]
    if { [lindex $row1 0] == 1 && [lindex $row2 1] == 1 } {
        return Axial
    } elseif { [lindex $row1 1] == 1 && [lindex $row2 2] == 1 } {
        return Sagittal
    } elseif { [lindex $row1 0] == 1 && [lindex $row2 2] == 1 } {
        return Coronal
    } else {
        error "Invalid orientation values: $values"
    }
}
 
##
##  Procedure: calc_tags
##  Description: Calculate/Assess tags for the specified DICOM dataset/series.
##  Arguments: 
##      dataset_cid  - the citeable id of the dataset.
##  Returns:
##      A list of tags can be applied to the dataset.
##
proc calc_tags { dataset_cid } {
    set tags [list]
    set accquisition_type [get_accquisition_type $dataset_cid]
    set contrast_bolus_agent [get_contrast_bolus_agent $dataset_cid]
    set repitition_time [get_repitition_time $dataset_cid]
    set echo_time [get_echo_time $dataset_cid]
    set inversion_time [get_inversion_time $dataset_cid]
    if { $accquisition_type == "3D" } {
        lappend tags "3D"
        if { $repitition_time == 1900 && $echo_time >= 2.43 && $echo_time <= 2.63 && $inversion_time == 900 } {
            if { $contrast_bolus_agent == "" } {
                lappend tags "T1"
            } else {
                lappend tags "T1-Contrast"
            }
        } elseif { $repitition_time == 27 && $echo_time == 20 && $inversion_time == "" } {
            lappend tags "T2*"
        } elseif { $repitition_time == 5000 && $echo_time >= 350 && $echo_time <= 395 && $inversion_time == 1800 } {
            lappend tags "T2-Flair"
        }
    } elseif { $accquisition_type == "2D" } {
        set orientation [get_orientation $dataset_cid]
        lappend tags "2D-${orientation}"
        if { $repitition_time >= 250 && $repitition_time <= 600 && $echo_time >= 2.46 && $echo_time <= 13 } {
            if { $contrast_bolus_agent == "" } {
                lappend tags "T1"
            } else {
                lappend tags "T1-Contrast"
            }
        } elseif { $repitition_time >= 2800 && $repitition_time <= 5460 && $echo_time >= 84 && $echo_time <= 117 && $inversion_time == "" } {
            lappend tags "T2"
        } elseif { $repitition_time >= 8000 && $repitition_time <= 10000 && $echo_time >= 89 && $echo_time <= 135 && $inversion_time >= 2000 && $inversion_time <= 2500 } {
            lappend tags "T2-Flair"
        }
    }
    return $tags
}

##
##  Procedure: create_gwas_dataset_tags
##  Description: Create tag dictionary entries for the given gwas project
##  Arguments: 
##      project_cid  - the citeable id of the project.
##      tags         - the list of tags to be created.
##  Returns:
##
proc create_gwas_dataset_tags { project_cid tags } {
    om.pssd.object.tag.dictionary.create :if-exists ignore :project $project_cid
    foreach tag $tags {
        om.pssd.object.tag.dictionary.entry.add :tag < :name $tag > :project $project_cid :type dataset :if-exists ignore
    }
}

proc show_gwas_dataset_tags { pid } {
    set depth [llength [split pid "."]]
    if { $depth != 7 } {
    	set datasets [xvalues cid [asset.query :where "(cid starts with '${pid}' or cid='${pid}') and (mf-dicom-series has value)" :action get-cid]]
    } else {
        set datasets {$pid}
    }
    set results [list]
    foreach dataset $datasets {
        set tags [calc_tags $dataset]
        set result "${dataset}: ${tags}"
        lappend results $result
    }
    foreach result $results {
        puts "${result}"
    }
}

proc apply_gwas_dataset_tags { pid } {
    set depth [llength [split pid "."]]
    if { $depth != 7 } {
    	set datasets [xvalues cid [asset.query :where "(cid starts with '${pid}' or cid='${pid}') and (mf-dicom-series has value)" :action get-cid]]
    } else {
        set datasets {$pid}
    }
    foreach dataset $datasets {
        set tags [calc_tags $dataset]
        foreach tag $tags {
            om.pssd.object.tag.add :cid $dataset :tag < :name $tag >
        }
    }
}


#########################################################
## Example below shows how to use the functions above  ##
## to tag all the DICOM datasets within a project.     ##
#########################################################

proc example1 { } {

    ## show all tags of the datasets in subject 1.5.95.1
    show_gwas_dataset_tags 1.5.95.1
}

proc example { } {
    
    ## create tag dictionary and add gwas tag entries
    set project_cid 1.5.95
    create_gwas_dataset_tags $project_cid { 3D 2D-Axial 2D-Sagittal 2D-Coronal T1 T1-Contrast T2 T2-Flair }

    ## apply gwas tags to all the dicom datasets within the project 1.5.95
    show_gwas_dataset_tags 1.5.95.1
    #apply_gwas_dataset_tags 1.5.95.1
    
}