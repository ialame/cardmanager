<template>
    <div class="container mt-3 pe-0">
        <FormRow class="w-100">
            <Column v-if="localization === undefined">
                <label>Langue</label>
                <LocalizationSelect :modelValue="modelValue.localization" @update:modelValue="v => update(p => p.localization = v)" />
            </Column>
            <Column>
                <label>
                    <a v-if="source?.url" :href="source?.url" target="_blank" rel="noopener">Source</a>
                    <span v-else>Source</span>
                </label>
                <select class="form-select form-control source-select" :value="modelValue.source" @change="e => update(p => p.source = e.target.value)">
                    <option v-for="s in sources" :value="s" :key="s" :class="{existing: exists(s)}">{{s}}</option>
                </select>
                <div v-if="exists(modelValue.source)" class="invalid-feedback d-block">Le pattern de cette source a déjà été renseigné, êtes-vous sûr de vouloir en ajouter un autre ?</div>
            </Column>
        </FormRow>
        <FormRow v-if="source?.type === 'image' || source?.type === 'both'" class="w-100 d-flex flex-row">
            <FormInput label="Image Href" :modelValue="modelValue.imgHref" @update:modelValue="v => update(p => p.imgHref = v)" />
            <FormInput label="Titre" :modelValue="modelValue.title" @update:modelValue="setTitle" />
            <FormButton v-if="wikiNames.includes(modelValue.source)" class="ms-1 w-auto mt-auto" color="info" @click="searchPatterns()">
                <Icon icon="search-outline"/>
            </FormButton>
        </FormRow>
        <FormRow v-if="source?.type === 'pattern' || source?.type === 'both'" class="w-100">
            <RegexInput :modelValue="modelValue.regex" @update:modelValue="v => update(p => p.regex = v)" @keyup="manualRegex = false" :placeholder="`(^|[\\s-]+)${modelValue.title}([\\s-]+|$)`" />
        </FormRow>
        <Row class="w-100">
            <FormInput label="Nom de remplacement" :modelValue="modelValue.replacementName" @update:modelValue="v => update(p => p.replacementName = v)" />
            <FormInput label="Nom d'étiquette de remplacement" :modelValue="modelValue.replacementLabelName" @update:modelValue="v => update(p => p.replacementLabelName = v)" />
        </Row>
    </div>
    <Modal ref="wikiPatternModal" label="Recherche de pattern" size="xxl">
        <WikiPatternSearch :name="modelValue.source" @setPattern="setWikiPattern" />
    </Modal>
</template>

<script lang="ts" setup>
import {FeatureTranslationPatternDTO} from '@/types';
import FormInput from '@components/form/FormInput.vue';
import FormRow from '@components/form/FormRow.vue';
import LocalizationSelect from '@components/form/LocalizationSelect.vue';
import Column from '@components/grid/Column.vue';
import {ref, toRef, watch} from 'vue';
import Row from '@components/grid/Row.vue';
import RegexInput from "@components/form/RegexInput.vue";
import {useSource, useSources, useWikiNames} from "@components/cards/pokemon/feature/logic";
import {ModelComposables} from "@/vue/composables/model/ModelComposables";
import {LocalizationCode} from "@/localization";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import Modal from "@components/modal/Modal.vue";
import WikiPatternSearch from "@components/cards/pokemon/feature/WikiPatternSearch.vue";

interface Props {
    modelValue: FeatureTranslationPatternDTO;
    localization?: LocalizationCode;
    otherPatterns?: FeatureTranslationPatternDTO[];
}
interface Emits {
    (e: 'update:modelValue', value: FeatureTranslationPatternDTO): void;
}

const props = withDefaults(defineProps<Props>(), {
    otherPatterns: () => []
});
const emit = defineEmits<Emits>();
const update = ModelComposables.useUpdateModel<FeatureTranslationPatternDTO>(() => props.modelValue, emit);

const manualRegex = ref(false);
const setTitle = (title: string) => update(p => {
    p.title = title;
    if (!manualRegex.value) {
        p.regex = `(^|[\\s-]+)${title}([\\s-]+|$)`
    }
});
const sources = useSources(toRef(props, 'localization'));
const wikiNames = useWikiNames();
const wikiPatternModal = ref<typeof Modal>();
const searchPatterns = () => wikiPatternModal.value?.show();
const setWikiPattern = (title: string, link: string) => update(p => {
    p.title = title;
    p.imgHref = link;
    wikiPatternModal.value?.hide();
});


watch([() => props.modelValue, () => props.localization], () => {
    if (props.localization !== undefined && props.modelValue.localization !== props.localization) {
        update(p => p.localization = props.localization);
    }
});
const exists = (s: string) => props.otherPatterns.find(p => p.source === s) !== undefined;
const source = useSource(() => props.modelValue.source);
</script>

<style lang="scss" scoped>
@import "src/variables";

option.existing {
    color: $danger;
}
.source-select {
    background-color: rgba($primary, 0.1);
}
</style>
`
