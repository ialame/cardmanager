<template>
    <AdvancedFormCollapse class="advanced-form-translation mb-3" :path="path" v-model:open="open">
        <template #label="{required: rr}">
            <LocalizationLabel :flagClass="{[statusClass]: required && rr}" :localization="localization" />
        </template>
        <template #after-label>
            <template v-if="!readOnly">
                <AdvancedFormRemoveTranslationButton :path="path" :localization="localization" @remove="remove()" />
                <FormButton v-if="reviewable" color="success" class="form-btn ms-2" @click.stop="review()">
                    <Icon name="checkmark-outline" />
                </FormButton>
                <AdvancedFormCheckbox v-if="availableSubpath" class="ms-2" :path="concatPaths([path, availableSubpath])" label="Available" />
            </template>
            <AdvancedFormFeedback class="ms-2" :validationResults="validationResults" />
            <AdvancedFormMergeButton v-if="readOnly" :path="path" />
        </template>
        <template #default="{required: rr}">
            <slot :localization="localization" :required="required && rr" :path="path" />
        </template>
    </AdvancedFormCollapse>
</template>

<script lang = "ts" setup>
import {ref} from 'vue';
import {LocalizationCode, LocalizationLabel} from "@/localization";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import {concatPaths, Path} from "@/path";
import AdvancedFormRemoveTranslationButton
    from "@components/form/advanced/input/translation/AdvancedFormRemoveTranslationButton.vue";
import AdvancedFormCollapse from "@components/form/advanced/AdvancedFormCollapse.vue";
import AdvancedFormMergeButton from "@components/form/advanced/merge/AdvancedFormMergeButton.vue";
import AdvancedFormFeedback from "@components/form/advanced/AdvancedFormFeedback.vue";
import {useAdvancedFormInput} from "@components/form/advanced/logic";
import AdvancedFormCheckbox from "@components/form/advanced/input/AdvancedFormCheckbox.vue";

interface Props {
    path: Path;
    availableSubpath: Path;
    localization: LocalizationCode;
    required?: boolean;
}
interface Emits {
    (e: 'remove'): void;
}

const props = withDefaults(defineProps<Props>(), {
    required: false,
});
const emit = defineEmits<Emits>();

const { value, readOnly, validationResults, statusClass, reviewable, reviewed } = useAdvancedFormInput<any, any>(() => props.path);

const open = ref(true);
const remove = async () => {
    emit('remove');
    open.value = false;
};
const review = () => {
    reviewed.value = true;
    if (!value.value) {
        open.value = false;
    }
};
</script>
