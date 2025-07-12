<template>
    <AdvancedFormCollapse class="advanced-form-translations card" :path="path" :label="label">
        <template #after-label>
            <template v-if="!readOnly">
                <FormButton v-if="reviewable" color="success" class="form-btn ms-2" @click.stop="reviewed = true">
                    <Icon name="checkmark-outline" />
                </FormButton>
            </template>
            <AdvancedFormFeedback class="ms-2" :validationResults="validationResults" />
            <AdvancedFormMergeButton v-if="readOnly" :path="path" />
            <div v-if="!readOnly && !fixedLocalizations && missingLocalizations.length > 0" class="ms-auto mb-3 missing-localizations">
                <FormButton v-for="l in missingLocalizations" :key="l" color="link" class="p-0 no-focus me-1" @click="addLocalization(l)">
                    <Flag :lang="l" />
                </FormButton>
            </div>
        </template>
        <template #default="{required: r}">
            <template v-for="(l, i) in usedLocalizations" :key="l">
                <component :is="translationComponent" :path="concatPaths([path, l])" :localization="l" :required="r" :availableSubpath="availableSubpath || ''" @remove="removeLocalization(l)" #default="slotProps">
                    <slot v-bind="slotProps" />
                    <template v-if="i !== usedLocalizations.length - 1">
                        <slot name="separator"><hr class="card-separator" /></slot>
                    </template>
                </component>
            </template>
        </template>
    </AdvancedFormCollapse>
</template>

<script lang = "ts" setup>
import {computed} from 'vue';
import AdvancedFormCollapse from "@components/form/advanced/AdvancedFormCollapse.vue";
import AdvancedFormMergeButton from "@components/form/advanced/merge/AdvancedFormMergeButton.vue";
import AdvancedFormFeedback from "@components/form/advanced/AdvancedFormFeedback.vue";
import {useAdvancedFormInput, watchAdvancedForm} from "@components/form/advanced/logic";
import {Flag, LocalizationCode, localizationCodes, sortLocalizations, Translations} from "@/localization";
import {cloneDeep, concat, get, set, uniq} from "lodash";
import FormButton from "@components/form/FormButton.vue";
import {concatPaths, Path} from "@/path";
import AdvancedFormTranslation from "@components/form/advanced/input/translation/AdvancedFormTranslation.vue";
import Icon from "@components/Icon.vue";
import {AsyncPredicate} from "@/types";
import {useProvideAdvancedFormTranslationContext} from "@components/form/advanced/input/translation/logic";
import AdvancedFormSingleLineTranslation
    from "@components/form/advanced/input/translation/AdvancedFormSingleLineTranslation.vue";


interface Props {
    path: Path;
    availableSubpath?: Path;
    localizationSubpath?: Path;
    localizations?: LocalizationCode[];
    label?: string;
    singleLine?: boolean;
    fixedLocalizations?: boolean;
    removeConfirmation?: AsyncPredicate<LocalizationCode>;
}

interface Emits {
    (e: 'update:localizations', args: LocalizationCode[]): void;
}

const props = withDefaults(defineProps<Props>(), {
    availableSubpath: 'available',
    localizationSubpath: 'localization',
    localizations: () => localizationCodes,
    label: 'Traductions',
    singleLine: false,
    fixedLocalizations: false
});
const emit = defineEmits<Emits>();

const { value, source, readOnly, validationResults, reviewable, reviewed } = useAdvancedFormInput<any, Translations<any>>(() => props.path);
useProvideAdvancedFormTranslationContext(() => props.fixedLocalizations, () => props.removeConfirmation);
const availableLocalizations = computed(() => {
    if (!source.value) {
        return [];
    }

    const localizations: LocalizationCode[] = [];

    for (const [k, v] of Object.entries(source.value)) {
        if (!props.availableSubpath || get(v, props.availableSubpath)) {
            localizations.push(k as LocalizationCode);
        }
    }
    return localizations;
});

const addLocalization = (localization: LocalizationCode) => {
    if (!value.value) {
        value.value = {};
        return;
    }

    const copy = cloneDeep(value.value);
    const translation = copy[localization] || {};

    if (props.availableSubpath) {
        set(translation, props.availableSubpath, true)
    }
    copy[localization] = translation;
    value.value = copy;
    emit('update:localizations', uniq([...props.localizations, localization]));
}
const removeLocalization = (localization: LocalizationCode) => {
    if (!value.value) {
        return;
    }

    const copy = cloneDeep(value.value);
    delete copy[localization];
    value.value = copy;
    emit('update:localizations', props.localizations.filter(l => l !== localization));
}
const usedLocalizations = computed(() => sortLocalizations(uniq(readOnly.value ? availableLocalizations.value : concat(props.localizations, availableLocalizations.value))));
const missingLocalizations = computed(() => sortLocalizations(localizationCodes.filter(l => !usedLocalizations.value.includes(l))));

const translationComponent = computed(() => props.singleLine ? AdvancedFormSingleLineTranslation : AdvancedFormTranslation);

watchAdvancedForm<any>((data: any) => {
    const copy = get(data, props.path);

    if (!copy) {
        set(data, props.path, {});
        return;
    }

    for (const k of Object.keys(copy)) {
        const  l = k as LocalizationCode;

        if (copy[l]) {
            if (props.availableSubpath && get(copy[l], props.availableSubpath) === undefined) {
                set(copy[l], props.availableSubpath, true);
            }
            set(copy[l], props.localizationSubpath, l);
        }
    }
});
</script>

<style lang="scss" scoped>
@import "../../AdvancedForm";

.advanced-form-translations {
    :deep(>div>.form-label) {
        @include bold-label;
    }
    .missing-localizations {
        background: $light-bg;
        padding: 0.1rem 0.3rem 0.4rem;
    }
}
</style>
