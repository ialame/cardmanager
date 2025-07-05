<template>
    <AdvancedFormCollapse :label="label"  :path="path">
        <div class="d-flex flex-row w-100" :class="statusClass">
            <div v-bind="mergeProps(inputProps, $attrs)" class="advanced-form-localization-select d-flex">
                <button v-for="l in localizations" :key="l" :data-localization="l" type="button" class="btn no-focus btn-link" :class="{ 'selected': source === l || !!source?.includes?.(l) }" @click="select(l)">
                    <div><Flag :lang="l" /></div>
                </button>
            </div>
            <FormButton v-if="reviewable" color="success" class="form-btn ms-2" @click="reviewed = true">
                <Icon name="checkmark-outline" />
            </FormButton>
            <AdvancedFormMergeButton v-if="readOnly" :path="path" />
            <AdvancedFormSourceButton v-else :path="path" />
        </div>
        <AdvancedFormFeedback :validationResults="validationResults" />
    </AdvancedFormCollapse>
</template>

<script lang="ts" setup>
import {mergeProps} from 'vue';
import AdvancedFormCollapse from "@components/form/advanced/AdvancedFormCollapse.vue";
import AdvancedFormMergeButton from "@components/form/advanced/merge/AdvancedFormMergeButton.vue";
import AdvancedFormFeedback from "@components/form/advanced/AdvancedFormFeedback.vue";
import {useAdvancedFormInput} from "@components/form/advanced/logic";
import {isArray} from "lodash";
import {Flag, LocalizationCode, localizationCodes} from "@/localization";
import Icon from "@components/Icon.vue";
import FormButton from '@components/form/FormButton.vue';
import {Path} from "@/path";
import AdvancedFormSourceButton from "@components/form/advanced/source/AdvancedFormSourceButton.vue";

type LocalizationCodeOrArray = LocalizationCode[] | LocalizationCode;

interface Props {
    path: Path;
    label?: string;
    multiple?: boolean;
    localizations?: LocalizationCode[];
}

const props = withDefaults(defineProps<Props>(), {
    label: '',
    multiple: false,
    localizations: () => localizationCodes,
});

const { value, source, validationResults, statusClass, reviewable, reviewed, inputProps, readOnly } = useAdvancedFormInput<any, LocalizationCodeOrArray>(() => props.path);

const select = (l: LocalizationCode) => {
    if (props.multiple) {
        const v = isArray(value.value) ? value.value : [value.value];

        if (v.includes(l)) {
            value.value = v.filter(ll => ll !== l) as LocalizationCode[];
        } else {
            value.value = [...v, l] as LocalizationCode[];
        }
    } else {
        value.value = l;
    }
}
</script>

<style lang="scss" scoped>
@import "src/variables";

div.advanced-form-localization-select button.btn {
    padding: 0;
    margin: 0.375rem 0.3rem;
    transition: transform .2s;

    &:hover, &.selected {
        transform: scale(1.5);
    }
    &.selected > div {
        border-bottom: 2px solid $primary;
        border-radius: 0;
        padding-bottom: 2px;
    }
}
</style>
