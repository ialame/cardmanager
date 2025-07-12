<template>
    <RequiredCollapse :label="label" :required="required">
        <template v-slot:label><slot name="label"></slot></template>
        <template v-slot="{required: r}">
            <div class="d-flex flex-row">
                <FormInput v-bind="$attrs" :required="r" />
                <slot />
            </div>
        </template>
    </RequiredCollapse>
</template>

<script lang="ts" setup>
import {defineProps, withDefaults} from 'vue';
import RequiredCollapse from "@components/collapse/RequiredCollapse.vue";
import FormInput from "@components/form/FormInput.vue";

interface Props {
    required?: boolean;
    label?: string;
}

withDefaults(defineProps<Props>(),  {
    required: false,
    label: ''
});

</script>
