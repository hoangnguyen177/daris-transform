# ===========================================================================
# Method for GWAS project.
# ===========================================================================

set name "Human-MRI-GWAS-Multiple-Workflows"
set description "Method with multiple transform/workflow steps for GWAS project."
set namespace "pssd/methods"


om.pssd.method.for.subject.update \
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
    :fillin true
