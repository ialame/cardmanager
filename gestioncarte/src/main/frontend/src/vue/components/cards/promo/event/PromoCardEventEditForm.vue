<template>
    <AdvancedForm v-model="value" :mergeSources="savedEvents" :rules="promoCardEventValidationRules" small>
        <AdvancedFormMergeHeader :labels="['Evenement extraite', 'Evenement en BDD']" />
        <div class="container">
            <FormRow>
                <AdvancedFormInput path="name" label="Nom" />
            </FormRow>
            <FormRow>
                <Column class="mb-2">
                    <AdvancedFormCheckbox path="championship" label="Championnat" />
                    <AdvancedFormCheckbox v-show="distribution || modelValue.withoutDate" path="withoutDate" label="Utiliser la date de l'extension" />
                </Column>
            </FormRow>
            <AdvancedFormListInput class="mb-2" path="traits" label="Caracteristiques" #default="{ path }">
                <AdvancedFormSelect :path="path" :values="filteredTraits" #default="{ value: trait }">
                    <PromoCardEventTraitLabel :trait="trait" /> <span class="ms-1">({{ trait?.type }})</span>
                </AdvancedFormSelect>
            </AdvancedFormListInput>
            <AdvancedFormTranslations path="translations" label="Traductions" availableSubpath="" #default="{path}" v-model:localizations="localizationsValue" singleLine>
                <FormRow class="w-100">
                    <Column :size="hasDate ? '8' : '12'">
                        <AdvancedFormInput :path="concatPaths([path, 'name'])" />
                    </Column>
                    <Column v-if="hasDate" size="4">
                        <AdvancedFormInput :path="concatPaths([path, 'releaseDate'])" type="date" />
                    </Column>
                </FormRow>
            </AdvancedFormTranslations>
        </div>
        <template #out-of-side>
            <div class="p-0">
                <div class="d-flex flex-row float-end">
                    <slot name="buttons" />
                    <AdvancedFormSaveButton @save="$emit('save')" />
                </div>
            </div>
        </template>
    </AdvancedForm>
</template>

<script lang="ts" setup>
import {
    AdvancedForm,
    AdvancedFormCheckbox,
    AdvancedFormInput,
    AdvancedFormMergeHeader,
    AdvancedFormSaveButton,
    AdvancedFormSelect,
    AdvancedFormTranslations
} from "@components/form/advanced";
import {PromoCardEventDTO} from "@/types";
import {computed} from "vue";
import FormRow from "@components/form/FormRow.vue";
import {concatPaths} from "@/path";
import Column from "@components/grid/Column.vue";
import {promoCardEventValidationRules} from "@components/cards/promo/event/validation";
import {cloneDeep, isEqual, isNil} from "lodash";
import {LocalizationCode} from "@/localization";
import AdvancedFormListInput from "@components/form/advanced/input/AdvancedFormListInput.vue";
import PromoCardEventTraitLabel from "@components/cards/promo/event/trait/PromoCardEventTraitLabel.vue";
import {promoCardEventTraitService} from "@components/cards/promo";
import {useVModel} from "@vueuse/core";
import {getTcg, useTcg} from "@/tcg";

interface Props {
    modelValue: PromoCardEventDTO;
    distribution?: boolean;
    savedEvents?: PromoCardEventDTO[];
    localizations?: LocalizationCode[];
}

interface Emits {
    (e: 'update:modelValue', v: PromoCardEventDTO): void
    (e: 'update:localizations', v: LocalizationCode[]): void
    (e: 'save'): void
}

const props = withDefaults(defineProps<Props>(), {
    distribution: true
});
const emit = defineEmits<Emits>();

const tcg = useTcg();

const traits = promoCardEventTraitService.all;
const filteredTraits = computed(() => traits.value.filter(t => !tcg.value || getTcg(t.tcg) === tcg.value));

const value = computed({
    get() {
        return props.modelValue;
    },
    set(v: PromoCardEventDTO) {
        if (isEqual(v, props.modelValue)) {
            return;
        }

        const copy = cloneDeep(v);

        if (copy.withoutDate) {
            for (const t of Object.values(copy.translations)) {
                t.releaseDate = undefined;
            }
        } else {
            const usDate = copy.translations.us?.releaseDate;
            const oldUsDate = props.modelValue.translations.us?.releaseDate;

            if (usDate) {
                for (const [l, t] of Object.entries(copy.translations)) {
                    if (l !== 'us' && (isNil(props.modelValue.translations[l]?.releaseDate) || props.modelValue.translations[l]?.releaseDate === oldUsDate)) {
                        t.releaseDate = usDate;
                    }
                }
            }
        }
        for (const t of Object.values(copy.translations)) {
            t.labelName = t.name;
        }
        emit('update:modelValue', copy);
    }
});
const localizationsValue = useVModel(props, 'localizations', emit, {passive: true, defaultValue: []});
const hasDate = computed(() => !value.value.withoutDate);
</script>
