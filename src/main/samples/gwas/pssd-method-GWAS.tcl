# ===========================================================================
# Methods for GWAS project.
# ===========================================================================

set name "GWAS-Multiple-Workflow-Steps"
set description "The method to process GWAS data in separate steps."
set namespace "pssd/methods"

set id1 [xvalue id [om.pssd.method.update \
    :name $name \
    :description $description \
    :namespace $namespace \
    :step < \
        :name BET \
        :transform < \
            :definition -version 0 4 \
            :iterator < \
                :scope subject \
                :type citeable-id \
                :query "(model='om.pssd.subject')" \
                :parameter subject_id \
            > \
        > \
    > \
    :step < \
        :name FAST \
        :transform < \
            :definition -version 0 5 \
            :iterator < \
                :scope subject \
                :type citeable-id \
                :query "(model='om.pssd.subject')" \
                :parameter subject_id \
            > \
        > \
    > \
    :step < \
        :name FLIRT1 \
        :transform < \
            :definition -version 0 6 \
            :iterator < \
                :scope subject \
                :type citeable-id \
                :query "(model='om.pssd.subject')" \
                :parameter subject_id \
            > \
        > \
    > \
    :step < \
        :name FLIRT2 \
        :transform < \
            :definition -version 0 7 \
            :iterator < \
                :scope subject \
                :type citeable-id \
                :query "(model='om.pssd.subject')" \
                :parameter subject_id \
            > \
        > \
    > \
    :fillin true]]
    
set name "GWAS-Single-Workflow-Step"
set description "The method to process GWAS data in one single step."
set namespace "pssd/methods"

set id2 [xvalue id [om.pssd.method.for.subject.update \
    :name $name \
    :description $description \
    :namespace $namespace \
    :step < \
        :name "GWAS Process" \
        :transform < \
            :definition -version 0 3 \
            :iterator < \
                :scope subject \
                :type citeable-id \
                :query "(model='om.pssd.subject')" \
                :parameter subject_id \
            > \
        > \
    > \
    :fillin true]]
    

set name "GWAS-Overall-Method"
set description "The overall method to process GWAS data."
set namespace "pssd/methods"

set id [xvalue id [om.pssd.method.for.subject.update \
    :name $name \
    :description $description \
    :namespace $namespace \
    :subject < \
        :project < \
	        :public < \
                :metadata < :definition -requirement optional hfi.pssd.identity > \
                :metadata < :definition -requirement optional hfi.pssd.subject :value < :type constant(animal) > > \
                :metadata < :definition -requirement optional hfi.pssd.animal.subject :value < :species constant(human) > > \
                :metadata < :definition -requirement optional hfi.pssd.human.subject > \
                :metadata < :definition -requirement optional hfi.pssd.animal.disease > \
                :metadata < :definition -requirement optional hfi.pssd.human.education > \
            > \
            :private < \
                :metadata < :definition -requirement mandatory hfi.pssd.human.identity > \
            > \
        > \
    > \
    :step < \
        :name MR \
        :study < \
            :dicom < :modality MR > \
            :type "Magnetic Resonance Imaging" \
        > \
    > \
    :step < \
        :name transforms \
        :branch -type or < \
            :method < :id ${id1} > \
            :method < :id ${id2} > \
        > \
    > \
    :fillin true]]