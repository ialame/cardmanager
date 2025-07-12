<template>
    <FormRow>
        <AdvancedFormListInput :path="path" :label="label" #default="{ path: p }">
            <CardTagInput :path="p" />
        </AdvancedFormListInput>
    </FormRow>
</template>

<script lang="ts" setup>
import FormRow from "@components/form/FormRow.vue";
import AdvancedFormListInput from "@components/form/advanced/input/AdvancedFormListInput.vue";
import {CardTagInput} from "@components/cards/tags";
import {Path} from "@/path";

interface Props {
    path?: Path;
    label?: string;
}

withDefaults(defineProps<Props>(), {
    path: 'tags',
    label: 'Tags',
});
</script>
