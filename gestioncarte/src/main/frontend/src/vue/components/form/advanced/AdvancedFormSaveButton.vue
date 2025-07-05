<template>
    <FormButton class="mt-4 mb-4 me-2" @click="save" loading>
        <slot>{{label}}</slot>
    </FormButton>
</template>

<script lang="ts" setup>
import FormButton from "@components/form/FormButton.vue";
import {SaveComposables} from "@/vue/composables/SaveComposables";
import {useAdvancedFormContext} from "@components/form/advanced/logic";
import {useIsInOpenedTab} from "@components/form/advanced/tab";
import {useAdvancedFormSide} from "@components/form/advanced/merge";

interface Props {
    shortcut?: boolean;
    label?: string;
}

interface Emits {
    (e: 'save', v: any): void;
}

const props = withDefaults(defineProps<Props>(), {
    shortcut: true,
    label: 'Enregistrer',
});
const emit = defineEmits<Emits>();

const { data, inputs } = useAdvancedFormContext();
const isInOpenedTab = useIsInOpenedTab();
const side = useAdvancedFormSide();

const validate = async () => {
    if (!isInOpenedTab.value || side.value !== -1) {
        return false;
    }
    for (const input of inputs.value) {
        if (await input.validate() === false) {
            return false;
        }
    }
    return true;
};
const save = SaveComposables.useSaveAsync(async () => {
    if (await validate()) {
        emit('save', data.value);
    }
}, () => props.shortcut);
</script>
