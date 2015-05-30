# ===========================================================================
# Method for GWAS project.
# ===========================================================================

set name "Human-MRI-GWAS-Single-Workflow"
set description "Method with a single transform/workflow step for GWAS project."
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
        :name "GWAS Analysis" \
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
    :fillin true
