<template>
    <AdvancedFormCollapse :label="label" :path="path">
        <template #label><slot name="label" /></template>
        <template #default="{required: r}">
            <div class="d-flex flex-row w-100" :class="statusClass">
                <slot name="before" />
                <input v-bind="mergeProps(inputProps, $attrs)" class="advanced-form-input form-control" :type="type" :value="source" @input="onInput" :required="r" />
                <slot name="after" />
                <FormButton v-if="reviewable" color="success" class="form-btn ms-2" @click="reviewed = true">
                    <Icon name="checkmark-outline" />
                </FormButton>
                <AdvancedFormMergeButton v-if="readOnly" :path="path" />
                <AdvancedFormSourceButton v-else :path="path" />
            </div>
            <AdvancedFormFeedback class="mt-2" :validationResults="validationResults" />
        </template>
    </AdvancedFormCollapse>
</template>

<script lang="ts" setup>
import {mergeProps} from "vue";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import {Path} from "@/path";
import {isString} from "lodash";
import AdvancedFormCollapse from "@components/form/advanced/AdvancedFormCollapse.vue";
import AdvancedFormMergeButton from "@components/form/advanced/merge/AdvancedFormMergeButton.vue";
import AdvancedFormFeedback from "@components/form/advanced/AdvancedFormFeedback.vue";
import {useAdvancedFormInput} from "@components/form/advanced/logic";
import AdvancedFormSourceButton from "@components/form/advanced/source/AdvancedFormSourceButton.vue";

interface Props {
    path: Path;
    label?: string;
    type?: string;
}

const props = withDefaults(defineProps<Props>(), {
    label: '',
    type: 'text',
});

const { value, source, validationResults, statusClass, reviewed, reviewable, inputProps, readOnly } = useAdvancedFormInput<any, string | number>(() => props.path);

const onInput = (e: Event) => {
    const v = (e.target as HTMLInputElement).value;
    
    if (isString(v) && props.type === 'number') {
        value.value = parseInt(v);
    } else {
        value.value = v;
    }
}
</script>
