#
# doc namespace: transform
#
if { [xvalue exists [asset.doc.namespace.exists :namespace transform]] != "true" } {
    asset.doc.namespace.create :namespace transform :description "the doc namespace for transform."
}
#
# doc: transform-definition
#
asset.doc.type.update :create true :type transform:transform-definition \
    :description "Transform definition." \
    :definition < \
        :element -name uid -type long -min-occurs 1 -max-occurs 1 -index true < \
            :description "The unique id of the definition." \
        > \
        :element -name type -type enumeration -min-occurs 1 -max-occurs 1 -index true < \
            :restriction -base enumeration < \
                :value kepler :value exec \
            > \
        > \
        :element -name name -type string -min-occurs 0 -max-occurs 1 -index true < \
            :description "The name of the definition." \
        > \
        :element -name description -type string -min-occurs 0 -max-occurs 1 -index true < \
            :description "The description about the definition." \
        > \
        :element -name parameter -type document -min-occurs 0 -max-occurs infinity -index true < \
            :description "A parameter for the transform." \
            :attribute -name name -type string -min-occurs 1 -index true < \
                :description "The name of the parameter." \
            > \
            :attribute -name type -type enumeration -min-occurs 0 -default string -index true < \
                :description "The data type of the parameter." \
                :restriction -base enumeration < \
                    :value string :value boolean :value integer :value long :value float :value double \
                > \
            > \
            :attribute -name min-occurs -type integer -min-occurs 0 -default 1 \
            :attribute -name max-occurs -type integer -min-occurs 0 -default 1 \
            :element -name description -type string -min-occurs 0 -max-occurs 1 < :description "The description about the parameter." > \
            :element -name value -type string -min-occurs 0 -max-occurs 1 < :description "The default value of the parameter if applicable." > \
        > \
    >

#
# doc: transform
#
asset.doc.type.update :create true :type transform:transform \
    :description "Transform (instance)." \
    :definition < \
        :element -name uid -type long -min-occurs 0 -max-occurs 1 -index true < \
           :description "The unique id of the transform." \
        > \
        :element -name type -type enumeration -min-occurs 1 -max-occurs 1 -index true < \
            :restriction -base enumeration < \
                :value kepler :value exec \
            > \
        > \
        :element -name definition -type long -min-occurs 1 -max-occurs 1 -index true < \
            :description "The unique id of the definition." \
            :attribute -name version -type integer -min-occurs 1 -index true < \
                :description "The version of the definition." \
            > \
        > \
        :element -name name -type string -min-occurs 0 -max-occurs 1 -index true < \
            :description "A name for the transform." \
        > \
        :element -name description -type string -min-occurs 0 -max-occurs 1 -index true < \
            :description "A description about the transform." \
        > \
        :element -name status -type enumeration -min-occurs 1 -max-occurs 1 -index true < \
            :attribute -name time -type date -min-occurs 1 -index true < \
                :description "The time when the state changes." \
            > \
            :restriction -base enumeration < \
                :value pending :value running :value suspended :value terminated :value failed :value unknown \
            > \
        > \
        :element -name log -type string -min-occurs 0 -max-occurs infinity -index true < \
            :attribute -name time -type date -min-occurs 1 -index true < \
                :description "The time when the log is added." \
            > \
            :attribute -name type -type enumeration -min-occurs 1 -index true < \
                :restriction -base enumeration < \
                    :value error :value warning :value info \
                > \
            > \
        > \
        :element -name progress -type integer -min-occurs 0 -max-occurs 1 -index true < \
            :attribute -name time -type date -min-occurs 1 -index true < \
                :description "The time when the progress is updated." \
            > \
            :attribute -name total -type integer -min-occurs 1 -index true \
        > \
        :element -name progress-detail -type document -ignore-descendants true -min-occurs 0 -max-occurs 1 < \
            :description "The progress detail." \
            :attribute -name time -type date -min-occurs 1 -index true < \
                :description "The time when the progress detail is updated." \
            > \
        > \
        :element -name parameter -type string -min-occurs 0 -max-occurs infinity -index true < \
            :description "The parameter" \
            :attribute -name name -type string -min-occurs 1 -index true \
        > \
        :element -name runtime -type document  -min-occurs 0 -max-occurs 1 -index true < \
            :element -name property -type string -min-occurs 0 -max-occurs infinity -index true < \
                :attribute -name name -type string -min-occurs 1 -index true \
            > \
        > \
    >

