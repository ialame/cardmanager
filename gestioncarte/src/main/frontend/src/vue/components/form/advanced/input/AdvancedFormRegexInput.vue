<template>
    <AdvancedFormInput :path="path">
        <template #label>
            <slot name="label">
                <a href="https://regex101.com/" target="_blank" rel="noopener">{{label}}</a>
                {{labelSuffix.trim().length > 0 ? `&nbsp;${labelSuffix.trim()}` : ''}}
            </slot>
        </template>
        <template v-for="(_, slot) in $slots" :key="slot" #[slot]="data">
            <slot :name="slot" v-bind="data" :key="slot"></slot>
        </template>
    </AdvancedFormInput>
</template>

<script lang="ts" setup>
import {Path} from "@/path";
import AdvancedFormInput from "@components/form/advanced/input/AdvancedFormInput.vue";

interface Props {
    path: Path;
    label?: string;
    labelSuffix?: string;
}
withDefaults(defineProps<Props>(), {
    label: 'Regex',
    labelSuffix: ''
});

</script>
