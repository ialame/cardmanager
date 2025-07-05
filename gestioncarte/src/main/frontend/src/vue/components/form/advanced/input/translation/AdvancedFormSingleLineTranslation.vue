<template>
    <div class="advanced-form-translation mb-3" v-bind="columnProps">
        <div class="d-flex flex-row w-100" :class="statusClass">
            <Flag class="icon-24 me-2 mt-1" :lang="localization" />
            <slot :localization="localization" :required="required" :path="path" />
            <AdvancedFormRemoveTranslationButton :path="path" :localization="localization" @remove="emit('remove')" />
            <FormButton v-if="reviewable" color="success" class="form-btn ms-2" @click="reviewed = true">
                <Icon name="checkmark-outline" />
            </FormButton>
            <AdvancedFormMergeButton :path="path" />
        </div>
        <AdvancedFormFeedback class="mt-2" :validationResults="validationResults" />
    </div>
</template>

<script lang = "ts" setup>
import AdvancedFormMergeButton from "@components/form/advanced/merge/AdvancedFormMergeButton.vue";
import AdvancedFormFeedback from "@components/form/advanced/AdvancedFormFeedback.vue";
import {useAdvancedFormInput} from "@components/form/advanced/logic";
import {LocalizationCode} from "@/localization";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import {Path, pathToString} from "@/path";
import Flag from "@/localization/Flag.vue";
import AdvancedFormRemoveTranslationButton
    from "@components/form/advanced/input/translation/AdvancedFormRemoveTranslationButton.vue";
import {useAlignedElement} from "@components/form/advanced/merge/alignment";

interface Props {
    path: Path;
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

const columnProps = useAlignedElement(() => pathToString(props.path));
const { validationResults, statusClass, reviewable, reviewed } = useAdvancedFormInput<any, any>(() => props.path);

</script>
